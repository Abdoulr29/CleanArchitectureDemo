package com.demo.cleanarchitecturedemo

import android.os.StrictMode
import android.view.View
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isRoot
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.demo.cleanarchitecturedemo.data.local.AppDatabase
import com.demo.cleanarchitecturedemo.data.remote.ApiService
import com.demo.cleanarchitecturedemo.presentation.ui.MainActivity
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.core.context.GlobalContext.loadKoinModules
import org.koin.core.context.GlobalContext.unloadKoinModules
import org.koin.core.context.loadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module
import org.koin.test.KoinTest
import org.koin.test.get
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
class EndToEndTest : KoinTest {

    private lateinit var mockServer: MockWebServer
    private lateinit var testNetworkModule: Module

    @Before
    fun setup() {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // 1. Clear DB to prevent stale data from breaking the test
        // This requires the Koin graph to be ready. If this fails, move it inside the test or use a separate DB module.
        try {
            val db = get<AppDatabase>()
            db.clearAllTables()
        } catch (e: Exception) {
            // Ignore if Koin isn't ready yet (first run)
        }

        mockServer = MockWebServer()
        mockServer.start()

        // 2. Use 127.0.0.1 for emulator reliability
        val baseUrl = mockServer.url("/").toString().replace("localhost", "127.0.0.1")

        testNetworkModule = module {
            single {
                Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(ApiService::class.java)
            }
        }
        loadKoinModules(testNetworkModule)
    }

    @After
    fun tearDown() {
        mockServer.shutdown()
        if (::testNetworkModule.isInitialized) {
            unloadKoinModules(testNetworkModule)
        }
    }

    @Test
    fun flow_FetchPostsFromNetwork_DisplayOnUI_And_VerifyPersistence() = runBlocking {
        // Arrange
        val mockJsonResponse = """
            [
                {"id": 1, "userId": 1, "title": "E2E Test Title", "body": "E2E Body Content"}
            ]
        """.trimIndent()

        mockServer.enqueue(
            MockResponse()
                .setBody(mockJsonResponse)
                .setResponseCode(200)
        )

        // Act
        val scenario = ActivityScenario.launch(MainActivity::class.java)

        // Wait/Verify UI
        // Increased timeout to 10s just in case emulator is slow
        onView(isRoot()).perform(waitForViewWithText("E2E Test Title", 10000L))

        onView(withText("E2E Test Title"))
            .check(matches(isDisplayed()))

        // Assert Persistence
        val db = get<AppDatabase>()
        val cachedPosts = db.postDao().getAllPosts()

        assertTrue("Database should not be empty", cachedPosts.isNotEmpty())
        assertEquals("E2E Test Title", cachedPosts[0].title)

        scenario.close()
    }

    private fun waitForViewWithText(text: String, timeoutMillis: Long): ViewAction {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> = isRoot()
            override fun getDescription(): String = "wait for view with text '$text' for $timeoutMillis ms"

            override fun perform(uiController: UiController, view: View) {
                val endTime = System.currentTimeMillis() + timeoutMillis

                while (System.currentTimeMillis() < endTime) {
                    val foundViews = ArrayList<View>()
                    view.findViewsWithText(foundViews, text, View.FIND_VIEWS_WITH_TEXT)

                    // Check if found AND visible
                    if (foundViews.any { it.visibility == View.VISIBLE }) {
                        return
                    }

                    uiController.loopMainThreadForAtLeast(50)
                }

                throw PerformException.Builder()
                    .withActionDescription(description)
                    .withViewDescription("Root view")
                    .withCause(TimeoutException("Waited $timeoutMillis ms for text '$text' to appear"))
                    .build()
            }
        }
    }
}
