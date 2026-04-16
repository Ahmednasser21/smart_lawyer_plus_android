package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.repository.AuthRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.AuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository
}