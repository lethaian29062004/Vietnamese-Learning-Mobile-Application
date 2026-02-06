package com.example.vietnameselearning

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Index
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Insert
import androidx.room.Delete
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Update
import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SupportSQLiteQuery
import androidx.room.RawQuery




/*
@Entity:  Marks this class as a database table definition.
Names the table in the SQLite database as 'FlashCards'.

indices : Creates a lookup index for a card. This allows the database to find a card instantly,
without having to scan every single row in the table.
*/
@Entity(tableName = "FlashCards", indices = [Index(value = ["english_card","vietnamese_card"],
    unique = true)])

// data class - a Kotlin feature used to hold data_ automatically generates useful methods like equals(), toString().
data class FlashCard(
    // @PrimaryKey : Marks this field as the unique identifier for each row in the database
    // autoGenerate=true : Room automatically assign a unique ID (1,2,3...) for a newly inserted card,
    // set the default ID value to 0 to indicate a new card added.
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    // @ColumnInfo : Customize the column name in the SQLite database.
    // String? - this field can be empty (null).
    @ColumnInfo(name = "english_card") val englishCard: String?,
    @ColumnInfo(name = "vietnamese_card") val vietnameseCard: String?
)


/*
 DAO (Data Access Object) INTERFACE
 Acts as a bridge between the Kotlin code and the SQLite database.
 We only declare function (WHAT we want to do), without writing the code logic inside { },
    and Room automatically generates the implementation code.
 */

// suspend : runs database operations on a background thread (using Coroutines).
//           this prevents the Main UI from freezing/lagging while waiting for data.
@Dao
interface FlashCardDao {
    // Used in SearchCardScreen
    @Query("SELECT * FROM FlashCards")
    suspend fun getAll(): List<FlashCard>

    // Used in StudyCardScreen. Loads a specific list of cards based on their IDs.
    @Query("SELECT * FROM FlashCards WHERE uid IN (:flashCardIds)")
    suspend fun loadAllByIds(flashCardIds: IntArray): List<FlashCard>

    // Used in AddCardScreen
    // Check if a card already exists. 'LIMIT 1' - Stop searching when 1 match is found.
    @Query("SELECT * FROM FlashCards WHERE english_card LIKE :english AND " +
            "vietnamese_card LIKE :vietnamese LIMIT 1")
    suspend fun findByCards(english: String, vietnamese: String): FlashCard?

    // Used in ShowCardScreen. Loads a specific card based on its ID.
    @Query("SELECT * FROM FlashCards WHERE uid = :uid")
    suspend fun getById(uid: Int): FlashCard

    // Inserts new cards into the database.
    // 'vararg' - allows passing many cards at once.
    @Insert
    suspend fun insertAll(vararg flashCard: FlashCard)

    // Used in ShowCardScreen. Deletes a specific card.
    // Room uses the 'uid' of the passed object to find which row to delete.
    @Delete
    suspend fun delete(flashCard: FlashCard)

    // Used in EditCardScreen. Updates a specific card.
    // Room looks for a row with the same 'uid' and overwrites it with the new data.
    @Update
    suspend fun update(flashCard: FlashCard)

    // Used in StudyCardScreen. Loads a random list of cards (3) every time.
    @Query("SELECT * FROM FlashCards ORDER BY RANDOM() LIMIT :size")
    suspend fun getLesson(size: Int): List<FlashCard>


    // Retrieves a filtered list of flashcards based on English and Vietnamese search criteria.
    // This query uses SQL 'CASE' statements to dynamically switch search logic:
    // 1. If :exactEn is true (1) -> It returns the exact English word.
    // 2. If :exactEn is false (0) -> It wraps the input in % % (LIKE '%a%') to perform a partial search.
    // Same for the Vietnamese field.
    @Query(
        "SELECT * FROM FlashCards WHERE " +
                "(CASE WHEN :exactEn THEN english_card LIKE :en  " +
                "WHEN NOT :exactEn  THEN english_card LIKE '%' || :en || '%' END) " +
                "AND " +
                "(CASE WHEN :exactVn THEN vietnamese_card LIKE :vn " +
                "WHEN NOT :exactVn THEN vietnamese_card LIKE '%' || :vn || '%' END)"
    )
    suspend fun getFilteredFlashCards(en: String, exactEn: Int, vn: String, exactVn: Int): List<FlashCard>



    // Advanced - not used for now
    // Allows executing raw SQL commands directly (bypassing Room's checks).
    // Used for system tasks like forcing the database to save immediately
    @RawQuery
    suspend fun checkpoint(supportSQLiteQuery: SupportSQLiteQuery): Int?
}


/*
DATABASE CONFIGURATION
* Abstract Class: We don't need to write code to connect, read, or write files.
  Room will automatically "fill in the blanks" and generate that code when we compile the app.
* entities: Lists all the tables (Entities) in this database.
* version: Used for database migrations. If change the table structure later - must increase this number.
*/
@Database(entities = [FlashCard::class], version = 1)
abstract class FlashCardDatabase : RoomDatabase() {
    abstract fun flashCardDao(): FlashCardDao

    /*
     SINGLETON PATTERN
     Opening a database takes a lot of RAM & CPU. If we create a new connection every time,
     the app will run out of memory and crash.
     -> Solution: Ensure Only 1 database object exists for the entire app lifecycle.
    */
    // companion object : Allows calling getDatabase directly via the class name .
    companion object {
        // @Volatile: Ensures that changes to INSTANCE are immediately visible to all threads.
        // Prevents caching issues where one thread might see an outdated null value.
        @Volatile
        // Holds the Singleton instance of the database to be reused - null before the 1st creation.
        private var INSTANCE: FlashCardDatabase? = null

        // Get Database Instance. This method guarantees that only one instance of the database is ever created.
        fun getDatabase(context: Context): FlashCardDatabase {
            // Check if the database instance already exists.
            return INSTANCE ?: synchronized(this) {
                // If instance is null, enter a synchronized block.
                // synchronized: Prevents multiple threads from executing this block at the same time.
                // This ensures the database is created exactly once.
                val instance = Room.databaseBuilder(   // Create a new instance of the database.
                    context.applicationContext, // Use applicationContext to prevent memory leaks
                    FlashCardDatabase::class.java,
                    "FlashCardDatabase"
                ).build()
                // Save the created instance to a variable for reusing
                INSTANCE = instance
                // Return the newly created instance
                instance
            }
        }
    }
}