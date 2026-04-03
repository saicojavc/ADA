package com.saico.ada.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.saico.ada.database.dao.BienestarDao
import com.saico.ada.database.dao.EventDao
import com.saico.ada.database.dao.NotaDao
import com.saico.ada.database.dao.TareaDao
import com.saico.ada.database.entity.BienestarEntity
import com.saico.ada.database.entity.EventEntity
import com.saico.ada.database.entity.NotaEntity
import com.saico.ada.database.entity.TareaEntity

@Database(
    entities = [
        EventEntity::class,
        TareaEntity::class,
        BienestarEntity::class,
        NotaEntity::class
    ],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AdaDatabase : RoomDatabase() {
    abstract val eventDao: EventDao
    abstract val tareaDao: TareaDao
    abstract val bienestarDao: BienestarDao
    abstract val notaDao: NotaDao

    companion object {
        const val DATABASE_NAME = "ada_db"
    }
}
