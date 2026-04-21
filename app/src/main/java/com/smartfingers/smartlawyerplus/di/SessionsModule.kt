package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.repository.SessionsRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.SessionsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SessionsModule {

    @Binds
    @Singleton
    abstract fun bindSessionsRepository(impl: SessionsRepositoryImpl): SessionsRepository
}