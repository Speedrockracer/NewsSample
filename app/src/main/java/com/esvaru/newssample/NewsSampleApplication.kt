package com.esvaru.newssample

import android.app.Application
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.android.architecture.ext.viewModel
import org.koin.android.ext.android.startKoin
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext

val newsModule: Module = applicationContext {
    viewModel { NewsViewModel(
            get(),
            Schedulers.newThread(),
            AndroidSchedulers.mainThread(),
            Schedulers.computation()
    )}
    bean { createNewsApiService() }
}

class NewsSampleApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin(this, listOf(newsModule))
    }
}
