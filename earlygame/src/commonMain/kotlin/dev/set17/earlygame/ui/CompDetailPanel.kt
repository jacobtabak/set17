package dev.set17.earlygame.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import dev.set17.tftacademy.champion.ChampionData
import dev.set17.tftacademy.champion.TeamCodeEncoder
import dev.set17.tftacademy.model.Comp

@Composable
fun CompDetailPanel(
    comp: Comp,
    onBack: (() -> Unit)? = null,
    useCloseIcon: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (onBack != null) {
            val alignment = if (useCloseIcon) Alignment.CenterEnd else Alignment.CenterStart
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = if (useCloseIcon) Icons.Filled.Close else Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = if (useCloseIcon) "Close" else "Back",
                        tint = TftColors.chipSelectedBorder,
                    )
                }
            }
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
        val realChamps = comp.finalComp.filter { it.apiName in ChampionData.champions }
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionHeader("Final Board")
            Spacer(Modifier.weight(1f))
            CopyTeamCodeButton(realChamps.map { it.apiName })
        }
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
        Row {
            // Name column — intrinsic width matches the longest name
            Column(modifier = Modifier.width(IntrinsicSize.Max)) {
                for (champ in comp.finalComp) {
                    val info = ChampionData.champions[champ.apiName] ?: continue
                    Box(modifier = Modifier.fillMaxWidth().height(28.dp), contentAlignment = Alignment.CenterStart) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = info.name,
                                color = TftColors.costColor(info.cost),
                                fontSize = 13.sp,
                            )
                            if (champ.stars > 1) {
                                repeat(champ.stars) {
                                    Icon(
                                        imageVector = Icons.Filled.Star,
                                        contentDescription = null,
                                        tint = TftColors.costColor(info.cost),
                                        modifier = Modifier.size(10.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.width(12.dp))
            // Items column
            Column {
                for (champ in comp.finalComp) {
                    if (champ.apiName !in ChampionData.champions) continue
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.height(28.dp),
                    ) {
                        for (item in champ.items) {
                            ItemIcon(apiName = item, modifier = Modifier.height(20.dp))
                        }
                    }
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

@Composable
private fun CopyTeamCodeButton(championApiNames: List<String>) {
    val clipboardManager = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Text(
        text = if (copied) "Copied!" else "Copy Team Code",
        color = if (copied) TftColors.matchedChampion else TftColors.chipSelectedBorder,
        fontSize = 11.sp,
        modifier = Modifier.pointerHoverIcon(PointerIcon.Hand).clickable {
            clipboardManager.setText(AnnotatedString(TeamCodeEncoder.encode(championApiNames)))
            copied = true
        },
    )

    LaunchedEffect(copied) {
        if (copied) {
            kotlinx.coroutines.delay(2000)
            copied = false
        }
    }
}
