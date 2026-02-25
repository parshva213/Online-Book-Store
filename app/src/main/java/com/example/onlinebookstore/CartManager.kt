package com.example.onlinebookstore

object CartManager {
    private val cartItems = mutableMapOf<String, CartItem>()

    data class CartItem(
        val book: Book,
        var quantity: Int
    )

    fun addToCart(book: Book) {
        val existingItem = cartItems[book.isbn]
        if (existingItem != null) {
            existingItem.quantity++
        } else {
            cartItems[book.isbn] = CartItem(book, 1)
        }
    }

    fun removeFromCart(isbn: String) {
        cartItems.remove(isbn)
    }

    fun updateQuantity(isbn: String, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(isbn)
        } else {
            cartItems[isbn]?.quantity = quantity
        }
    }

    fun getCartItems(): List<CartItem> {
        return cartItems.values.toList()
    }

    fun clearCart() {
        cartItems.clear()
    }

    fun getTotalPrice(): Double {
        return cartItems.values.sumOf { item ->
            item.book.price * item.quantity
        }
    }

    fun getItemCount(): Int {
        return cartItems.values.sumOf { it.quantity }
    }
}
