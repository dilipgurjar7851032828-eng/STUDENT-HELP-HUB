package com.example.ui.touch

import android.os.SystemClock
import android.view.KeyEvent

/**
 * Global Virtual Phone Keyboard & Input Event Handler & Debouncer.
 *
 * Solves the known browser streaming emulator / virtual phone issue where physical
 * keyboard keystrokes produce duplicate synthetic KeyEvent / input events (within 0-70ms)
 * for the same key, causing a single physical keypress to enter a character twice.
 *
 * Rules:
 * 1. One physical key press = exactly ONE character/input (duplicate DOWN events within 75ms dropped).
 * 2. Rapid typing on different keys (e.g. fast typing of words) is never blocked.
 * 3. Intentional repeat of the same key (e.g. double letters like "oo" in "book" or "ll" in "hello")
 *    in normal human typing cadence (>= 100ms) is fully preserved.
 * 4. Intentional key holding / auto-repeat (repeatCount > 0, e.g. holding Backspace or arrow keys)
 *    is fully preserved while filtering duplicate repeat packets.
 * 5. Dropped duplicate DOWN events have their duplicate UP events cleanly consumed to prevent orphan states.
 * 6. Works universally across all text fields, search bars, login, account creation, and dialogs.
 */
object VirtualPhoneKeyHandler {

    // Threshold below which rapid consecutive DOWN events for the same key code are treated as synthetic duplicates
    const val ACCIDENTAL_KEY_DUPLICATE_THRESHOLD_MS = 75L

    // Minimum interval after ACTION_UP before a new ACTION_DOWN for the same key is considered intentional
    const val MIN_KEY_UP_TO_DOWN_INTERVAL_MS = 45L

    // Threshold for duplicate ACTION_UP events for the same key
    const val DUPLICATE_KEY_UP_THRESHOLD_MS = 45L

    // Threshold for duplicate auto-repeat events
    const val DUPLICATE_REPEAT_THRESHOLD_MS = 35L

    @Volatile
    var lastDownKey: Int = KeyEvent.KEYCODE_UNKNOWN

    @Volatile
    var lastDownTime: Long = 0L

    @Volatile
    var lastDownEventTime: Long = 0L

    @Volatile
    var lastRepeatCount: Int = -1

    @Volatile
    var lastUpKey: Int = KeyEvent.KEYCODE_UNKNOWN

    @Volatile
    var lastUpTime: Long = 0L

    @Volatile
    var isKeyDown: Boolean = false

    @Volatile
    var wasLastDownDuplicateFiltered: Boolean = false

    @Volatile
    var lastFilteredKey: Int = KeyEvent.KEYCODE_UNKNOWN

    @Volatile
    var lastFilteredEventTime: Long = 0L

    /**
     * Intercepts and filters KeyEvents at the Activity level before window dispatch.
     * Returns true if the event is a duplicate and should be consumed/dropped.
     */
    fun shouldFilterKeyEvent(event: KeyEvent): Boolean {
        val now = SystemClock.uptimeMillis()
        val eventTime = if (event.eventTime > 0L) event.eventTime else now
        val downTime = if (event.downTime > 0L) event.downTime else eventTime

        return processKeyEvent(
            action = event.action,
            keyCode = event.keyCode,
            repeatCount = event.repeatCount,
            eventTime = eventTime,
            downTime = downTime
        )
    }

