package com.example.onlinebookstore

import android.content.Intent
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.Coil
import coil.ImageLoader
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private var allBooks = mutableListOf<Book>()
    private lateinit var toolbar: MaterialToolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Initialize Coil with GIF support
        val imageLoader = ImageLoader.Builder(this)
            .components {
                if (SDK_INT >= 28) {
                    add(ImageDecoderDecoder.Factory())
                } else {
                    add(GifDecoder.Factory())
                }
            }
            .build()
        Coil.setImageLoader(imageLoader)

        setContentView(R.layout.activity_main)

        toolbar = findViewById(R.id.mainToolbar)
        setupToolbar()

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        // Pre-initialize adapter with empty list to prevent crash
        bookAdapter = BookAdapter(allBooks) { book ->
            val intent = Intent(this@MainActivity, BookDetailActivity::class.java)
            intent.putExtra("BOOK", book)
            startActivity(intent)
        }
        recyclerView.adapter = bookAdapter

        lifecycleScope.launch {
            val books = loadBooksFromCSV()
            allBooks.clear()
            allBooks.addAll(books)
            bookAdapter.updateData(allBooks)
        }

        findViewById<FloatingActionButton>(R.id.btnViewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun setupToolbar() {
        toolbar.inflateMenu(R.menu.main_menu)
        val searchItem = toolbar.menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.queryHint = getString(R.string.search_hint)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterBooks(newText)
                return true
            }
        })
    }

    private fun filterBooks(query: String?) {
        if (!::bookAdapter.isInitialized) return

        val filteredList = if (query.isNullOrBlank()) {
            allBooks
        } else {
            allBooks.filter { 
                it.title.contains(query, ignoreCase = true) || 
                it.author.contains(query, ignoreCase = true)
            }
        }
        bookAdapter.updateData(filteredList)
    }

    private suspend fun loadBooksFromCSV(): List<Book> = withContext(Dispatchers.IO) {
        val booksMap = mutableMapOf<String, Book>()
        
        try {
            val inputStream = assets.open("books.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, "ISO-8859-1"))
            reader.readLine() // Skip header
            
            var line: String?
            var count = 0
            while (reader.readLine().also { line = it } != null && count < 2000) {
                val tokens = line!!.split("\";\"").map { it.replace("\"", "") }
                if (tokens.size >= 8) {
                    val isbn = tokens[0]
                    
                    val price = Random.nextDouble(299.0, 1499.0)
                    val oldPrice = price * Random.nextDouble(1.1, 1.5)
                    
                    booksMap[isbn] = Book(
                        isbn = isbn,
                        title = tokens[1],
                        author = tokens[2],
                        year = tokens[3],
                        publisher = tokens[4],
                        imageUrlS = tokens[5],
                        imageUrlM = tokens[6],
                        imageUrlL = tokens[7],
                        price = price,
                        oldPrice = oldPrice,
                        binding = if (Random.nextBoolean()) "Paperback" else "Hardcover"
                    )
                    count++
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading books.csv: ${e.message}")
        }

        try {
            val inputStream = assets.open("ratings.csv")
            val reader = BufferedReader(InputStreamReader(inputStream, "ISO-8859-1"))
            reader.readLine()
            
            val ratingsData = mutableMapOf<String, MutableList<Int>>()
            
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                val tokens = line!!.split("\";\"").map { it.replace("\"", "") }
                if (tokens.size >= 3) {
                    val isbn = tokens[1]
                    val rating = tokens[2].toIntOrNull() ?: 0
                    if (rating > 0) {
                        ratingsData.getOrPut(isbn) { mutableListOf() }.add(rating)
                    }
                }
            }
            
            for ((isbn, ratings) in ratingsData) {
                if (booksMap.containsKey(isbn)) {
                    val book = booksMap[isbn]!!
                    book.averageRating = ratings.average()
                    book.ratingsCount = ratings.size
                }
            }
            reader.close()
        } catch (e: Exception) {
            Log.e("MainActivity", "Error loading ratings.csv: ${e.message}")
        }

        booksMap.values.toList()
    }
}
