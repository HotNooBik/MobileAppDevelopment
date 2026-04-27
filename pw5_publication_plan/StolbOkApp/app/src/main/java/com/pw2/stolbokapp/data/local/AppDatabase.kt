package com.pw2.stolbokapp.data.local

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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pw2.stolbokapp.data.calendar.Attendance
import com.pw2.stolbokapp.data.calendar.DayStatus
import com.pw2.stolbokapp.data.calendar.Season
import com.pw2.stolbokapp.data.calendar.StolbyStatus
import com.pw2.stolbokapp.data.calendar.TickActivity
import com.pw2.stolbokapp.data.calendar.Weather
import com.pw2.stolbokapp.data.peaks.Difficulty
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

@Entity(tableName = "calendar_days")
data class CalendarDayEntity(
    @PrimaryKey val date: String,
    val dayOfWeek: String,
    val month: String,
    val monthIndex: Int,
    val dayNumber: Int,
    val year: Int,
    val status: DayStatus,
    val temperature: Int,
    val weather: Weather,
    val tickActivity: TickActivity,
    val attendance: Attendance,
    val stolbyStatus: StolbyStatus,
    val season: Season
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
            e.printStackTrace()
            Difficulty.EASY
        }
    }

    @TypeConverter
    fun fromDayStatus(value: DayStatus): String = value.name

    @TypeConverter
    fun toDayStatus(value: String): DayStatus = DayStatus.valueOf(value)

    @TypeConverter
    fun fromWeather(value: Weather): String = value.name

    @TypeConverter
    fun toWeather(value: String): Weather = Weather.valueOf(value)

    @TypeConverter
    fun fromTickActivity(value: TickActivity): String = value.name

    @TypeConverter
    fun toTickActivity(value: String): TickActivity = TickActivity.valueOf(value)

    @TypeConverter
    fun fromAttendance(value: Attendance): String = value.name

    @TypeConverter
    fun toAttendance(value: String): Attendance = Attendance.valueOf(value)

    @TypeConverter
    fun fromStolbyStatus(value: StolbyStatus): String = value.name

    @TypeConverter
    fun toStolbyStatus(value: String): StolbyStatus = StolbyStatus.valueOf(value)

    @TypeConverter
    fun fromSeason(value: Season): String = value.name

    @TypeConverter
    fun toSeason(value: String): Season = Season.valueOf(value)
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

    @Query("DELETE FROM hikes WHERE hikeId = :hikeId")
    suspend fun deleteHikeById(hikeId: Long)

    @Query("DELETE FROM hike_photos WHERE hikeId = :hikeId")
    suspend fun deletePhotosByHikeId(hikeId: Long)

    @Query("DELETE FROM HikePeakCrossRef WHERE hikeId = :hikeId")
    suspend fun deletePeakRefsByHikeId(hikeId: Long)

    @Transaction
    suspend fun addHike(hike: HikeEntity, peakIds: List<Int>, photoUris: List<String>) {
        val hikeId = insertHike(hike)

        val refs = peakIds.map { HikePeakCrossRef(hikeId, it) }
        insertPeakRefs(refs)

        val photos = photoUris.map { HikePhotoEntity(hikeId = hikeId, uri = it) }
        insertPhotos(photos)
    }

    @Transaction
    suspend fun updateHike(hike: HikeEntity, peakIds: List<Int>, photoUris: List<String>) {
        insertHike(hike)
        deletePeakRefsByHikeId(hike.hikeId)
        deletePhotosByHikeId(hike.hikeId)

        val refs = peakIds.map { HikePeakCrossRef(hike.hikeId, it) }
        insertPeakRefs(refs)

        val photos = photoUris.map { HikePhotoEntity(hikeId = hike.hikeId, uri = it) }
        insertPhotos(photos)
    }
}

@Dao
interface CalendarDayDao {
    @Query("SELECT * FROM calendar_days ORDER BY date ASC")
    fun getAllCalendarDays(): Flow<List<CalendarDayEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(days: List<CalendarDayEntity>)

    @Query("DELETE FROM calendar_days")
    suspend fun deleteAll()

    @Transaction
    suspend fun replaceAll(days: List<CalendarDayEntity>) {
        deleteAll()
        insertAll(days)
    }
}

// --- Database ---

@Database(
    entities = [PeakEntity::class, HikeEntity::class, HikePhotoEntity::class, HikePeakCrossRef::class, PlanEntity::class, CalendarDayEntity::class],
    version = 5,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun peakDao(): PeakDao
    abstract fun hikeDao(): HikeDao
    abstract fun planDao(): PlanDao
    abstract fun calendarDayDao(): CalendarDayDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `calendar_days` (
                        `date` TEXT NOT NULL,
                        `dayOfWeek` TEXT NOT NULL,
                        `month` TEXT NOT NULL,
                        `monthIndex` INTEGER NOT NULL,
                        `dayNumber` INTEGER NOT NULL,
                        `year` INTEGER NOT NULL,
                        `status` TEXT NOT NULL,
                        `temperature` INTEGER NOT NULL,
                        `weather` TEXT NOT NULL,
                        `tickActivity` TEXT NOT NULL,
                        `attendance` TEXT NOT NULL,
                        `stolbyStatus` TEXT NOT NULL,
                        `season` TEXT NOT NULL,
                        PRIMARY KEY(`date`)
                    )
                    """.trimIndent()
                )
            }
        }

        // Legacy migration kept to avoid downgrade issues on devices that already opened version 4.
        private val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `profile_summary_cache` (
                        `cacheId` INTEGER NOT NULL,
                        `profileSinceText` TEXT NOT NULL,
                        `hikesCount` INTEGER NOT NULL,
                        `visitedPeaksCount` INTEGER NOT NULL,
                        `totalPeaksCount` INTEGER NOT NULL,
                        PRIMARY KEY(`cacheId`)
                    )
                    """.trimIndent()
                )
            }
        }

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("DROP TABLE IF EXISTS `profile_summary_cache`")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stolbok_database"
                )
                .addMigrations(MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .fallbackToDestructiveMigration(true)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
