package dev.set17.tftacademy.api

import dev.set17.tftacademy.db.DbMapper
import dev.set17.tftacademy.db.DriverFactory
import dev.set17.tftacademy.db.TftAcademyDatabase
import dev.set17.tftacademy.db.createDatabase
import dev.set17.tftacademy.item.ItemComponentMap
import dev.set17.tftacademy.model.*
import dev.set17.tftacademy.network.TftAcademyClient
import dev.set17.tftacademy.parser.SvelteKitDataParser

class TftAcademyRepository(
    private val client: TftAcademyClient,
    private val driverFactory: DriverFactory,
) {
    private val parser = SvelteKitDataParser()
    private var database: TftAcademyDatabase? = null
    private var _dbMapper: DbMapper? = null

    private suspend fun db(): DbMapper {
        if (_dbMapper == null) {
            database = createDatabase(driverFactory)
            _dbMapper = DbMapper(database!!)
        }
        return _dbMapper!!
    }

    suspend fun refresh(set: Int = 17): Int {
        val rawJson = client.fetchTierListData(set)
        val comps = parser.parse(rawJson)
        db().replaceAll(comps)
        db().seedItemRecipes(ItemComponentMap.recipes)
        return comps.size
    }

    suspend fun hasData(): Boolean = db().compCount() > 0

    suspend fun getAllComps(): List<CompSummary> = db().readAllSummaries()

    suspend fun getComp(compSlug: String): Comp? = db().readFullComp(compSlug)

    suspend fun getCompsByTier(tier: Tier): List<CompSummary> = db().readSummariesByTier(tier.name)

    suspend fun getCompsByDifficulty(difficulty: Difficulty): List<CompSummary> =
        db().readSummariesByDifficulty(difficulty.name)

    suspend fun getCompsByStyle(style: String): List<CompSummary> = db().readSummariesByStyle(style)

    suspend fun getCompsForChampion(apiName: String): List<CompSummary> =
        db().readSummariesByChampion(apiName)

    suspend fun getCompsForItem(itemApiName: String): List<CompSummary> =
        db().readSummariesByItem(itemApiName)

    suspend fun getCompsForComponent(componentApiName: String): List<CompSummary> =
        db().readSummariesByComponent(componentApiName)

    suspend fun getCompsForChampionInRole(apiName: String, role: String): List<CompSummary> =
        db().readSummariesByChampionAndRole(apiName, role)

    suspend fun getChampionNamesForRole(role: String): List<String> =
        db().readDistinctChampionNamesByRole(role)
}
