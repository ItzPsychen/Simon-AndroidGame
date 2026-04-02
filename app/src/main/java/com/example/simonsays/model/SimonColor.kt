package com.example.simonsays.model

enum class SimonColor(val colorRes: Int, val label: String) {
    RED(0xFFFF5252.toInt(), "R"),
    GREEN(0xFF4CAF50.toInt(), "G"),
    BLUE(0xFF2196F8.toInt(), "B"),
    MAGENTA(0xFF9C27B0.toInt(), "M"),
    YELLOW(0xFFFFEB3B.toInt(), "Y"),
    CYAN(0xFF00BCD0.toInt(), "C")
}
