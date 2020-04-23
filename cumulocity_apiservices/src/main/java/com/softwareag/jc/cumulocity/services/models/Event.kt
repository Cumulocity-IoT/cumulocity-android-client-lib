package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.iSO861StringToDate
import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

const val C8Y_EVENT_SOURCE = "source"
const val C8Y_EVENT_SOURCE_ID = "id"
const val C8Y_EVENT_TYPE = "type"
const val C8Y_EVENT_TEXT = "text"
const val C8Y_EVENT_TIME = "time"

/**
 * Represents an event, non measurable activity triggered by or for a device
 *
 * Formal definition is given in [Cumulocity REST API Guide](https://cumulocity.com/guides/reference/events/#event)
 *
 * @constructor Create a new Event to be posted to Cumulocity
 * @param source identifies associated device/object
 * @param type label to allow events to be grouped/typed
 * @param text arbritary text describing event
 */
data class Event(val source: Source, val type: String, val text: String):
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

    private var _events: HashMap<String, Any> = HashMap()

    /**
     * Allows supplementary adhoc information to be included in event details
     */
    val properties: Map<String, Any>
        get() {
            return _events
        }
    private var _id: String? = null
    private var _dateTime: Date

    init {
        _dateTime = Date()
    }

    /**
     * Alternative constructor used internally to parse JSON response returned by Cumulocity
     */
    constructor(o: JSONObject): this(Source(o.getJSONObject(C8Y_EVENT_SOURCE).getString(C8Y_EVENT_SOURCE_ID)), o.getString(C8Y_EVENT_TYPE), o.getString(C8Y_EVENT_TEXT)) {

        _captureValues(o)
        _dateTime = o.getString(C8Y_EVENT_TIME).iSO861StringToDate()
        _events = getProperties(o)
    }

    /**
     * Allows arbitrary values to be included in event details
     */
    fun addValue(label: String, event: Any) {

        if (event is JsonSerializable)
            (properties as HashMap).put(label, event)
        else
            (properties as HashMap).put(label, AnyEventValue(event))
    }

    /**
     * Includes GPS formatted position 'c8y_LocationUpdate' to be included in event details
     */
    fun addPosition(position: Position) {

        (properties as HashMap).put("c8y_LocationUpdate", position)
    }

    /**
     * Allows this object type to converted to a JSON String
     * This method is used internally, you do not need to use it
     */
    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }

    private fun _captureValues(o: JSONObject) {

        o.keys().forEach { k ->

            if (k != C8Y_EVENT_SOURCE && k != C8Y_EVENT_TYPE && k != C8Y_EVENT_TIME && o.get(k) is JSONObject) {
                (properties as HashMap).put(k, JSonEventValue(o.getJSONObject(k)))
            }
        }
    }

    companion object {

        private val exclude = listOf("creationTime", "source", "type", "self", "time", "text", "id")

        private fun getProperties(o: JSONObject): HashMap<String, Any> {

            return _getProperties(o, exclude)
        }

        private fun _getProperties(o: JSONObject, exc: List<String>?): HashMap<String, Any> {

            val props: HashMap<String, Any> = HashMap()

            o.keys().forEach { k ->

                if (exc == null || !exc!!.contains(k)) {

                    if (o.get(k) is JSONObject) {
                        props[k] = _getProperties(o.getJSONObject(k), null)
                    } else {
                        props[k] = o.get(k)
                    }
                }
            }

            return props
        }
    }

    class AnyEventValue(o: Any): HashMap<String, Any>(),
        JsonSerializable {

        override fun toJSONString(): String {
            return JsonSerializable.toJSONString(this)
        }
    }

    class JSonEventValue(o: JSONObject): HashMap<String, Any>(),
        JsonSerializable {

        init {

            val keys: MutableIterator<String>? = o.keys()

            while (keys!!.hasNext()) {
                val k: String = keys.next()

                super.put(k, o.get(k))
            }
         }

         override fun toJSONString(): String {
             return JsonSerializable.toJSONString(this)
         }
     }
}