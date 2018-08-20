package com.esvaru.newssample.views

import android.app.SearchManager
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.transition.Fade
import android.transition.Slide
import android.view.*
import com.esvaru.newssample.NewsViewModel
import com.esvaru.newssample.R
import com.esvaru.newssample.models.LoadingStatus
import com.pawegio.kandroid.onQueryChange
import kotlinx.android.synthetic.main.fragment_news_list.*
import org.koin.android.architecture.ext.sharedViewModel

class NewsListFragment: Fragment(), ArticlesAdapter.ClickListener {
    private val model by sharedViewModel<NewsViewModel>()
    private val articlesAdapter = ArticlesAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setHasOptionsMenu(true)

        model.getStatusData().observe(this, Observer {
            setDataOnViews(it)
        })

        // Create our enter and exit transitions
        exitTransition = Fade()
        reenterTransition = Slide(Gravity.LEFT)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_news_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewArticleList.layoutManager = LinearLayoutManager(context)
        viewArticleList.adapter = articlesAdapter
        viewArticleList.setHasFixedSize(true)

        viewSwypeRefresh.setOnRefreshListener {
            model.refreshArticles()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.menu_search, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        // Setup search
        val searchActionItem = menu?.findItem(R.id.search)
        val searchView = searchActionItem?.actionView as? SearchView

        val searchManager = context?.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView?.queryHint = getString(R.string.search_hint)
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(activity?.componentName))
        searchView?.isIconified = false
        searchView?.onQueryChange { query ->
            model.onSearchChanged(query)
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onArticleClicked(vh: ArticlesAdapter.ArticleViewHolder, index: Int) {
        val article = vh.article ?: return
        (activity as? ArticlesInterface)?.openArticle(article, vh)
    }

    private fun setDataOnViews(status: LoadingStatus?) {
        articlesAdapter.setItems(status?.data?.articles ?: listOf())

        viewSwypeRefresh.isRefreshing = status?.isLoading ?: false

        if (status?.error != null) {
            viewErrorMessage.text = status.error
            viewErrorMessage.visibility = View.VISIBLE
        } else {
            viewErrorMessage.visibility = View.GONE
        }
    }

    companion object {
        const val TAG = "NewsListFragment"
    }
}