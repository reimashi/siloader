package com.github.reimashi.siloader.data

import com.github.reimashi.siloader.services.DatabaseObject

class TimeRecord : DatabaseObject {
    var year: Int = 1950

    var month: Int = 1
        set(value) { if (value > 12) field = 12 else if (value < 1) field = 0 else field = value }

    var day: Int = 1
        set(value) { if (value > 31) field = 31 else if (value < 1) field = 0 else field = value }

    var hour: Int = 0
        set(value) { if (value > 23) field = 23 else if (value < 0) field = 0 else field = value }

    var minute: Int = 0
        set(value) { if (value > 59) field = 59 else if (value < 0) field = 0 else field = value }

    var second: Int = 0
        set(value) { if (value > 59) field = 59 else if (value < 0) field = 0 else field = value }

    override fun getFields(): Map<String, Any?> {
        return hashMapOf(
            "year" to year,
            "month" to month,
            "day" to day,
            "hour" to hour,
            "minute" to minute,
            "second" to second
        )
    }

    override fun getTable(): String {
        return "time"
    }
}