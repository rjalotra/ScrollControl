package com.scrollcontrol.app.data

import kotlin.random.Random

data class MotivationalQuote(
    val quote: String,
    val author: String,
    val category: String = "Focus"
)

object MotivationalQuotes {

    val quotes = listOf(
        MotivationalQuote(
            quote = "You have power over your mind, not outside events. Realize this, and you will find strength.",
            author = "Marcus Aurelius",
            category = "Self-Mastery"
        ),
        MotivationalQuote(
            quote = "Waste no more time arguing what a good person should be. Be one.",
            author = "Marcus Aurelius",
            category = "Action"
        ),
        MotivationalQuote(
            quote = "Life is long if you know how to use it wisely.",
            author = "Seneca",
            category = "Time"
        ),
        MotivationalQuote(
            quote = "No person is free who is not master of oneself.",
            author = "Epictetus",
            category = "Self-Mastery"
        ),
        MotivationalQuote(
            quote = "First say to yourself what you would be, and then do what you have to do.",
            author = "Epictetus",
            category = "Intention"
        ),
        MotivationalQuote(
            quote = "He who fears death will never do anything worthy of a person who is alive.",
            author = "Seneca",
            category = "Courage"
        ),
        MotivationalQuote(
            quote = "It is not that we have a short time to live, but that we waste a lot of it.",
            author = "Seneca",
            category = "Time"
        ),
        MotivationalQuote(
            quote = "Protect your focus today so you can build your future tomorrow.",
            author = "ScrollControl",
            category = "Focus"
        ),
        MotivationalQuote(
            quote = "Step away from the endless feed. Your real priorities await your attention.",
            author = "ScrollControl",
            category = "Mindfulness"
        ),
        MotivationalQuote(
            quote = "Small moments of discipline add up to lifetime achievements.",
            author = "ScrollControl",
            category = "Discipline"
        ),
        MotivationalQuote(
            quote = "Attention is finite. Spend it on what genuinely matters.",
            author = "ScrollControl",
            category = "Focus"
        ),
        MotivationalQuote(
            quote = "Take a breath, reset your intention, and return to what matters.",
            author = "ScrollControl",
            category = "Mindfulness"
        )
    )

    fun getRandomQuote(excludeIndex: Int? = null): MotivationalQuote {
        if (quotes.isEmpty()) {
            return MotivationalQuote(
                quote = "Protect your focus, protect your future.",
                author = "ScrollControl",
                category = "Focus"
            )
        }
        if (quotes.size == 1 || excludeIndex == null) {
            return quotes[Random.nextInt(quotes.size)]
        }
        var newIndex = Random.nextInt(quotes.size)
        while (newIndex == excludeIndex) {
            newIndex = Random.nextInt(quotes.size)
        }
        return quotes[newIndex]
    }
}
