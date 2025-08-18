package net.hovancik.stretchly

data class GuidedStep(
    val instruction: String,
    val duration: Int // in seconds
)

data class GuidedRoutine(
    val title: String,
    val steps: List<GuidedStep>
)

object GuidedRoutineFactory {
    
    fun getRoutine(type: String): GuidedRoutine {
        return when (type) {
            "breathing" -> getBreathingRoutine()
            "eye_exercises" -> getEyeExerciseRoutine()
            "neck_stretches" -> getNeckStretchRoutine()
            else -> getBreathingRoutine()
        }
    }
    
    private fun getBreathingRoutine(): GuidedRoutine {
        return GuidedRoutine(
            title = "Box Breathing",
            steps = listOf(
                GuidedStep("Inhale slowly through your nose", 4),
                GuidedStep("Hold your breath", 4),
                GuidedStep("Exhale slowly through your mouth", 4),
                GuidedStep("Hold your breath", 4),
                GuidedStep("Repeat the cycle", 4),
                GuidedStep("Inhale slowly through your nose", 4),
                GuidedStep("Hold your breath", 4),
                GuidedStep("Exhale slowly through your mouth", 4),
                GuidedStep("Hold your breath", 4)
            )
        )
    }
    
    private fun getEyeExerciseRoutine(): GuidedRoutine {
        return GuidedRoutine(
            title = "20-20-20 Eye Exercise",
            steps = listOf(
                GuidedStep("Look at something 20 feet away", 20),
                GuidedStep("Blink slowly 10 times", 10),
                GuidedStep("Look at something 20 feet away", 20),
                GuidedStep("Focus on near object for 5 seconds", 5),
                GuidedStep("Focus on far object for 5 seconds", 5),
                GuidedStep("Repeat near/far focus 3 times", 30),
                GuidedStep("Close your eyes and relax", 10)
            )
        )
    }
    
    private fun getNeckStretchRoutine(): GuidedRoutine {
        return GuidedRoutine(
            title = "Neck & Shoulder Stretches",
            steps = listOf(
                GuidedStep("Slowly tilt your head to the right", 5),
                GuidedStep("Hold the stretch", 10),
                GuidedStep("Return to center", 2),
                GuidedStep("Slowly tilt your head to the left", 5),
                GuidedStep("Hold the stretch", 10),
                GuidedStep("Return to center", 2),
                GuidedStep("Slowly turn your head to the right", 5),
                GuidedStep("Hold the stretch", 10),
                GuidedStep("Return to center", 2),
                GuidedStep("Slowly turn your head to the left", 5),
                GuidedStep("Hold the stretch", 10),
                GuidedStep("Return to center", 2),
                GuidedStep("Shrug your shoulders up", 3),
                GuidedStep("Hold", 5),
                GuidedStep("Release and relax", 5)
            )
        )
    }
}
