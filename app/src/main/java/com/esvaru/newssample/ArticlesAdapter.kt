package com.esvaru.newssample

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.esvaru.newssample.models.Article
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.vh_article.view.*

class ArticlesAdapter(val clickListener: ClickListener?): RecyclerView.Adapter<ArticlesAdapter.ArticleViewHolder>() {
    private var items: List<Article> = listOf()

    fun setItems(items: List<Article>) {
        this.items = items
        this.notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.vh_article, parent, false)

        return ArticleViewHolder(view, clickListener)
    }

    override fun getItemCount(): Int = items.count()

    override fun onBindViewHolder(viewHolder: ArticleViewHolder, index: Int) {
        viewHolder.setItem(items[index], index)
    }


    class ArticleViewHolder(
            itemView: View,
            private val clickListener: ClickListener?
    ): RecyclerView.ViewHolder(itemView) {

        var article: Article? = null

        fun setItem(article: Article, index: Int) {
            this.article = article
            itemView.viewTitle.text = article.title

            Picasso.get().load(article.urlToImage)
                    .into(itemView.viewImage)

            itemView.setOnClickListener { clickListener?.onArticleClicked(this, index) }
        }
    }

    interface ClickListener {
        fun onArticleClicked(vh: ArticlesAdapter.ArticleViewHolder, pos: Int)
    }
}