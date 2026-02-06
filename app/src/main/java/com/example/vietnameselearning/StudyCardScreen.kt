package com.example.vietnameselearning

import android.content.Context
import android.util.Base64
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import androidx.compose.foundation.clickable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.saveable.rememberSaveable
import java.security.MessageDigest
import androidx.compose.foundation.shape.RoundedCornerShape

// Saves raw audio data (ByteArray) into the app's private internal storage.
// Allows the app to play the audio later without downloading it again.
fun saveAudioToInternalStorage(context: Context, audioData: ByteArray, filename: String) {
    // Create a file reference in the app's private directory
    // (the directory is located inside the storage)
    // 'context.filesDir' points to /data/user/0/com.example.vietnameselearning/files/
    val file = File(context.filesDir, filename)
    // Open a stream to write data into the file (automatically closed after finishing)
    // Writes (converts) raw bytes directly into an .mp3 file.
    FileOutputStream(file).use { fos ->
        fos.write(audioData)
    }
}

@Composable
fun StudyCardsScreen(
    changeMessage: (String) -> Unit,
    flashCardDao: FlashCardDao,
    networkService: NetworkService
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var lesson by remember { mutableStateOf<List<FlashCard>>(emptyList()) }
    var currentIndex by rememberSaveable { mutableStateOf(0) }
    var lang by rememberSaveable { mutableStateOf("en") }
    var isAudioExist by remember { mutableStateOf(false) }
    var isGenerating by remember { mutableStateOf(false) }

    // Track  the current audio file
    LaunchedEffect(currentIndex, lang, lesson) {
        isGenerating = false

        if (lesson.isNotEmpty() && lang == "vn") {
            // Get the current Vietnamese word
            // At any index, this operation always return the current index (0,1,2 % 3 equal to this number)
            // ?: - provide an empty string if the value is null
            val vnWord = lesson[currentIndex % lesson.size].vietnameseCard ?: ""

            // Create a unique filename using HashCode to avoid special characters/spaces
            val audioFile = hashStringSha256(vnWord.trim())

            // Create a reference (address) to where the file SHOULD be in internal storage
            val file = File(context.filesDir, audioFile)
            // Check if the file exists
            isAudioExist = file.exists()
        }
    }

    // Loads the lesson data when the screen first opens
    LaunchedEffect(Unit) {
        // Fetch 3 random cards from the Room database
        lesson = flashCardDao.getLesson(3)
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        // Only display UI if the lesson list is not empty
        if (lesson.isNotEmpty()) {
            // Calculate the current card using modulo operator (%) to loop the list infinitely
            val currentCard = lesson[currentIndex % lesson.size]
            // Determine which text to show based on the 'lang' state
            val displayWord = if (lang == "en") currentCard.englishCard else currentCard.vietnameseCard
            val vnWord = currentCard.vietnameseCard ?: ""

            Card(
                // Toggle language when the card is clicked
                modifier = Modifier.fillMaxWidth().clickable { lang = if (lang == "en") "vn" else "en" },
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = CardDefaults.outlinedCardBorder(),
                shape = RoundedCornerShape(percent = 50)
            ) {
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
                ) {
                    Text(text = displayWord ?: "", fontSize = 32.sp)
                }
            }

            // Only show audio functions if the Vietnamese side is displaying
            if (lang == "vn") {
                // CASE 1: Audio file exists -> show 'Play' button
                if (isAudioExist) {
                    Button(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), onClick = {

                        val audioFile = hashStringSha256(vnWord.trim())
                        val file = File(context.filesDir, audioFile)

                        // Prepare ExoPlayer to play the local file
                        val filePath = file.absolutePath
                        // convert the file path to standard format
                        val uri = filePath.toUri()
                        val mediaItem = MediaItem.fromUri(uri)
                        val player = ExoPlayer.Builder(context).build()


                        // Add a listener to monitor the player's events asynchronously.
                        /* Since playing audio happens in background, need that to notify
                           the app about status changes (loading,playing, finished). */
                        player.addListener(object : Player.Listener {
                            // This callback is triggered automatically whenever the player switches states.
                            // playbackState: An integer representing the current status (Buffering, Ready,
                            override fun onPlaybackStateChanged(playbackState: Int) {
                                when (playbackState) {
                                    // The player is loading data (from file or network) and is not ready.
                                    Player.STATE_BUFFERING -> { changeMessage("Buffering...") }
                                    // The player has buffered enough data and can start playing.
                                    Player.STATE_READY -> { changeMessage("Ready") }
                                    // The audio has finished playing.
                                    // When audio finishes, release the player to free up memory
                                    Player.STATE_ENDED -> {
                                        player.release()
                                        changeMessage("Finished")
                                    }
                                    // The player is instantiated but has no media to play, or has stopped.
                                    Player.STATE_IDLE -> { }
                                }
                            }
                        })
                        // Load media and start playing
                        player.setMediaItem(mediaItem)
                        player.prepare()
                        player.play()
                    }) {
                        Text("Play")
                    }

                } else {
                    // CASE 2: Audio does NOT exist -> show 'Generate' button (Call API)
                    Button(modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        enabled = !isGenerating,
                        onClick = {
                            scope.launch {
                                isGenerating = true
                                changeMessage("Generating audio...")
                                try {
                                    // Retrieve credentials from DataStore
                                    val prefs = context.dataStore.data.first()
                                    val rawEmail = (prefs[EMAIL] ?: "").trim()
                                    val rawToken = (prefs[TOKEN] ?: "").trim()

                                    // Remove all whitespace characters (spaces, newlines, tabs) to prevent HTTP 500 errors
                                    val cleanEmail = rawEmail.replace("\\s+".toRegex(), "")
                                    val cleanToken = rawToken.replace("\\s+".toRegex(), "")

                                    if (cleanEmail.isNotEmpty() && cleanToken.isNotEmpty()) {
                                        // Call AWS Lambda to generate audio
                                        val response = networkService.generateAudio(
                                            request = AudioRequest(vnWord.trim(), cleanEmail, cleanToken)
                                        )
                                        if (response.code == 200) {
                                            // Decode the Base64 audio string from server
                                            val audioData = Base64.decode(response.message, Base64.DEFAULT)

                                            val audioFile = hashStringSha256(vnWord.trim())

                                            // Save to internal storage and update UI state
                                            saveAudioToInternalStorage(context, audioData, audioFile)
                                            isAudioExist = true
                                            changeMessage("Generated and Saved")
                                        } else {
                                            changeMessage("Error: ${response.message}")
                                        }
                                    } else {
                                        changeMessage("Missing credentials. Please login.")
                                    }
                                } catch (e: Exception) {
                                    changeMessage("Error: ${e.message}")
                                } finally {
                                    isGenerating = false
                                }
                            }
                        }) {
                        if (isGenerating) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text("Generate")
                        }
                    }
                }

                // Button to move to the next card
                Button(onClick = { currentIndex++; lang = "en" }, modifier = Modifier.fillMaxWidth().padding(top = 16.dp)) {
                    Text("Next Card")
                }
            }
        }
    }
}


// Convert a word to a SHA256-hash string
// Ensures filenames are unique and handle special characters safely
private fun hashStringSha256(input: String): String {
    return MessageDigest.getInstance("SHA-256")
        // convert the word to a byte array, first
        .digest(input.toByteArray())
        // finally, convert the byte array to a string
        .joinToString("") { "%02x".format(it) }
}