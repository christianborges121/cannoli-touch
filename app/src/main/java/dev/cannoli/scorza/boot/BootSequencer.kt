package dev.cannoli.scorza.boot

class BootSequencer {

    companion object {
        fun nextState(
            current: BootState,
            hasStorage: Boolean,
            hasBluetooth: Boolean,
            setupResolved: Boolean,
            volumes: List<Pair<String, String>>,
        ): BootState {
            if (!hasStorage || !hasBluetooth) {
                return BootState.NeedsPermission(storageGranted = hasStorage, bluetoothGranted = hasBluetooth)
            }
            return when (current) {
                is BootState.Initializing -> current
                is BootState.Error -> current
                BootState.Ready -> BootState.Ready
                else -> if (setupResolved) {
                    BootState.Initializing(BootPhase.IMPORT, 0f, "Preparing")
                } else {
                    BootState.NeedsSetup(volumes)
                }
            }
        }
    }
}
