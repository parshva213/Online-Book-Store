package com.example.onlinebookstore

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var bookAdapter: BookAdapter
    private var allBooks = mutableListOf<Book>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        
        lifecycleScope.launch {
            val books = loadBooksFromCSV()
            allBooks.clear()
            allBooks.addAll(books)
            
            bookAdapter = BookAdapter(allBooks) { book ->
                val intent = Intent(this@MainActivity, BookDetailActivity::class.java)
                intent.putExtra("BOOK", book)
                startActivity(intent)
            }
            recyclerView.adapter = bookAdapter
        }

        findViewById<FloatingActionButton>(R.id.btnViewCart).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
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
                    
                    // Generate realistic prices and discounts
                    val price = Random.nextDouble(299.0, 1499.0)
                    val oldPrice = price * Random.nextDouble(1.1, 1.5) // 10% to 50% higher
                    
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
