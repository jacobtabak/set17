package dev.set17.tftacademy.parser

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SvelteKitDataParserTest {

    private val parser = SvelteKitDataParser()

    private fun loadSampleJson(): String {
        return this::class.java.getResource("/raw_data_sample.json")!!.readText()
    }

    @Test
    fun `parses all comps`() {
        val comps = parser.parse(loadSampleJson())
        assertTrue(comps.size >= 40, "Expected at least 40 comps, got ${comps.size}")
    }

    @Test
    fun `parses Vex 9-5 correctly`() {
        val comps = parser.parse(loadSampleJson())
        val vex = comps.first { it.compSlug == "set-17-vex-9-5" }

        assertEquals("Vex 9-5", vex.title)
        assertEquals("S", vex.tier)
        assertEquals("Fast 9", vex.style)
        assertEquals("HARD", vex.difficulty)
        assertEquals(17, vex.set)
        assertEquals("TFT17_Vex", vex.mainChampionApiName)
        assertEquals(5, vex.mainChampionCost)
        assertTrue(vex.finalComp.isNotEmpty())
        assertTrue(vex.tips.isNotEmpty())
        assertTrue(vex.augments.isNotEmpty())
    }

    @Test
    fun `parses finalComp champions with items`() {
        val comps = parser.parse(loadSampleJson())
        val vex = comps.first { it.compSlug == "set-17-vex-9-5" }

        val vexChamp = vex.finalComp.first { it.apiName == "TFT17_Vex" }
        assertTrue(vexChamp.items.isNotEmpty(), "Vex should have items")
        assertTrue(vexChamp.items.contains("TFT_Item_GuinsoosRageblade"))
        assertTrue(vexChamp.boardIndex != null, "Final comp units should have board positions")
    }

    @Test
    fun `parses NOVA Marauders with maxCap predecessors`() {
        val comps = parser.parse(loadSampleJson())
        val nova = comps.first { it.compSlug == "set-17-nova-yi" }

        assertEquals("S", nova.tier)
        assertTrue(nova.maxCap.isNotEmpty(), "NOVA should have maxCap entries")
        val fiora = nova.maxCap.first { it.apiName == "TFT17_Fiora" }
        assertTrue(fiora.predecessors.contains("TFT17_MasterYi"))
    }

    @Test
    fun `all comps have required fields`() {
        val comps = parser.parse(loadSampleJson())
        for (comp in comps) {
            assertTrue(comp.id.isNotBlank(), "${comp.title}: id is blank")
            assertTrue(comp.title.isNotBlank(), "title is blank")
            assertTrue(comp.tier in setOf("S", "A", "B", "C", "X"), "${comp.title}: unexpected tier ${comp.tier}")
            assertTrue(comp.difficulty in setOf("EASY", "MEDIUM", "HARD", "CONDITIONAL"), "${comp.title}: unexpected difficulty ${comp.difficulty}")
            assertTrue(comp.finalComp.isNotEmpty(), "${comp.title}: finalComp is empty")
            assertTrue(comp.mainChampionApiName.isNotBlank(), "${comp.title}: mainChampion is blank")
        }
    }

    @Test
    fun `parses augments correctly`() {
        val comps = parser.parse(loadSampleJson())
        val vex = comps.first { it.compSlug == "set-17-vex-9-5" }

        assertTrue(vex.augmentTypes.contains("ECON"))
        assertTrue(vex.augments.any { it.apiName.contains("Augment") })
    }

    @Test
    fun `parses carousel items`() {
        val comps = parser.parse(loadSampleJson())
        val vex = comps.first { it.compSlug == "set-17-vex-9-5" }

        assertTrue(vex.carousel.isNotEmpty(), "Should have carousel priority items")
        assertTrue(vex.carousel.all { it.startsWith("TFT_Item_") })
    }

    @Test
    fun `parses stage tips`() {
        val comps = parser.parse(loadSampleJson())
        val vex = comps.first { it.compSlug == "set-17-vex-9-5" }

        assertTrue(vex.tips.size >= 3, "Should have at least 3 stage tips")
        assertTrue(vex.tips.any { it.stage == "Stage 2" })
    }
}
