package com.saico.ada.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saico.ada.data.local.dao.EventDao
import com.saico.ada.data.local.entity.EventEntity

@Database(entities = [EventEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AdaDatabase : RoomDatabase() {
    abstract val eventDao: EventDao

    companion object {
        const val DATABASE_NAME = "ada_db"
    }
}
