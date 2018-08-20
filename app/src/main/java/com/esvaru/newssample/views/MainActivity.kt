package com.esvaru.newssample.views

import android.os.Build
import android.os.Bundle
import android.support.v4.view.ViewCompat
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.esvaru.newssample.NewsViewModel
import com.esvaru.newssample.R
import com.esvaru.newssample.models.Article
import kotlinx.android.synthetic.main.vh_article.view.*
import org.koin.android.architecture.ext.viewModel

interface ArticlesInterface {
    fun openArticle(article: Article, vh: ArticlesAdapter.ArticleViewHolder)
    fun detailFragmentOpened()
    fun detailFragmentClosed()
}

class MainActivity : AppCompatActivity(), ArticlesInterface {

    private val model by viewModel<NewsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        super.onResume()

        if(supportFragmentManager.findFragmentByTag(NewsListFragment.TAG) == null) {
            initializeList()
        }
    }

    private fun initializeList() {
        val fragment = NewsListFragment()
        supportFragmentManager.beginTransaction()
                .replace(R.id.viewMainLayout, fragment, NewsListFragment.TAG)
                .commit()
    }

    override fun openArticle(article: Article, vh: ArticlesAdapter.ArticleViewHolder) {
        // We could do something different here if this was a tablet.
        // For example open the detail fragment next to the list.
        model.onArticleSelected(article)
        val detailFragment = NewsDetailFragment()

        // Shared transitions only available on newer android
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val imageMove = DetailsTransition()
            imageMove.duration = 300
            imageMove.startDelay = 300
            detailFragment.sharedElementEnterTransition = imageMove
        }

        ViewCompat.setTransitionName(vh.itemView.viewImage, "ArticleImage")

        supportFragmentManager.beginTransaction()
                .addSharedElement(vh.itemView.viewImage, "ArticleImage")
                .replace(R.id.viewMainLayout, detailFragment, NewsDetailFragment.TAG)
                .addToBackStack(null)
                .commit()
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item?.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }

        return super.onOptionsItemSelected(item)
    }

    override fun detailFragmentOpened() {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun detailFragmentClosed() {
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
}
