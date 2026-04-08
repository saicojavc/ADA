package com.saico.ada.database.dao

import androidx.room.*
import com.saico.ada.database.entity.TareaExcepcionEntity
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

@Dao
interface TareaExcepcionDao {
    @Query("SELECT * FROM tarea_excepciones WHERE plantillaId = :plantillaId")
    fun getExcepcionesByPlantilla(plantillaId: Int): Flow<List<TareaExcepcionEntity>>

    @Query("SELECT * FROM tarea_excepciones")
    fun getAllExcepciones(): Flow<List<TareaExcepcionEntity>>

    @Query("SELECT * FROM tarea_excepciones WHERE fecha = :fecha")
    suspend fun getExcepcionesByFecha(fecha: LocalDate): List<TareaExcepcionEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertExcepcion(excepcion: TareaExcepcionEntity)

    @Query("DELETE FROM tarea_excepciones WHERE plantillaId = :plantillaId AND fecha >= :desde")
    suspend fun deleteExcepcionesFuturas(plantillaId: Int, desde: LocalDate)
}
