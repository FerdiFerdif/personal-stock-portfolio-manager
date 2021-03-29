package com.example.stockportfoliomanager

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_portfolio.*
import kotlinx.android.synthetic.main.activity_register.*
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

                            var shareNameData = document.getString("shareName")
                            var shareNoData = document.getString("shareNo")
                            var sharePriceData = document.getString("sharePrice")

                            val tl = findViewById<View>(tableLayout.id) as TableLayout
                            val tr = TableRow(this)

                            val stockText = TextView(this)
                            stockText.setText("$shareNameData")
                            stockText.setTextColor(Color.parseColor("#DAA03F"))
                            stockText.setGravity(Gravity.CENTER_HORIZONTAL)
                            stockText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                            stockText.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tr.addView(stockText)

                            var shareNumber = TextView(this)
                            shareNumber.setText("$shareNoData")
                            shareNumber.setTextColor(Color.parseColor("#DAA03F"))
                            shareNumber.setGravity(Gravity.CENTER_HORIZONTAL)
                            shareNumber.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tr.addView(shareNumber)

                            var sharePrice = TextView(this)
                            sharePrice.setText("$ $sharePriceData")
                            sharePrice.setTextColor(Color.parseColor("#DAA03F"))
                            sharePrice.setGravity(Gravity.CENTER_HORIZONTAL)
                            sharePrice.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tr.addView(sharePrice)

                            var currentPrice = TextView(this)
                            currentPrice.setText("N/A")
                            currentPrice.setTextColor(Color.parseColor("#DAA03F"))
                            currentPrice.setGravity(Gravity.CENTER_HORIZONTAL)
                            currentPrice.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tr.addView(currentPrice)

                            var currentReturn = TextView(this)
                            currentReturn.setText("N/A")
                            currentReturn.setTextColor(Color.parseColor("#DAA03F"))
                            currentReturn.setGravity(Gravity.CENTER_HORIZONTAL)
                            currentReturn.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                            tr.addView(currentReturn)

                            tl.addView(tr, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                        }
                    }
                }

                .addOnFailureListener {
                    Toast.makeText(this@PortfolioActivity, "Failed to load data.", Toast.LENGTH_LONG).show()
                }

        addPortfolioButton.setOnClickListener{

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
                                Toast.makeText(this@PortfolioActivity, "Successfully updated data.", Toast.LENGTH_LONG).show()
                                val tl = findViewById<View>(tableLayout.id) as TableLayout
                                val tr = TableRow(this)

                                val stockText = TextView(this)
                                stockText.setText(stockNameField.text.toString())
                                stockText.setTextColor(Color.parseColor("#DAA03F"))
                                stockText.setGravity(Gravity.CENTER_HORIZONTAL)
                                stockText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14F)
                                stockText.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                                tr.addView(stockText)

                                var shareNumber = TextView(this)
                                shareNumber.setText(sharesNumberField.text.toString())
                                shareNumber.setTextColor(Color.parseColor("#DAA03F"))
                                shareNumber.setGravity(Gravity.CENTER_HORIZONTAL)
                                shareNumber.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                                tr.addView(shareNumber)

                                var sharepriceNo = priceNumberField.text.toString()

                                var sharePrice = TextView(this)
                                sharePrice.setText("$ $sharepriceNo")
                                sharePrice.setTextColor(Color.parseColor("#DAA03F"))
                                sharePrice.setGravity(Gravity.CENTER_HORIZONTAL)
                                sharePrice.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                                tr.addView(sharePrice)

                                var currentPrice = TextView(this)
                                currentPrice.setText("N/A")
                                currentPrice.setTextColor(Color.parseColor("#DAA03F"))
                                currentPrice.setGravity(Gravity.CENTER_HORIZONTAL)
                                currentPrice.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                                tr.addView(currentPrice)

                                var currentReturn = TextView(this)
                                currentReturn.setText("N/A")
                                currentReturn.setTextColor(Color.parseColor("#DAA03F"))
                                currentReturn.setGravity(Gravity.CENTER_HORIZONTAL)
                                currentReturn.setLayoutParams(TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT))
                                tr.addView(currentReturn)

                                tl.addView(tr, TableLayout.LayoutParams(TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.WRAP_CONTENT))

                            }
                            .addOnFailureListener {
                                Toast.makeText(this@PortfolioActivity, "Failed to update data.", Toast.LENGTH_LONG).show()
                            }

                }

            }

        }

    }
}