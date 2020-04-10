package com.softwareag.jc.cumulocity.services.models.extension

import org.json.JSONObject

const val CY_LORA_DEVICE = "cylora-device"

const val CY_LORA_DEVICE_CODEC = "lora_codec_DeviceCodecRepresentation"
const val CY_LORA_DEVICE_LNS_INSTANCE = "LNSType"
const val CY_LORA_DEVICE_APP_KEY = "appKey"
const val CY_LORA_DEVICE_APP_EUI = "appEUI"
const val CY_LORA_DEVICE_CODEC_NAME = "name"
const val CY_LORA_DEVICE_CODEC_ID = "id"

data class LoRaDeviceInfo(val codecId: String?, val codecName: String?, val appKey: String?, val appEUI: String?) {

    constructor(o: JSONObject): this(o.getJSONObject(CY_LORA_DEVICE_CODEC).getString(CY_LORA_DEVICE_CODEC_ID),
                                        o.getJSONObject(CY_LORA_DEVICE_CODEC).getString(CY_LORA_DEVICE_CODEC_NAME),
                                        appKey(o),
                                        appEUI(o)) {
    }

    companion object {

        fun appKey(o: JSONObject): String? {
            return if (o.has(CY_LORA_DEVICE_APP_KEY))
                return o.getString(CY_LORA_DEVICE_APP_KEY)
            else
                null
        }

        fun appEUI(o: JSONObject): String? {
            return if (o.has(CY_LORA_DEVICE_APP_EUI))
                return o.getString(CY_LORA_DEVICE_APP_EUI)
            else
                null
        }
    }
}

