package com.softwareag.jc.cumulocity.services.models.extension

import org.json.JSONObject

const val JC_MANAGED_OBJECT_SUPPLIER = "xSuppliers"

const val JC_MANAGED_OBJECT_SUPPLIER_ID = "id"
const val JC_MANAGED_OBJECT_SUPPLIER_NAME = "name"
const val JC_MANAGED_OBJECT_SUPPLIER_NETWORK_TYPE = "networkType"
const val JC_MANAGED_OBJECT_SUPPLIER_SITE = "site"

data class Supplier(val id: String, val name: String, val networkType: String?, val site: String?) {

    constructor(o: JSONObject): this(o.getString(JC_MANAGED_OBJECT_SUPPLIER_ID), o.getString(JC_MANAGED_OBJECT_SUPPLIER_NAME),o.getString(JC_MANAGED_OBJECT_SUPPLIER_NETWORK_TYPE), o.getString(JC_MANAGED_OBJECT_SUPPLIER_SITE)) {
    }
}