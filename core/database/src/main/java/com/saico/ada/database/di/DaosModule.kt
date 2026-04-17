package com.saico.ada.database.di

import com.saico.ada.database.AdaDatabase
import com.saico.ada.database.dao.BienestarDao
import com.saico.ada.database.dao.CategoriaDao
import com.saico.ada.database.dao.EventDao
import com.saico.ada.database.dao.NotaDao
import com.saico.ada.database.dao.TareaDao
import com.saico.ada.database.dao.TareaExcepcionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun provideEventDao(database: AdaDatabase): EventDao = database.eventDao

    @Provides
    fun provideTareaDao(database: AdaDatabase): TareaDao = database.tareaDao

    @Provides
    fun provideBienestarDao(database: AdaDatabase): BienestarDao = database.bienestarDao

    @Provides
    fun provideNotaDao(database: AdaDatabase): NotaDao = database.notaDao

    @Provides
    fun provideTareaExcepcionDao(database: AdaDatabase): TareaExcepcionDao = database.tareaExcepcionDao

    @Provides
    fun provideCategoriaDao(database: AdaDatabase): CategoriaDao = database.categoriaDao
}
