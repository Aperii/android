package com.aperii.utilities.text.nodes

import android.content.Context

data class BioRenderContext(
    override val context: Context,
    override val lineHeight: Int
) : BaseRenderContext
