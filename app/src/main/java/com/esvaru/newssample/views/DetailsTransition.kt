package com.esvaru.newssample.views

import android.annotation.TargetApi
import android.os.Build
import android.transition.ChangeBounds
import android.transition.ChangeImageTransform
import android.transition.ChangeTransform
import android.transition.TransitionSet

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
class DetailsTransition: TransitionSet() {

    init {
        ordering = ORDERING_TOGETHER
        addTransition(ChangeBounds())
                .addTransition(ChangeTransform())
                .addTransition(ChangeImageTransform())
    }
}