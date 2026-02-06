package com.example.vietnameselearning

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

@Composable
fun FilterCardsScreen(
    changeMessage: (String) -> Unit,
    onSearchClicked: (String, Boolean, String, Boolean) -> Unit,
) {

    var enWord by remember { mutableStateOf("") }
    var exactEn by remember { mutableStateOf(false) }
    var vnWord by remember { mutableStateOf("") }
    var exactVn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        changeMessage("Enter criteria to filter cards.")
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {


        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Finding the exact English word
            Checkbox(
                checked = exactEn,
                onCheckedChange = { exactEn = it },
                modifier = Modifier.semantics { contentDescription = "enCheckbox" }
            )
            TextField(
                value = enWord,
                onValueChange = { enWord = it },
                label = { Text("English") },
                modifier = Modifier.weight(1f).semantics { contentDescription = "enFilterInput" }
            )
        }



        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Finding the exact Vietnamese word
            Checkbox(
                checked = exactVn,
                onCheckedChange = { exactVn = it },
                modifier = Modifier.semantics { contentDescription = "vnCheckbox" }
            )
            TextField(
                value = vnWord,
                onValueChange = { vnWord = it },
                label = { Text("Vietnamese") },
                modifier = Modifier.weight(1f).semantics { contentDescription = "vnFilterInput" }
            )
        }


        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                // Pass 4 values to the Navigator
                onSearchClicked(enWord.trim(), exactEn, vnWord.trim(), exactVn)
            },
            modifier = Modifier.semantics { contentDescription = "SearchButton" }
        ) {
            Text("Search")
        }
    }
}