package net.hovancik.stretchly

data class ContentPack(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean = false,
    val microbreakIdeas: List<String> = emptyList(),
    val breakIdeas: List<BreakIdea> = emptyList(),
    val guidedRoutines: List<String> = emptyList()
)

data class BreakIdea(
    val title: String,
    val description: String
)

object ContentPackManager {
    
    private val contentPacks = mutableListOf<ContentPack>()
    
    init {
        initializeDefaultPacks()
    }
    
    private fun initializeDefaultPacks() {
        contentPacks.addAll(listOf(
            getGeneralWellnessPack().copy(isEnabled = true),
            getEyeCarePack(),
            getDeskMobilityPack()
        ))
    }
    
    private fun getGeneralWellnessPack(): ContentPack {
        return ContentPack(
            id = "general_wellness",
            name = "General Wellness",
            description = "Basic wellness ideas and routines",
            microbreakIdeas = listOf(
                "Take a deep breath and relax your shoulders",
                "Stand up and stretch your arms overhead",
                "Look away from the screen and blink slowly",
                "Take a sip of water",
                "Roll your shoulders backward and forward"
            ),
            breakIdeas = listOf(
                BreakIdea("Short Walk", "Take a 5-minute walk around your workspace"),
                BreakIdea("Hydration Break", "Get up and refill your water bottle"),
                BreakIdea("Stretch Session", "Do some gentle stretching exercises"),
                BreakIdea("Mindful Moment", "Practice a quick mindfulness exercise")
            ),
            guidedRoutines = listOf("breathing", "eye_exercises")
        )
    }
    
    private fun getEyeCarePack(): ContentPack {
        return ContentPack(
            id = "eye_care",
            name = "Eye Care",
            description = "Specialized routines for eye health",
            microbreakIdeas = listOf(
                "Look at something 20 feet away for 20 seconds",
                "Blink slowly 10 times",
                "Close your eyes and gently massage your eyelids",
                "Focus on a distant object, then a near object",
                "Roll your eyes in a circular motion"
            ),
            breakIdeas = listOf(
                BreakIdea("Eye Rest", "Close your eyes and rest them for a few minutes"),
                BreakIdea("Visual Break", "Look out the window at natural scenery"),
                BreakIdea("Eye Exercise", "Practice eye focusing exercises"),
                BreakIdea("Palming", "Cover your eyes with your palms and relax")
            ),
            guidedRoutines = listOf("eye_exercises")
        )
    }
    
    private fun getDeskMobilityPack(): ContentPack {
        return ContentPack(
            id = "desk_mobility",
            name = "Desk Mobility",
            description = "Mobility exercises for desk workers",
            microbreakIdeas = listOf(
                "Roll your neck gently from side to side",
                "Shrug your shoulders up and down",
                "Rotate your wrists in both directions",
                "Stretch your fingers wide, then make a fist",
                "Tilt your head to each shoulder"
            ),
            breakIdeas = listOf(
                BreakIdea("Neck Stretches", "Do a series of neck mobility exercises"),
                BreakIdea("Shoulder Mobility", "Practice shoulder rolls and stretches"),
                BreakIdea("Wrist Exercises", "Stretch and strengthen your wrists"),
                BreakIdea("Full Body Stretch", "Stand up and do a complete body stretch")
            ),
            guidedRoutines = listOf("neck_stretches")
        )
    }
    
    fun getAllPacks(): List<ContentPack> = contentPacks.toList()
    
    fun getEnabledPacks(): List<ContentPack> = contentPacks.filter { it.isEnabled }
    
    fun enablePack(packId: String, enabled: Boolean) {
        contentPacks.find { it.id == packId }?.let { pack ->
            val index = contentPacks.indexOf(pack)
            contentPacks[index] = pack.copy(isEnabled = enabled)
        }
    }
    
    fun getRandomMicrobreakIdea(): String {
        val enabledPacks = getEnabledPacks()
        if (enabledPacks.isEmpty()) {
            return "Take a moment to relax and breathe"
        }
        
        val allIdeas = enabledPacks.flatMap { it.microbreakIdeas }
        return if (allIdeas.isNotEmpty()) {
            allIdeas.random()
        } else {
            "Take a moment to relax and breathe"
        }
    }
    
    fun getRandomBreakIdea(): BreakIdea {
        val enabledPacks = getEnabledPacks()
        if (enabledPacks.isEmpty()) {
            return BreakIdea("Take a Break", "Use this time to relax and recharge")
        }
        
        val allIdeas = enabledPacks.flatMap { it.breakIdeas }
        return if (allIdeas.isNotEmpty()) {
            allIdeas.random()
        } else {
            BreakIdea("Take a Break", "Use this time to relax and recharge")
        }
    }
    
    fun getAvailableGuidedRoutines(): List<String> {
        return getEnabledPacks().flatMap { it.guidedRoutines }.distinct()
    }
}
