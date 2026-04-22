package com.smartfingers.smartlawyerplus.di

import com.smartfingers.smartlawyerplus.data.remote.api.CalendarApiService
import com.smartfingers.smartlawyerplus.data.remote.api.NotificationsApiService
import com.smartfingers.smartlawyerplus.data.repository.CalendarRepositoryImpl
import com.smartfingers.smartlawyerplus.data.repository.NotificationsRepositoryImpl
import com.smartfingers.smartlawyerplus.domain.repository.CalendarRepository
import com.smartfingers.smartlawyerplus.domain.repository.NotificationsRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CalendarModule {

    @Binds
    @Singleton
    abstract fun bindCalendarRepository(impl: CalendarRepositoryImpl): CalendarRepository

    companion object {
        @Provides
        @Singleton
        fun provideCalendarApiService(retrofit: Retrofit): CalendarApiService =
            retrofit.create(CalendarApiService::class.java)
    }
}

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationsModule {

    @Binds
    @Singleton
    abstract fun bindNotificationsRepository(impl: NotificationsRepositoryImpl): NotificationsRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationsApiService(retrofit: Retrofit): NotificationsApiService =
            retrofit.create(NotificationsApiService::class.java)
    }
}
