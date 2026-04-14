package dev.set17.tftacademy.db

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.async.coroutines.awaitAsOneOrNull
import dev.set17.tftacademy.model.Augment
import dev.set17.tftacademy.model.Champion
import dev.set17.tftacademy.model.CompSummary
import dev.set17.tftacademy.model.Difficulty
import dev.set17.tftacademy.model.MainChampion
import dev.set17.tftacademy.model.MaxCapChampion
import dev.set17.tftacademy.model.Tier
import dev.set17.tftacademy.model.Tip
import dev.set17.tftacademy.parser.*
import dev.set17.tftacademy.model.Comp as DomainComp

class DbMapper(private val database: TftAcademyDatabase) {

    private val q get() = database.tftAcademyQueries

    suspend fun replaceAll(comps: List<ParsedComp>) {
        database.transaction {
            q.deleteAllComps()
            comps.forEach { insertComp(it) }
        }
    }

    suspend fun seedItemRecipes(recipes: Map<String, Pair<String, String>>) {
        database.transaction {
            q.deleteAllRecipes()
            for ((completed, components) in recipes) {
                q.insertItemRecipe(completed, components.first)
                if (components.first != components.second) {
                    q.insertItemRecipe(completed, components.second)
                }
            }
        }
    }

    private suspend fun insertComp(comp: ParsedComp) {
        q.insertComp(
            id = comp.id,
            title = comp.title,
            tier = comp.tier,
            style = comp.style,
            difficulty = comp.difficulty,
            comp_slug = comp.compSlug,
            set_number = comp.set.toLong(),
            created = comp.created,
            updated = comp.updated,
            augments_tip = comp.augmentsTip,
            main_champion_api_name = comp.mainChampionApiName,
            main_champion_cost = comp.mainChampionCost.toLong(),
        )

        insertChampions(comp.id, "final", comp.finalComp)
        insertChampions(comp.id, "early", comp.earlyComp)
        insertChampions(comp.id, "alt", comp.altBuilds)
        insertMaxCapChampions(comp.id, comp.maxCap)

        comp.augments.forEachIndexed { i, aug ->
            q.insertCompAugment(comp.id, aug.apiName, if (aug.disabled) 1L else 0L, i.toLong())
        }
        comp.augmentTypes.forEach { type ->
            q.insertCompAugmentType(comp.id, type)
        }
        comp.carousel.forEachIndexed { i, item ->
            q.insertCompCarousel(comp.id, item, i.toLong())
        }
        comp.tips.forEachIndexed { i, tip ->
            q.insertCompTip(comp.id, tip.stage, tip.tip, i.toLong())
        }
    }

    private suspend fun insertChampions(compId: String, role: String, champions: List<ParsedChampion>) {
        champions.forEachIndexed { i, champ ->
            q.insertCompChampion(
                comp_id = compId,
                role = role,
                api_name = champ.apiName,
                stars = champ.stars.toLong(),
                board_index = champ.boardIndex?.toLong(),
                position_in_list = i.toLong(),
            )
            val champId = q.lastInsertRowId().awaitAsOne()
            champ.items.forEachIndexed { j, item ->
                q.insertChampionItem(champId, item, j.toLong())
            }
        }
    }

    private suspend fun insertMaxCapChampions(compId: String, maxCap: List<ParsedMaxCapChampion>) {
        maxCap.forEachIndexed { i, champ ->
            q.insertCompChampion(
                comp_id = compId,
                role = "maxcap",
                api_name = champ.apiName,
                stars = champ.stars.toLong(),
                board_index = null,
                position_in_list = i.toLong(),
            )
            val champId = q.lastInsertRowId().awaitAsOne()
            champ.items.forEachIndexed { j, item ->
                q.insertChampionItem(champId, item, j.toLong())
            }
            champ.predecessors.forEach { pred ->
                q.insertMaxcapPredecessor(champId, pred)
            }
        }
    }

    suspend fun readAllSummaries(): List<CompSummary> {
        return q.selectAllComps().awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByTier(tier: String): List<CompSummary> {
        return q.selectCompsByTier(tier).awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByDifficulty(difficulty: String): List<CompSummary> {
        return q.selectCompsByDifficulty(difficulty).awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByStyle(style: String): List<CompSummary> {
        return q.selectCompsByStyle(style).awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByChampion(apiName: String): List<CompSummary> {
        return q.selectCompsByChampion(apiName).awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByItem(itemApiName: String): List<CompSummary> {
        return q.selectCompsByItem(itemApiName).awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByComponent(componentApiName: String): List<CompSummary> {
        return q.selectCompsByComponent(componentApiName).awaitAsList().map { it.toSummary() }
    }

    suspend fun readSummariesByChampionAndRole(apiName: String, role: String): List<CompSummary> {
        return q.selectCompsByChampionAndRole(apiName, role).awaitAsList().map { it.toSummary() }
    }

    suspend fun readDistinctChampionNamesByRole(role: String): List<String> {
        return q.selectDistinctChampionNamesByRole(role).awaitAsList()
    }

    suspend fun readFullComp(compSlug: String): DomainComp? {
        val row = q.selectCompBySlug(compSlug).awaitAsOneOrNull() ?: return null
        return assembleComp(row)
    }

    suspend fun compCount(): Long = q.compCount().awaitAsOne()

    private suspend fun assembleComp(row: Comp): DomainComp {
        val id = row.id
        return DomainComp(
            id = id,
            title = row.title,
            tier = Tier.valueOf(row.tier),
            style = row.style,
            difficulty = Difficulty.valueOf(row.difficulty),
            compSlug = row.comp_slug,
            set = row.set_number.toInt(),
            created = row.created,
            updated = row.updated,
            augmentsTip = row.augments_tip,
            mainChampion = MainChampion(row.main_champion_api_name, row.main_champion_cost.toInt()),
            earlyComp = loadChampions(id, "early"),
            finalComp = loadChampions(id, "final"),
            altBuilds = loadChampions(id, "alt"),
            maxCap = loadMaxCapChampions(id),
            augments = q.selectAugmentsForComp(id).awaitAsList().map {
                Augment(it.api_name, it.disabled != 0L)
            },
            augmentTypes = q.selectAugmentTypesForComp(id).awaitAsList(),
            carousel = q.selectCarouselForComp(id).awaitAsList().map { it.api_name },
            tips = q.selectTipsForComp(id).awaitAsList().map { Tip(it.stage, it.tip) },
        )
    }

    private suspend fun loadChampions(compId: String, role: String): List<Champion> {
        return q.selectChampionsForComp(compId, role).awaitAsList().map { cc ->
            val items = q.selectItemsForChampion(cc.id).awaitAsList().map { it.api_name }
            Champion(cc.api_name, cc.stars.toInt(), cc.board_index?.toInt(), items)
        }
    }

    private suspend fun loadMaxCapChampions(compId: String): List<MaxCapChampion> {
        return q.selectChampionsForComp(compId, "maxcap").awaitAsList().map { cc ->
            val items = q.selectItemsForChampion(cc.id).awaitAsList().map { it.api_name }
            val preds = q.selectPredecessors(cc.id).awaitAsList().map { it.api_name }
            MaxCapChampion(cc.api_name, cc.stars.toInt(), items, preds)
        }
    }

    private fun Comp.toSummary() = CompSummary(
        id = id,
        title = title,
        tier = Tier.valueOf(tier),
        style = style,
        difficulty = Difficulty.valueOf(difficulty),
        compSlug = comp_slug,
        mainChampionApiName = main_champion_api_name,
    )
}
