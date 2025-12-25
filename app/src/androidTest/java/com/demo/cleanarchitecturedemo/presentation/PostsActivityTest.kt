package com.demo.cleanarchitecturedemo.presentation

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.util.HumanReadables
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.cleanarchitecturedemo.R
import com.demo.cleanarchitecturedemo.presentation.ui.MainActivity
import org.hamcrest.Matcher
import org.hamcrest.Matchers.any
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.test.KoinTest
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
class PostsActivityTest : KoinTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun checkPostsAreDisplayedInList() {
        onView(withId(R.id.list_of_post_rv))
            .perform(waitUntilVisible(5000L)) // Wait up to 5 seconds

        onView(withId(R.id.list_of_post_rv))
            .perform(
                RecyclerViewActions.scrollTo<RecyclerView.ViewHolder>(
                    hasDescendant(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
                )
            )

        onView(withText("sunt aut facere repellat provident occaecati excepturi optio reprehenderit"))
            .check(matches(isDisplayed()))
    }

    /**
     * Helper function to wait for a view to become visible
     */
    private fun waitUntilVisible(timeoutMillis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = any(View::class.java)

            override fun getDescription(): String = "wait for view to become visible within $timeoutMillis ms"

            override fun perform(uiController: UiController, view: View) {
                val endTime = System.currentTimeMillis() + timeoutMillis
                do {
                    if (view.visibility == View.VISIBLE) return
                    uiController.loopMainThreadForAtLeast(50)
                } while (System.currentTimeMillis() < endTime)

                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription(HumanReadables.describe(view))
                    .withCause(TimeoutException("Waited $timeoutMillis ms for view to become visible"))
                    .build()
            }
        }
    }
}
