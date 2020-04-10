package com.softwareag.jc.cumulocity.services.models.extension

import org.json.JSONObject

const val JC_MANAGED_OBJECT_INFO = "xGroup"

const val JC_MANAGED_OBJECT_INFO_NAME = "xGroupDescription"
const val JC_MANAGED_OBJECT_INFO_ADDRESS = "xGroupAddress"

data class Info(val siteName: String, val siteAddress: String) {

    constructor(o: JSONObject): this(o.getString(JC_MANAGED_OBJECT_INFO_NAME), o.getString(JC_MANAGED_OBJECT_INFO_ADDRESS)) {
    }
}