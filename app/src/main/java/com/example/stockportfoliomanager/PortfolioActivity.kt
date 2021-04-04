package com.example.stockportfoliomanager

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_portfolio.*
import kotlinx.android.synthetic.main.activity_register.*
import net.yslibrary.android.keyboardvisibilityevent.util.UIUtil
import java.util.*
import kotlin.collections.HashMap

//VOOR BEROEPSEXAMEN FERDI VAN DEN BROM | 2079865

class PortfolioActivity : AppCompatActivity() {

    lateinit var auth: FirebaseAuth
    var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_portfolio)
        auth = FirebaseAuth.getInstance()
        var userId = auth.currentUser.uid
        db.collection("users").document("$userId").collection("stocks")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    if (document != null) {

                        var documentPath = document.id

                        var shareNameData = document.getString("shareName")
                        var shareNoData = document.getString("shareNo")
                        var sharePriceData = document.getString("sharePrice")

                        if (shareNameData != null) {
                            if (shareNoData != null) {
                                if (sharePriceData != null) {
                                    addStockTab(documentPath, shareNameData, shareNoData, sharePriceData)
                                }
                            }
                        }

                    }
                }
            }

            .addOnFailureListener {
                Toast.makeText(
                    this@PortfolioActivity,
                    "Failed to load data.",
                    Toast.LENGTH_LONG
                ).show()
            }

        addPortfolioButton.setOnClickListener {

            when {
                TextUtils.isEmpty(stockNameField.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@PortfolioActivity,
                        "Please enter the Stock Name",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(sharesNumberField.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@PortfolioActivity,
                        "Please enter the number of Shares ",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                TextUtils.isEmpty(priceNumberField.text.toString().trim { it <= ' ' }) -> {
                    Toast.makeText(
                        this@PortfolioActivity,
                        "Please enter a Price",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                else -> {

                    val user: MutableMap<String, Any> = HashMap()
                    user["shareName"] = stockNameField.text.toString()
                    user["shareNo"] = sharesNumberField.text.toString()
                    user["sharePrice"] = priceNumberField.text.toString()

                    var uniqueID = UUID.randomUUID().toString()
                    db.collection("users").document("$userId")
                        .collection("stocks").document("$uniqueID")
                        .set(user, SetOptions.merge())
                        .addOnSuccessListener {
                            addStockTab(uniqueID, stockNameField.text.toString(), sharesNumberField.text.toString(), priceNumberField.text.toString())
                            UIUtil.hideKeyboard(this)
                            Toast.makeText(
                                this@PortfolioActivity,
                                "Successfully updated data.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                }
            }
        }
    }

    private fun addStockTab(uniqueID: String, shareName: String, shareAmount: String, sharePrice: String) {

        val userId = auth.currentUser.uid

        val tablelayout = findViewById<View>(tableLayout.id) as TableLayout
        val tablerow = TableRow(this)

        val stockText = TextView(this)
        stockText.text = "$shareName"
        stockText.setTextColor(Color.parseColor("#DAA03F"))
        stockText.gravity = Gravity.CENTER_HORIZONTAL
        stockText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
        stockText.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.FILL_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tablerow.addView(stockText)

        var shareNumberText = TextView(this)
        shareNumberText.text = "$shareAmount"
        shareNumberText.setTextColor(Color.parseColor("#DAA03F"))
        shareNumberText.gravity = Gravity.CENTER_HORIZONTAL
        shareNumberText.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.FILL_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tablerow.addView(shareNumberText)

        var sharePriceText = TextView(this)
        sharePriceText.text = "$ $sharePrice"
        sharePriceText.setTextColor(Color.parseColor("#DAA03F"))
        sharePriceText.gravity = Gravity.CENTER_HORIZONTAL
        sharePriceText.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.FILL_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tablerow.addView(sharePriceText)

        var currentPriceText = TextView(this)
        currentPriceText.text = "N/A"
        currentPriceText.setTextColor(Color.parseColor("#DAA03F"))
        currentPriceText.gravity = Gravity.CENTER_HORIZONTAL
        currentPriceText.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.FILL_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tablerow.addView(currentPriceText)

        var currentReturnText = TextView(this)
        currentReturnText.text = "N/A"
        currentReturnText.setTextColor(Color.parseColor("#DAA03F"))
        currentReturnText.gravity = Gravity.CENTER_HORIZONTAL
        currentReturnText.layoutParams = TableRow.LayoutParams(
            TableRow.LayoutParams.FILL_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT
        )
        tablerow.addView(currentReturnText)

        tablerow.setOnClickListener {
            val popup = PopupMenu(this, tablerow)
            popup.inflate(R.menu.config_entry)
            popup.setOnMenuItemClickListener {


                if (it.title == "Delete") {
                    db.collection("users").document("$userId")
                        .collection("stocks").document("$uniqueID")
                        .delete()
                        .addOnSuccessListener {
                            tablelayout.removeView(tablerow)
                            Toast.makeText(
                                this@PortfolioActivity,
                                "Successfully deleted.",
                                Toast.LENGTH_SHORT
                            ).show()

                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(
                                this@PortfolioActivity,
                                "Error: $e",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }

                if (it.title == "Edit") {
                    val dialogBuilder = AlertDialog.Builder(this)

                    val layoutInflater: LayoutInflater = LayoutInflater.from(applicationContext)
                    val view: View = layoutInflater.inflate(R.layout.config_stock, portfolioRoot, false)

                    dialogBuilder.setView(view)
                    val dialog = dialogBuilder.create()

                    val newStockName = view.findViewById<TextView>(R.id.stockNameConfig)
                    val newStockAmount = view.findViewById<TextView>(R.id.stockAmountConfig)
                    val newStockPrice = view.findViewById<TextView>(R.id.stockPriceConfig)

                    val newApplyButton = view.findViewById<Button>(R.id.applyButtonConfig)

                    newApplyButton.setOnClickListener {
                        when {
                            TextUtils.isEmpty(
                                newStockName.text.toString()
                                    .trim { it <= ' ' }) -> {
                                Toast.makeText(
                                    this@PortfolioActivity,
                                    "Please enter the Stock Name",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            TextUtils.isEmpty(
                                newStockAmount.text.toString()
                                    .trim { it <= ' ' }) -> {
                                Toast.makeText(
                                    this@PortfolioActivity,
                                    "Please enter the number of Shares ",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            TextUtils.isEmpty(
                                newStockPrice.text.toString()
                                    .trim { it <= ' ' }) -> {
                                Toast.makeText(
                                    this@PortfolioActivity,
                                    "Please enter a Price",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            else -> {

                                val user: MutableMap<String, Any> = HashMap()
                                user["shareName"] = newStockName.text.toString()
                                user["shareNo"] = newStockAmount.text.toString()
                                user["sharePrice"] = newStockPrice.text.toString()

                                db.collection("users").document("$userId")
                                    .collection("stocks").document("$uniqueID")
                                    .set(user)
                                    .addOnSuccessListener {
                                        tablelayout.removeView(tablerow)

                                        addStockTab(uniqueID, newStockName.text.toString(), newStockAmount.text.toString(), newStockPrice.text.toString())
                                        UIUtil.hideKeyboard(this)
                                        dialog.dismiss()

                                        Toast.makeText(
                                            this@PortfolioActivity,
                                            "Successfully edited.",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            this@PortfolioActivity,
                                            "Error: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }
                        }
                    }

                    dialog.show()
                }
                true
            }
            popup.show()
        }

        tablelayout.addView(
            tablerow, TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT
            )
        )

    }

}