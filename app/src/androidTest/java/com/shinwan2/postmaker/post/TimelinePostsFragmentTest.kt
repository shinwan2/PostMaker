package com.shinwan2.postmaker.post

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.MediumTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.shinwan2.postmaker.AUTHENTICATION_SERVICE_SIGNED_IN
import com.shinwan2.postmaker.DaggerCoreTestComponent
import com.shinwan2.postmaker.MyApplication
import com.shinwan2.postmaker.POST_SERVICE_ALL_OK
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.USER_SERVICE_ALL_OK
import com.shinwan2.postmaker.di.DaggerApplicationComponent
import com.shinwan2.postmaker.home.HomeActivity
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@MediumTest
class TimelinePostsFragmentTest {

    @get:Rule
    val intentsTestRule = ActivityTestRule(HomeActivity::class.java, false, false)

    private val app: MyApplication
        get() = InstrumentationRegistry.getTargetContext().applicationContext as MyApplication

    @Before
    fun setUp() {
        val coreComponent = DaggerCoreTestComponent.builder()
            .authenticationService(AUTHENTICATION_SERVICE_SIGNED_IN)
            .postService(POST_SERVICE_ALL_OK)
            .userService(USER_SERVICE_ALL_OK)
            .build()
        app.component = DaggerApplicationComponent.builder()
            .coreComponent(coreComponent)
            .build()
        app.component.inject(app)
    }

    @Test
    fun testLoadItemsSucceed() {
        intentsTestRule.launchActivity(null)

        Espresso.onView(ViewMatchers.withId(R.id.recyclerView))
            .check(ViewAssertions.matches(
                Matchers.allOf(
                    ViewMatchers.hasChildCount(2),
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(com.shinwan2.postmaker.MOCK_POST1.textContent)
                    ),
                    ViewMatchers.hasDescendant(
                        ViewMatchers.withText(com.shinwan2.postmaker.MOCK_POST2.textContent)
                    )
                )
            ))
    }
}