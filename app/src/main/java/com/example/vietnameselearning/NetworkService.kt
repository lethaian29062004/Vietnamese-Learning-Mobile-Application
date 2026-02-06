package com.example.vietnameselearning

import retrofit2.http.Body
import retrofit2.http.PUT
import retrofit2.http.Url

// Defines the API Endpoints (URLs) and the structure of network requests.
// The Retrofit library uses this to automatically generate the code for executing HTTP calls.
interface NetworkService {

    // Send the user's email to the backend (lambda function url) to request the login token.
    @PUT
    suspend fun generateToken(
        @Url url: String = "https://egsbwqh7kildllpkijk6nt4soq0wlgpe.lambda-url.ap-southeast-1.on.aws/",
        @Body email: UserCredential
    ): TokenResponse


    // Request the pronunciation audio file for a specific word.
    @PUT
    suspend fun generateAudio(
        @Url url: String = "https://ityqwv3rx5vifjpyufgnpkv5te0ibrcx.lambda-url.ap-southeast-1.on.aws/",
        @Body request: AudioRequest
    ): AudioResponse
}