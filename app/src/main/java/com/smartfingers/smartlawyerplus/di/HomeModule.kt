package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.repository.HomeRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.HomeRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class HomeModule {

    @Binds
    @Singleton
    abstract fun bindHomeRepository(impl: HomeRepositoryImpl): HomeRepository
}