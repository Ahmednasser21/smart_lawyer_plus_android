package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.remote.api.CasesApiService
import com.smartfingers.smartlawyerplus.data.repository.CasesRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.CasesRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CasesModule {

    @Binds
    @Singleton
    abstract fun bindCasesRepository(impl: CasesRepositoryImpl): CasesRepository

    companion object {
        @Provides
        @Singleton
        fun provideCasesApiService(retrofit: Retrofit): CasesApiService =
            retrofit.create(CasesApiService::class.java)
    }
}