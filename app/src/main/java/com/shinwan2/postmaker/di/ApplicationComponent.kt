package com.shinwan2.postmaker.di

import com.shinwan2.postmaker.MyApplication
import com.shinwan2.postmaker.annotation.ApplicationScope
import com.shinwan2.postmaker.core.di.CoreComponent
import dagger.Component

@Component(
    dependencies = [CoreComponent::class],
    modules = [ActivityModule::class]
)
@ApplicationScope
interface ApplicationComponent {
    fun inject(application: MyApplication)
}