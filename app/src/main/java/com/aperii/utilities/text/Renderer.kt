package com.aperii.utilities.text

import android.text.SpannableStringBuilder
import com.aperii.utilities.text.nodes.BaseRenderContext
import com.discord.simpleast.core.node.Node
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.simple.SimpleMarkdownRules

object Renderer {
    private val defaultParser = createDefaultParser()
    private val emojiParser = createEmojiParser()

    private fun Parser<BaseRenderContext, Node<BaseRenderContext>, Any?>.render(
        text: String,
        renderContext: BaseRenderContext
    ) = SpannableStringBuilder().apply {
        for (node in parse(text, ""))
            node.render(this, renderContext)
    }

    private fun createDefaultParser() =
        Parser<BaseRenderContext, Node<BaseRenderContext>, Any?>(false).apply {
            addRule(Rules.createUrlRule())
            addRule(Rules.createMentionRule())
            addRule(Rules.createEmojiRule())
            addRule(SimpleMarkdownRules.createTextRule())
        }

    fun render(text: String, renderContext: BaseRenderContext) =
        defaultParser.render(text, renderContext)

    private fun createEmojiParser() =
        Parser<BaseRenderContext, Node<BaseRenderContext>, Any?>(false).apply {
            addRule(Rules.createEmojiRule())
            addRule(SimpleMarkdownRules.createTextRule())
        }

    fun renderTwemoji(text: String, renderContext: BaseRenderContext) =
        emojiParser.render(text, renderContext)
}