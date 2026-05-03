package dev.cannoli.ui.components

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.delay

/**
 * Owns transient OSD message state. One instance lives per surface that draws an [OsdPill]
 * (currently the launcher and the in-game activity, since they have separate lifecycles).
 *
 * Callers invoke [show] with a message; the controller schedules a clear after [defaultDurationMs].
 * [OsdHost] drives the actual clear via Compose [LaunchedEffect] so timing follows the
 * composition lifecycle.
 */
class OsdController(
    val defaultDurationMs: Long = 3000L,
) {
    private val _request = mutableStateOf<Request?>(null)
    val request: State<Request?> = _request

    data class Request(val message: String, val durationMs: Long, val nonce: Int)

    private var counter = 0

    fun show(message: String, durationMs: Long = defaultDurationMs) {
        counter += 1
        _request.value = Request(message, durationMs, counter)
    }

    fun clear() {
        _request.value = null
    }
}

/**
 * Renders the controller's current message as an [OsdPill] anchored top-center, and clears it
 * after the request's duration via Compose. Place inside a [BoxScope].
 */
@Composable
fun BoxScope.OsdHost(controller: OsdController) {
    val current = controller.request.value
    LaunchedEffect(current) {
        if (current != null) {
            delay(current.durationMs)
            // Only clear if no newer request has displaced this one in the meantime.
            if (controller.request.value?.nonce == current.nonce) controller.clear()
        }
    }
    if (current != null) OsdPill(message = current.message)
}
