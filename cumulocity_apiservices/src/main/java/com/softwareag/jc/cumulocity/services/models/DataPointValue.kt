package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import org.json.JSONObject

const val C8Y_MANAGED_OBJECT_DATA_POINTS = "c8y_DataPoint"

const val C8Y_MANAGED_OBJECT_DATAPOINT_FRAGMENT = "fragment"
const val C8Y_MANAGED_OBJECT_DATAPOINT_UNIT = "unit"
const val C8Y_MANAGED_OBJECT_DATAPOINT_COLOR = "color" // rgb e.g. #ffffff
const val C8Y_MANAGED_OBJECT_DATAPOINT_SERIES = "series"
const val C8Y_MANAGED_OBJECT_DATAPOINT_LINE_TYPE = "lineType"
const val C8Y_MANAGED_OBJECT_DATAPOINT_LABEL = "label"
const val C8Y_MANAGED_OBJECT_DATAPOINT_ID = "_id"
const val C8Y_MANAGED_OBJECT_DATAPOINT_RENDER_TYPE = "renderType" // e.g. min

/**
 * Represents a set of DataPoints for a [ManagedObject], which allows measurements captured by
 * Cumulocity to be displayed more intuitively by prefacing them with unit labels, line colors etc.
 * @constructor Creates a new empty DataPoints holder
 */
class DataPoints(): JsonSerializable {

    /**
     * Groups the data points by key
     */
    val values: Map<String, Value> = HashMap()

    /**
     * Alternative constructor to allow JSON string representation to be converted to object
     * You do not need to use this method, called implicitly by the [ManagedObjectService] class
     * when fetching [ManagedObject]s from Cumulocity
     */
    constructor(o: JSONObject): this() {

        Value.make(o, values as HashMap<String, Value>)
    }

    /**
     * Allows this object type to converted to a JSON String
     * You do not need to use this method directly, instead use the [ManagedObjectService] class
     */
    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }

    /**
     * Represents a single DataPoint value
     * @constructor create a new DataPoint to allow measurements to be contextualised
     * @param id id of the of the datapoint
     * @param fragment name of the measurement that this data point is referencing
     * @param label human readable label to identify the measurement
     * @param unit human readable unit of measure for the measurement value
     * @param series required if the measurement comprises sub values or values in different units
     * @param lineType clue for drawing tools to determine how this measurement should be displayed
     * @param color clue from drawing tools to determine what the color of the measurement
     */
    class Value(val id: String, val fragment: String, val label: String, val unit: String, val series: String, val lineType: String, val color: String, val renderType: String):
        JsonSerializable {

        /**
         * Allows this object type to converted to a JSON String
         * You do not need to use this method directly, instead use the [ManagedObjectService] class
         */
        override fun toJSONString(): String {

            return JsonSerializable.toJSONString(this)
        }

        companion object {

            /**
             * Utility method to allow values to be built
             */
             fun make(a: JSONObject, map: HashMap<String, Value>) {

                map.clear()

                a.keys().forEach { k ->

                    val v: JSONObject = a.getJSONObject(k)

                    map[k] = Value(k, v.getString(C8Y_MANAGED_OBJECT_DATAPOINT_FRAGMENT), v.getString(C8Y_MANAGED_OBJECT_DATAPOINT_LABEL), v.getString(C8Y_MANAGED_OBJECT_DATAPOINT_UNIT), v.getString(C8Y_MANAGED_OBJECT_DATAPOINT_SERIES), v.getString(
                        C8Y_MANAGED_OBJECT_DATAPOINT_LINE_TYPE), v.getString(C8Y_MANAGED_OBJECT_DATAPOINT_COLOR), v.getString(C8Y_MANAGED_OBJECT_DATAPOINT_RENDER_TYPE))
                }
            }
        }
    }
}
