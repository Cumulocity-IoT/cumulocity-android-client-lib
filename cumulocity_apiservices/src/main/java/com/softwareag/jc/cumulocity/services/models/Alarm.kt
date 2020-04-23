package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.iSO861StringToDate
import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import org.json.JSONObject
import java.util.*

const val C8Y_ALARM_SOURCE = "source"
const val C8Y_ALARM_SOURCE_ID = "id"
const val C8Y_ALARM_TYPE = "type"
const val C8Y_ALARM_TIME = "time"
const val C8Y_ALARM_STATUS = "status"
const val C8Y_ALARM_SEVERITY = "severity"
const val C8Y_ALARM_TEXT = "text"

/**
 * Represents an alarm associated with a device.
 *
 * Formal definition is given in [Cumulocity REST API Guide](https://cumulocity.com/guides/reference/alarms/#alarm)
 *
 * @constructor Create a new alarm to be posted to Cumulocity
 * @property source Identifies the device to which the alarm is associated
 * @property type label identifying the type of alarm
 * @property status Identifies if the alarm is active, acknowledged or has been resolved (cleared)
 * @property severity One of Critical, Major, Minor or Warning
 * @property text arbitrary label describing reason for alarm
 */
data class Alarm(val source: Source, val type: String, val status: Status, val severity: Severity, val text: String):
    JsonSerializable {

    /**
     * Allowed values for Alarm Status
     *
     */
    enum class Status {
        /**
         * Alarm is currently live and has not yet been resolved
         */
        ACTIVE,

        /**
         * Alarm is still active, but has been acknowledged by someone
         */
        ACKNOWLEDGED,

        /**
         * Alarm has been resolved, is visible only for monitoring reasons
         */
        CLEARED
    }

    /**
     * Allowed values for Alarm Severity
     */
    enum class Severity {
        CRITICAL, MAJOR, MINOR, WARNING
    }

    /**
     * Wraps id to identify associated device
     */
    data class Source(val id: String):
        JsonSerializable {

        override fun toJSONString(): String {

            return JsonSerializable.toJSONString(this)
        }
    }

    /**
     * Date time when alarm was raised
     */
    val time: Date
        get() {
            return _dateTime
        }

    private var _id: String? = null
    private var _dateTime: Date

    init {
        _dateTime = Date()
    }

    /**
     * Alternative constructor used to parse Alarm fetched from Cumulocity.
     * NOTE: You do not need to use this method. It is called implicitly when using the [AlarmsService get] method
     *
     * @param o JSON object complying with [Cumulocity REST definition](https://cumulocity.com/guides/reference/alarms/#alarm)
     */
    constructor(o: JSONObject): this(
        Source(o.getJSONObject(C8Y_ALARM_SOURCE).getString(
            C8Y_ALARM_SOURCE_ID
        )), o.getString(C8Y_ALARM_TYPE), Status.valueOf(o.getString(C8Y_ALARM_STATUS)), Severity.valueOf(o.getString(
            C8Y_ALARM_SEVERITY
        )), o.getString(C8Y_ALARM_TEXT)) {

        _dateTime = o.getString(C8Y_ALARM_TIME).iSO861StringToDate()
    }

    /**
     * Ensures alarm can be transformed into a JSON String so that it can be sent to Cumulocity
     * NOTE: You do not need to use this method. It is called implicitly when using the [AlarmsService post] or
     * [AlarmsService put] method

     *
     * @return JSON formatted string complying to [Cumulocity REST definition](https://cumulocity.com/guides/reference/alarms/#alarm)
     */
    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }
}