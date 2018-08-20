package com.esvaru.newssample

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.transition.Fade
import android.transition.Slide
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esvaru.newssample.models.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_news_detail.*
import org.koin.android.architecture.ext.sharedViewModel

class NewsDetailFragment: Fragment() {

    private val model by sharedViewModel<NewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        model.getSelectedArticleData().observe(this, Observer {
            if (it != null) { setDataOnViews(it) }
        })

        // Enter transition for the new element
        val enterFade = Fade()
        enterFade.startDelay = 500
        enterTransition = enterFade
        returnTransition = Slide(Gravity.RIGHT)
    }

    override fun onResume() {
        super.onResume()
        (activity as? ArticlesInterface)?.detailFragmentOpened()
    }

    override fun onPause() {
        super.onPause()
        (activity as? ArticlesInterface)?.detailFragmentClosed()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_detail, container, false)
    }

    private fun setDataOnViews(article: Article) {
        viewTitle.text = article.title
        viewDate.text = article.publishedAt
        viewContent.text = article.description

        Picasso.get().load(article.urlToImage).into(viewImage)
    }

    companion object {
        const val TAG = "NewsDetailFragment"
    }
}