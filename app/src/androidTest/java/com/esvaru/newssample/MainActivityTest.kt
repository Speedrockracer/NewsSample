package com.esvaru.newssample

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.*
import android.support.test.espresso.matcher.ViewMatchers.*
import com.esvaru.newssample.matchers.*
import android.support.test.espresso.assertion.ViewAssertions.*
import android.support.test.runner.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.rule.ActivityTestRule
import com.esvaru.newssample.models.Article
import com.esvaru.newssample.models.ArticleResult
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.TestScheduler
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.applicationContext
import org.koin.standalone.StandAloneContext.loadKoinModules
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private val testScheduler = TestScheduler()

    private val mockNewsResult = ArticleResult(
            "ok",
            4,
            listOf(
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl"),
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl"),
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl"),
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl")
            )
    )

    private val newsApiService = mock<NewsApiService>()

    @Rule
    @JvmField
    val rule = object: ActivityTestRule<MainActivity>(MainActivity::class.java) {
        override fun beforeActivityLaunched() {
            super.beforeActivityLaunched()
            // Inject testing modules in the DI system so we can control and check what is happening
            val testingModule = applicationContext {
                viewModel { NewsViewModel(
                        get(),
                        testScheduler,
                        AndroidSchedulers.mainThread(),
                        testScheduler
                ) }
                bean { newsApiService }
            }

            loadKoinModules(listOf(testingModule))
        }
    }

    @Before
    fun setup() {
        reset(newsApiService) // Reset so the tests don't interfere with eachother
        whenever(newsApiService.getHeadLines(anyOrNull())).doReturn(Observable.just(mockNewsResult))
    }

    @Test
    fun testShowsLoadingData() {
        testScheduler.triggerActions() // Start the loading action
        onView(withId(R.id.viewSwypeRefresh))
                .check(matches(isRefreshing()))

        // Advance the scheduler so the data is loaded
        testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
        testScheduler.triggerActions()

        // Check if the refresh has stopped refreshing
        onView(withId(R.id.viewSwypeRefresh))
                .check(matches(not(isRefreshing())))

        // Check if there is data in the recyclerview
        onView(withId(R.id.viewArticleList))
                .check(matches(hasDescendant(withText("testTitle"))))
    }

    @Test
    fun testSearchOpensSearchMode() {
        onView(withId(R.id.search))
                .perform(click())

        onView(withHint(R.string.search_hint))
                .check(matches(withHint("Search")))
                .perform(typeText("TEST"), closeSoftKeyboard())

        // Trigger the actions so we can check if the loading calls where made
        testScheduler.triggerActions()
        testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
        testScheduler.triggerActions()

        onView(withHint(R.string.search_hint))
                .check(matches(withText("TEST")))

        verify(newsApiService).getHeadLines("TEST")
    }
}