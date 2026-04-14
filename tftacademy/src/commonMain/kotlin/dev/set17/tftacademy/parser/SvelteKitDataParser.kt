package dev.set17.tftacademy.parser

import kotlinx.serialization.json.*

/**
 * Parses the SvelteKit __data.json format from TFT Academy.
 *
 * The format uses an indexed/deduped encoding where a flat JsonArray holds all values,
 * and objects reference values by their index in that array.
 */
class SvelteKitDataParser {

    fun parse(rawJson: String): List<ParsedComp> {
        val root = Json.parseToJsonElement(rawJson).jsonObject
        val nodes = root["nodes"]!!.jsonArray
        val dataArray = nodes[2].jsonObject["data"]!!.jsonArray

        val guideIndices = dataArray[1].jsonArray.map { it.jsonPrimitive.int }
        return guideIndices.map { index ->
            parseGuide(dataArray[index].jsonObject, dataArray)
        }
    }

    private fun parseGuide(schema: JsonObject, data: JsonArray): ParsedComp {
        fun str(field: String): String = data[schema[field]!!.jsonPrimitive.int].jsonPrimitive.content
        fun int(field: String): Int = data[schema[field]!!.jsonPrimitive.int].jsonPrimitive.int
        fun bool(field: String): Boolean = data[schema[field]!!.jsonPrimitive.int].jsonPrimitive.boolean
        fun strOrNull(field: String): String? {
            val idx = schema[field]?.jsonPrimitive?.int ?: return null
            val el = data[idx]
            return if (el is JsonNull) null else el.jsonPrimitive.content
        }

        fun resolveStringList(field: String): List<String> {
            val arrIdx = schema[field]!!.jsonPrimitive.int
            return data[arrIdx].jsonArray.map { data[it.jsonPrimitive.int].jsonPrimitive.content }
        }

        fun resolveObj(field: String): JsonObject {
            val idx = schema[field]!!.jsonPrimitive.int
            return data[idx].jsonObject
        }

        fun resolveObjList(field: String): List<JsonObject> {
            val arrIdx = schema[field]!!.jsonPrimitive.int
            return data[arrIdx].jsonArray.map { data[it.jsonPrimitive.int].jsonObject }
        }

        fun parseChampion(obj: JsonObject): ParsedChampion {
            val apiName = data[obj["apiName"]!!.jsonPrimitive.int].jsonPrimitive.content
            val stars = data[obj["stars"]!!.jsonPrimitive.int].jsonPrimitive.int
            val boardIndex = obj["boardIndex"]?.jsonPrimitive?.int?.let { data[it].jsonPrimitive.int }
            val itemsArr = data[obj["items"]!!.jsonPrimitive.int].jsonArray
            val items = itemsArr.map { data[it.jsonPrimitive.int].jsonPrimitive.content }
            return ParsedChampion(apiName, stars, boardIndex, items)
        }

        fun parseMaxCapChampion(obj: JsonObject): ParsedMaxCapChampion {
            val apiName = data[obj["apiName"]!!.jsonPrimitive.int].jsonPrimitive.content
            val stars = data[obj["stars"]!!.jsonPrimitive.int].jsonPrimitive.int
            val itemsArr = data[obj["items"]!!.jsonPrimitive.int].jsonArray
            val items = itemsArr.map { data[it.jsonPrimitive.int].jsonPrimitive.content }
            val predsArr = obj["predecessors"]?.jsonPrimitive?.int?.let { data[it].jsonArray }
            val predecessors = predsArr?.map { data[it.jsonPrimitive.int].jsonPrimitive.content } ?: emptyList()
            return ParsedMaxCapChampion(apiName, stars, items, predecessors)
        }

        fun parseAugment(obj: JsonObject): ParsedAugment {
            val apiName = data[obj["apiName"]!!.jsonPrimitive.int].jsonPrimitive.content
            val disabled = data[obj["disabled"]!!.jsonPrimitive.int].jsonPrimitive.boolean
            return ParsedAugment(apiName, disabled)
        }

        fun parseTip(obj: JsonObject): ParsedTip {
            val stage = data[obj["stage"]!!.jsonPrimitive.int].jsonPrimitive.content
            val tip = data[obj["tip"]!!.jsonPrimitive.int].jsonPrimitive.content
            return ParsedTip(stage, tip)
        }

        val mainChampObj = resolveObj("mainChampion")
        val mainChampApiName = data[mainChampObj["apiName"]!!.jsonPrimitive.int].jsonPrimitive.content
        val mainChampCost = data[mainChampObj["cost"]!!.jsonPrimitive.int].jsonPrimitive.int

        val augmentTypeIndices = data[schema["augmentTypes"]!!.jsonPrimitive.int].jsonArray
        val augmentTypes = augmentTypeIndices.map { data[it.jsonPrimitive.int].jsonPrimitive.content }

        val carouselObjs = resolveObjList("carousel")
        val carouselItems = carouselObjs.map { data[it["apiName"]!!.jsonPrimitive.int].jsonPrimitive.content }

        return ParsedComp(
            id = str("id"),
            title = str("title"),
            tier = str("tier"),
            style = str("style"),
            difficulty = str("difficulty"),
            compSlug = str("compSlug"),
            set = int("set"),
            isPublic = bool("isPublic"),
            created = str("created"),
            updated = str("updated"),
            augmentsTip = str("augmentsTip"),
            mainChampionApiName = mainChampApiName,
            mainChampionCost = mainChampCost,
            earlyComp = resolveObjList("earlyComp").map(::parseChampion),
            finalComp = resolveObjList("finalComp").map(::parseChampion),
            altBuilds = resolveObjList("altBuilds").map(::parseChampion),
            maxCap = resolveObjList("maxCap").map(::parseMaxCapChampion),
            augments = resolveObjList("augments").map(::parseAugment),
            augmentTypes = augmentTypes,
            carousel = carouselItems,
            tips = resolveObjList("tips").map(::parseTip),
        )
    }
}

data class ParsedComp(
    val id: String,
    val title: String,
    val tier: String,
    val style: String,
    val difficulty: String,
    val compSlug: String,
    val set: Int,
    val isPublic: Boolean,
    val created: String,
    val updated: String,
    val augmentsTip: String,
    val mainChampionApiName: String,
    val mainChampionCost: Int,
    val earlyComp: List<ParsedChampion>,
    val finalComp: List<ParsedChampion>,
    val altBuilds: List<ParsedChampion>,
    val maxCap: List<ParsedMaxCapChampion>,
    val augments: List<ParsedAugment>,
    val augmentTypes: List<String>,
    val carousel: List<String>,
    val tips: List<ParsedTip>,
)

data class ParsedChampion(
    val apiName: String,
    val stars: Int,
    val boardIndex: Int?,
    val items: List<String>,
)

data class ParsedMaxCapChampion(
    val apiName: String,
    val stars: Int,
    val items: List<String>,
    val predecessors: List<String>,
)

data class ParsedAugment(
    val apiName: String,
    val disabled: Boolean,
)

data class ParsedTip(
    val stage: String,
    val tip: String,
)
