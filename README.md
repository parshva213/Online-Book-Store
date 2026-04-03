# Online Book Store 📚

A native Android application built with Kotlin that simulates an online bookstore. Users can browse a catalog of books, view detailed information about each book, and manage a shopping cart.

## Features

*   **Browse Books:** View a list of available books in the `MainActivity`.
*   **Book Details:** View deeper information about a specific book, such as title, author, description, and price in the `BookDetailActivity`.
*   **Shopping Cart:** Add books to a cart, view the cart in the `CartActivity`, and manage the items you've added.
*   **Image Loading:** Uses [Coil](https://coil-kt.github.io/coil/) for fast and efficient image loading, including support for animated GIFs.

## Tech Stack

*   **Platform:** Android
*   **Language:** Kotlin
*   **Build System:** Gradle (Kotlin DSL)
*   **Minimum SDK:** 24
*   **Target SDK:** 35
*   **Third-party Libraries:**
    *   [Coil](https://github.com/coil-kt/coil): Image loading backed by Kotlin Coroutines.

## Architecture & Components

The application follows a straightforward activity-based architecture with the following key components:

*   **`MainActivity`**: The starting point of the app, displaying the book catalog via `BookAdapter`.
*   **`BookDetailActivity`**: Displays the granular details of a selected `Book` object.
*   **`CartActivity`**: Displays the items currently in the user's cart using `CartAdapter`.
*   **`CartManager`**: A centralized manager class for handling cart state and operations (adding, updating, or removing items).
*   **`Book`**: The core data model representing a book.

## Getting Started

1. Open the project folder (`Online-Book-Store`) in **Android Studio**.
2. Allow Gradle to synchronize and download the necessary dependencies.
3. Build and run the app on an emulator or a physical Android device.
