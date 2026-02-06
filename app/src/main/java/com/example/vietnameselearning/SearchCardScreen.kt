package com.example.vietnameselearning

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun FlashCardList(
    flashCards: List<FlashCard>,
    // ADDED: Callbacks for Edit and Delete buttons
    onEditClick: (FlashCard) -> Unit,
    onDeleteClick: (FlashCard) -> Unit
) {
    // LazyColumn - only renders items currently visible on the screen, not try to load the below items
    // Efficiently renders a scrollable list to save memory, avoid lagging.
    LazyColumn(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        items(
            items = flashCards,
            key = { flashCard ->
                flashCard.uid
            }
        ) { flashCard ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${flashCard.englishCard} = ${flashCard.vietnameseCard}",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge
                )


                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Edit Button
                    Button(
                        onClick = { onEditClick(flashCard) }
                    ) {
                        Text("Edit")
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Delete Button
                    Button(
                        onClick = { onDeleteClick(flashCard) }
                    ) {
                        Text("Delete")
                    }
                }
            }
        }
    }
}


@Composable
fun SearchCardsScreen(
    changeMessage: (String) -> Unit,
    getFilteredFlashCards: suspend (String, Boolean, String, Boolean) -> Unit,
    readStateAllFlashCards: () -> List<FlashCard>,

    // ADDED: Functions for Delete and Navigate to Edit
    deleteFlashCard: suspend (FlashCard) -> Unit,
    navigateToEditCard: (FlashCard) -> Unit,

    enQuery: String,
    exactEn: Boolean,
    vnQuery: String,
    exactVn: Boolean

) {
    var flashCards by remember { mutableStateOf(emptyList<FlashCard>()) }
    // ADDED: Scope to launch suspend functions (like delete)
    val scope = rememberCoroutineScope()

    // Helper function to refresh list
    val refreshList = {
        scope.launch {
            getFilteredFlashCards(enQuery, exactEn, vnQuery, exactVn)
            flashCards = readStateAllFlashCards()

            if (flashCards.isEmpty()) {
                changeMessage("No cards found.")
            } else {
                changeMessage("Found ${flashCards.size} result(s).")
            }
        }
    }

    LaunchedEffect(Unit) {
        refreshList()
    }


    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Spacer(
            modifier = Modifier.size(16.dp)
        )

        FlashCardList(
            flashCards = flashCards,
            // ADDED: Connect buttons to actions
            onEditClick = { card ->
                navigateToEditCard(card)
            },
            onDeleteClick = { card ->
                scope.launch {
                    deleteFlashCard(card)
                    changeMessage("Deleted: ${card.englishCard}")
                    // Refresh the list after deletion to update UI
                    refreshList()
                }
            }
        )

    }
}