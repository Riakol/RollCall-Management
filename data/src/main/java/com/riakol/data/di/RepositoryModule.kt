package com.riakol.data.di

import com.riakol.data.repository.SchoolRepositoryImpl
import com.riakol.domain.repository.SchoolRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindSchoolRepository(
        impl: SchoolRepositoryImpl
    ): SchoolRepository
}
