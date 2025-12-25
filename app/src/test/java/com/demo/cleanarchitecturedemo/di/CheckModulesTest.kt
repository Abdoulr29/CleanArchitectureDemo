package com.demo.cleanarchitecturedemo.di

import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.test.KoinTest
import org.koin.test.check.checkModules

@OptIn(ExperimentalCoroutinesApi::class)
class CheckModulesTest : KoinTest {

    @Before
    fun setup() {
        // Set the Main dispatcher to a Test dispatcher
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        // Reset the Main dispatcher to the original Main dispatcher
        Dispatchers.resetMain()
    }

    @Test
    fun verifyKoinConfiguration() {
        checkModules {
            androidContext(mockk(relaxed = true))
            modules(appModule)
        }
    }
}