    /**
     * Pure state machine for KeyEvent handling, testable without Android Context or Emulator.
     */
    fun processKeyEvent(
        action: Int,
        keyCode: Int,
        repeatCount: Int,
        eventTime: Long,
        downTime: Long
    ): Boolean {
        // Unknown key codes are passed through
        if (keyCode == KeyEvent.KEYCODE_UNKNOWN) {
            return false
        }

        when (action) {
            KeyEvent.ACTION_DOWN -> {
                val timeSinceLastDown = eventTime - lastDownEventTime
                val timeSinceLastUp = eventTime - lastUpTime
                val isSameKey = (keyCode == lastDownKey)

                // 1. Check auto-repeat (holding down key): repeatCount > 0
                if (repeatCount > 0) {
                    if (isSameKey && repeatCount == lastRepeatCount && timeSinceLastDown < DUPLICATE_REPEAT_THRESHOLD_MS) {
                        // Duplicate repeat event
                        return true
                    }
                    // Valid intentional auto-repeat (e.g. holding backspace to delete text)
                    lastDownEventTime = eventTime
                    lastRepeatCount = repeatCount
                    isKeyDown = true
                    wasLastDownDuplicateFiltered = false
                    return false
                }

                // 2. Initial press (repeatCount == 0):
                // Check if this is an accidental synthetic duplicate of the same physical key press
                val isIdenticalDownTime = isSameKey && downTime > 0L && downTime == lastDownTime && lastDownEventTime > 0L
                val isRapidDownDuplicate = isSameKey && lastDownEventTime > 0L && timeSinceLastDown < ACCIDENTAL_KEY_DUPLICATE_THRESHOLD_MS
                val isRapidUpToDownDuplicate = (keyCode == lastUpKey) && lastUpTime > 0L && timeSinceLastUp < MIN_KEY_UP_TO_DOWN_INTERVAL_MS
                val isDuplicateWhileAlreadyDown = isSameKey && isKeyDown && timeSinceLastDown < 100L

                val isDuplicate = isIdenticalDownTime || isRapidDownDuplicate || isRapidUpToDownDuplicate || isDuplicateWhileAlreadyDown

                if (isDuplicate) {
                    wasLastDownDuplicateFiltered = true
                    lastFilteredKey = keyCode
                    lastFilteredEventTime = eventTime
                    // Consume the duplicate event so exactly ONE character is entered
                    return true
                } else {
                    // Valid single key press
                    lastDownKey = keyCode
                    lastDownTime = downTime
                    lastDownEventTime = eventTime
                    lastRepeatCount = 0
                    isKeyDown = true
                    wasLastDownDuplicateFiltered = false
                    return false
                }
            }

            KeyEvent.ACTION_UP -> {
                val timeSinceLastUp = eventTime - lastUpTime
                val isSameAsUpKey = (keyCode == lastUpKey)
                val isSameAsFilteredKey = (keyCode == lastFilteredKey && wasLastDownDuplicateFiltered)
                val timeSinceFilteredDown = eventTime - lastFilteredEventTime

                // If this UP corresponds to an already-filtered duplicate DOWN, or is a duplicate UP within threshold
                if ((isSameAsUpKey && timeSinceLastUp < DUPLICATE_KEY_UP_THRESHOLD_MS) ||
                    (isSameAsFilteredKey && timeSinceFilteredDown < 80L && !isKeyDown)
                ) {
                    // Consume duplicate UP
                    return true
                }

                // Valid key release
                lastUpKey = keyCode
                lastUpTime = eventTime
                isKeyDown = false
                wasLastDownDuplicateFiltered = false
                return false
            }

            KeyEvent.ACTION_MULTIPLE -> {
                val timeSinceLastDown = eventTime - lastDownEventTime
                if (keyCode == lastDownKey && timeSinceLastDown < ACCIDENTAL_KEY_DUPLICATE_THRESHOLD_MS) {
                    return true
                }
                return false
            }

            else -> {
                return false
            }
        }
    }

    /**
     * Reset internal states (useful in unit tests).
     */
    fun reset() {
        lastDownKey = KeyEvent.KEYCODE_UNKNOWN
        lastDownTime = 0L
        lastDownEventTime = 0L
        lastRepeatCount = -1
        lastUpKey = KeyEvent.KEYCODE_UNKNOWN
        lastUpTime = 0L
        isKeyDown = false
        wasLastDownDuplicateFiltered = false
        lastFilteredKey = KeyEvent.KEYCODE_UNKNOWN
        lastFilteredEventTime = 0L
    }
}
