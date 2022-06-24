package com.aperii.utilities.text

import android.content.Context
import com.aperii.utilities.text.nodes.BaseRenderContext
import com.aperii.utilities.text.nodes.EmojiNode
import com.aperii.utilities.text.nodes.MentionNode
import com.aperii.utilities.text.nodes.UrlNode
import com.discord.simpleast.core.node.Node
import com.discord.simpleast.core.parser.ParseSpec
import com.discord.simpleast.core.parser.Parser
import com.discord.simpleast.core.parser.Rule
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.regex.Matcher
import java.util.regex.Pattern

object Rules: KoinComponent {
    val PATTERN_MENTIONS: Pattern = Pattern.compile("^@([a-zA-Z_0-9]{1,32})")
    val PATTERN_URL: Pattern = Pattern.compile("^(https?://[^\\s<]+[^<.,:;\"')\\]\\s])")
    val ctx: Context by inject()

    fun <S> createMentionRule(): Rule<BaseRenderContext, Node<BaseRenderContext>, S> {
        return object : Rule<BaseRenderContext, Node<BaseRenderContext>, S>(PATTERN_MENTIONS) {
            override fun parse(
                matcher: Matcher,
                parser: Parser<BaseRenderContext, in Node<BaseRenderContext>, S>,
                state: S
            ): ParseSpec<BaseRenderContext, S> {
                return ParseSpec.createTerminal(MentionNode(matcher.group(1)!!), state)
            }
        }
    }

    fun <S> createUrlRule(): Rule<BaseRenderContext, Node<BaseRenderContext>, S> {
        return object : Rule<BaseRenderContext, Node<BaseRenderContext>, S>(PATTERN_URL) {
            override fun parse(
                matcher: Matcher,
                parser: Parser<BaseRenderContext, in Node<BaseRenderContext>, S>,
                state: S
            ): ParseSpec<BaseRenderContext, S> {
                return ParseSpec.createTerminal(UrlNode(matcher.group(1)!!), state)
            }
        }
    }

    fun <S> createEmojiRule(): Rule<BaseRenderContext, Node<BaseRenderContext>, S> {
        return object : Rule<BaseRenderContext, Node<BaseRenderContext>, S>(Pattern.compile(ctx.loadEmojiRegex())) {
            override fun parse(
                matcher: Matcher,
                parser: Parser<BaseRenderContext, in Node<BaseRenderContext>, S>,
                state: S
            ): ParseSpec<BaseRenderContext, S> {
                return ParseSpec.createTerminal(EmojiNode(matcher.group(1)!!), state)
            }
        }
    }

}