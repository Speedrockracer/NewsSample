package com.esvaru.newssample.matchers

import android.support.v4.widget.SwipeRefreshLayout
import android.view.View
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

fun isRefreshing() = object : TypeSafeMatcher<View>() {

    override fun describeTo(description: Description) {
        description.appendText("is a SwipeRefreshLayout that is currently refreshing")
    }

    override fun matchesSafely(item: View?) = (item as? SwipeRefreshLayout)?.isRefreshing ?: false
}