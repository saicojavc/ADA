package com.saico.ada.database.dao

import androidx.room.*
import com.saico.ada.database.entity.BienestarEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BienestarDao {
    @Query("SELECT * FROM bienestar_registros ORDER BY fecha DESC")
    fun getAllRegistros(): Flow<List<BienestarEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistro(registro: BienestarEntity)

    @Delete
    suspend fun deleteRegistro(registro: BienestarEntity)
}
