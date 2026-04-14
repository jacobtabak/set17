package dev.set17.tftacademy.integration

import dev.set17.tftacademy.api.TftAcademyRepository
import dev.set17.tftacademy.db.DriverFactory
import dev.set17.tftacademy.network.TftAcademyClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class CompDetailTest {

    @Test
    fun `print S tier comp details`() = runTest {
        val repo = TftAcademyRepository(TftAcademyClient(HttpClient(CIO)), DriverFactory())
        repo.refresh()

        val comp = repo.getComp("set-17-nova-yi")!!

        println("╔══════════════════════════════════════════╗")
        println("║  ${comp.title}")
        println("╠══════════════════════════════════════════╣")
        println("║  Tier: ${comp.tier}  |  Style: ${comp.style}  |  Difficulty: ${comp.difficulty}")
        println("║  Main carry: ${comp.mainChampion.apiName.removePrefix("TFT17_")} (${comp.mainChampion.cost}-cost)")
        println("╠══════════════════════════════════════════╣")
        println("║  FINAL BOARD")
        for (c in comp.finalComp) {
            val name = c.apiName.removePrefix("TFT17_").removePrefix("TFT_")
            val stars = "*".repeat(c.stars)
            val items = c.items.joinToString(", ") { it.removePrefix("TFT_Item_").removePrefix("TFT17_Item_") }
            val itemStr = if (items.isNotEmpty()) "  ->  $items" else ""
            val pos = if (c.boardIndex != null) " [pos ${c.boardIndex}]" else ""
            println("║    $name $stars$pos$itemStr")
        }
        println("╠══════════════════════════════════════════╣")
        println("║  EARLY COMP")
        for (c in comp.earlyComp) {
            val name = c.apiName.removePrefix("TFT17_").removePrefix("TFT_")
            println("║    $name")
        }
        if (comp.maxCap.isNotEmpty()) {
            println("╠══════════════════════════════════════════╣")
            println("║  MAX CAP (Level 9 upgrades)")
            for (c in comp.maxCap) {
                val name = c.apiName.removePrefix("TFT17_")
                val replaces = c.predecessors.joinToString(", ") { it.removePrefix("TFT17_") }
                val items = c.items.joinToString(", ") { it.removePrefix("TFT_Item_").removePrefix("TFT17_Item_") }
                println("║    $name (replaces $replaces)  ->  $items")
            }
        }
        if (comp.altBuilds.isNotEmpty()) {
            println("╠══════════════════════════════════════════╣")
            println("║  ALT BUILDS")
            for (c in comp.altBuilds) {
                val name = c.apiName.removePrefix("TFT17_").removePrefix("TFT_")
                val items = c.items.joinToString(", ") { it.removePrefix("TFT_Item_").removePrefix("TFT17_Item_") }
                val itemStr = if (items.isNotEmpty()) "  ->  $items" else ""
                println("║    $name$itemStr")
            }
        }
        println("╠══════════════════════════════════════════╣")
        println("║  CAROUSEL PRIORITY")
        println("║    ${comp.carousel.joinToString(" > ") { it.removePrefix("TFT_Item_") }}")
        println("╠══════════════════════════════════════════╣")
        println("║  AUGMENTS (${comp.augmentTypes.joinToString(" > ")})")
        for (a in comp.augments) {
            val name = a.apiName.removePrefix("TFT_Augment_").removePrefix("TFT6_Augment_").removePrefix("TFT7_Augment_").removePrefix("TFT9_Augment_").removePrefix("TFT11_Augment_").removePrefix("TFT16_Augment_")
            println("║    $name${if (a.disabled) " (disabled)" else ""}")
        }
        println("╠══════════════════════════════════════════╣")
        println("║  TIPS")
        for (t in comp.tips) {
            println("║  ${t.stage}: ${t.tip}")
        }
        println("║")
        if (comp.augmentsTip.isNotBlank()) {
            println("║  Note: ${comp.augmentsTip}")
        }
        println("╚══════════════════════════════════════════╝")
    }
}
