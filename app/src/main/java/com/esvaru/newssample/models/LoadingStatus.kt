package com.esvaru.newssample.models

data class LoadingStatus(
        val data: ArticleResult? = null,
        val isLoading: Boolean = false,
        val error: String? = null
)