package com.example.onlinebookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.load
import java.util.Locale

class CartAdapter(
    private var items: List<CartManager.CartItem>,
    private val onUpdate: () -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.cartBookTitle)
        val price: TextView = view.findViewById(R.id.cartBookPrice)
        val image: ImageView = view.findViewById(R.id.cartBookImage)
        val quantity: TextView = view.findViewById(R.id.tvQuantity)
        val btnPlus: Button = view.findViewById(R.id.btnPlus)
        val btnMinus: Button = view.findViewById(R.id.btnMinus)
        val btnRemove: ImageButton = view.findViewById(R.id.btnRemove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.book.title
        
        // Display price in Rupees
        holder.price.text = String.format(Locale.getDefault(), "₹%.2f", item.book.price)
        holder.quantity.text = item.quantity.toString()
        
        val cleanUrl = item.book.imageUrlL
            .replace("http://images.amazon.com", "https://images-na.ssl-images-amazon.com")
            .replace("\"", "")
            .trim()
        
        holder.image.load(cleanUrl) {
            crossfade(true)
            placeholder(android.R.drawable.ic_menu_gallery)
            error(android.R.drawable.ic_menu_report_image)
        }

        holder.btnPlus.setOnClickListener {
            CartManager.addToCart(item.book)
            onUpdate()
        }

        holder.btnMinus.setOnClickListener {
            CartManager.updateQuantity(item.book.isbn, item.quantity - 1)
            onUpdate()
        }

        holder.btnRemove.setOnClickListener {
            CartManager.removeFromCart(item.book.isbn)
            onUpdate()
        }
    }

    override fun getItemCount() = items.size
    
    fun updateData(newItems: List<CartManager.CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}
