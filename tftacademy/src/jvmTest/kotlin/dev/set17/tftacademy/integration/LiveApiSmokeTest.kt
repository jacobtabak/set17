package dev.set17.tftacademy.integration

import dev.set17.tftacademy.api.TftAcademyRepository
import dev.set17.tftacademy.db.DriverFactory
import dev.set17.tftacademy.model.Tier
import dev.set17.tftacademy.network.TftAcademyClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Smoke test that hits the live TFT Academy API.
 * Run manually: ./gradlew :tftacademy:jvmTest --tests "*LiveApiSmokeTest*"
 */
class LiveApiSmokeTest {

    @Test
    fun `full refresh and query`() = runTest {
        val client = TftAcademyClient(HttpClient(CIO))
        val repo = TftAcademyRepository(client, DriverFactory())

        val count = repo.refresh()
        println("Loaded $count comps")
        assertTrue(count >= 40)

        println("\n=== S Tier ===")
        repo.getCompsByTier(Tier.S).forEach {
            println("  ${it.title} (${it.style}, ${it.difficulty})")
        }

        println("\n=== Comps using TFT17_Vex ===")
        val vexComps = repo.getCompsForChampion("TFT17_Vex")
        vexComps.forEach { println("  ${it.title} [${it.tier}]") }
        assertTrue(vexComps.isNotEmpty())

        println("\n=== Comps needing BF Sword ===")
        val bfComps = repo.getCompsForComponent("TFT_Item_BFSword")
        bfComps.forEach { println("  ${it.title} [${it.tier}]") }
        assertTrue(bfComps.isNotEmpty())

        println("\n=== Full Vex 9-5 comp ===")
        val vex = repo.getComp("set-17-vex-9-5")!!
        println("  ${vex.title} | ${vex.tier} | ${vex.style} | ${vex.difficulty}")
        vex.finalComp.forEach { c ->
            val items = if (c.items.isNotEmpty()) " [${c.items.joinToString()}]" else ""
            println("    ${c.apiName} ${c.stars}*$items")
        }
    }
}
