package com.example.vietnameselearning

import kotlinx.serialization.Serializable

// @Serializable
// Data needs to be converted from Kotlin Objects (for the App) to JSON Strings (for the Server), and vice versa.
// Acts as a bridge between Kotlin Objects (App logic) and JSON (Network format).
// This process is called Serialization(Object->JSON) / Deserialization (JSON->Object).


// DTO (Data Transfer Objects) Definition


// PAYLOAD: What we send to the server to request a login.
// Maps to JSON: {"email": "user@example.com"}
@Serializable
data class UserCredential(val email: String)

// RESPONSE: What the server sends back after we request a login.
// Maps to JSON: {"code": 200, "message": "Token sent to email..."}
@Serializable
data class TokenResponse(
    val code: Int,
    val message: String
)




// PAYLOAD: The package sent to WS Lambda.
// Maps to JSON: {"word": "Hello", "email": "...", "token": "..."}
@Serializable
data class AudioRequest(
    val word: String,
    val email: String,
    val token: String
)

// RESPONSE: The result from the Audio API.
// Maps to JSON: {"code": 200, "message": "BASE64_ENCODED_AUDIO_STRING"}
@Serializable
data class AudioResponse(
    val code: Int,
    val message: String
)