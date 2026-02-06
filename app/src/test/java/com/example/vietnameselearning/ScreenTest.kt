//package com.example.vietnameselearning
//
//import android.database.sqlite.SQLiteConstraintException
//import androidx.compose.ui.test.assertTextEquals
//import androidx.compose.ui.test.junit4.StateRestorationTester
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onNodeWithContentDescription
//import androidx.compose.ui.test.performClick
//import androidx.compose.ui.test.performTextInput
//import androidx.navigation.NavDestination.Companion.hasRoute // Quan trọng: Import này cho Type-Safe
//import androidx.navigation.compose.ComposeNavigator
//import androidx.navigation.testing.TestNavHostController
//import androidx.test.core.app.ApplicationProvider
//import org.junit.Assert.assertEquals
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//
//// --- DUMMY DAOs (Đã sửa để khớp với Interface của bạn) ---
//
//class DummyFlashCardDao : FlashCardDao {
//    override suspend fun getAll(): List<FlashCard> {
//        return emptyList()
//    }
//
//    override suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard> {
//        return emptyList()
//    }
//
//    override suspend fun findByCards(english: String, vietnamese: String): FlashCard {
//        return FlashCard(0, "", "")
//    }
//
//    override suspend fun getById(uid: Int): FlashCard {
//        return FlashCard(0, "", "")
//    }
//
//    override suspend fun insertAll(vararg flashCard: FlashCard) {
//        // Giả lập thành công, không làm gì cả
//    }
//
//    // Sửa: Hàm update của bạn nhận vào Object, không phải 4 strings
//    override suspend fun update(flashCard: FlashCard) {
//    }
//
//    // Sửa: Hàm delete của bạn nhận vào Object
//    override suspend fun delete(flashCard: FlashCard) {
//    }
//}
//
//class DummyFlashCardDaoUnsuccessfulInsert : FlashCardDao {
//    // Các hàm bắt buộc khác
//    override suspend fun getAll(): List<FlashCard> = emptyList()
//    override suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard> = emptyList()
//    override suspend fun findByCards(english: String, vietnamese: String): FlashCard = FlashCard(0, "", "")
//    override suspend fun getById(uid: Int): FlashCard = FlashCard(0, "", "")
//    override suspend fun update(flashCard: FlashCard) {}
//    override suspend fun delete(flashCard: FlashCard) {}
//
//    // Hàm quan trọng gây lỗi giả lập
//    override suspend fun insertAll(vararg flashCard: FlashCard) {
//        throw SQLiteConstraintException()
//    }
//}
//
//@RunWith(RobolectricTestRunner::class)
//class ScreenTest {
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    @Test
//    fun homeStartDestination() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        // Kiểm tra Type-Safe Route
//        assertEquals(true, navController.currentDestination?.hasRoute<HomeRoute>())
//    }
//
//    @Test
//    fun clickOnStudyCards() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        // ComposeTestRule cần chờ UI render xong
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToStudyCards")
//            .assertExists()
//            .assertTextEquals("Study Cards")
//            .performClick()
//
//        assertEquals(true, navController.currentDestination?.hasRoute<StudyCardsRoute>())
//    }
//
//    @Test
//    fun clickOnAddCard() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToAddCard")
//            .assertExists()
//            .assertTextEquals("Add Card")
//            .performClick()
//
//        assertEquals(true, navController.currentDestination?.hasRoute<AddCardRoute>())
//    }
//
//    @Test
//    fun clickOnSearchCards() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToSearchCards")
//            .assertExists()
//            .assertTextEquals("Search Cards")
//            .performClick()
//
//        assertEquals(true, navController.currentDestination?.hasRoute<SearchCardsRoute>())
//    }
//
//    @Test
//    fun homeScreenRetained_afterConfigChange() {
//        val stateRestorationTester = StateRestorationTester(composeTestRule)
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        stateRestorationTester.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//
//        stateRestorationTester.emulateSavedInstanceStateRestore()
//        assertEquals(true, navController.currentDestination?.hasRoute<HomeRoute>())
//    }
//
//    @Test
//    fun clickOnAddCardAndBack() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToAddCard").performClick()
//
//        // Kiểm tra nút Back
//        composeTestRule.onNodeWithContentDescription("navigateBack")
//            .assertExists()
//            .performClick()
//
//        assertEquals(true, navController.currentDestination?.hasRoute<HomeRoute>())
//    }
//
//    @Test
//    fun typeOnEnTextInput() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToAddCard").performClick()
//
//        val textInput = "house"
//        composeTestRule.onNodeWithContentDescription("enTextField")
//            .assertExists()
//            .performTextInput(textInput)
//
//        // Kiểm tra xem text field có giữ giá trị không
//        // Lưu ý: assertTextEquals kiểm tra value của Node
//        composeTestRule.onNodeWithContentDescription("enTextField")
//            .assertTextEquals("en", textInput)
//    }
//
//    @Test
//    fun keepEnglishStringAfterRotation() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        val stateRestorationTester = StateRestorationTester(composeTestRule)
//        stateRestorationTester.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//
//        composeTestRule.onNodeWithContentDescription("navigateToAddCard").performClick()
//
//        val textInput = "house"
//        composeTestRule.onNodeWithContentDescription("enTextField")
//            .performTextInput(textInput)
//
//
//        composeTestRule.onNodeWithContentDescription("enTextField")
//            .assertTextEquals("en", textInput)
//
//        stateRestorationTester.emulateSavedInstanceStateRestore()
//
//        composeTestRule.onNodeWithContentDescription("enTextField")
//            .assertTextEquals("en", textInput)
//    }
//
//    @Test
//    fun clickOnAddCardSuccessful() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDao()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToAddCard").performClick()
//
//        // Nhập liệu (để tránh lỗi nếu có validate)
//        composeTestRule.onNodeWithContentDescription("enTextField").performTextInput("Hello")
//        composeTestRule.onNodeWithContentDescription("vnTextField").performTextInput("Xin chao")
//
//        composeTestRule.onNodeWithContentDescription("Add")
//            .assertExists()
//            .performClick()
//
//        // SỬA: Chuỗi thông báo phải khớp với AddCardScreen.kt của bạn
//        // Cũ: "Flash card successfully added to your database."
//        // Mới (Của bạn): "The flash card has been added to your database."
//        composeTestRule.onNodeWithContentDescription("Message")
//            .assertExists()
//            .assertTextEquals("The flash card has been added to your database.")
//    }
//
//    @Test
//    fun clickOnAddCardUnSuccessful() {
//        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
//        navController.navigatorProvider.addNavigator(ComposeNavigator())
//        val dummyFlashCardDao = DummyFlashCardDaoUnsuccessfulInsert()
//
//        composeTestRule.setContent {
//            Navigator(navController = navController, flashCardDao = dummyFlashCardDao)
//        }
//        composeTestRule.waitForIdle()
//
//        composeTestRule.onNodeWithContentDescription("navigateToAddCard").performClick()
//
//        composeTestRule.onNodeWithContentDescription("Add").performClick()
//
//        composeTestRule.onNodeWithContentDescription("Message")
//            .assertExists()
//            .assertTextEquals("The flash card already exists in your database.")
//    }
//}