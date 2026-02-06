package com.example.vietnameselearning

import android.database.sqlite.SQLiteConstraintException
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.saveable.rememberSaveable


@Composable
fun AddCardScreen(
    changeMessage: (String) -> Unit,
    insertFlashCard: suspend (FlashCard) -> Unit
) {
    // 'rememberSaveable' preserves the state (keep the text) during configuration changes (e.g, screen rotation)
    var enWord by rememberSaveable { mutableStateOf("") }
    var vnWord by rememberSaveable { mutableStateOf("") }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        changeMessage("Please, add a flash card.")
    }

    // Arrange the TextFields and Button vertically
    Column {
        TextField(
            value = enWord,
            // 'it' represents the new text typed by the user
            onValueChange = { enWord = it },
            modifier = Modifier.semantics { contentDescription = "enTextField" },
            label = { Text("en") }
        )
        TextField(
            value = vnWord,
            onValueChange = { vnWord = it },
            modifier = Modifier.semantics { contentDescription = "vnTextField" },
            label = { Text("vn") }
        )
        Button(
            modifier = Modifier.semantics { contentDescription = "Add" },
            onClick = {
                    scope.launch {
                        try {
                            insertFlashCard(
                                FlashCard(
                                    // uid is set up - autoGenerate=True
                                    // uid = 0 tells Room DB: "Ignore this ID and auto-generate a new unique ID
                                    uid = 0,
                                    // .trim() removes accidental spaces at the start/end
                                    // (e.g., " Hello " -> "Hello")
                                    englishCard = enWord.trim(),
                                    vietnameseCard = vnWord.trim()
                                )
                            )
                            enWord = ""
                            vnWord = ""
                            changeMessage("The flash card has been added to your database.")

                        } catch (e: SQLiteConstraintException) {

                            changeMessage("The flash card already exists in your database.")
                        } catch (e: Exception) {

                            changeMessage("Unexpected error: ")

                        }
                    }

            })
        {
            Text("Add")
        }
    }


}