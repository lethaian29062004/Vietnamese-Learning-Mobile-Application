package com.example.vietnameselearning

import kotlinx.serialization.Serializable

// Defines all routes for Type-Safe Navigation.
/*
 TYPE-SAFE NAVIGATION
 Using objects/data classes with @Serializable prevents errors like typos in URLs
 or passing the wrong data type (e.g., passing a String when an Int is required).
 Errors are caught at compile-time instead of crashing the app at runtime.
 */

// object - Static routes - not require input data to open
// data class - Dynamic routes - require input data to open

@Serializable
object HomeRoute

@Serializable
object AddCardRoute

@Serializable
object FilterCardsRoute

@Serializable
object StudyCardsRoute



@Serializable
data class SearchCardsRoute (
    val enWord: String,
    val exactEn: Boolean,
    val vnWord: String,
    val exactVn: Boolean
)

@Serializable
data class ShowCardRoute(val uid: Int)
@Serializable
data class EditCardRoute(val uid: Int)
@Serializable
object LoginRoute
@Serializable
data class TokenRoute(val email: String)
