package com.saico.ada.database.di

import com.saico.ada.database.repository.EventRepositoryImpl
import com.saico.ada.domain.repository.EventRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository
}
