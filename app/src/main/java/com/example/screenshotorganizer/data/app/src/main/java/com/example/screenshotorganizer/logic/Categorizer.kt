package com.example.screenshotorganizer.logic

object Categorizer {

    private val payment = listOf(
        "payment", "upi", "transaction", "paid", "phonepe", "gpay",
        "google pay", "paytm", "bank", "debited", "credited", "₹", "rs."
    )
    private val shopping = listOf(
        "amazon", "flipkart", "order", "product", "cart", "delivery",
        "myntra", "meesho", "buy now"
    )
    private val study = listOf(
        "notes", "book", "exam", "class", "chapter", "lecture",
        "homework", "syllabus", "study"
    )

    fun categorize(text: String): String {
        val t = text.lowercase()
        return when {
            payment.any { t.contains(it) } -> "Payment"
            shopping.any { t.contains(it) } -> "Shopping"
            study.any { t.contains(it) } -> "Study"
            else -> "Others"
        }
    }
}
