package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import org.json.JSONObject

const val C8Y_MESUREMENT_VALUE_UNIT = "unit"
const val C8Y_MESUREMENT_VALUE_VALUE = "value"


/**
 * Individual measurement consisting of a human readable label, its unit of measure and the value
 */
data class MeasurementValue(val label: String, val value: ValueHolder): JsonSerializable {

    data class ValueHolder(val unit: String?, val value: Number): JsonSerializable {

        constructor(o: JSONObject): this(unit(o), o.get(C8Y_MESUREMENT_VALUE_VALUE) as Number)

        override fun toJSONString(): String {

            return JsonSerializable.toJSONString(this)
        }

        companion object {

            fun unit(o: JSONObject): String? {

                return if (o.has(C8Y_MESUREMENT_VALUE_UNIT))
                    o.getString(C8Y_MESUREMENT_VALUE_UNIT)
                else
                    null
            }
        }
    }

    constructor(o: JSONObject): this(MeasurementValue.label(o), MeasurementValue.captureValue(o)) {

    }

    constructor(label: String, unit: String, value: Number): this(label, ValueHolder(unit, value)) {

    }

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(label, value)
    }

    companion object {

        fun label(o: JSONObject): String {

            var label: String? = null
            var keys: MutableIterator<String>? = o.keys()

            while (keys!!.hasNext()) {
                val k: String = keys.next()

                if (o.get(k) is JSONObject) {
                    label = k
                    break
                }
            }

            return label!!
        }

        fun captureValue(o: JSONObject): ValueHolder {

            return ValueHolder(o.getJSONObject(MeasurementValue.label(o)))
        }
    }
}