package com.softwareag.jc.common.kotlin.extensions

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.dateToISO861
import java.lang.StringBuilder
import java.lang.reflect.Method
import java.util.*

interface JsonSerializable {

    fun toJSONString(): String

    companion object {

        fun toJSONString(label: String, value: JsonSerializable): String {

            val builder: StringBuilder = StringBuilder()
            builder.append("{")
            _keyValuePairToJSONString(
                label,
                value,
                builder
            )

            if (builder.endsWith(","))
                builder.deleteCharAt(builder.length-1)

            builder.append("}")

            return builder.toString()
        }

        fun toJSONString(o: JsonSerializable): String {

            val methods: Array<Method> = o.javaClass.declaredMethods

            val builder: StringBuilder = StringBuilder()

            builder.append("{")
            methods.forEach { m ->

                if (m.name.startsWith("get") && m.parameterCount == 0) {

                    var k: String = m.name.substring(3, 4).toLowerCase() + m.name.substring(4)
                    val v: Any? = m.invoke(o)

                    if (k.indexOf(".") != -1)
                        k = k.substring(k.lastIndexOf(".")+1)

                    //Log.i("debug", "public - ${k}")

                    _keyValuePairToJSONString(
                        k,
                        v,
                        builder
                    )
                }
            }

            /*if (o is PropertiesBase) {
                // add custom props too

                (o as PropertiesBase).properties.forEach { (k, v) ->
                    _keyValuePairToJSONString(k, v, builder)
                }
            }*/

            if (builder.endsWith(","))
                builder.deleteCharAt(builder.length-1)

            builder.append("}")

            return builder.toString()
        }

        private fun _keyValuePairToJSONString(k: String, v: Any?, builder: StringBuilder) {

            if (v != null) {

                if (v is List<*> && v.size > 0) {

                    builder.append("\"${k}\": [")
                    (v as List<*>).forEach { x ->
                        if (x is String || x is Int || x is Float || x is Double || x is Long || x is Boolean) {
                            builder.append("\"$x\",")
                        } else if (x is JsonSerializable) {
                            builder.append((x as JsonSerializable).toJSONString()).append(",")
                        }
                    }

                    if (builder.endsWith(","))
                        builder.deleteCharAt(builder.length-1)

                    builder.append("],")
                }
                else if (v is Map<*, *>) {

                    (v as Map<*, *>).forEach { (k, v) ->
                        builder.append("\"$k\":")

                        if (v is Number || v is Boolean)
                            builder.append("$v,")
                        if (v is String) {
                            builder.append("\"$v\",")
                        } else if (v is JsonSerializable) {
                            builder.append((v as JsonSerializable).toJSONString()).append(",")
                        }
                    }

                    //if (builder.endsWith(","))
                    //    builder.deleteCharAt(builder.length-1)
                }
                else {
                    _toJSONString(
                        k,
                        v,
                        builder
                    )
                }
            }
        }

        private fun _toJSONString(k: String, prop: Any, builder: StringBuilder) {

            if (prop is Number || prop is Boolean)
                builder.append("\"$k\":").append("${prop},")
            if (prop is String) {
                builder.append("\"$k\":").append("\"${prop}\",")
            } else if (prop is Date) {
                builder.append("\"$k\":").append("\"${prop.dateToISO861()}\",")
            } else if (prop is Enum<*>) {
                builder.append("\"$k\":").append("\"${prop}\",")
            } else if (prop is JsonSerializable) {
                builder.append("\"$k\":").append(prop.toJSONString()).append(",")
            } else { //ignore
               // builder.append("\"${prop}\",")
            }
        }
    }
}