package ru.netology.nmedia.util

object MyUtils {
    fun numToShortString(value: Int): String {
        return when {
            value < 10_000 -> {
                val thousands = value / 1_000
                val hundreds = (value % 1_000) / 100

                if (thousands == 0) {
                    value.toString()
                } else if (hundreds == 0) {
                    "${thousands}K"  // Убираем точку и ноль, если сотни = 0
                } else {
                    "${thousands}.${hundreds}K"
                }
            }

            value in 10_000 until 1_000_000 -> {
                "${value / 1_000}K"  // Всегда без дробной части
            }

            else -> {
                val millions = value / 1_000_000
                val hundredThousands = (value % 1_000_000) / 100_000

                if (hundredThousands == 0) {
                    "${millions}M"  // Убираем точку и ноль, если сотни тысяч = 0
                } else {
                    "${millions}.${hundredThousands}M"
                }
            }
        }
    }
}