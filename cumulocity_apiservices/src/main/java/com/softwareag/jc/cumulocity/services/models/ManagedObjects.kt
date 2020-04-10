package com.softwareag.jc.cumulocity.services.models

import org.json.JSONArray
import org.json.JSONObject


const val C8Y_MANAGED_OBJECTS_API = "/inventory/managedObjects"
const val C8Y_MANAGED_OBJECTS_EXT_API = "/identity/externalIds/<TYPE>/<EXTID>"

const val C8Y_TYPE_DEVICE_TYPE = "c8y_DeviceGroup"

const val C8Y_MANAGED_OBJECTS_OBJECTS = "managedObjects"

/**
 * A collection of [ManagedObject]s as returned by one of the methods of [ManagedObjectService] class
 */
data class ManagedObjects(private val o: JSONObject) {

    init {

    }

    fun objects(): List<ManagedObject> {

        val m:ArrayList<ManagedObject>  = ArrayList()

        var l: JSONArray = o.getJSONArray(C8Y_MANAGED_OBJECTS_OBJECTS)

        for (i in 0 until l.length()) {

            val obj = ManagedObject(l.getJSONObject(i))

            if (obj.type != null)
                m.add(obj)
        }


        return m
    }

}