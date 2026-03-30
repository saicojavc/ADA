package com.saico.ada.domain.service

import com.saico.ada.domain.model.EventCategory
import java.util.Locale

object EventCategorizer {

    private val workKeywords = listOf("meeting", "deadline", "project", "office", "call", "client", "work", "presentation", "standup")
    private val homeKeywords = listOf("grocery", "cleaning", "dinner", "family", "kids", "repair", "laundry", "cook", "house")
    private val wellnessKeywords = listOf("yoga", "gym", "meditation", "walk", "spa", "doctor", "health", "exercise", "workout", "therapy", "rest")
    private val maternityKeywords = listOf("baby", "prenatal", "pediatrician", "nursery", "pregnancy", "breastfeeding", "diaper", "midwife")

    fun categorize(title: String, description: String): EventCategory {
        val text = "$title $description".lowercase(Locale.getDefault())

        return when {
            maternityKeywords.any { text.contains(it) } -> EventCategory.MATERNITY
            wellnessKeywords.any { text.contains(it) } -> EventCategory.WELLNESS
            workKeywords.any { text.contains(it) } -> EventCategory.WORK
            homeKeywords.any { text.contains(it) } -> EventCategory.HOME
            else -> EventCategory.UNCATEGORIZED
        }
    }
}
