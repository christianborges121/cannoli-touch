package dev.cannoli.scorza.input.runtime

import dev.cannoli.scorza.input.AnalogRole
import dev.cannoli.scorza.input.CanonicalButton

sealed interface CanonicalEvent {
    data class Pressed(val button: CanonicalButton) : CanonicalEvent
    data class Released(val button: CanonicalButton) : CanonicalEvent
    data class AnalogChanged(val role: AnalogRole, val value: Float) : CanonicalEvent
}
