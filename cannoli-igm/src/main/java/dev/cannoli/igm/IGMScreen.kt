package dev.cannoli.igm

sealed class IGMScreen {
    abstract val selectedIndex: Int

    data class Menu(override val selectedIndex: Int = 0, val confirmDeleteSlot: Boolean = false) : IGMScreen()
    data class Settings(override val selectedIndex: Int = 0) : IGMScreen()
    data class Video(override val selectedIndex: Int = 0) : IGMScreen()
    data class Advanced(override val selectedIndex: Int = 0) : IGMScreen()

    data class Emulator(override val selectedIndex: Int = 0, val showDescription: Boolean = false) : IGMScreen()
    data class EmulatorCategory(override val selectedIndex: Int = 0, val categoryKey: String, val categoryTitle: String = "", val showDescription: Boolean = false) : IGMScreen()
    data class Shortcuts(override val selectedIndex: Int = 0, val listening: Boolean = false, val heldKeys: Set<Int> = emptySet(), val countdownMs: Int = 0) : IGMScreen()
    data class ShaderSettings(override val selectedIndex: Int = 0) : IGMScreen()
    data class SavePrompt(override val selectedIndex: Int = 0) : IGMScreen()
    data class Info(override val selectedIndex: Int = 0) : IGMScreen()
    data class Achievements(override val selectedIndex: Int = 0, val achievements: List<AchievementInfo> = emptyList(), val filter: Int = 0, val status: String = "") : IGMScreen()
    data class AchievementDetail(override val selectedIndex: Int = 0, val achievement: AchievementInfo, val parentIndex: Int = 0) : IGMScreen()
    data class GuidePicker(override val selectedIndex: Int = 0) : IGMScreen()
    data class Guide(override val selectedIndex: Int = 0, val filePath: String, val page: Int = 0, val textZoom: Int = 1) : IGMScreen()

    data class Buttons(
        override val selectedIndex: Int = 0,
        val listeningCanonical: String? = null,
        val countdownMs: Int = 0,
    ) : IGMScreen()
    /**
     * In-game multi-controller seat reassignment. [selectedIndex] picks one of the four port
     * rows. [swapWithIndex] is the row currently being held in 'swap mode' (set when the user
     * confirmed on a row to start a swap; the next confirm picks the partner). -1 means idle.
     */
    data class ReassignPlayers(
        override val selectedIndex: Int = 0,
        val swapWithIndex: Int = -1,
    ) : IGMScreen()
}

fun IGMScreen.withSelectedIndex(selectedIndex: Int): IGMScreen = when (this) {
    is IGMScreen.Menu -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Settings -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Video -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Advanced -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Emulator -> copy(selectedIndex = selectedIndex)
    is IGMScreen.EmulatorCategory -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Shortcuts -> copy(selectedIndex = selectedIndex)
    is IGMScreen.ShaderSettings -> copy(selectedIndex = selectedIndex)
    is IGMScreen.SavePrompt -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Info -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Achievements -> copy(selectedIndex = selectedIndex)
    is IGMScreen.AchievementDetail -> copy(selectedIndex = selectedIndex)
    is IGMScreen.GuidePicker -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Guide -> copy(selectedIndex = selectedIndex)
    is IGMScreen.Buttons -> copy(selectedIndex = selectedIndex)
    is IGMScreen.ReassignPlayers -> copy(selectedIndex = selectedIndex)
}
