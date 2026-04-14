package dev.set17.earlygame

/**
 * Configurable weights for the recommendation scoring algorithm.
 * All weights are multiplied by tier weight (S=3, A=2, B=1).
 *
 * Component pool is consumed in priority order to prevent double-counting:
 * carry full -> tank full -> carry partial -> tank partial -> carousel.
 */
data class ScoringConfig(
    /** Carry items 1 & 2 — fully craftable (both components available). Strongest signal. */
    val carryItemWeight: Int = 7,

    /** Carry item 3 — fully craftable. Slightly less important than items 1 & 2. */
    val carryItem3Weight: Int = 5,

    /** Carry items 1 & 2 — partially craftable (1 of 2 components). */
    val partialItemWeight: Int = 2,

    /** Carry item 3 — partially craftable. */
    val partialItem3Weight: Int = 1,

    /** Tank item — fully craftable. Stronger than carry item 3, weaker than carry items 1 & 2. */
    val tankItemWeight: Int = 6,

    /** Tank item — partially craftable. */
    val tankPartialWeight: Int = 1,

    /** Carousel priority component match. Weakest item signal. */
    val carouselMatchWeight: Int = 1,

    /** Champion that matches the comp's early board. */
    val championMatchWeight: Int = 1,
)
