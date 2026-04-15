package dev.set17.tftacademy.champion

/**
 * Encodes champion lists into TFT Team Planner codes.
 *
 * Format: `02` + 10 three-hex-digit champion slots + `TFTSet17`
 * Champion IDs are `team_planner_code` values from Community Dragon's tftchampions-teamplanner.json.
 */
object TeamCodeEncoder {

    private const val PREFIX = "02"
    private const val SUFFIX = "TFTSet17"
    private const val SLOT_COUNT = 10
    private const val EMPTY_SLOT = "000"

    /**
     * Our apiName -> CDragon team_planner_code.
     *
     * Naming discrepancies with CDragon:
     *   TFT17_Meepsie (ours) = TFT17_IvernMinion (CDragon) -> 20
     *   TFT17_Reksai  (ours) = TFT17_RekSai      (CDragon) -> 104
     */
    private val teamPlannerCode: Map<String, Int> = mapOf(
        "TFT17_Aatrox" to 29,
        "TFT17_Akali" to 13,
        "TFT17_AurelionSol" to 38,
        "TFT17_Aurora" to 16,
        "TFT17_Bard" to 28,
        "TFT17_Belveth" to 15,
        "TFT17_Blitzcrank" to 56,
        "TFT17_Briar" to 14,
        "TFT17_Caitlyn" to 27,
        "TFT17_Chogath" to 69,
        "TFT17_Corki" to 25,
        "TFT17_Diana" to 67,
        "TFT17_Ezreal" to 62,
        "TFT17_Fiora" to 19,
        "TFT17_Fizz" to 21,
        "TFT17_Galio" to 39,
        "TFT17_Gnar" to 23,
        "TFT17_Gragas" to 49,
        "TFT17_Graves" to 80,
        "TFT17_Gwen" to 53,
        "TFT17_Illaoi" to 17,
        "TFT17_Jax" to 44,
        "TFT17_Jhin" to 35,
        "TFT17_Jinx" to 18,
        "TFT17_Kaisa" to 32,
        "TFT17_Karma" to 34,
        "TFT17_Kindred" to 31,
        "TFT17_Leblanc" to 68,
        "TFT17_Leona" to 66,
        "TFT17_Lissandra" to 70,
        "TFT17_Lulu" to 48,
        "TFT17_Maokai" to 30,
        "TFT17_MasterYi" to 47,
        "TFT17_Meepsie" to 20,
        "TFT17_Milio" to 61,
        "TFT17_MissFortune" to 1,
        "TFT17_Mordekaiser" to 78,
        "TFT17_Morgana" to 88,
        "TFT17_Nami" to 55,
        "TFT17_Nasus" to 52,
        "TFT17_Nunu" to 57,
        "TFT17_Ornn" to 54,
        "TFT17_Pantheon" to 37,
        "TFT17_Poppy" to 26,
        "TFT17_Pyke" to 45,
        "TFT17_Rammus" to 24,
        "TFT17_Reksai" to 104,
        "TFT17_Rhaast" to 33,
        "TFT17_Riven" to 60,
        "TFT17_Samira" to 50,
        "TFT17_Shen" to 59,
        "TFT17_Sona" to 41,
        "TFT17_TahmKench" to 79,
        "TFT17_Talon" to 42,
        "TFT17_Teemo" to 51,
        "TFT17_TwistedFate" to 43,
        "TFT17_Urgot" to 36,
        "TFT17_Veigar" to 22,
        "TFT17_Vex" to 58,
        "TFT17_Viktor" to 46,
        "TFT17_Xayah" to 63,
        "TFT17_Zed" to 71,
        "TFT17_Zoe" to 65,
    )

    /** Encode a list of champion apiNames into a Team Planner code. */
    fun encode(championApiNames: List<String>): String {
        val hexSlots = championApiNames
            .mapNotNull { teamPlannerCode[it] }
            .take(SLOT_COUNT)
            .map { it.toString(16).padStart(3, '0') }

        val padded = hexSlots + List(SLOT_COUNT - hexSlots.size) { EMPTY_SLOT }

        return PREFIX + padded.joinToString("") + SUFFIX
    }
}
