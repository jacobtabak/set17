package dev.set17.tftacademy.integration

import dev.set17.tftacademy.db.DbMapper
import dev.set17.tftacademy.db.TftAcademyDatabase
import dev.set17.tftacademy.item.ItemComponentMap
import dev.set17.tftacademy.model.Difficulty
import dev.set17.tftacademy.model.Tier
import dev.set17.tftacademy.parser.SvelteKitDataParser
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlin.test.*

class FullRoundTripTest {

    private lateinit var database: TftAcademyDatabase
    private lateinit var dbMapper: DbMapper
    private val parser = SvelteKitDataParser()

    @BeforeTest
    fun setup() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        TftAcademyDatabase.Schema.create(driver)
        database = TftAcademyDatabase(driver)
        dbMapper = DbMapper(database)

        val json = this::class.java.getResource("/raw_data_sample.json")!!.readText()
        val comps = parser.parse(json)
        dbMapper.replaceAll(comps)
        dbMapper.seedItemRecipes(ItemComponentMap.recipes)
    }

    @Test
    fun `stores and retrieves all comps`() {
        val all = dbMapper.readAllSummaries()
        assertTrue(all.size >= 40, "Expected at least 40 comps, got ${all.size}")
    }

    @Test
    fun `comps are ordered by tier`() {
        val all = dbMapper.readAllSummaries()
        val tierOrder = all.map { it.tier }
        val sIndex = tierOrder.indexOfFirst { it == Tier.S }
        val xIndex = tierOrder.indexOfLast { it == Tier.X }
        assertTrue(sIndex < xIndex, "S tier should come before X tier")
    }

    @Test
    fun `reads full comp with all children`() {
        val comp = dbMapper.readFullComp("set-17-vex-9-5")
        assertNotNull(comp)
        assertEquals("Vex 9-5", comp.title)
        assertEquals(Tier.S, comp.tier)
        assertTrue(comp.finalComp.isNotEmpty())
        assertTrue(comp.tips.isNotEmpty())
        assertTrue(comp.augments.isNotEmpty())
        assertTrue(comp.carousel.isNotEmpty())

        val vex = comp.finalComp.first { it.apiName == "TFT17_Vex" }
        assertTrue(vex.items.isNotEmpty())
        assertNotNull(vex.boardIndex)
    }

    @Test
    fun `queries by tier`() {
        val sTier = dbMapper.readSummariesByTier("S")
        assertTrue(sTier.isNotEmpty())
        assertTrue(sTier.all { it.tier == Tier.S })
    }

    @Test
    fun `queries by difficulty`() {
        val easy = dbMapper.readSummariesByDifficulty("EASY")
        assertTrue(easy.isNotEmpty())
        assertTrue(easy.all { it.difficulty == Difficulty.EASY })
    }

    @Test
    fun `queries by champion`() {
        val vexComps = dbMapper.readSummariesByChampion("TFT17_Vex")
        assertTrue(vexComps.isNotEmpty(), "Vex should appear in at least one comp")
        assertTrue(vexComps.any { it.compSlug == "set-17-vex-9-5" })
    }

    @Test
    fun `queries by item`() {
        val guinsooComps = dbMapper.readSummariesByItem("TFT_Item_GuinsoosRageblade")
        assertTrue(guinsooComps.isNotEmpty(), "Guinsoo's should appear in at least one comp")
    }

    @Test
    fun `queries by base component`() {
        val bfSwordComps = dbMapper.readSummariesByComponent("TFT_Item_BFSword")
        assertTrue(bfSwordComps.isNotEmpty(), "BF Sword should be needed by at least one comp")

        val bowComps = dbMapper.readSummariesByComponent("TFT_Item_RecurveBow")
        assertTrue(bowComps.isNotEmpty(), "Recurve Bow should be needed by at least one comp")
    }

    @Test
    fun `maxcap champions have predecessors`() {
        val nova = dbMapper.readFullComp("set-17-nova-yi")
        assertNotNull(nova)
        assertTrue(nova.maxCap.isNotEmpty(), "NOVA should have maxCap entries")
        val fiora = nova.maxCap.first { it.apiName == "TFT17_Fiora" }
        assertTrue(fiora.predecessors.contains("TFT17_MasterYi"))
    }

    @Test
    fun `refresh replaces old data`() {
        val countBefore = dbMapper.compCount()
        val json = this::class.java.getResource("/raw_data_sample.json")!!.readText()
        val comps = parser.parse(json)
        dbMapper.replaceAll(comps)
        val countAfter = dbMapper.compCount()
        assertEquals(countBefore, countAfter, "Count should be the same after re-import")
    }

    @Test
    fun `queries by style`() {
        val fast8 = dbMapper.readSummariesByStyle("4-Cost Fast 8")
        assertTrue(fast8.isNotEmpty(), "Should have Fast 8 comps")
        assertTrue(fast8.all { it.style == "4-Cost Fast 8" })
    }
}
