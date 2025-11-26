package com.riakol.data.di

import android.content.Context
import androidx.room.Room
import com.riakol.data.local.SchoolDatabase
import com.riakol.data.local.dao.SchoolDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): SchoolDatabase {
        return Room.databaseBuilder(
                context,
                SchoolDatabase::class.java,
                "school_db"
            ).fallbackToDestructiveMigration(false)
            .build()
    }

    @Provides
    fun provideSchoolDao(db: SchoolDatabase): SchoolDao {
        return db.schoolDao()
    }
}