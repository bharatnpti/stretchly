package net.hovancik.stretchly

import android.content.Context

class IdeasLoader(private val context: Context) {

    fun getRandomMicrobreakIdea(): String {
        val ideas = context.resources.getStringArray(R.array.microbreak_ideas)
        return ideas.random()
    }

    fun getRandomBreakIdea(): Pair<String, String> {
        val titles = context.resources.getStringArray(R.array.break_ideas_titles)
        val texts = context.resources.getStringArray(R.array.break_ideas_texts)
        val randomIndex = (titles.indices).random()
        return Pair(titles[randomIndex], texts[randomIndex])
    }
}
