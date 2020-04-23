package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.iSO861StringToDate
import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import com.softwareag.jc.cumulocity.services.models.MeasurementValue

import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

const val C8Y_MESUREMENT_SOURCE = "source"
const val C8Y_MESUREMENT_SOURCE_ID = "id"
const val C8Y_MESUREMENT_TYPE = "type"
const val C8Y_MESUREMENT_TIME = "time"

/**
 * Represents individual measurements that can be queried, created and deleted
 *
 * Formal definition is given in [Cumulocity REST API Guide](https://cumulocity.com/guides/reference/measurements/#measurement)
 *
 * @constructor allows a new measurement to be created (Use prior to calling [MeasurementsService]
 */
data class Measurement(val source: Source, val type: String):
    JsonSerializable {

    data class Source(val id: String):
        JsonSerializable {

        override fun toJSONString(): String {

            return JsonSerializable.toJSONString(this)
        }
    }

    val time: Date
        get() {
            return _dateTime
        }

    val values: HashMap<String, MeasurementValue> = HashMap()

    private var _id: String? = null
    private var _dateTime: Date

    init {
        _dateTime = Date()
    }

    constructor(o: JSONObject): this(Source(o.getJSONObject(C8Y_MESUREMENT_SOURCE).getString(C8Y_MESUREMENT_SOURCE_ID)), o.getString(C8Y_MESUREMENT_TYPE)) {

        _captureValues(o)
        _dateTime = o.getString(C8Y_MESUREMENT_TIME).iSO861StringToDate()
    }

    fun addValue(label: String, unit: String, value: Number) {
        (values as HashMap).put(label, MeasurementValue(label, unit, value))
    }

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }

    private fun _captureValues(o: JSONObject) {

        o.keys().forEach { k ->

            if (k != C8Y_MESUREMENT_SOURCE && k != C8Y_MESUREMENT_TYPE && k != C8Y_MESUREMENT_TIME && o.get(k) is JSONObject) {
                (values as HashMap).put(k, MeasurementValue(o.getJSONObject(k)))
            }
        }
    }
}