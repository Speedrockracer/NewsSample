package com.esvaru.newssample.models

data class Article(
        val publishedAt: String,

        val title: String,
        val description: String,

        val url: String,
        val urlToImage: String
)
