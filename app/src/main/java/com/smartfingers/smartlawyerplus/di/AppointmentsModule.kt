package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.remote.api.AppointmentsApiService
import com.smartfingers.smartlawyerplus.data.repository.AppointmentsRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.AppointmentsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AppointmentsModule {

    @Binds
    @Singleton
    abstract fun bindAppointmentsRepository(impl: AppointmentsRepositoryImpl): AppointmentsRepository

    companion object {
        @Provides
        @Singleton
        fun provideAppointmentsApiService(retrofit: Retrofit): AppointmentsApiService =
            retrofit.create(AppointmentsApiService::class.java)
    }
}