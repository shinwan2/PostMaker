package com.shinwan2.postmaker.home

import com.shinwan2.postmaker.annotation.FragmentScope
import com.shinwan2.postmaker.post.TimelinePostsFragment
import com.shinwan2.postmaker.post.TimelinePostsFragmentModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class HomeActivityModule {
    @FragmentScope
    @ContributesAndroidInjector
    abstract fun contributeDrawerFragmentInjector(): DrawerFragment

    @FragmentScope
    @ContributesAndroidInjector(modules = [TimelinePostsFragmentModule::class])
    abstract fun contributeTimelinePostsFragmentInjector(): TimelinePostsFragment
}
