package com.saico.ada.database.di

import com.saico.ada.database.datasource.EventLocalDataSource
import com.saico.ada.database.datasource.EventLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {

    @Binds
    abstract fun bindEventLocalDataSource(impl: EventLocalDataSourceImpl): EventLocalDataSource
}
