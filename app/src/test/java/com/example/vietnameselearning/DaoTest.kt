//package com.example.vietnameselearning
//
//import android.content.Context
//import android.database.sqlite.SQLiteConstraintException
//import androidx.room.Room
//import androidx.test.core.app.ApplicationProvider
//import kotlinx.coroutines.runBlocking
//import org.junit.After
//import org.junit.Assert.assertEquals
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.robolectric.RobolectricTestRunner
//
//@RunWith(RobolectricTestRunner::class)
//class DaoTest {
//    // SỬA: Sử dụng AppDatabase thay vì FlashCardDatabase
//    private lateinit var db: AppDatabase
//    private lateinit var flashCardDao: FlashCardDao
//
//    @Before
//    fun setup() {
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        // SỬA: AppDatabase
//        db = Room.inMemoryDatabaseBuilder(
//            context, AppDatabase::class.java).allowMainThreadQueries().build()
//        flashCardDao = db.flashCardDao()
//    }
//
//    @After
//    fun close(){
//        db.close()
//    }
//
//    @Test
//    fun insertFlashCardSuccessful() {
//        val flashCard = FlashCard(
//            uid = 0, // 0 để Room tự sinh ID
//            englishCard = "test_english",
//            vietnameseCard = "test_vietnamese"
//        )
//
//        runBlocking {
//            flashCardDao.insertAll(flashCard)
//        }
//
//        val item: FlashCard?
//        runBlocking {
//            item = flashCardDao.findByCards("test_english", "test_vietnamese")
//        }
//
//        // Kiểm tra kết quả
//        assertEquals(flashCard.englishCard, item?.englishCard)
//        assertEquals(flashCard.vietnameseCard, item?.vietnameseCard)
//    }
//
//    @Test
//    fun insertFlashCardUnSuccessful() {
//        // Thiết lập lại DB sạch cho test này
//        val context = ApplicationProvider.getApplicationContext<Context>()
//        db = Room.inMemoryDatabaseBuilder(
//            context, AppDatabase::class.java).allowMainThreadQueries().build()
//        flashCardDao = db.flashCardDao()
//
//        val flashCard = FlashCard(
//            uid = 1, // Fix cứng UID để gây trùng lặp nếu insert lại (hoặc dựa vào unique constraint)
//            englishCard = "test_english",
//            vietnameseCard = "test_vietnamese"
//        )
//
//        runBlocking {
//            flashCardDao.insertAll(flashCard)
//        }
//
//        var error = false
//        runBlocking {
//            try {
//                // Insert thẻ y hệt lần nữa -> Vi phạm Unique Constraint
//                val duplicateCard = FlashCard(
//                    uid = 0,
//                    englishCard = "test_english",
//                    vietnameseCard = "test_vietnamese"
//                )
//                flashCardDao.insertAll(duplicateCard)
//            } catch (e: SQLiteConstraintException){
//                error = true
//            }
//        }
//        assertEquals(true, error)
//    }
//
//    /* Delete */
//    @Test
//    fun deleteExistingFlashCard() {
//        val flashCard = FlashCard(
//            uid = 0,
//            englishCard = "test_english",
//            vietnameseCard = "test_vietnamese"
//        )
//
//        var flashCardsBefore: List<FlashCard>
//        runBlocking {
//            // Lấy danh sách ban đầu (có thể rỗng)
//            flashCardsBefore = flashCardDao.getAll()
//        }
//
//        runBlocking{
//            // 1. Insert
//            flashCardDao.insertAll(flashCard)
//
//            // 2. Tìm thẻ vừa insert để lấy đúng UID (hoặc object đầy đủ)
//            val cardToDelete = flashCardDao.findByCards("test_english", "test_vietnamese")
//
//            // 3. SỬA: Gọi hàm delete(FlashCard) thay vì deleteFlashCard(String, String)
//            if (cardToDelete != null) {
//                flashCardDao.delete(cardToDelete)
//            }
//        }
//
//        var flashCardsAfter: List<FlashCard>
//        runBlocking {
//            flashCardsAfter = flashCardDao.getAll()
//        }
//
//        // Danh sách sau khi thêm rồi xóa phải bằng danh sách ban đầu
//        assertEquals(flashCardsBefore.size, flashCardsAfter.size)
//    }
//
//    @Test
//    fun deleteNonExistingFlashCard() {
//        val flashCard = FlashCard(
//            uid = 0,
//            englishCard = "test_english",
//            vietnameseCard = "test_vietnamese"
//        )
//
//        runBlocking {
//            flashCardDao.insertAll(flashCard)
//        }
//
//        var flashCardsBefore: List<FlashCard>
//        runBlocking {
//            flashCardsBefore = flashCardDao.getAll()
//        }
//
//        runBlocking {
//            // Cố gắng tìm và xóa một thẻ không tồn tại
//            val nonExistingCard = flashCardDao.findByCards("test_english_1", "test_vietnamese_1")
//
//            // Logic SỬA: Nếu tìm thấy mới xóa (ở đây sẽ là null nên không xóa)
//            // Hoặc nếu code của bạn gọi delete(null) sẽ lỗi, nên cần check null
//            if (nonExistingCard != null) {
//                flashCardDao.delete(nonExistingCard)
//            }
//        }
//
//        var flashCardsAfter: List<FlashCard>
//        runBlocking {
//            flashCardsAfter = flashCardDao.getAll()
//        }
//
//        // Số lượng thẻ không được thay đổi
//        assertEquals(flashCardsBefore.size, flashCardsAfter.size)
//    }
//}