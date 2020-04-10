package com.softwareag.jc.cumulocity.services.models.extension

import org.json.JSONObject

const val CY_LORA_NETWORK = "cylora-network"

const val CY_LORA_NETWORK_LNS = "lora_ns_LNSInstanceRepresentation"
const val CY_LORA_NETWORK_LNS_TYPE = "type"
const val CY_LORA_NETWORK_LNS_PROPERTIES = "properties"
const val CY_LORA_NETWORK_LNS_PROPERTIES_APIKEY = "apikey"
const val CY_LORA_NETWORK_LNS_PROPERTIES_ID = "id"
const val CY_LORA_NETWORK_LNS_PROPERTIES_USER = "user"
const val CY_LORA_NETWORK_LNS_PROPERTIES_PASSWORD = "password"

data class LoRaNetorkInfo(val lnsId: String, val lnsType: String, val apiKey: String?, val user: String?, val password: String?) {

    constructor(o: JSONObject): this(lnsInstanceId(o), o.getJSONObject(CY_LORA_NETWORK_LNS).getString(CY_LORA_NETWORK_LNS_TYPE), apiKey(o), user(o), password(o)) {
    }

    companion object {

        fun lnsInstanceId(o: JSONObject): String {

            return o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).getString(
                    CY_LORA_NETWORK_LNS_PROPERTIES_ID)
        }

        fun apiKey(o: JSONObject): String? {

            return if (o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).has(CY_LORA_NETWORK_LNS_PROPERTIES_APIKEY))
                o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).getString(CY_LORA_NETWORK_LNS_PROPERTIES_APIKEY)
            else
                null
        }

        fun user(o: JSONObject): String? {

            return if (o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).has(CY_LORA_NETWORK_LNS_PROPERTIES_USER))
                o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).getString(CY_LORA_NETWORK_LNS_PROPERTIES_PASSWORD)
            else
                null
        }

        fun password(o: JSONObject): String? {

            return if (o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).has(CY_LORA_NETWORK_LNS_PROPERTIES_USER))
                o.getJSONObject(CY_LORA_NETWORK_LNS).getJSONObject(CY_LORA_NETWORK_LNS_PROPERTIES).getString(CY_LORA_NETWORK_LNS_PROPERTIES_PASSWORD)
            else
                null
        }
    }
}

