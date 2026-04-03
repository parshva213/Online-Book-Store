package com.example.onlinebookstore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import coil.ImageLoader
import coil.load
import coil.request.CachePolicy
import coil.request.ImageRequest
import java.util.Locale

class BookAdapter(
    private var books: List<Book>,
    private val onItemClick: (Book) -> Unit
) : RecyclerView.Adapter<BookAdapter.BookViewHolder>() {

    class BookViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.bookTitle)
        val author: TextView = view.findViewById(R.id.bookAuthor)
        val price: TextView = view.findViewById(R.id.bookPrice)
        val image: ImageView = view.findViewById(R.id.bookImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_book, parent, false)
        return BookViewHolder(view)
    }

    override fun onBindViewHolder(holder: BookViewHolder, position: Int) {
        val book = books[position]
        holder.title.text = book.title
        holder.author.text = book.author
        holder.price.text = String.format(Locale.getDefault(), "₹%.2f", book.price)

        val cleanUrl = book.imageUrlL
            .replace("http://images.amazon.com", "https://images-na.ssl-images-amazon.com")
            .replace("\"", "")
            .trim()

        val context = holder.itemView.context
        val placeholderRequest = ImageRequest.Builder(context)
            .data("file:///android_asset/loding.gif")
            .target { drawable ->
                holder.image.load(cleanUrl) {
                    crossfade(true)
                    placeholder(drawable)
                    error(android.R.drawable.ic_menu_report_image)
                    diskCachePolicy(CachePolicy.ENABLED)
                    memoryCachePolicy(CachePolicy.ENABLED)
                }
            }
            .build()
        
        context.imageLoader.enqueue(placeholderRequest)

        holder.itemView.setOnClickListener {
            onItemClick(book)
        }
    }

    override fun getItemCount() = books.size

    fun updateData(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }
    
    private val android.content.Context.imageLoader: ImageLoader
        get() = (applicationContext as? android.app.Application)?.let { coil.Coil.imageLoader(it) } ?: coil.Coil.imageLoader(this)
}
