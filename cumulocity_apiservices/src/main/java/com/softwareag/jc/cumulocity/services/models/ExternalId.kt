package com.softwareag.jc.cumulocity.services.models

import org.json.JSONObject

const val C8Y_MANAGED_EXTID_REF = "externalId"
const val C8Y_MANAGED_EXTID_TYPE = "type"

/**
 * Represents an external id associated with a [ManagedObject]
 */
class ExternalId(val type: String, val externalId: String) {


    constructor(o: JSONObject): this( o.getString(C8Y_MANAGED_EXTID_TYPE), o.getString(C8Y_MANAGED_EXTID_REF)) {
    }

    override fun toString(): String {

        return "${pad(type)}: $externalId"
    }

    private fun pad(v: String): String {

        val r: Int = 10 - v.length

        if (r > 0)
            return v.padStart(r, ' ')
        else
            return v
    }
}