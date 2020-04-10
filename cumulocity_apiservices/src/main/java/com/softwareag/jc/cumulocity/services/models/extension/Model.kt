package com.softwareag.jc.cumulocity.services.models.extension

import org.json.JSONObject

const val JC_MANAGED_OBJECT_MODEL = "xModels"

const val JC_MANAGED_OBJECT_MODEL_ID = "id"
const val JC_MANAGED_OBJECT_MODEL_NAME = "name"
const val JC_MANAGED_OBJECT_MODEL_LINK = "link"

data class Model(val id: String, val name: String, val link: String?) {

    constructor(o: JSONObject): this(o.getString(JC_MANAGED_OBJECT_MODEL_ID), o.getString(JC_MANAGED_OBJECT_MODEL_NAME),o.getString(JC_MANAGED_OBJECT_MODEL_LINK) ) {
    }
}