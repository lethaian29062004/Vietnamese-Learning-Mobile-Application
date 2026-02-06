package com.example.vietnameselearning

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.compose.rememberNavController
import com.example.vietnameselearning.ui.theme.VietnameseLearningTheme
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

// Context.dataStore - key-value storage
// For the app to remember the user_credentials after being closed & reopened
val Context.dataStore by preferencesDataStore(name = "user_credentials")

// labelling TOKEN - EMAIL keys as token - email
val TOKEN = stringPreferencesKey("token")
val EMAIL = stringPreferencesKey("email")


// This is the main entry point of the application.
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Use applicationContext to prevent memory leaks
        val appContext = applicationContext

        // Database initialization : Get the Singleton instance of the Database
        val db = FlashCardDatabase.getDatabase(appContext)
        // Get the tool DAO from the database
        val flashCardDao = db.flashCardDao()


        // Configures the HTTP connection
        // Sets a 30-second timeout. If the server (Lambda) takes longer than 30s to reply,
        // the app cancels the request to prevent freezing.
        val sharedOkHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build()

        // Retrofit builder
        val retrofit: Retrofit = Retrofit.Builder()
            // this default url will be overridden with dynamic urls in NetworkService.kt
            .baseUrl("https://placeholder.com")
            .client(sharedOkHttpClient)
            // converts the JSON data from server to Kotlin objects
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        // Initialize the implementation of the NetworkService interface, to make the API call
        val networkService = retrofit.create(NetworkService::class.java)


        setContent {
            VietnameseLearningTheme {
                // Initialize the Navigation Controller to manage screen transition
                val navController = rememberNavController()
                // Pass the DAO & NetworkService down to the Navigator
                // The Navigator will distribute these tools to the specific screen
                Navigator(navController, flashCardDao, networkService )

            }

        }

    }

}

