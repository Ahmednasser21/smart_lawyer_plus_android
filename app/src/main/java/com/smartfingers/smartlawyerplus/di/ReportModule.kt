package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.remote.api.ReportApiService
import com.smartfingers.smartlawyerplus.data.repository.ReportRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.ReportRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class ReportModule {

    @Binds
    @Singleton
    abstract fun bindReportRepository(impl: ReportRepositoryImpl): ReportRepository

    companion object {
        @Provides
        @Singleton
        fun provideReportApiService(retrofit: Retrofit): ReportApiService =
            retrofit.create(ReportApiService::class.java)
    }
}