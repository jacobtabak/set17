package dev.set17.earlygame.ui

import androidx.compose.ui.graphics.Color
import dev.set17.tftacademy.model.Tier

object TftColors {
    val background = Color(0xFF0A0A14)
    val surface = Color(0xFF14141E)
    val surfaceVariant = Color(0xFF1E1E2E)
    val border = Color(0xFF2A2A3A)
    val textPrimary = Color(0xFFE0E0E0)
    val textSecondary = Color(0xFF8888AA)
    val textMuted = Color(0xFF555566)

    val chipSelected = Color(0xFF2A4060)
    val chipSelectedBorder = Color(0xFF4488CC)
    val chipDefault = Color(0xFF1A1A28)
    val chipDefaultBorder = Color(0xFF333344)
    val chipDisabled = Color(0xFF111118)
    val chipDisabledText = Color(0xFF444455)

    val tierS = Color(0xFFFFD700)
    val tierA = Color(0xFFAA66FF)
    val tierB = Color(0xFF4488FF)
    val tierC = Color(0xFF44BB66)
    val tierX = Color(0xFF888888)

    fun tierColor(tier: Tier): Color = when (tier) {
        Tier.S -> tierS
        Tier.A -> tierA
        Tier.B -> tierB
        Tier.C -> tierC
        Tier.X -> tierX
    }

    val matchedChampion = Color(0xFF66BB6A)
    val missingChampion = Color(0xFFFF8A65)

    val cost1 = Color(0xFF9E9E9E)
    val cost2 = Color(0xFF4CAF50)
    val cost3 = Color(0xFF2196F3)
    val cost4 = Color(0xFFAB47BC)
    val cost5 = Color(0xFFFFD740)

    fun costColor(cost: Int): Color = when (cost) {
        1 -> cost1
        2 -> cost2
        3 -> cost3
        4 -> cost4
        5 -> cost5
        else -> textPrimary
    }
}
