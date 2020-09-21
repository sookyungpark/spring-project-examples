package com.customproject.coffeeshop.api.support

import com.customproject.coffeeshop.api.exception.InvalidValueException
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.chrono.IsoChronology
import java.time.format.DateTimeFormatter

class DateTimeSupport {
    companion object {
        public val FORMATTER_UUUUMMDDHHMMSS: DateTimeFormatter = DateTimeFormatter.ofPattern("uuuuMMddHHmmss")
                .withChronology(IsoChronology.INSTANCE)

        private val MIN_TIMESTAMP = Timestamp.valueOf(LocalDateTime.of(1970, 1, 1, 0, 0)).nanos

        @Throws(Exception::class)
        fun validate(value: String, formatter: DateTimeFormatter) {
            deserialize(
                    time = value,
                    formatter = formatter
            ).let {
                if (it < MIN_TIMESTAMP) {
                    throw InvalidValueException("date is invalid")
                }
            }
        }

        fun generate(dateTimeFormatter: DateTimeFormatter): String {
            return ZonedDateTime.now().format(dateTimeFormatter)
        }

        @Throws(Exception::class)
        fun serialize(time: Long, formatter: DateTimeFormatter) : String {
            val instant = Instant.ofEpochMilli(time)
            val zonedDateTime = ZonedDateTime.ofInstant(instant, ZoneOffset.UTC)
            return zonedDateTime.format(formatter)
        }

        @Throws(Exception::class)
        fun deserialize(time: String, formatter: DateTimeFormatter): Long {
            try {
                val zdt = LocalDateTime.parse(time, formatter)
                return Timestamp.valueOf(zdt).time + (1000 * 60 * 60 * 9) // timezone + 9
            } catch (e: Exception) {
                throw InvalidValueException("cannot deserialize datetime: $time", e)
            }
        }
    }
}