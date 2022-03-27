package com.aperii.app

import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import com.aperii.R
import com.aperii.views.Toolbar

abstract class AppFragment : Fragment {

    val appActivity: AppActivity
        get() = requireActivity() as AppActivity

    val toolbar: Toolbar?
        get() = view?.findViewById(R.id.toolbar)

    constructor() : super()

    constructor(@LayoutRes layout: Int) : super(layout)

}