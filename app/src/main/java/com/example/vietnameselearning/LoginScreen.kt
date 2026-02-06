package com.example.vietnameselearning

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    changeMessage: (String) -> Unit,
    networkService: NetworkService,
    navigateToToken: (String) -> Unit
) {
    var email by rememberSaveable { mutableStateOf("") }
    var isLoading by rememberSaveable { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        changeMessage("Please log in.")
    }

    Column(
        modifier = Modifier.padding(16.dp).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TextField(
            value = email,
            onValueChange = { email = it.trim() },
            label = { Text("email") },
            enabled = !isLoading,
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "emailTextField" }
        )

        Button(
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Enter" },
            enabled = !isLoading,
            onClick = {
                // Launch a coroutine to handle the network request
                scope.launch {
                    //
                    isLoading = true
                    changeMessage("Logging in...")


                    // Switch to the IO thread for network operations (prevents UI freezing)
                    withContext(Dispatchers.IO) {
                        try {
                            // Call the AWS Lambda function to generate a token
                            val result = networkService.generateToken(email = UserCredential(email))
                            Log.d("FLASHCARD", result.toString())

                            if (result.code == 200) {
                                // Switch back to the Main thread to update the UI
                                withContext(Dispatchers.Main) {
                                    changeMessage("Email sent. Please check your inbox.")
                                    navigateToToken(email)
                                }
                            } else {
                                changeMessage("Error: ${result.message}")
                            }
                        } catch (e: Exception) {
                            changeMessage("Network error: ${e.message}")
                            Log.d("FLASHCARD", "Exception: $e")
                        } finally {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                            }
                        }

                    }
                }
            }
        ) {
            if (isLoading) {
                // Display 'loading' circle icon
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Enter")
            }
        }
    }
}