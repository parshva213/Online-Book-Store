package com.example.onlinebookstore

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private lateinit var adapter: CartAdapter
    private lateinit var totalPriceView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        totalPriceView = findViewById(R.id.cartTotalPrice)
        val recyclerView = findViewById<RecyclerView>(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        adapter = CartAdapter(CartManager.getCartItems()) {
            updateTotal()
        }
        recyclerView.adapter = adapter

        updateTotal()

        findViewById<Button>(R.id.checkoutButton).setOnClickListener {
            if (CartManager.getCartItems().isNotEmpty()) {
                Toast.makeText(this, "Order placed successfully!", Toast.LENGTH_LONG).show()
                CartManager.clearCart()
                finish()
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateTotal() {
        totalPriceView.text = String.format(Locale.getDefault(), "₹%.2f", CartManager.getTotalPrice())
        if (::adapter.isInitialized) {
            adapter.updateData(CartManager.getCartItems())
        }
    }
}
