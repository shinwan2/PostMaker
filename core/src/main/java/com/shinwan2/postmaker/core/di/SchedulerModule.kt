package com.shinwan2.postmaker.core.di

import com.shinwan2.postmaker.core.SchedulerManagerImpl
import com.shinwan2.postmaker.domain.SchedulerManager
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class SchedulerModule {
    @Provides
    @Reusable
    fun provideSchedulerManager(): SchedulerManager = SchedulerManagerImpl()
}