package dev.set17.earlygame.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.set17.tftacademy.champion.ChampionData
import dev.set17.tftacademy.model.Comp

@Composable
fun CompDetailPanel(
    comp: Comp,
    onBack: (() -> Unit)? = null,
    backLabel: String = "\u2190 Back",
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onBack != null) {
            val align = if (backLabel == "\u2715") Modifier.fillMaxWidth() else Modifier
            Text(
                text = backLabel,
                color = TftColors.chipSelectedBorder,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                textAlign = if (backLabel == "\u2715") androidx.compose.ui.text.style.TextAlign.End else null,
                modifier = align.clickable(onClick = onBack).padding(bottom = 4.dp),
            )
        }
        // Header
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TierBadge(comp.tier)
            Text(
                text = comp.title,
                color = TftColors.textPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Text(
            text = "${comp.style}  |  ${comp.difficulty}",
            color = TftColors.textSecondary,
            fontSize = 13.sp,
        )

        HorizontalDivider(color = TftColors.border)

        // Final board
        SectionHeader("Final Board")
        val realChamps = comp.finalComp.filter { it.apiName in ChampionData.champions }
        val activated = ChampionData.activatedTraits(realChamps.map { it.apiName })
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            for ((trait, pair) in activated.entries.sortedByDescending { it.value.first }) {
                val (count, _) = pair
                Text(
                    text = "$count $trait",
                    color = TftColors.textSecondary,
                    fontSize = 11.sp,
                )
            }
        }
        Spacer(Modifier.height(4.dp))
        for (champ in comp.finalComp) {
            val info = ChampionData.champions[champ.apiName] ?: continue
            val name = info.name
            val cost = info.cost
            val stars = if (champ.stars > 1) " \u2605".repeat(champ.stars) else ""
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$name$stars",
                    color = TftColors.costColor(cost),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.widthIn(min = 120.dp),
                )
                for (item in champ.items) {
                    ItemIcon(apiName = item, modifier = Modifier.height(20.dp))
                }
            }
        }

        // Early comp
        val earlyChamps = comp.earlyComp.filter { it.apiName in ChampionData.champions }
        if (earlyChamps.isNotEmpty()) {
            HorizontalDivider(color = TftColors.border)
            SectionHeader("Early Comp")
            Text(
                text = earlyChamps.joinToString(", ") { ChampionData.displayName(it.apiName) },
                color = TftColors.textSecondary,
                fontSize = 13.sp,
            )
        }

        // Carousel priority
        if (comp.carousel.isNotEmpty()) {
            HorizontalDivider(color = TftColors.border)
            SectionHeader("Carousel Priority")
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                comp.carousel.forEachIndexed { i, item ->
                    if (i > 0) {
                        Text(">", color = TftColors.textMuted, fontSize = 12.sp)
                    }
                    ItemIcon(apiName = item, modifier = Modifier.height(28.dp))
                }
            }
        }

        // Tips
        if (comp.tips.isNotEmpty()) {
            HorizontalDivider(color = TftColors.border)
            SectionHeader("Tips")
            for (tip in comp.tips) {
                Text(
                    text = tip.stage,
                    color = TftColors.textPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = tip.tip,
                    color = TftColors.textSecondary,
                    fontSize = 12.sp,
                )
                Spacer(Modifier.height(4.dp))
            }
        }

        // Augment tip
        if (comp.augmentsTip.isNotBlank()) {
            HorizontalDivider(color = TftColors.border)
            SectionHeader("Notes")
            Text(
                text = comp.augmentsTip,
                color = TftColors.textSecondary,
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        color = TftColors.textMuted,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp,
    )
}
