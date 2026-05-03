package dev.set17.tftacademy.item

object ItemComponentMap {

    val BASE_COMPONENTS = setOf(
        "TFT_Item_BFSword",
        "TFT_Item_RecurveBow",
        "TFT_Item_NeedlesslyLargeRod",
        "TFT_Item_TearOfTheGoddess",
        "TFT_Item_ChainVest",
        "TFT_Item_NegatronCloak",
        "TFT_Item_GiantsBelt",
        "TFT_Item_SparringGloves",
        "TFT_Item_FryingPan",
        "TFT_Item_Spatula",
    )

    /** Completed item apiName -> (component1, component2). Source: Community Dragon. */
    val recipes: Map<String, Pair<String, String>> = mapOf(
        // BF Sword combinations
        "TFT_Item_Deathblade" to ("TFT_Item_BFSword" to "TFT_Item_BFSword"),
        "TFT_Item_MadredsBloodrazor" to ("TFT_Item_BFSword" to "TFT_Item_RecurveBow"),
        "TFT_Item_HextechGunblade" to ("TFT_Item_BFSword" to "TFT_Item_NeedlesslyLargeRod"),
        "TFT_Item_SpearOfShojin" to ("TFT_Item_BFSword" to "TFT_Item_TearOfTheGoddess"),
        "TFT_Item_GuardianAngel" to ("TFT_Item_BFSword" to "TFT_Item_ChainVest"),
        "TFT_Item_Bloodthirster" to ("TFT_Item_BFSword" to "TFT_Item_NegatronCloak"),
        "TFT_Item_SteraksGage" to ("TFT_Item_BFSword" to "TFT_Item_GiantsBelt"),
        "TFT_Item_InfinityEdge" to ("TFT_Item_BFSword" to "TFT_Item_SparringGloves"),

        // Recurve Bow combinations
        "TFT_Item_RapidFireCannon" to ("TFT_Item_RecurveBow" to "TFT_Item_RecurveBow"),
        "TFT_Item_GuinsoosRageblade" to ("TFT_Item_RecurveBow" to "TFT_Item_NeedlesslyLargeRod"),
        "TFT_Item_StatikkShiv" to ("TFT_Item_RecurveBow" to "TFT_Item_TearOfTheGoddess"),
        "TFT_Item_TitansResolve" to ("TFT_Item_ChainVest" to "TFT_Item_RecurveBow"),
        "TFT_Item_RunaansHurricane" to ("TFT_Item_NegatronCloak" to "TFT_Item_RecurveBow"),
        "TFT_Item_Leviathan" to ("TFT_Item_RecurveBow" to "TFT_Item_GiantsBelt"),
        "TFT_Item_LastWhisper" to ("TFT_Item_RecurveBow" to "TFT_Item_SparringGloves"),

        // Needlessly Large Rod combinations
        "TFT_Item_RabadonsDeathcap" to ("TFT_Item_NeedlesslyLargeRod" to "TFT_Item_NeedlesslyLargeRod"),
        "TFT_Item_ArchangelsStaff" to ("TFT_Item_NeedlesslyLargeRod" to "TFT_Item_TearOfTheGoddess"),
        "TFT_Item_Crownguard" to ("TFT_Item_NeedlesslyLargeRod" to "TFT_Item_ChainVest"),
        "TFT_Item_IonicSpark" to ("TFT_Item_NeedlesslyLargeRod" to "TFT_Item_NegatronCloak"),
        "TFT_Item_Morellonomicon" to ("TFT_Item_NeedlesslyLargeRod" to "TFT_Item_GiantsBelt"),
        "TFT_Item_JeweledGauntlet" to ("TFT_Item_NeedlesslyLargeRod" to "TFT_Item_SparringGloves"),

        // Tear of the Goddess combinations
        "TFT_Item_BlueBuff" to ("TFT_Item_TearOfTheGoddess" to "TFT_Item_TearOfTheGoddess"),
        "TFT_Item_FrozenHeart" to ("TFT_Item_TearOfTheGoddess" to "TFT_Item_ChainVest"),
        "TFT_Item_AdaptiveHelm" to ("TFT_Item_NegatronCloak" to "TFT_Item_TearOfTheGoddess"),
        "TFT_Item_Redemption" to ("TFT_Item_TearOfTheGoddess" to "TFT_Item_GiantsBelt"),
        "TFT_Item_UnstableConcoction" to ("TFT_Item_TearOfTheGoddess" to "TFT_Item_SparringGloves"),

        // Chain Vest combinations
        "TFT_Item_BrambleVest" to ("TFT_Item_ChainVest" to "TFT_Item_ChainVest"),
        "TFT_Item_GargoyleStoneplate" to ("TFT_Item_ChainVest" to "TFT_Item_NegatronCloak"),
        "TFT_Item_RedBuff" to ("TFT_Item_ChainVest" to "TFT_Item_GiantsBelt"),
        "TFT_Item_NightHarvester" to ("TFT_Item_ChainVest" to "TFT_Item_SparringGloves"),

        // Negatron Cloak combinations
        "TFT_Item_DragonsClaw" to ("TFT_Item_NegatronCloak" to "TFT_Item_NegatronCloak"),
        "TFT_Item_SpectralGauntlet" to ("TFT_Item_NegatronCloak" to "TFT_Item_GiantsBelt"),
        "TFT_Item_Quicksilver" to ("TFT_Item_SparringGloves" to "TFT_Item_NegatronCloak"),

        // Giant's Belt combinations
        "TFT_Item_WarmogsArmor" to ("TFT_Item_GiantsBelt" to "TFT_Item_GiantsBelt"),
        "TFT_Item_PowerGauntlet" to ("TFT_Item_GiantsBelt" to "TFT_Item_SparringGloves"),

        // Sparring Gloves combinations
        "TFT_Item_ThiefsGloves" to ("TFT_Item_SparringGloves" to "TFT_Item_SparringGloves"),
    )

