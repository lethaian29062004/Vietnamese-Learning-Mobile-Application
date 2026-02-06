//package com.example.vietnameselearning
//
//import androidx.compose.ui.test.*
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithText
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.compose.ui.test.performClick
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//// KHẮC PHỤC LỖI IMPORT CHO LOCALIZATION TEST
//import androidx.compose.ui.tooling.preview.DeviceConfigurationOverride
//import androidx.compose.ui.tooling.preview.LocaleList
//import androidx.compose.ui.test.assertTextEquals
//import androidx.compose.ui.test.hasText
//
//import org.junit.Assert.assertEquals
//import org.junit.Rule
//import org.junit.Test
//
//// KHAI BÁO CÁC MOCK (DOUBLE) VÀ DESTINATIONS
//// Các khai báo này phải được giữ lại để các bài test của thầy bạn hoạt động
//val getUserDouble: () -> String = { "test_user" }
//val writeCredentialsDouble: (String) -> Unit = {}
//val makeRequestWithCredentialsSuccessDouble: (String, String) -> Boolean = { _, _ -> true }
//
//object AnNamDestinations {
//    const val ADD_WORD = "add_card"
//}
//
//class MyComposeTest {
//
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    // -----------------------------------------------------------
//    // TEST GỐC
//    // -----------------------------------------------------------
//    @Test
//    fun navigateAddCard_andBack_returnsToHome() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//
//        composeTestRule.setContent {
//            Navigator(navController = navController)
//        }
//
//        assertEquals("home", navController.currentDestination?.route)
//        composeTestRule.onNodeWithText("Add Card").assertIsDisplayed()
//        composeTestRule.onNodeWithText("Add Card").performClick()
//        composeTestRule.onNodeWithText("Back").assertIsDisplayed()
//        composeTestRule.onNodeWithText("Back").performClick()
//        composeTestRule.onNodeWithText("Add Card").assertIsDisplayed()
//    }
//
//    @Test
//    fun clickOnStudyCardButton_navigatesToStudyCardScreen() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//
//        composeTestRule.setContent {
//            AppNavigator(navController = navController)
//        }
//
//        assertEquals("home", navController.currentDestination?.route)
//        composeTestRule.onNodeWithContentDescription("navigateToStudyCards")
//            .assertExists()
//            .assertIsDisplayed()
//            .performClick()
//
//        assertEquals("study_card", navController.currentDestination?.route)
//    }
//
//    // -----------------------------------------------------------
//    // TEST MỚI 1: Kiểm tra thông báo Global Message
//    // -----------------------------------------------------------
//    @Test
//    fun displayMessage() {
//        lateinit var navController: TestNavHostController
//
//        composeTestRule.setContent {
//            navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//            navController.navigatorProvider.addNavigator(ComposeNavigator())
//
//            AppNavigator(navController = navController)
//        }
//
//        composeTestRule.runOnUiThread {
//            navController.navigate(AnNamDestinations.ADD_WORD)
//        }
//
//        composeTestRule.onNodeWithContentDescription("Message")
//            .assertExists()
//            .assert(hasText("Add a new card"))
//    }
//
//    // -----------------------------------------------------------
//    // TEST MỚI 2: Kiểm tra Localization ở Locale Tiếng Việt
//    // -----------------------------------------------------------
//    @Test
//    fun viDisplayEmptyEnglish() {
//        composeTestRule.setContent {
//            DeviceConfigurationOverride(
//                DeviceConfigurationOverride.Locales(LocaleList("vi"))
//            ) {
//                // SỬ DỤNG HÀM MOCK PHÙ HỢP VỚI SIGNATURE CỦA AddCardScreen
//                AddCardScreen(
//                    onNavigateUp = {},
//                    onCardAdded = {}
//                )
//            }
//        }
//
//        composeTestRule.onNodeWithContentDescription("EnTextInput")
//            .assertTextEquals("Tiếng Anh", "")
//    }
//}