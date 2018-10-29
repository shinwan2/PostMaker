package com.shinwan2.postmaker.auth

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso
import android.support.test.espresso.action.ViewActions
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.shinwan2.postmaker.DaggerCoreTestComponent
import com.shinwan2.postmaker.MyApplication
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.di.DaggerApplicationComponent
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.home.HomeActivity
import io.reactivex.Completable
import io.reactivex.Observable
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val VALID_EMAIL = "abcdef@gmail.com"
private const val VALID_PASSWORD = "123456"

@RunWith(AndroidJUnit4::class)
@MediumTest
class SignUpActivityTest {

    @get:Rule
    val intentsTestRule = IntentsTestRule(SignUpActivity::class.java, false, false)

    private val app: MyApplication
        get() = InstrumentationRegistry.getTargetContext().applicationContext as MyApplication

    private lateinit var authenticationService: AuthenticationService

    @Before
    fun setUp() {
        authenticationService = mock()
        val coreComponent = DaggerCoreTestComponent.builder()
            .authenticationService(authenticationService)
            .postService(POST_SERVICE_ALL_OK)
            .userService(USER_SERVICE_ALL_OK)
            .build()
        app.component = DaggerApplicationComponent.builder()
            .coreComponent(coreComponent)
            .build()
        app.component.inject(app)

        // app should not fail
        whenever(authenticationService.authStateChanged())
            .thenReturn(Observable.never())
    }

    @Test
    fun testSignUpSucceeds() {
        whenever(authenticationService.signUp(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())

        intentsTestRule.launchActivity(null)

        Espresso.onView(ViewMatchers.withId(R.id.emailEditText))
            .perform(ViewActions.typeText(VALID_EMAIL))
        Espresso.onView(ViewMatchers.withId(R.id.passwordEditText))
            .perform(ViewActions.typeText(VALID_PASSWORD))
        Espresso.closeSoftKeyboard()
        Espresso.onView(ViewMatchers.withId(R.id.signUpButton))
            .perform(ViewActions.click())

        // we need to delay for debounce
        Thread.sleep(500)
        Intents.intended(IntentMatchers.hasComponent(HomeActivity::class.java.name))
    }
}
