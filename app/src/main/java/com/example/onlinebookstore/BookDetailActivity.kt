package com.example.onlinebookstore

import android.content.Intent
import android.graphics.Paint
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.google.android.material.appbar.MaterialToolbar
import java.util.Locale

class BookDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_book_detail)

        // Setup Toolbar and Back Button
        val toolbar = findViewById<MaterialToolbar>(R.id.toolbar)
        toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val book = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra("BOOK", Book::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getSerializableExtra("BOOK") as? Book
        }

        book?.let {
            findViewById<TextView>(R.id.detailBookTitle).text = it.title
            findViewById<TextView>(R.id.detailBookAuthor).text = "by ${it.author}"
            
            // Rating display
            val ratingBar = findViewById<RatingBar>(R.id.detailRatingBar)
            ratingBar.rating = (it.averageRating / 2).toFloat()
            findViewById<TextView>(R.id.detailRatingsCount).text = String.format(Locale.getDefault(), "%,d ratings", it.ratingsCount)
            
            // Pricing
            findViewById<TextView>(R.id.detailBookPrice).text = String.format(Locale.getDefault(), "%.0f", it.price)
            val oldPriceView = findViewById<TextView>(R.id.detailOldPrice)
            oldPriceView.text = String.format(Locale.getDefault(), "₹%.0f", it.oldPrice)
            oldPriceView.paintFlags = oldPriceView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            
            val savings = it.oldPrice - it.price
            val savingsPercent = (savings / it.oldPrice) * 100
            findViewById<TextView>(R.id.detailSaveText).text = String.format(Locale.getDefault(), "You save: ₹%.0f (%.0f%%)", savings, savingsPercent)

            // Product Details
            val details = "Binding: ${it.binding}\n" +
                         "Publisher: ${it.publisher}\n" +
                         "Publication Year: ${it.year}\n" +
                         "ISBN-10: ${it.isbn}\n" +
                         "Language: English"
            findViewById<TextView>(R.id.detailBookDescription).text = details
            
            // Image loading
            val urls = listOf(it.imageUrlL, it.imageUrlM, it.imageUrlS)
            loadFirstWorkingImage(findViewById(R.id.detailBookImage), urls)
        }

        findViewById<Button>(R.id.addToCartButton).setOnClickListener {
            book?.let {
                CartManager.addToCart(it)
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show()
            }
        }

        findViewById<Button>(R.id.buyNowButton).setOnClickListener {
            book?.let {
                // Clear current cart and add only this book for a single-item "Order Now" flow
                CartManager.clearCart()
                CartManager.addToCart(it)
                val intent = Intent(this, CheckoutActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun loadFirstWorkingImage(imageView: ImageView, urls: List<String>) {
        if (urls.isEmpty()) return
        val cleanUrl = urls[0].replace("http://images.amazon.com", "https://images-na.ssl-images-amazon.com").replace("\"", "").trim()
        imageView.load(cleanUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            listener(onError = { _, _ -> if (urls.size > 1) loadFirstWorkingImage(imageView, urls.drop(1)) })
        }
    }
}
