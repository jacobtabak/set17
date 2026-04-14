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

    fun componentsOf(completedItem: String): Pair<String, String>? = recipes[completedItem]

    fun isComponent(apiName: String): Boolean = apiName in BASE_COMPONENTS

    fun isDefensiveItem(apiName: String): Boolean = apiName in DEFENSIVE_ITEMS
}
