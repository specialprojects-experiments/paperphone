package com.withgoogle.experiments.unplugged.ui.onboarding

import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.withgoogle.experiments.unplugged.R

class FoldingFragment: Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_onboarding_folding, container, false)

        with(view.findViewById<ImageView>(R.id.animation)) {
            val frameAnimation = drawable as AnimationDrawable

            frameAnimation.start()
        }

        return view
    }
}