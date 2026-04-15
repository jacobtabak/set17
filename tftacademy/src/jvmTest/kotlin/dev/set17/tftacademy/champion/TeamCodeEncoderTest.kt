package dev.set17.tftacademy.champion

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TeamCodeEncoderTest {

    private fun apiNames(vararg names: String) = names.map { "TFT17_$it" }

    // Verified real team codes

    @Test
    fun `encode Illaoi Mordekaiser Viktor Nami Rhaast Meepsie Pyke Lissandra`() {
        val result = TeamCodeEncoder.encode(
            apiNames("Illaoi", "Mordekaiser", "Viktor", "Nami", "Rhaast", "Meepsie", "Pyke", "Lissandra")
        )
        assertEquals("0201104e02e03702101402d046000000TFTSet17", result)
    }

    @Test
    fun `encode Rammus Kindred Fiora Shen Maokai Ornn Akali Aatrox Caitlyn`() {
        val result = TeamCodeEncoder.encode(
            apiNames("Rammus", "Kindred", "Fiora", "Shen", "Maokai", "Ornn", "Akali", "Aatrox", "Caitlyn")
        )
        assertEquals("0201801f01303b01e03600d01d01b000TFTSet17", result)
    }

    @Test
    fun `encode Leblanc Nunu Karma Leona Illaoi Meepsie Mordekaiser Zoe`() {
        val result = TeamCodeEncoder.encode(
            apiNames("Leblanc", "Nunu", "Karma", "Leona", "Illaoi", "Meepsie", "Mordekaiser", "Zoe")
        )
        assertEquals("0204403902204201101404e041000000TFTSet17", result)
    }

    @Test
    fun `encode MissFortune Viktor Ornn Rhaast Maokai Gragas Aatrox`() {
        val result = TeamCodeEncoder.encode(
            apiNames("MissFortune", "Viktor", "Ornn", "Rhaast", "Maokai", "Gragas", "Aatrox")
        )
        assertEquals("0200102e03602101e03101d000000000TFTSet17", result)
    }

    @Test
    fun `encode Belveth Reksai Kindred Akali Maokai Aatrox Briar Caitlyn`() {
        val result = TeamCodeEncoder.encode(
            apiNames("Belveth", "Reksai", "Kindred", "Akali", "Maokai", "Aatrox", "Briar", "Caitlyn")
        )
        assertEquals("0200f06801f00d01e01d00e01b000000TFTSet17", result)
    }

    @Test
    fun `encode Meepsie Rhaast Fizz Kaisa Ornn Karma Rammus Riven`() {
        val result = TeamCodeEncoder.encode(
            apiNames("Meepsie", "Rhaast", "Fizz", "Kaisa", "Ornn", "Karma", "Rammus", "Riven")
        )
        assertEquals("0201402101502003602201803c000000TFTSet17", result)
    }

    @Test
    fun `encode Morgana alone - verified from game client`() {
        val result = TeamCodeEncoder.encode(apiNames("Morgana"))
        assertEquals("02058000000000000000000000000000TFTSet17", result)
    }

    // Edge cases

    @Test
    fun `empty list produces all-zero slots`() {
        val result = TeamCodeEncoder.encode(emptyList())
        assertEquals("02000000000000000000000000000000TFTSet17", result)
    }

    @Test
    fun `unknown apiNames are skipped`() {
        val result = TeamCodeEncoder.encode(listOf("TFT17_Morgana", "Relic_1", "Flex", "Unknown"))
        assertEquals("02058000000000000000000000000000TFTSet17", result)
    }

    @Test
    fun `more than 10 champions truncates to 10`() {
        val champs = apiNames(
            "Aatrox", "Akali", "Bard", "Briar", "Caitlyn",
            "Corki", "Diana", "Ezreal", "Fiora", "Fizz", "Gnar"
        )
        val result = TeamCodeEncoder.encode(champs)
        // 11 champions, only first 10 encoded, no empty slots
        assertTrue(result.matches(Regex("^02[0-9a-f]{30}TFTSet17$")))
        // Gnar (11th) should not appear
        val champHex = result.removePrefix("02").removeSuffix("TFTSet17")
        assertEquals(30, champHex.length)
        // Verify no empty slots (all 10 filled)
        assertFalse(champHex.endsWith("000"))
    }

    @Test
    fun `output format matches expected pattern`() {
        val result = TeamCodeEncoder.encode(apiNames("Aatrox"))
        assertTrue(result.matches(Regex("^02[0-9a-f]{30}TFTSet17$")))
    }

    private fun assertFalse(condition: Boolean) {
        assertEquals(false, condition)
    }
}
