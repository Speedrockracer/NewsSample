package com.esvaru.newssample

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.esvaru.newssample.models.Article
import com.esvaru.newssample.models.ArticleQuery
import com.esvaru.newssample.models.LoadingStatus
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.subjects.BehaviorSubject
import java.io.InterruptedIOException
import java.util.concurrent.TimeUnit

class NewsViewModel (
        private val newsApiService: NewsApiService,
        processScheduler: Scheduler,
        androidScheduler: Scheduler,
        debouceScheduler: Scheduler
): ViewModel() {

    private val queryPublishSubject = BehaviorSubject.create<ArticleQuery>()
    private val statusData = MutableLiveData<LoadingStatus>()
    private val selectedArticleData = MutableLiveData<Article>()

    init {
        queryPublishSubject
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe {
                    // show the loader when the query is changed
                    val currentStatus = statusData.value
                    statusData.value = LoadingStatus(
                            currentStatus?.data,
                            true,
                            currentStatus?.error
                    )
                }

        queryPublishSubject // Load data when the query is changed
                .debounce(300, TimeUnit.MILLISECONDS, debouceScheduler)
                .switchMap { getRequestObservable(it) }
                .subscribeOn(processScheduler)
                .observeOn(androidScheduler)
                .subscribe {
                    statusData.value = it
                }

        queryPublishSubject.onNext(ArticleQuery())
    }

    private fun getRequestObservable(q: ArticleQuery): Observable<LoadingStatus>? {
        return newsApiService.getHeadLines(q.query)
                .map { LoadingStatus(it, false) }
                .onErrorResumeNext { error: Throwable -> // Replace the error with a normal object
                    // Error handling
                    println(error.localizedMessage)

                    when (error) {
                        is InterruptedIOException -> Observable.empty() // There was another request overriding the previous one
                        else -> Observable.just(LoadingStatus(
                                null,
                                false,
                                error.localizedMessage
                        ))
                    }
                }
    }

    fun getStatusData(): LiveData<LoadingStatus> = statusData
    fun getSelectedArticleData(): LiveData<Article> = selectedArticleData

    fun refreshArticles() { // Use the existing query to refresh
        queryPublishSubject.onNext(queryPublishSubject.value ?: ArticleQuery())
    }

    fun onSearchChanged(input: String) {
        queryPublishSubject.onNext(ArticleQuery(input))
    }

    fun onArticleSelected(article: Article) {
        selectedArticleData.value = article
    }
}