    /**
     * Emblem recipes keyed by trait name. Each trait's emblem is crafted from
     * either a Frying Pan or Spatula plus a base component.
     *
     * Not all traits have craftable emblems — unique/rare trait emblems
     * can only be found through augments or other mechanics.
     */
    val emblemRecipes: Map<String, Pair<String, String>> = mapOf(
        // Frying Pan emblems
        "Challenger" to ("TFT_Item_FryingPan" to "TFT_Item_RecurveBow"),
        "Vanguard" to ("TFT_Item_FryingPan" to "TFT_Item_NegatronCloak"),
        "Brawler" to ("TFT_Item_FryingPan" to "TFT_Item_GiantsBelt"),
        "Rogue" to ("TFT_Item_FryingPan" to "TFT_Item_SparringGloves"),
        "Shepherd" to ("TFT_Item_FryingPan" to "TFT_Item_TearOfTheGoddess"),
        "Voyager" to ("TFT_Item_FryingPan" to "TFT_Item_NeedlesslyLargeRod"),
        "Marauder" to ("TFT_Item_FryingPan" to "TFT_Item_BFSword"),
        "Bastion" to ("TFT_Item_FryingPan" to "TFT_Item_ChainVest"),
        // Spatula emblems
        "Arbiter" to ("TFT_Item_Spatula" to "TFT_Item_NegatronCloak"),
        "N.O.V.A." to ("TFT_Item_Spatula" to "TFT_Item_SparringGloves"),
        "Primordian" to ("TFT_Item_Spatula" to "TFT_Item_GiantsBelt"),
        "Dark Star" to ("TFT_Item_Spatula" to "TFT_Item_BFSword"),
        "Meeple" to ("TFT_Item_Spatula" to "TFT_Item_ChainVest"),
        "Stargazer" to ("TFT_Item_Spatula" to "TFT_Item_NeedlesslyLargeRod"),
        "Timebreaker" to ("TFT_Item_Spatula" to "TFT_Item_RecurveBow"),
        "Space Groove" to ("TFT_Item_Spatula" to "TFT_Item_TearOfTheGoddess"),
    )

    /**
     * Maps emblem item API names (as used by TFT Academy) to their trait name.
     * This allows looking up the recipe for an emblem item via [emblemRecipes].
     * Add new mappings here when TFT Academy data uses a new emblem API name.
     */
    val emblemApiNameToTrait: Map<String, String> = mapOf(
        "TFT17_Item_AssassinTraitEmblemItem" to "Rogue",
        "TFT17_Item_DRXEmblemItem" to "N.O.V.A.",
        "TFT17_Item_DarkStarEmblemItem" to "Dark Star",
        "TFT17_Item_PsyOpsEmblemItem" to "Psionic",
        "TFT17_Item_RangedTraitEmblemItem" to "Sniper",
        "TFT17_Item_ShieldTankEmblemItem" to "Bastion",
        "TFT17_Item_SpaceGrooveEmblemItem" to "Space Groove",
        "TFT17_Item_StargazerEmblemItem" to "Stargazer",
        "TFT17_Item_SummonTraitEmblemItem" to "Shepherd",
    )

    /** Reverse lookup: component -> list of completed items that use it. */
    val completedItemsByComponent: Map<String, List<String>> by lazy {
        val result = mutableMapOf<String, MutableList<String>>()
        for ((completed, components) in recipes) {
            result.getOrPut(components.first) { mutableListOf() }.add(completed)
            if (components.first != components.second) {
                result.getOrPut(components.second) { mutableListOf() }.add(completed)
            }
        }
        result
    }

