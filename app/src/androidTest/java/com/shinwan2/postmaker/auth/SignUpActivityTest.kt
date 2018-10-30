package com.shinwan2.postmaker.auth

import android.support.test.InstrumentationRegistry
import android.support.test.espresso.Espresso.closeSoftKeyboard
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.clearText
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.pressImeActionButton
import android.support.test.espresso.action.ViewActions.typeText
import android.support.test.espresso.assertion.ViewAssertions
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.intent.Intents
import android.support.test.espresso.intent.matcher.IntentMatchers
import android.support.test.espresso.intent.rule.IntentsTestRule
import android.support.test.espresso.matcher.RootMatchers.withDecorView
import android.support.test.espresso.matcher.ViewMatchers
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.isEnabled
import android.support.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.filters.MediumTest
import android.support.test.runner.AndroidJUnit4
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.whenever
import com.shinwan2.postmaker.DaggerCoreTestComponent
import com.shinwan2.postmaker.MyApplication
import com.shinwan2.postmaker.POST_SERVICE_ALL_OK
import com.shinwan2.postmaker.R
import com.shinwan2.postmaker.USER_SERVICE_ALL_OK
import com.shinwan2.postmaker.di.DaggerApplicationComponent
import com.shinwan2.postmaker.domain.auth.AuthenticationService
import com.shinwan2.postmaker.home.HomeActivity
import io.reactivex.Completable
import io.reactivex.Observable
import org.hamcrest.CoreMatchers.not
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

private const val VALID_EMAIL = "abcdef@gmail.com"
private const val VALID_PASSWORD = "123456"
private const val ERROR = "ERROR"

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
    fun testInitialState() {
        intentsTestRule.launchActivity(null)

        verifyButtonDisabled()
        verifyErrorEmailRequiredGone()
        verifyErrorPasswordRequiredGone()
    }

    @Test
    fun testFillEmailOnly() {
        intentsTestRule.launchActivity(null)

        onView(withId(R.id.emailEditText)).perform(typeText(VALID_EMAIL))

        verifyErrorEmailRequiredGone()
        verifyErrorPasswordRequiredGone()
        verifyButtonDisabled()
    }

    @Test
    fun testFillPasswordOnly() {
        intentsTestRule.launchActivity(null)

        onView(withId(R.id.passwordEditText)).perform(typeText(VALID_PASSWORD))

        verifyErrorEmailRequiredGone()
        verifyErrorPasswordRequiredGone()
        verifyButtonDisabled()
    }

    @Test
    fun testFillEmailAndPassword() {
        intentsTestRule.launchActivity(null)

        onView(withId(R.id.emailEditText))
            .perform(typeText(VALID_EMAIL))
        onView(withId(R.id.passwordEditText))
            .perform(typeText(VALID_PASSWORD))

        verifyErrorEmailRequiredGone()
        verifyErrorPasswordRequiredGone()
        verifyButtonEnabled()
    }

    @Test
    fun testFillEmailAndPasswordThenEmpty() {
        intentsTestRule.launchActivity(null)

        onView(withId(R.id.emailEditText))
            .perform(typeText(VALID_EMAIL))
        onView(withId(R.id.passwordEditText))
            .perform(typeText(VALID_PASSWORD))

        onView(withId(R.id.emailEditText)).perform(clearText())
        onView(withId(R.id.passwordEditText)).perform(clearText())

        verifyErrorEmailRequiredVisible()
        verifyErrorPasswordRequiredVisible()
        verifyButtonDisabled()
    }

    @Test
    fun testSignUpFail() {
        whenever(authenticationService.signUp(any(), any()))
            .thenReturn(Completable.error(IllegalStateException(ERROR)))

        intentsTestRule.launchActivity(null)

        onView(withId(R.id.emailEditText)).perform(typeText(VALID_EMAIL))
        onView(withId(R.id.passwordEditText))
            .perform(typeText(VALID_PASSWORD))
            .perform(pressImeActionButton())

        onView(withText(ERROR))
            .inRoot(withDecorView(not(intentsTestRule.activity.window.decorView)))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testSignUpSucceeds() {
        whenever(authenticationService.signUp(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())

        intentsTestRule.launchActivity(null)

        onView(withId(R.id.emailEditText)).perform(typeText(VALID_EMAIL))
        onView(withId(R.id.passwordEditText)).perform(typeText(VALID_PASSWORD))
        closeSoftKeyboard()
        onView(withId(R.id.signUpButton)).perform(click())

        Intents.intended(IntentMatchers.hasComponent(HomeActivity::class.java.name))
    }

    @Test
    fun testSignUpSucceedsFromImeAction() {
        whenever(authenticationService.signUp(VALID_EMAIL, VALID_PASSWORD))
            .thenReturn(Completable.complete())

        intentsTestRule.launchActivity(null)

        onView(withId(R.id.emailEditText)).perform(typeText(VALID_EMAIL))
        onView(withId(R.id.passwordEditText))
            .perform(typeText(VALID_PASSWORD))
            .perform(pressImeActionButton())

        Intents.intended(IntentMatchers.hasComponent(HomeActivity::class.java.name))
    }

    private fun verifyButtonEnabled() {
        onView(withId(R.id.signUpButton))
            .check(ViewAssertions.matches(isEnabled()))
    }

    private fun verifyButtonDisabled() {
        onView(withId(R.id.signUpButton))
            .check(ViewAssertions.matches(Matchers.not(isEnabled())))
    }

    private fun verifyErrorEmailRequiredVisible() {
        onView(withText(R.string.signup_email_required_error))
            .check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    private fun verifyErrorEmailRequiredGone() {
        onView(withText(R.string.signup_email_required_error))
            .check(ViewAssertions.doesNotExist())
    }

    private fun verifyErrorPasswordRequiredVisible() {
        onView(withText(R.string.signup_password_required_error))
            .check(ViewAssertions.matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)))
    }

    private fun verifyErrorPasswordRequiredGone() {
        onView(withText(R.string.signup_password_required_error))
            .check(ViewAssertions.doesNotExist())
    }
}
