package net.hovancik.stretchly

import android.content.Context
import android.content.res.Resources
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations

class IdeasLoaderTest {

    @Mock
    private lateinit var mockContext: Context

    @Mock
    private lateinit var mockResources: Resources

    private lateinit var ideasLoader: IdeasLoader

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        `when`(mockContext.resources).thenReturn(mockResources)
        ideasLoader = IdeasLoader(mockContext)
    }

    @Test
    fun testGetRandomMicrobreakIdea() {
        val ideas = arrayOf("Idea 1", "Idea 2", "Idea 3")
        `when`(mockResources.getStringArray(R.array.microbreak_ideas)).thenReturn(ideas)
        val idea = ideasLoader.getRandomMicrobreakIdea()
        assert(ideas.contains(idea))
    }

    @Test
    fun testGetRandomBreakIdea() {
        val titles = arrayOf("Title 1", "Title 2", "Title 3")
        val texts = arrayOf("Text 1", "Text 2", "Text 3")
        `when`(mockResources.getStringArray(R.array.break_ideas_titles)).thenReturn(titles)
        `when`(mockResources.getStringArray(R.array.break_ideas_texts)).thenReturn(texts)
        val idea = ideasLoader.getRandomBreakIdea()
        assert(titles.contains(idea.first))
        assert(texts.contains(idea.second))
    }
}
