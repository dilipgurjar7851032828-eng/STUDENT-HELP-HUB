package com.example.ui.touch

import android.content.Context
import android.os.SystemClock
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.hypot

/**
 * Global Virtual Phone Touch Event Handler & Debouncer.
 *
 * Solves the known browser streaming emulator / virtual phone issue where synthetic
 * pointer/mouse duplicate events or network jitter cause a single tap to be delivered
 * as two rapid events (within 10-120ms) at the same coordinates, causing misclassification
 * as a double tap or duplicate single-tap actions.
 *
 * Rules:
 * 1. One physical tap = exactly one single-tap action (duplicate events within 140ms dropped).
 * 2. Two intentional quick taps = double-tap allowed when supported (interval in 150-350ms window preserved).
 * 3. Rapid taps at different locations (e.g. typing on keyboard) are never blocked.
 * 4. Movement / scroll gestures are never blocked.
 * 5. Dropped gestures have their subsequent MOVE and UP events cleanly consumed to prevent orphan states.
 */
object VirtualPhoneTouchHandler {

    // Threshold below which rapid consecutive taps at the same position are treated as accidental duplicates / bounce
    const val ACCIDENTAL_DUPLICATE_THRESHOLD_MS = 140L

    // Minimum interval after ACTION_UP before a new ACTION_DOWN at the exact same location is considered intentional
    const val MIN_UP_TO_DOWN_INTERVAL_MS = 120L

    // Standard touch slop in DP for virtual phone pointer jitter
    const val TOUCH_SLOP_FALLBACK_DP = 24f

    @Volatile
    var lastDownTime: Long = 0L

    @Volatile
    var lastDownX: Float = 0f

    @Volatile
    var lastDownY: Float = 0f

    @Volatile
    var lastUpTime: Long = 0L

    @Volatile
    var lastUpX: Float = 0f

    @Volatile
    var lastUpY: Float = 0f

    @Volatile
    var isCurrentGestureFiltered: Boolean = false

    // State for Compose pointer input filter
    @Volatile
    private var lastComposeDownTime: Long = 0L

    @Volatile
    private var lastComposeDownX: Float = 0f

    @Volatile
    private var lastComposeDownY: Float = 0f

    /**
     * Intercepts and filters MotionEvents at the Activity or Window level.
     * Returns true if the event was consumed and should be dropped.
     */
    fun shouldFilterTouchEvent(context: Context, event: MotionEvent): Boolean {
        val action = event.actionMasked
        val density = context.resources.displayMetrics.density
        val scaledTouchSlop = try {
            ViewConfiguration.get(context).scaledTouchSlop.toFloat()
        } catch (_: Exception) {
            TOUCH_SLOP_FALLBACK_DP * density
        }
        val effectiveSlop = maxOf(scaledTouchSlop, TOUCH_SLOP_FALLBACK_DP * density)
        val eventTime = if (event.eventTime > 0L) event.eventTime else SystemClock.uptimeMillis()

        return processTouchEvent(
            action = action,
            x = event.x,
            y = event.y,
            eventTime = eventTime,
            touchSlop = effectiveSlop
        )
    }

    /**
     * Pure state machine for MotionEvent handling, testable without Context.
     */
    fun processTouchEvent(
        action: Int,
        x: Float,
        y: Float,
        eventTime: Long,
        touchSlop: Float
    ): Boolean {
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                val timeSinceLastDown = eventTime - lastDownTime
                val timeSinceLastUp = eventTime - lastUpTime
                val distFromLastDown = hypot(x - lastDownX, y - lastDownY)
                val distFromLastUp = hypot(x - lastUpX, y - lastUpY)

                val isDuplicate = (lastDownTime > 0L && timeSinceLastDown < ACCIDENTAL_DUPLICATE_THRESHOLD_MS && distFromLastDown <= touchSlop) ||
                        (lastUpTime > 0L && timeSinceLastUp < MIN_UP_TO_DOWN_INTERVAL_MS && distFromLastUp <= touchSlop)

                if (isDuplicate) {
                    isCurrentGestureFiltered = true
                    return true // Consume and discard duplicate event
                } else {
                    isCurrentGestureFiltered = false
                    lastDownTime = eventTime
                    lastDownX = x
                    lastDownY = y
                    return false // Valid tap, allow through
                }
            }

            MotionEvent.ACTION_MOVE -> {
                if (isCurrentGestureFiltered) {
                    return true // Discard move of a filtered duplicate
                }
                return false
            }

            MotionEvent.ACTION_UP -> {
                if (isCurrentGestureFiltered) {
                    isCurrentGestureFiltered = false
                    return true // Discard up of a filtered duplicate
                }
                lastUpTime = eventTime
                lastUpX = x
                lastUpY = y
                return false
            }

            MotionEvent.ACTION_CANCEL -> {
                if (isCurrentGestureFiltered) {
                    isCurrentGestureFiltered = false
                    return true
                }
                return false
            }

            MotionEvent.ACTION_POINTER_DOWN,
            MotionEvent.ACTION_POINTER_UP -> {
                // Multi-touch gestures (e.g. pinch-to-zoom) are preserved
                return false
            }

            else -> {
                return false
            }
        }
    }

    /**
     * Checks if a Compose PointerInputChange press is an accidental duplicate event.
     */
    fun shouldFilterPointerDown(x: Float, y: Float, eventTime: Long, touchSlop: Float): Boolean {
        val timeDelta = eventTime - lastComposeDownTime
        val dist = hypot(x - lastComposeDownX, y - lastComposeDownY)
        val isDuplicate = lastComposeDownTime > 0L && timeDelta < ACCIDENTAL_DUPLICATE_THRESHOLD_MS && dist <= touchSlop

        return if (isDuplicate) {
            true
        } else {
            lastComposeDownTime = eventTime
            lastComposeDownX = x
            lastComposeDownY = y
            false
        }
    }

    /**
     * Reset internal states (useful in unit tests).
     */
    fun reset() {
        lastDownTime = 0L
        lastDownX = 0f
        lastDownY = 0f
        lastUpTime = 0L
        lastUpX = 0f
        lastUpY = 0f
        isCurrentGestureFiltered = false
        lastComposeDownTime = 0L
        lastComposeDownX = 0f
        lastComposeDownY = 0f
    }
}

/**
 * Compose Modifier providing reliable touch debouncing at the Compose layout level.
 */
fun Modifier.reliableVirtualTouch(): Modifier = this.pointerInput(Unit) {
    awaitPointerEventScope {
        while (true) {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            val downChange = event.changes.firstOrNull { it.pressed && !it.previousPressed }
            if (downChange != null) {
                val now = SystemClock.uptimeMillis()
                val slop = 24.dp.toPx()
                val isFiltered = VirtualPhoneTouchHandler.shouldFilterPointerDown(
                    x = downChange.position.x,
                    y = downChange.position.y,
                    eventTime = now,
                    touchSlop = slop
                )
                if (isFiltered) {
                    event.changes.forEach { it.consume() }
                }
            }
        }
    }
}
