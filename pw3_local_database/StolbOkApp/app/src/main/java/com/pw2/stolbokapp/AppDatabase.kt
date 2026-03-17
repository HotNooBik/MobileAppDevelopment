package com.pw2.stolbokapp

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import kotlinx.coroutines.flow.Flow

// --- Entities ---

@Entity(tableName = "peaks")
data class PeakEntity(
    @PrimaryKey(autoGenerate = true) val peakId: Int = 0,
    val name: String,
    val description: String,
    val difficulty: Difficulty,
    val height: String,
    val climbTime: String,
    val distanceFromPereval: String,
    val mapDistanceLabel: String,
    val lat: Double,
    val lng: Double,
    val imageRes1: Int = 0,
    val imageRes2: Int = 0,
    val imageRes3: Int = 0,
    val isVisited: Boolean = false
)

@Entity(tableName = "hikes")
data class HikeEntity(
    @PrimaryKey(autoGenerate = true) val hikeId: Long = 0,
    val date: Long,
    val comment: String
)

@Entity(tableName = "hike_photos",
    foreignKeys = [ForeignKey(
        entity = HikeEntity::class,
        parentColumns = ["hikeId"],
        childColumns = ["hikeId"],
        onDelete = CASCADE
    )]
)
data class HikePhotoEntity(
    @PrimaryKey(autoGenerate = true) val photoId: Long = 0,
    val hikeId: Long,
    val uri: String
)

@Entity(tableName = "plans", primaryKeys = ["dayNumber", "month", "year"])
data class PlanEntity(
    val dayNumber: Int,
    val month: Int, // 1..12
    val year: Int
)

@Entity(primaryKeys = ["hikeId", "peakId"],
    foreignKeys = [
        ForeignKey(entity = HikeEntity::class, parentColumns = ["hikeId"], childColumns = ["hikeId"], onDelete = CASCADE),
        ForeignKey(entity = PeakEntity::class, parentColumns = ["peakId"], childColumns = ["peakId"], onDelete = CASCADE)
    ]
)
data class HikePeakCrossRef(
    val hikeId: Long,
    val peakId: Int
)

// --- Relation POJO ---
data class HikeWithDetails(
    @androidx.room.Embedded val hike: HikeEntity,
    @androidx.room.Relation(
        parentColumn = "hikeId",
        entityColumn = "peakId",
        associateBy = androidx.room.Junction(HikePeakCrossRef::class)
    )
    val peaks: List<PeakEntity>,
    @androidx.room.Relation(
        parentColumn = "hikeId",
        entityColumn = "hikeId"
    )
    val photos: List<HikePhotoEntity>
)

// --- Converters ---

class Converters {
    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String {
        return difficulty.name
    }

    @TypeConverter
    fun toDifficulty(value: String): Difficulty {
        return try {
            Difficulty.valueOf(value)
        } catch (e: Exception) {
            Difficulty.EASY
        }
    }
}

// --- DAOs ---

@Dao
interface PeakDao {
    @Query("SELECT * FROM peaks")
    fun getAllPeaks(): Flow<List<PeakEntity>>

    @Query("SELECT * FROM peaks")
    suspend fun getAllPeaksList(): List<PeakEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(peaks: List<PeakEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(peak: PeakEntity)

    @Query("SELECT COUNT(*) FROM peaks")
    suspend fun getCount(): Int

    @Query("DELETE FROM peaks")
    suspend fun deleteAll()
}

@Dao
interface PlanDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addPlan(plan: PlanEntity)

    @Delete
    suspend fun removePlan(plan: PlanEntity)

    @Query("SELECT * FROM plans ORDER BY year, month, dayNumber")
    fun getAllPlans(): Flow<List<PlanEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM plans WHERE dayNumber = :day AND month = :month AND year = :year)")
    suspend fun isBookmarked(day: Int, month: Int, year: Int): Boolean
}

@Dao
interface HikeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHike(hike: HikeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPhotos(photos: List<HikePhotoEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPeakRefs(refs: List<HikePeakCrossRef>)

    @Transaction
    @Query("SELECT * FROM hikes ORDER BY date DESC")
    fun getAllHikes(): Flow<List<HikeWithDetails>>

    @Transaction
    @Query("SELECT * FROM hikes WHERE hikeId = :id")
    suspend fun getHikeById(id: Long): HikeWithDetails?

    @Query("SELECT COUNT(*) FROM hikes")
    suspend fun getHikesCount(): Int

    @Query("DELETE FROM hikes")
    suspend fun deleteAll()

    @Transaction
    suspend fun addHike(hike: HikeEntity, peakIds: List<Int>, photoUris: List<String>) {
        val hikeId = insertHike(hike)

        val refs = peakIds.map { HikePeakCrossRef(hikeId, it) }
        insertPeakRefs(refs)

        val photos = photoUris.map { HikePhotoEntity(hikeId = hikeId, uri = it) }
        insertPhotos(photos)
    }
}

// --- Database ---

@Database(
    entities = [PeakEntity::class, HikeEntity::class, HikePhotoEntity::class, HikePeakCrossRef::class, PlanEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun peakDao(): PeakDao
    abstract fun hikeDao(): HikeDao
    abstract fun planDao(): PlanDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stolbok_database"
                )
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
