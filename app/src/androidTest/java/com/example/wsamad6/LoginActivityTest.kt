package com.example.wsamad6

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class LoginActivityTest{
    private lateinit var scenario: ActivityScenario<LoginActivity>

    @Before
    fun setup(){
        scenario = ActivityScenario.launch(LoginActivity::class.java)
    }

    @Test
    fun activityIsDisplayed(){
        onView(withId(R.id.loginScreen)).check(matches(isDisplayed()))
    }

    @Test
    fun both_edit_texts_are_empty(){
        onView(withId(R.id.btnSignIn)).perform(click())
        onView(withId(R.id.txtAlert)).check(matches(withText("Any field can't be empty")))
    }
    @Test
    fun email_is_empty(){
        onView(withId(R.id.edtPassword)).perform(typeText("1234"))
        onView(withId(R.id.btnSignIn)).perform(scrollTo(), click())
        onView(withId(R.id.txtAlert)).check(matches(withText("Any field can't be empty")))
    }

    @Test
    fun password_is_empty(){
        onView(withId(R.id.edtEmail)).perform(typeText("healthy@wsa.com"))
        onView(withId(R.id.btnSignIn)).perform(scrollTo(), click())
        onView(withId(R.id.txtAlert)).check(matches(withText("Any field can't be empty")))
    }

    @Test
    fun emailHaveWrongFormatAndPasswordIsCorrect(){
        onView(withId(R.id.edtEmail)).perform(typeText("healthy@gmail.com"))
        onView(withId(R.id.edtPassword)).perform(scrollTo(),typeText("1234"))
        onView(withId(R.id.btnSignIn)).perform(scrollTo(), click())
        onView(withId(R.id.txtAlert)).check(matches(withText("The email field have a wrong format")))
    }

    @Test
    fun passwordHaveWrongFormatAndEmailIsCorrect(){
        onView(withId(R.id.edtEmail)).perform(typeText("healthy@wsa.com"))
        onView(withId(R.id.edtPassword)).perform(scrollTo(),typeText("a1234"))
        onView(withId(R.id.btnSignIn)).perform(scrollTo(), click())
        onView(withId(R.id.txtAlert)).check(matches(withText("The Password field have a wrong format")))
    }

    @Test
    fun emailAndPasswordAreCorrects(){
        onView(withId(R.id.edtEmail)).perform(typeText("healthy@wsa.com"))
        onView(withId(R.id.edtPassword)).perform(scrollTo(), typeText("1234"))
        onView(withId(R.id.btnSignIn)).perform(scrollTo(), click())
        onView(withId(R.id.txtAlert)).check(matches(withText("")))
    }
}