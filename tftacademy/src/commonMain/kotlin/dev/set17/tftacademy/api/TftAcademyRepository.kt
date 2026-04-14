package dev.set17.tftacademy.api

import dev.set17.tftacademy.db.DbMapper
import dev.set17.tftacademy.db.DriverFactory
import dev.set17.tftacademy.db.TftAcademyDatabase
import dev.set17.tftacademy.item.ItemComponentMap
import dev.set17.tftacademy.model.*
import dev.set17.tftacademy.network.TftAcademyClient
import dev.set17.tftacademy.parser.SvelteKitDataParser

class TftAcademyRepository(
    private val client: TftAcademyClient,
    driverFactory: DriverFactory,
) {
    private val parser = SvelteKitDataParser()
    private val database = TftAcademyDatabase(driverFactory.createDriver())
    private val dbMapper = DbMapper(database)

    /** Fetch latest data from TFT Academy, parse, and store in the local database. */
    suspend fun refresh(set: Int = 17): Int {
        val rawJson = client.fetchTierListData(set)
        val comps = parser.parse(rawJson)
        dbMapper.replaceAll(comps)
        dbMapper.seedItemRecipes(ItemComponentMap.recipes)
        return comps.size
    }

    fun hasData(): Boolean = dbMapper.compCount() > 0

    fun getAllComps(): List<CompSummary> = dbMapper.readAllSummaries()

    fun getComp(compSlug: String): Comp? = dbMapper.readFullComp(compSlug)

    fun getCompsByTier(tier: Tier): List<CompSummary> = dbMapper.readSummariesByTier(tier.name)

    fun getCompsByDifficulty(difficulty: Difficulty): List<CompSummary> =
        dbMapper.readSummariesByDifficulty(difficulty.name)

    fun getCompsByStyle(style: String): List<CompSummary> = dbMapper.readSummariesByStyle(style)

    fun getCompsForChampion(apiName: String): List<CompSummary> =
        dbMapper.readSummariesByChampion(apiName)

    fun getCompsForItem(itemApiName: String): List<CompSummary> =
        dbMapper.readSummariesByItem(itemApiName)

    fun getCompsForComponent(componentApiName: String): List<CompSummary> =
        dbMapper.readSummariesByComponent(componentApiName)

    fun getCompsForChampionInRole(apiName: String, role: String): List<CompSummary> =
        dbMapper.readSummariesByChampionAndRole(apiName, role)

    fun getChampionNamesForRole(role: String): List<String> =
        dbMapper.readDistinctChampionNamesByRole(role)
}
