package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.remote.api.TasksDetailApiService
import com.smartfingers.smartlawyerplus.data.repository.AppointmentsAddRepositoryImpl
import com.smartfingers.smartlawyerplus.data.repository.SessionsAddRepositoryImpl
import com.smartfingers.smartlawyerplus.data.repository.TasksRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.AppointmentsAddRepository
import com.smartfingers.smartlawyerplus.domain.repository.SessionsAddRepository
import com.smartfingers.smartlawyerplus.domain.repository.TasksRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class TasksDetailModule {

    @Binds @Singleton
    abstract fun bindTasksRepository(impl: TasksRepositoryImpl): TasksRepository

    @Binds @Singleton
    abstract fun bindSessionsAddRepository(impl: SessionsAddRepositoryImpl): SessionsAddRepository

    @Binds @Singleton
    abstract fun bindAppointmentsAddRepository(impl: AppointmentsAddRepositoryImpl): AppointmentsAddRepository

    companion object {
        @Provides @Singleton
        fun provideTasksDetailApiService(retrofit: Retrofit): TasksDetailApiService =
            retrofit.create(TasksDetailApiService::class.java)
    }
}