package com.example.vietnameselearning

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import androidx.datastore.preferences.core.edit

@Composable
fun HomeScreen(
    navigateToAddCard: () -> Unit,
    navigateToStudyCards: () -> Unit,
    navigateToSearchCards: () -> Unit,
    navigateToLogin: () -> Unit,
    changeMessage: (String) -> Unit
) {
    // Get the current Android Context to access system services (like DataStore)
    val context = LocalContext.current
    val appContext = context.applicationContext
    // Create a Coroutine Scope to run background tasks
    val scope = rememberCoroutineScope()


    LaunchedEffect(Unit) {
        changeMessage("Please, select an option.")
        //
        val preferences = appContext.dataStore.data.first()
        val savedEmail = preferences[EMAIL]
        if (!savedEmail.isNullOrEmpty()) {
            changeMessage(savedEmail) // Display email at bottom bar
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))


        Button(modifier = Modifier.fillMaxWidth().semantics{contentDescription="navigateToStudyCards"}, onClick = { navigateToStudyCards() }) { Text("Study Cards") }
        Button(modifier = Modifier.fillMaxWidth().semantics{contentDescription="navigateToAddCard"}, onClick = { navigateToAddCard() }) { Text("Add Card") }
        Button(modifier = Modifier.fillMaxWidth().semantics{contentDescription="navigateToSearchCards"}, onClick = { navigateToSearchCards() }) { Text("Search Cards") }

        Button(modifier = Modifier.fillMaxWidth().semantics { contentDescription = "navigateToLogin" }, onClick = {
                changeMessage("") // Delete email at Bottom bar after click 'Login'
                navigateToLogin()
            }
        ) {
            Text("Log in")
        }


        Button(modifier = Modifier.fillMaxWidth().semantics { contentDescription = "ExecuteLogout" }, onClick = {
            /* 'scope.launch' starts a background task (Coroutine).
                We MUST use this because writing/deleting data (DataStore) is slow
                and would freeze the app if done on the main UI thread.
            */
                scope.launch {
                    appContext.dataStore.edit { preferences ->
                        preferences.remove(EMAIL)
                        preferences.remove(TOKEN)
                    }
                    changeMessage("") // Delete email at Bottom bar after click 'Logout'
                }
            }
        ) {
            Text("Log out")
        }
    }
}