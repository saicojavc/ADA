package com.saico.ada.database.di

import android.content.Context
import androidx.room.Room
import com.saico.ada.database.AdaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAdaDatabase(@ApplicationContext context: Context): AdaDatabase {
        return Room.databaseBuilder(
            context,
            AdaDatabase::class.java,
            AdaDatabase.Companion.DATABASE_NAME
        )
        .fallbackToDestructiveMigration()
        .build()
    }
}
