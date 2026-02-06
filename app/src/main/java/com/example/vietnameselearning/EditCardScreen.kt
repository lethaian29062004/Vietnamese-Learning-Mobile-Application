package com.example.vietnameselearning

import android.util.Base64
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.security.MessageDigest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.remember

@Composable
fun EditCardScreen(
    uid: Int,
    changeMessage: (String) -> Unit,
    getFlashCardById: suspend (Int) -> FlashCard, // Function to fetch card details
    updateFlashCard: suspend (FlashCard) -> Unit,
    getFlashCards: suspend () -> Unit, // Function to refresh the list after update
    navigateBack: () -> Unit,
    networkService: NetworkService
) {
    var enWord by rememberSaveable { mutableStateOf("") }
    var vnWord by rememberSaveable { mutableStateOf("") }

    var email by rememberSaveable { mutableStateOf("") }
    var token by rememberSaveable { mutableStateOf("") }

    var isAudioExist by rememberSaveable { mutableStateOf(false) }

    var isGenerating by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val appContext = context.applicationContext


    // Check if the audio file exists in internal storage
    fun checkAudioExistence(word: String) {
        if (word.isBlank()) {
            isAudioExist = false
            return
        }
        // Generate the unique filename using SHA-256 hash
        val filename = hashStringSHA256(word.trim())
        val file = File(context.filesDir, filename)
        isAudioExist = file.exists()
    }


    LaunchedEffect(uid) {
        // Retrieve user credentials from DataStore
        val prefs = appContext.dataStore.data.first()
        // Trim to remove any accidental whitespace that causes HTTP 500 errors
        email = (prefs[EMAIL] ?: "").trim()
        token = (prefs[TOKEN] ?: "").trim()

        // Fetch FlashCard details from database
        val flashCard = getFlashCardById(uid)
        enWord = flashCard.englishCard ?: ""
        vnWord = flashCard.vietnameseCard ?: ""

        changeMessage("Editing flash card.")
        checkAudioExistence(vnWord)
    }

    // Re-check audio existence whenever the user changes the Vietnamese word
    LaunchedEffect(vnWord) {
        checkAudioExistence(vnWord)
    }


    Column(
        modifier = Modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = enWord,
            onValueChange = { enWord = it },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "enTextField" },
            label = { Text("en") }
        )

        TextField(
            value = vnWord,
            onValueChange = { vnWord = it },
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "vnTextField" },
            label = { Text("vn") }
        )

        // Read-only field - the hashed filename of the audio
        TextField(
            value = if(vnWord.isNotBlank()) hashStringSHA256(vnWord.trim()) else "",
            onValueChange = {},
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            label = { Text("audio") }
        )

        Spacer(modifier = Modifier.height(16.dp))


        // Update Button
        Button(
            modifier = Modifier.fillMaxWidth().semantics { contentDescription = "Update" },
            onClick = {
                scope.launch {
                    try {
                        updateFlashCard(
                            FlashCard(
                                uid = uid,
                                englishCard = enWord.trim(),
                                vietnameseCard = vnWord.trim()
                            )
                        )
                        getFlashCards() // Refresh the list
                        changeMessage("Flash card updated successfully.")
                        navigateBack() // Go back to the list
                    } catch (e: Exception) {
                        changeMessage("Error updating flash card: ${e.message}")
                    }
                }
            }

        ) {
            Text("Update flashcard")
        }



        // Logic for Audio Buttons

        // Case 1 : Audio Exist -> Show 'Clean' & 'Play' button
            // Clean
        if (isAudioExist) {
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                onClick = {
                    val filename = hashStringSHA256(vnWord.trim())
                    val file = File(context.filesDir, filename)
                    if (file.exists()) {
                        file.delete()
                        isAudioExist = false // Update UI state
                        changeMessage("Audio deleted.")
                    }
                }

            ) {
                Text("Clean audio")
            }

            // Play
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                onClick = {
                    val filename = hashStringSHA256(vnWord.trim())
                    val file = File(context.filesDir, filename)
                    val uri = file.absolutePath.toUri()
                    val mediaItem = MediaItem.fromUri(uri)
                    val player = ExoPlayer.Builder(context).build()

                    // Listener to release player resources when finished
                    player.addListener(object : Player.Listener {
                        override fun onPlaybackStateChanged(playbackState: Int) {
                            if (playbackState == Player.STATE_ENDED) {
                                player.release()
                            }
                        }
                    })
                    player.setMediaItem(mediaItem)
                    player.prepare()
                    player.play()
                }

            ) {
                Text("Play audio")
            }


        } else {

            // CASE 2: Audio does not exist -> Show 'Generate' button
            Button(
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                enabled = !isGenerating,
                onClick = {
                    scope.launch {
                        isGenerating = true
                        try {
                            if (email.isNotEmpty() && token.isNotEmpty() && vnWord.isNotBlank()) {
                                changeMessage("Generating audio...")

                                // Remove all whitespace characters (spaces, newlines, tabs) to prevent HTTP 500 errors.
                                val cleanEmail = email.replace("\\s+".toRegex(), "")
                                val cleanToken = token.replace("\\s+".toRegex(), "")
                                val cleanWord = vnWord.trim()

                                // Call API to generate audio
                                val response = networkService.generateAudio(
                                    request = AudioRequest(cleanWord, cleanEmail, cleanToken)
                                )

                                if (response.code == 200) {
                                    // Decode audio data and save to file
                                    val audioData = Base64.decode(response.message, Base64.DEFAULT)
                                    val filename = hashStringSHA256(cleanWord)
                                    saveAudioToInternalStorage(context, audioData, filename)

                                    isAudioExist = true
                                    changeMessage("Audio generated and saved.")
                                } else {
                                    changeMessage("Error: ${response.message}")
                                }
                            } else {
                                changeMessage("Missing credentials or word (Please Login).")
                            }
                        } catch (e: Exception) {
                            changeMessage("Error: ${e.message}")
                        } finally {
                            isGenerating = false
                        }
                    }
                }
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Generate audio")
                }
            }
        }
    }
}



private fun hashStringSHA256(input: String): String {
    return MessageDigest.getInstance("SHA-256")
        .digest(input.toByteArray())
        .joinToString("") { "%02x".format(it) }
}