    /** Items that indicate a champion is a tank. */
    val DEFENSIVE_ITEMS = setOf(
        "TFT_Item_WarmogsArmor",
        "TFT_Item_BrambleVest",
        "TFT_Item_DragonsClaw",
        "TFT_Item_GargoyleStoneplate",
        "TFT_Item_RedBuff",
        "TFT_Item_Redemption",
        "TFT_Item_NightHarvester",
        "TFT_Item_Crownguard",
        "TFT_Item_SteraksGage",
        "TFT_Item_TitansResolve",
        "TFT_Item_SpectralGauntlet",
        "TFT_Item_FrozenHeart",
        "TFT_Item_IonicSpark",
    )

    /** Current in-game display names. Source: Community Dragon. */
    val displayNames: Map<String, String> = mapOf(
        // Base components
        "TFT_Item_BFSword" to "B.F. Sword",
        "TFT_Item_RecurveBow" to "Recurve Bow",
        "TFT_Item_NeedlesslyLargeRod" to "Needlessly Large Rod",
        "TFT_Item_TearOfTheGoddess" to "Tear of the Goddess",
        "TFT_Item_ChainVest" to "Chain Vest",
        "TFT_Item_NegatronCloak" to "Negatron Cloak",
        "TFT_Item_GiantsBelt" to "Giant's Belt",
        "TFT_Item_SparringGloves" to "Sparring Gloves",
        "TFT_Item_FryingPan" to "Frying Pan",
        "TFT_Item_Spatula" to "Spatula",
        // Completed items (many renamed from their API names)
        "TFT_Item_ArchangelsStaff" to "Archangel's Staff",
        "TFT_Item_Bloodthirster" to "Bloodthirster",
        "TFT_Item_BlueBuff" to "Blue Buff",
        "TFT_Item_BrambleVest" to "Bramble Vest",
        "TFT_Item_Crownguard" to "Crownguard",
        "TFT_Item_Deathblade" to "Deathblade",
        "TFT_Item_DragonsClaw" to "Dragon's Claw",
        "TFT_Item_FrozenHeart" to "Protector's Vow",
        "TFT_Item_GargoyleStoneplate" to "Gargoyle Stoneplate",
        "TFT_Item_GuardianAngel" to "Edge of Night",
        "TFT_Item_GuinsoosRageblade" to "Guinsoo's Rageblade",
        "TFT_Item_HextechGunblade" to "Hextech Gunblade",
        "TFT_Item_InfinityEdge" to "Infinity Edge",
        "TFT_Item_IonicSpark" to "Ionic Spark",
        "TFT_Item_JeweledGauntlet" to "Jeweled Gauntlet",
        "TFT_Item_LastWhisper" to "Last Whisper",
        "TFT_Item_Leviathan" to "Nashor's Tooth",
        "TFT_Item_MadredsBloodrazor" to "Giant Slayer",
        "TFT_Item_Morellonomicon" to "Morellonomicon",
        "TFT_Item_NightHarvester" to "Steadfast Heart",
        "TFT_Item_PowerGauntlet" to "Striker's Flail",
        "TFT_Item_Quicksilver" to "Quicksilver",
        "TFT_Item_RabadonsDeathcap" to "Rabadon's Deathcap",
        "TFT_Item_RapidFireCannon" to "Red Buff",
        "TFT_Item_RedBuff" to "Sunfire Cape",
        "TFT_Item_Redemption" to "Spirit Visage",
        "TFT_Item_RunaansHurricane" to "Kraken's Fury",
        "TFT_Item_SpearOfShojin" to "Spear of Shojin",
        "TFT_Item_SpectralGauntlet" to "Evenshroud",
        "TFT_Item_StatikkShiv" to "Void Staff",
        "TFT_Item_SteraksGage" to "Sterak's Gage",
        "TFT_Item_ThiefsGloves" to "Thief's Gloves",
        "TFT_Item_TitansResolve" to "Titan's Resolve",
        "TFT_Item_UnstableConcoction" to "Hand of Justice",
        "TFT_Item_WarmogsArmor" to "Warmog's Armor",
        "TFT_Item_AdaptiveHelm" to "Adaptive Helm",
    )

    /** Get display name for any item API name. Falls back to formatted API name. */
    fun displayName(apiName: String): String {
        displayNames[apiName]?.let { return it }
        // Emblem items
        emblemApiNameToTrait[apiName]?.let { return "$it Emblem" }
        // Fallback: strip prefix and insert spaces
        return apiName
            .removePrefix("TFT_Item_")
            .removePrefix("TFT17_Item_")
            .replace(Regex("([a-z])([A-Z])"), "$1 $2")
    }

    fun componentsOf(completedItem: String): Pair<String, String>? {
        recipes[completedItem]?.let { return it }
        val trait = emblemApiNameToTrait[completedItem] ?: return null
        return emblemRecipes[trait]
    }

    fun isComponent(apiName: String): Boolean = apiName in BASE_COMPONENTS

    fun isDefensiveItem(apiName: String): Boolean = apiName in DEFENSIVE_ITEMS
}
