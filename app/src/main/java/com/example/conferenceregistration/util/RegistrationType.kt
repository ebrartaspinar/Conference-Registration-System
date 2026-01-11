package com.example.conferenceregistration.util

/**
 * Enum class for Registration Types with associated colors.
 */
enum class RegistrationType(val value: Int, val colorResId: Int) {
    FULL(1, android.R.color.holo_green_light),
    STUDENT(2, android.R.color.holo_blue_light),
    NONE(3, android.R.color.holo_orange_light);

    companion object {
        fun fromValue(value: Int): RegistrationType {
            return entries.find { it.value == value } ?: NONE
        }
    }
}
