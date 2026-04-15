package dev.set17.tftacademy.champion

object ChampionData {

    data class ChampionInfo(
        val name: String,
        val cost: Int,
        val traits: List<String>,
    )

    /** Champion apiName -> info. Source: mobalytics.gg Set 17 */
    val champions: Map<String, ChampionInfo> = mapOf(
        "TFT17_Aatrox" to ChampionInfo("Aatrox", 1, listOf("N.O.V.A.", "Bastion")),
        "TFT17_Akali" to ChampionInfo("Akali", 2, listOf("N.O.V.A.", "Marauder")),
        "TFT17_AurelionSol" to ChampionInfo("Aurelion Sol", 4, listOf("Mecha", "Conduit")),
        "TFT17_Aurora" to ChampionInfo("Aurora", 3, listOf("Anima", "Voyager")),
        "TFT17_Bard" to ChampionInfo("Bard", 5, listOf("Meeple", "Conduit")),
        "TFT17_Belveth" to ChampionInfo("Bel'Veth", 2, listOf("Primordian", "Challenger", "Marauder")),
        "TFT17_Blitzcrank" to ChampionInfo("Blitzcrank", 5, listOf("Party Animal", "Space Groove", "Vanguard")),
        "TFT17_Briar" to ChampionInfo("Briar", 1, listOf("Anima", "Primordian", "Rogue")),
        "TFT17_Caitlyn" to ChampionInfo("Caitlyn", 1, listOf("N.O.V.A.", "Fateweaver")),
        "TFT17_Chogath" to ChampionInfo("Cho'Gath", 1, listOf("Dark Star", "Brawler")),
        "TFT17_Corki" to ChampionInfo("Corki", 4, listOf("Meeple", "Fateweaver")),
        "TFT17_Diana" to ChampionInfo("Diana", 3, listOf("Arbiter", "Challenger")),
        "TFT17_Ezreal" to ChampionInfo("Ezreal", 1, listOf("Timebreaker", "Sniper")),
        "TFT17_Fiora" to ChampionInfo("Fiora", 5, listOf("Divine Duelist", "Anima", "Marauder")),
        "TFT17_Fizz" to ChampionInfo("Fizz", 3, listOf("Meeple", "Rogue")),
        "TFT17_Gnar" to ChampionInfo("Gnar", 2, listOf("Meeple", "Sniper")),
        "TFT17_Gragas" to ChampionInfo("Gragas", 2, listOf("Psionic", "Brawler")),
        "TFT17_Graves" to ChampionInfo("Graves", 5, listOf("Factory New")),
        "TFT17_Gwen" to ChampionInfo("Gwen", 2, listOf("Space Groove", "Rogue")),
        "TFT17_Illaoi" to ChampionInfo("Illaoi", 3, listOf("Anima", "Vanguard", "Shepherd")),
        "TFT17_Jax" to ChampionInfo("Jax", 2, listOf("Stargazer", "Bastion")),
        "TFT17_Jhin" to ChampionInfo("Jhin", 5, listOf("Dark Star", "Eradicator", "Sniper")),
        "TFT17_Jinx" to ChampionInfo("Jinx", 2, listOf("Anima", "Challenger")),
        "TFT17_Kaisa" to ChampionInfo("Kai'Sa", 3, listOf("Dark Star", "Rogue")),
        "TFT17_Karma" to ChampionInfo("Karma", 4, listOf("Dark Star", "Voyager")),
        "TFT17_Kindred" to ChampionInfo("Kindred", 4, listOf("N.O.V.A.", "Challenger")),
        "TFT17_Leblanc" to ChampionInfo("LeBlanc", 4, listOf("Arbiter", "Shepherd")),
        "TFT17_Leona" to ChampionInfo("Leona", 1, listOf("Arbiter", "Vanguard")),
        "TFT17_Lissandra" to ChampionInfo("Lissandra", 1, listOf("Dark Star", "Shepherd", "Replicator")),
        "TFT17_Lulu" to ChampionInfo("Lulu", 3, listOf("Stargazer", "Replicator")),
        "TFT17_Maokai" to ChampionInfo("Maokai", 3, listOf("N.O.V.A.", "Brawler")),
        "TFT17_MasterYi" to ChampionInfo("Master Yi", 4, listOf("Psionic", "Marauder")),
        "TFT17_Meepsie" to ChampionInfo("Meepsie", 2, listOf("Meeple", "Shepherd", "Voyager")),
        "TFT17_IvernMinion" to ChampionInfo("Meepsie", 2, listOf("Meeple", "Shepherd", "Voyager")),
        "TFT17_Milio" to ChampionInfo("Milio", 2, listOf("Timebreaker", "Fateweaver")),
        "TFT17_MissFortune" to ChampionInfo("Miss Fortune", 3, listOf("Gun Goddess")),
        "TFT17_Mordekaiser" to ChampionInfo("Mordekaiser", 2, listOf("Dark Star", "Conduit", "Vanguard")),
        "TFT17_Morgana" to ChampionInfo("Morgana", 5, listOf("Dark Lady")),
        "TFT17_Nami" to ChampionInfo("Nami", 4, listOf("Space Groove", "Replicator")),
        "TFT17_Nasus" to ChampionInfo("Nasus", 1, listOf("Space Groove", "Vanguard")),
        "TFT17_Nunu" to ChampionInfo("Nunu", 4, listOf("Stargazer", "Vanguard")),
        "TFT17_Ornn" to ChampionInfo("Ornn", 3, listOf("Space Groove", "Bastion")),
        "TFT17_Pantheon" to ChampionInfo("Pantheon", 2, listOf("Timebreaker", "Brawler", "Replicator")),
        "TFT17_Poppy" to ChampionInfo("Poppy", 1, listOf("Meeple", "Bastion")),
        "TFT17_Pyke" to ChampionInfo("Pyke", 2, listOf("Psionic", "Voyager")),
        "TFT17_Reksai" to ChampionInfo("Rek'Sai", 1, listOf("Primordian", "Brawler")),
        "TFT17_Rhaast" to ChampionInfo("Rhaast", 3, listOf("Redeemer")),
        "TFT17_Riven" to ChampionInfo("Riven", 4, listOf("Timebreaker", "Rogue")),
        "TFT17_Samira" to ChampionInfo("Samira", 3, listOf("Space Groove", "Sniper")),
        "TFT17_Shen" to ChampionInfo("Shen", 5, listOf("Bulwark", "Bastion")),
        "TFT17_Sona" to ChampionInfo("Sona", 5, listOf("Commander", "Psionic", "Shepherd")),
        "TFT17_TahmKench" to ChampionInfo("Tahm Kench", 4, listOf("Oracle", "Brawler")),
        "TFT17_Talon" to ChampionInfo("Talon", 1, listOf("Stargazer", "Rogue")),
        "TFT17_Teemo" to ChampionInfo("Teemo", 1, listOf("Space Groove", "Shepherd")),
        "TFT17_Galio" to ChampionInfo("The Mighty Mech", 4, listOf("Mecha", "Voyager")),
        "TFT17_Rammus" to ChampionInfo("Rammus", 4, listOf("Meeple", "Bastion")),
        "TFT17_TwistedFate" to ChampionInfo("Twisted Fate", 1, listOf("Stargazer", "Fateweaver")),
        "TFT17_Urgot" to ChampionInfo("Urgot", 3, listOf("Mecha", "Brawler", "Marauder")),
        "TFT17_Veigar" to ChampionInfo("Veigar", 1, listOf("Meeple", "Replicator")),
        "TFT17_Vex" to ChampionInfo("Vex", 5, listOf("Doomer")),
        "TFT17_Viktor" to ChampionInfo("Viktor", 3, listOf("Psionic", "Conduit")),
        "TFT17_Xayah" to ChampionInfo("Xayah", 4, listOf("Stargazer", "Sniper")),
        "TFT17_Zed" to ChampionInfo("Zed", 5, listOf("Galaxy Hunter")),
        "TFT17_Zoe" to ChampionInfo("Zoe", 2, listOf("Arbiter", "Conduit")),
    )

