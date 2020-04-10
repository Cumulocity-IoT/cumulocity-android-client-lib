package com.softwareag.jc.common.kotlin.extensions

import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.*
import java.util.*


const val DATE_TOOLS_ISO861TZ = "yyyy-MM-dd'T'HH:mm:ssXXX"
const val DATE_TOOLS_ISO861 = "yyyy-MM-dd'T'HH:mm:ss.SSS"

class DateTools {

    companion object {

        fun Date?.isToday(): Boolean {
            return this.isSameDay(Calendar.getInstance().time)
        }

        fun Date?.isSameDay(date2: Date?): Boolean {

            if (this != null && date2 != null) {
                val cal1 = Calendar.getInstance()
                cal1.time = this
                val cal2 = Calendar.getInstance()
                cal2.time = date2
                return isSameDay(
                    cal1,
                    cal2
                )
            } else {
                return false
            }
        }

        fun isSameDay(cal1: Calendar?, cal2: Calendar?): Boolean {
            require(!(cal1 == null || cal2 == null)) { "The dates must not be null" }
            return cal1[Calendar.ERA] === cal2[Calendar.ERA] && cal1[Calendar.YEAR] === cal2[Calendar.YEAR] && cal1[Calendar.DAY_OF_YEAR] === cal2[Calendar.DAY_OF_YEAR]
        }

        fun dateTimeAtStartOfDay(): Date {

            val today: LocalDate = LocalDate.now(ZoneId.systemDefault())
            return Date.from(today.atStartOfDay(ZoneId.systemDefault()).toInstant())
        }

        fun Date?.dateToISO861(): String? {

            if (this == null)
                return null

            try {
                return SimpleDateFormat(DATE_TOOLS_ISO861TZ).format(this)
            } catch(e: ParseException) {
                // try without TZ
                return SimpleDateFormat(DATE_TOOLS_ISO861).format(this)
            }
        }

        fun String?.iSO861StringToDate(): Date {

            try {
                return SimpleDateFormat(DATE_TOOLS_ISO861TZ).parse(this)
            } catch(e: ParseException) {
                // try without TZ and set local time zone
                val date = SimpleDateFormat(DATE_TOOLS_ISO861).parse(this)

                // add local offset

                 return Date(date.time + TimeZone.getDefault().getOffset(date.time))
            }
        }

        fun Date?.shortDateString(): String {

            val myFormat = "EEE d MMMM" //In which you need put here
            val sdf = SimpleDateFormat(myFormat)

            return sdf.format(this)
        }

        fun Date?.timeString(): String {

            val pattern = "HH:mm"
            val simpleDateFormat = SimpleDateFormat(pattern)

            return simpleDateFormat.format(this)
        }
    }
}