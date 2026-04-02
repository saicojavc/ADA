package com.saico.ada.database.dao

import androidx.room.*
import com.saico.ada.database.entity.NotaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotaDao {
    @Query("SELECT * FROM notas_rapidas ORDER BY fechaCreacion DESC")
    fun getAllNotas(): Flow<List<NotaEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNota(nota: NotaEntity)

    @Delete
    suspend fun deleteNota(nota: NotaEntity)
}
