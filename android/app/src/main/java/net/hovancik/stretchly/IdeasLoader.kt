package net.hovancik.stretchly

import android.content.Context

class IdeasLoader(private val context: Context) {

    fun getRandomMicrobreakIdea(): String {
        // First try to get from content packs
        val contentPackIdea = ContentPackManager.getRandomMicrobreakIdea()
        if (contentPackIdea != "Take a moment to relax and breathe") {
            return contentPackIdea
        }
        
        // Fallback to default ideas
        val ideas = context.resources.getStringArray(R.array.microbreak_ideas)
        return ideas.random()
    }

    fun getRandomBreakIdea(): Pair<String, String> {
        // First try to get from content packs
        val contentPackIdea = ContentPackManager.getRandomBreakIdea()
        if (contentPackIdea.title != "Take a Break") {
            return Pair(contentPackIdea.title, contentPackIdea.description)
        }
        
        // Fallback to default ideas
        val titles = context.resources.getStringArray(R.array.break_ideas_titles)
        val texts = context.resources.getStringArray(R.array.break_ideas_texts)
        val randomIndex = (titles.indices).random()
        return Pair(titles[randomIndex], texts[randomIndex])
    }
}
