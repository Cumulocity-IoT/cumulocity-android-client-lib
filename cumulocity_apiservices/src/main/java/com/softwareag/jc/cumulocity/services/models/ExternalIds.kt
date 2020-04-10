package com.softwareag.jc.cumulocity.services.models

import org.json.JSONArray
import org.json.JSONObject

const val C8Y_MANAGED_EXTIDS_API = "/identity/globalIds/<DEVICE>/externalIds"

const val C8Y_MANAGED_EXTIDS_OBJECTS = "externalIds"

/**
 * Represents a collection of external id's associated with a single [ManagedObject]
 */
class ExternalIds(private val o: JSONObject) {

    init {

    }

    fun objects(): List<ExternalId> {

        val m:ArrayList<ExternalId> = ArrayList()

        var l: JSONArray = o.getJSONArray(C8Y_MANAGED_EXTIDS_OBJECTS)

        for (i in 0 until l.length()) {

            m.add(ExternalId(l.getJSONObject(i)))
        }

        return m
    }
}