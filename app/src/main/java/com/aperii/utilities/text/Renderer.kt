package com.aperii.utilities.text

import android.text.SpannableStringBuilder
import com.aperii.utilities.text.nodes.BaseRenderContext
import com.discord.simpleast.core.node.Node
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.simple.SimpleMarkdownRules

object Renderer {
    private val mParser = createParser()

    private fun createParser() = Parser<BaseRenderContext, Node<BaseRenderContext>, Any?>(false).apply {
        addRule(Rules.createUrlRule())
        addRule(Rules.createMentionRule())
        addRule(SimpleMarkdownRules.createTextRule())
    }

    fun render(text: String, renderContext: BaseRenderContext) = SpannableStringBuilder().apply {
        for (node in mParser.parse(text, ""))
            node.render(this, renderContext)
    }

}