    /** Trait name -> sorted list of breakpoints (minimum units to activate each tier). */
    val traitBreakpoints: Map<String, List<Int>> = mapOf(
        // Unique / special (activate at 1)
        "Arbiter" to listOf(1, 2, 3),
        "Bulwark" to listOf(1),
        "Commander" to listOf(1),
        "Dark Lady" to listOf(1),
        "Divine Duelist" to listOf(1),
        "Doomer" to listOf(1),
        "Eradicator" to listOf(1),
        "Factory New" to listOf(1),
        "Galaxy Hunter" to listOf(1),
        "Gun Goddess" to listOf(1),
        "Oracle" to listOf(1),
        "Party Animal" to listOf(1),
        "Redeemer" to listOf(1),
        "Stargazer" to listOf(3),
        // Multi-tier origins
        "Anima" to listOf(3, 6),
        "Dark Star" to listOf(2, 4, 6, 9),
        "Mecha" to listOf(3, 4, 6),
        "Meeple" to listOf(3, 5, 7, 10),
        "N.O.V.A." to listOf(2, 5),
        "Primordian" to listOf(2, 3),
        "Psionic" to listOf(2, 4),
        "Space Groove" to listOf(3, 5, 7, 10),
        "Timebreaker" to listOf(2, 3, 4),
        // Classes
        "Bastion" to listOf(2, 4, 6),
        "Brawler" to listOf(2, 4, 6),
        "Challenger" to listOf(2, 3, 4, 5),
        "Conduit" to listOf(2, 3, 4, 5),
        "Fateweaver" to listOf(2, 4),
        "Marauder" to listOf(2, 4, 6),
        "Replicator" to listOf(2, 4),
        "Rogue" to listOf(2, 3, 4, 5),
        "Shepherd" to listOf(3, 5, 7),
        "Sniper" to listOf(2, 3, 4, 5),
        "Vanguard" to listOf(2, 4, 6),
        "Voyager" to listOf(2, 3, 4, 5, 6),
    )

    /** Get display name for a champion apiName. */
    fun displayName(apiName: String): String =
        champions[apiName]?.name ?: apiName.removePrefix("TFT17_").removePrefix("TFT_")

    /** Get traits for a champion apiName. */
    fun traits(apiName: String): List<String> =
        champions[apiName]?.traits ?: emptyList()

    /** Compute trait counts for a list of champion apiNames. Returns trait -> count. */
    fun activeTraits(championApiNames: List<String>): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        for (apiName in championApiNames) {
            for (trait in traits(apiName)) {
                counts[trait] = (counts[trait] ?: 0) + 1
            }
        }
        return counts
    }

    /** Returns only traits that hit at least their first breakpoint. Maps trait -> (count, active tier). */
    fun activatedTraits(championApiNames: List<String>): Map<String, Pair<Int, Int>> {
        val counts = activeTraits(championApiNames)
        val result = mutableMapOf<String, Pair<Int, Int>>()
        for ((trait, count) in counts) {
            val breakpoints = traitBreakpoints[trait] ?: continue
            val activeTier = breakpoints.count { count >= it }
            if (activeTier > 0) {
                result[trait] = count to activeTier
            }
        }
        return result
    }
}
