package com.example.filmgyujtemeny.ui.theme

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import androidx.annotation.Nullable
import com.example.filmgyujtemeny.Movie

class MovieDatabaseHelper(context: Context):
    SQLiteOpenHelper(context, "movies.db", null, 1){
        override fun onCreate(db: SQLiteDatabase){
            db.execSQL(
                """
                    CREATE TABLE movies(
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        title TEXT NOT NULL,
                        genre TEXT NOT NULL,
                        rating INTEGER NOT NULL
                    )
                """ .trimIndent()
            )
        }
        override fun onUpgrade(
            db: SQLiteDatabase,
            oldversion: Int,
            newVersion: Int
        ){
            db.execSQL("DROP TABLE IF EXISTS movies")
            onCreate(db)
        }
        fun addMovie(movie: Movie){
            val values = ContentValues().apply {
                put("title", movie.title)
                put("genre", movie.genre)
                put("rating", movie.rating)
            }
            writableDatabase.insert("movies", null, values)
        }

        fun getAllMovies(): List<Movie>{
            val movies = mutableListOf<Movie>()
            val cursor = readableDatabase.rawQuery(
                "SELECT * FROM movies",
                null
            )
            cursor.use {
                while (it.moveToNext()){
                    val id = it.getInt(it.getColumnIndexOrThrow("id"))
                    val title = it.getString(it.getColumnIndexOrThrow("title"))
                    val genre = it.getString(it.getColumnIndexOrThrow("genre"))
                    val rating = it.getInt(it.getColumnIndexOrThrow("rating"))
                    movies.add(Movie(id, title, genre, rating))
                }
            }
            return movies
            }

        fun upDateMovie(movie: Movie){
            val values = ContentValues().apply {
                put("title", movie.title)
                put("genre", movie.genre)
                put("rating", movie.rating)
            }
            writableDatabase.update(
                "movies",
                values,
                "id = ?",
                arrayOf(movie.id.toString())
            )
        }
        fun deleteMovie(id: Int){
            writableDatabase.delete(
                "movies",
                "id = ?",
                arrayOf(id.toString())
            )
        }
    }