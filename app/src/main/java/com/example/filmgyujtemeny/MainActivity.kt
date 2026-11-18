package com.example.filmgyujtemeny

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.filmgyujtemeny.ui.theme.FilmgyujtemenyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme{
                MovieScreen()
            }
        }
    }
}

@Composable
fun MovieScreen() {
    val context = LocalContext.current

    val db = remember {
        MovieDatabaseHelper(context.applicationContext)
    }

    var movies by remember {
        mutableStateOf(db.getAllMovies())
    }

    var title by remember {
        mutableStateOf("")
    }

    var genre by remember {
        mutableStateOf("")
    }

    var rating by remember {
        mutableStateOf(0)
    }

    var editingId by remember {
        mutableStateOf<Int?>(null)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(16.dp)
    ) {
        Text(
            text = "Filmgyűjtemény",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Cím") },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = genre,
            onValueChange = { genre = it },
            label = { Text("Műfaj") },
            modifier = Modifier.padding(bottom = 8.dp)
        )
        OutlinedTextField(
            value = rating.toString(),
            onValueChange = { rating = it.toIntOrNull() ?: 0 },
            label = { Text("Értékelés") },
            modifier = Modifier.padding(bottom = 16.dp)
        )
        Spacer(Modifier.height(10.dp))

        Button(enabled = title.isNotBlank() && genre.isNotBlank() && rating in 1..10,
            onClick = {
                val selectedId = editingId
                if (selectedId == null) {
                    val movie = Movie(0, title, genre, rating)
                    db.addMovie(movie)
                } else {
                    db.upDateMovie(
                        Movie(selectedId, title, genre, rating)
                    )
                }
                movies = db.getAllMovies()
                title = ""
                genre = ""
                rating = 0
                editingId = null
            }) {
            Text(if (editingId == null) "Film hozzáadása" else "Film szerkesztése")

            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(
            text = "Filmek (${movies.size})db",
            style = MaterialTheme.typography.titleLarge,
        )
        if (movies.isEmpty()){
            Text(
                text = "Nincsenek hozzáadott filmek",
                style = MaterialTheme.typography.titleMedium,
            )
        }
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(movies, key = { it.id }) {
                Card(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = it.title,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Műfaj: ${it.genre}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "Értékelés: ${it.rating}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Button(onClick = {
                            editingId = it.id
                            title = it.title
                            genre = it.genre
                            rating = it.rating
                        }) {
                            Text("Szerkesztés")
                        }
                        Button(onClick = {
                            db.deleteMovie(it.id)
                            movies = db.getAllMovies()
                        }) {
                            Text("Törlés")
                        }
                    }
                }
            }
        }

    }
}


