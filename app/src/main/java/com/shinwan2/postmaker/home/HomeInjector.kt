package com.shinwan2.postmaker.home

import com.shinwan2.postmaker.annotation.FragmentScope
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class HomeActivityModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeDrawerFragmentInjector(): DrawerFragment
}
