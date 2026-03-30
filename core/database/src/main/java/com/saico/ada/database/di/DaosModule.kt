package com.saico.ada.database.di

import com.saico.ada.database.AdaDatabase
import com.saico.ada.database.dao.EventDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DaosModule {

    @Provides
    fun provideEventDao(database: AdaDatabase): EventDao = database.eventDao
}
