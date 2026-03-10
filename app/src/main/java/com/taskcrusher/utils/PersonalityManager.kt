package com.taskcrusher.utils

object PersonalityManager {

    enum class Mode {
        HYPE_MAN, ROASTER
    }

    private val hypePhrases = listOf(
        "Let's GO! Time to CONQUER this!",
        "YOU'VE GOT THIS! Crush it RIGHT NOW!",
        "RISE AND GRIND! This task won't complete itself!",
        "You're a BEAST. Prove it. Do this NOW!",
        "Champions don't wait. Legends act. BE LEGENDARY!",
        "ZERO EXCUSES. MAXIMUM RESULTS. Let's get it!",
        "Your future self is CHEERING for you. Don't let them down!",
        "This is YOUR moment. Own it! SMASH that task!"
    )

    private val roasterPhrases = listOf(
        "Snoozing again? Your future self is cringing. Do it NOW.",
        "Oh look, another snooze. At this rate, you'll finish... never.",
        "Your to-do list is laughing at you. Prove it wrong.",
        "Even your pet thinks you're lazy. Don't let them be right.",
        "Plot twist: successful people don't snooze their tasks. Just saying.",
        "Breaking news: local human discovers snooze button for 4th time. Embarrassing.",
        "Your goals called. They're concerned about your commitment.",
        "Time is the one thing you can't get back. Stop wasting it."
    )

    private val snoozeRoasterPhrases = listOf(
        "AGAIN?! Your future self filed a restraining order against your excuses.",
        "This is getting sad. The task is still there. YOU are still procrastinating.",
        "Multiple snoozes? Your goals are formally disowning you.",
        "At this pace, your obituary will read: Died mid-snooze.",
        "WAKE UP. The world isn't going to wait for you."
    )

    fun getAlarmPhrase(mode: Mode, snoozeCount: Int): String {
        return when (mode) {
            Mode.HYPE_MAN -> hypePhrases.random()
            Mode.ROASTER -> {
                if (snoozeCount >= 2) snoozeRoasterPhrases.random()
                else roasterPhrases.random()
            }
        }
    }

    fun getModeFromString(value: String): Mode {
        return if (value == "roaster") Mode.ROASTER else Mode.HYPE_MAN
    }
}
