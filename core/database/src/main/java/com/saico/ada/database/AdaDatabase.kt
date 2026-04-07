package com.saico.ada.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.saico.ada.database.dao.BienestarDao
import com.saico.ada.database.dao.EventDao
import com.saico.ada.database.dao.NotaDao
import com.saico.ada.database.dao.TareaDao
import com.saico.ada.database.dao.TareaExcepcionDao
import com.saico.ada.database.entity.BienestarEntity
import com.saico.ada.database.entity.EventEntity
import com.saico.ada.database.entity.NotaEntity
import com.saico.ada.database.entity.TareaEntity
import com.saico.ada.database.entity.TareaExcepcionEntity

@Database(
    entities = [
        EventEntity::class,
        TareaEntity::class,
        BienestarEntity::class,
        NotaEntity::class,
        TareaExcepcionEntity::class
    ],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AdaDatabase : RoomDatabase() {
    abstract val eventDao: EventDao
    abstract val tareaDao: TareaDao
    abstract val bienestarDao: BienestarDao
    abstract val notaDao: NotaDao
    abstract val tareaExcepcionDao: TareaExcepcionDao

    companion object {
        const val DATABASE_NAME = "ada_db"

        val MIGRATION_3_4 = object : Migration(3, 4) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // 1. Crear la nueva tabla de excepciones
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS `tarea_excepciones` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, 
                        `plantillaId` INTEGER NOT NULL, 
                        `fecha` INTEGER NOT NULL, 
                        `estaCompletada` INTEGER NOT NULL, 
                        `estaSaltada` INTEGER NOT NULL, 
                        FOREIGN KEY(`plantillaId`) REFERENCES `tareas`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE 
                    )
                """.trimIndent())
                
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_tarea_excepciones_plantillaId` ON `tarea_excepciones` (`plantillaId`)")

                // 2. Modificar la tabla 'tareas' para agregar los nuevos campos de repetición
                // Room no soporta ADD COLUMN con valores por defecto complejos o múltiples en una sola migración fácilmente en algunas versiones de SQLite,
                // pero para estos campos simples funciona:
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `esPlantilla` INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `tipoRepeticion` TEXT NOT NULL DEFAULT 'NINGUNA'")
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `diasRepeticion` TEXT")
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `horaInicio` TEXT")
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `duracionMinutos` INTEGER NOT NULL DEFAULT 60")
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `fechaInicioRepeticion` INTEGER")
                db.execSQL("ALTER TABLE `tareas` ADD COLUMN `fechaFinRepeticion` INTEGER")
            }
        }
    }
}
