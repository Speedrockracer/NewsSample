package com.esvaru.newssample

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.Observer
import com.esvaru.newssample.models.Article
import com.esvaru.newssample.models.ArticleResult
import com.esvaru.newssample.models.LoadingStatus
import com.nhaarman.mockitokotlin2.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit

class NewsViewModelTest {

    val mockNewsResult = ArticleResult(
            "ok",
            4,
            listOf(
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl"),
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl"),
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl"),
                    Article("testDate", "testTitle", "testDescription", "testUrl", "testImageUrl")
            )
    )

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun testLoadsArticles() {
        val observer = mock<Observer<LoadingStatus>> ()
        val newsApiService = mock<NewsApiService> {
            on { getHeadLines(null) } doReturn Observable.just(mockNewsResult) // Return mock data
        }

        // Create the class to test
        val testScheduler = TestScheduler()
        val newsViewModel = NewsViewModel(newsApiService, testScheduler, testScheduler, testScheduler)
        newsViewModel.getStatusData().observeForever(observer)

        // We are empty at the start
        assertNull(newsViewModel.getStatusData().value)

        // Trigger the first actions (This will make the loading status set to loading)
        testScheduler.triggerActions()
        verify(observer).onChanged(any())
        assertTrue(newsViewModel.getStatusData().value?.isLoading ?: false)

        // Advance the scheduler by 300 ms (the debounce time)
        // And trigger actions again. Now the data is loaded.
        testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
        testScheduler.triggerActions()

        verify(observer, times(2)).onChanged(any())
        assertFalse(newsViewModel.getStatusData().value?.isLoading ?: true) // Loading should be false
        assertNull(newsViewModel.getStatusData().value?.error)
        assertEquals(newsViewModel.getStatusData().value?.data, mockNewsResult) // Data is loaded?
    }

    @Test
    fun testLoadingErrorHandling() {
        val observer = mock<Observer<LoadingStatus>> ()
        val mockException = Exception("Loading error")
        val newsApiService = mock<NewsApiService> {
            on { getHeadLines(null) } doReturn Observable.error<ArticleResult> { mockException }
        }

        // Create the class to test
        val testScheduler = TestScheduler()
        val newsViewModel = NewsViewModel(newsApiService, testScheduler, testScheduler, testScheduler)
        newsViewModel.getStatusData().observeForever(observer)

        // Trigger the first actions (This will make the loading status set to loading)
        testScheduler.triggerActions()
        // Advance the scheduler by 300 ms (the debounce time)
        // And trigger actions again. Now the error should be present.
        testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
        testScheduler.triggerActions()

        verify(observer, times(2)).onChanged(any())
        assertFalse(newsViewModel.getStatusData().value?.isLoading ?: true) // Loading should be false
        assertNull(newsViewModel.getStatusData().value?.data) // Data is null
        assertEquals(newsViewModel.getStatusData().value?.error, mockException.localizedMessage) // Error is triggered?
    }

    @Test
    fun testIgnoreInterruptionErrors() {
        val observer = mock<Observer<LoadingStatus>> ()
        val mockException = InterruptedIOException("Interrupted")
        val newsApiService = mock<NewsApiService> {
            on { getHeadLines(null) } doReturn Observable.error<ArticleResult> { mockException }
        }

        // Create the class to test
        val testScheduler = TestScheduler()
        val newsViewModel = NewsViewModel(newsApiService, testScheduler, testScheduler, testScheduler)
        newsViewModel.getStatusData().observeForever(observer)

        // Trigger the first actions (This will make the loading status set to loading)
        testScheduler.triggerActions()
        // Advance the scheduler by 300 ms (the debounce time)
        // And trigger actions again. Nothing should happen now.
        testScheduler.advanceTimeBy(300, TimeUnit.MILLISECONDS)
        testScheduler.triggerActions()

        // the request was interupted by the next request so we expect it to still be loading and do nothing
        verify(observer, times(1)).onChanged(any())
        assertTrue(newsViewModel.getStatusData().value?.isLoading ?: false) // Loading should be false
        assertNull(newsViewModel.getStatusData().value?.data) // Data is null
        assertNull(newsViewModel.getStatusData().value?.error) // Data is null
    }
}