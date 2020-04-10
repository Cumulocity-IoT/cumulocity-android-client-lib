package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.iSO861StringToDate
import org.json.JSONObject
import java.util.*

const val C8Y_MSERIES_SERIES = "series"
const val C8Y_MSERIES_SERIES_NAME = "name"
const val C8Y_MSERIES_SERIES_TYPE = "type"
const val C8Y_MSERIES_SERIES_UNIT = "unit"
const val C8Y_MSERIES_VALUES = "values"
const val C8Y_MSERIES_VALUES_MIN = "min"
const val C8Y_MSERIES_VALUES_MAX = "max"

/**
 * Represents a series of measurements captured over a period of time that can be queried, created and deleted
 *
 * Formal definition is given in [Cumulocity REST API Guide](https://cumulocity.com/guides/reference/measurements/)
 *
 */
data class MeasurementSeries(val name: String, val type: String, val unit: String, val values: List<Value>) {

    data class Value(val date: Date, val min: Long, val max: Long) {

    }

    constructor(o: JSONObject): this(_captureSeries(o, C8Y_MSERIES_SERIES_NAME),
                                        _captureSeries(o, C8Y_MSERIES_SERIES_TYPE),
                                        _captureSeries(o,C8Y_MSERIES_SERIES_UNIT),
                                        _captureValues(o.getJSONObject(C8Y_MSERIES_VALUES))) {
    }

    companion object {

        fun _captureSeries(o: JSONObject, label: String): String {

            if (o.has(C8Y_MSERIES_SERIES)) {
                var a = o.getJSONArray(C8Y_MSERIES_SERIES)

                if (a.length() > 0) {
                    return a.getJSONObject(0).getString(label)
                } else {
                    return ""
                }
            } else {
                return ""
            }
        }

        fun _captureValues(o: JSONObject): List<Value> {

            val out: ArrayList<Value> = ArrayList()

            var keys = o.keys()

            while(keys.hasNext()) {
                val k = keys.next()
                val obj = o.getJSONArray(k)

                for (i in 0 until obj.length()) {
                    out.add(Value(k.iSO861StringToDate(), obj.getJSONObject(i).getLong(C8Y_MSERIES_VALUES_MIN), obj.getJSONObject(i).getLong(C8Y_MSERIES_VALUES_MAX)))
                }
            }

            return out
        }
    }
}