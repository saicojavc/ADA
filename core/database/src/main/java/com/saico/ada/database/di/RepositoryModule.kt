package com.saico.ada.database.di

import com.saico.ada.database.repository.BienestarRepositoryImpl
import com.saico.ada.database.repository.EventRepositoryImpl
import com.saico.ada.database.repository.NotaRepositoryImpl
import com.saico.ada.database.repository.TareaRepositoryImpl
import com.saico.ada.database.repository.TareaExcepcionRepositoryImpl
import com.saico.ada.domain.repository.BienestarRepository
import com.saico.ada.domain.repository.EventRepository
import com.saico.ada.domain.repository.NotaRepository
import com.saico.ada.domain.repository.TareaRepository
import com.saico.ada.domain.repository.TareaExcepcionRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindEventRepository(impl: EventRepositoryImpl): EventRepository

    @Binds
    abstract fun bindTareaRepository(impl: TareaRepositoryImpl): TareaRepository

    @Binds
    abstract fun bindBienestarRepository(impl: BienestarRepositoryImpl): BienestarRepository

    @Binds
    abstract fun bindNotaRepository(impl: NotaRepositoryImpl): NotaRepository

    @Binds
    abstract fun bindTareaExcepcionRepository(impl: TareaExcepcionRepositoryImpl): TareaExcepcionRepository
}
