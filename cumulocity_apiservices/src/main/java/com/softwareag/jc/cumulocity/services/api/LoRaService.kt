package com.softwareag.jc.cumulocity.services.api

import com.softwareag.jc.common.api.ConnectionRequest
import com.softwareag.jc.common.api.Method
import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import com.softwareag.jc.cumulocity.services.models.User

const val C8Y_LORA_OBJENIOUS_MS = "/service/lora-ns-objenious/<NETWORK>/devices"

/**
 * Custom class to leverage LoRa agent developed by Cyril Poder - cyril.poder@softwarag.com
 */
class LoRaService(connection: CumulocityConnectionFactory.CumulocityConnection) : ConnectionRequest<User, String>(connection) {

    private var _network: String? = null
    private var _id: String? = null

    data class LoRaDeviceActivation(val name: String, val appEUI: String, val appKey: String, val devEUI: String, val deviceModel: String?, val lat: Double?, val lng: Double?):
        JsonSerializable {

        override fun toJSONString(): String {

            return JsonSerializable.toJSONString(this)
        }
    }

    /**
     * Deploys the device to the specified LoRa network
     */
    fun provision(name: String, network: String, devEUI: String, codec: String?, appKey: String, appEUI: String, lat: Double?, lng: Double?, responder: (Boolean) -> Unit) {

        _network = network

        val loRaDevice = LoRaDeviceActivation(name, appEUI, appKey, devEUI, codec, lat, lng)

        super.execute(Method.POST, "application/json", loRaDevice.toJSONString()) {

            if (it.status in 200..201)
                responder(true)
            else
                responder(false)
        }
    }

    /**
     * Unregisters the device from the specified LoRa networt
     */
    fun deprovision(id: String, network: String, responder: (Boolean) -> Unit) {

        _id = id
        _network = network

        super.execute(Method.DELETE) {

            if (it.status in 200..201)
                responder(true)
            else
                responder(false)
        }
    }

    protected override fun path(): String {

        var path =  C8Y_LORA_OBJENIOUS_MS.replace("<NETWORK>", _network!!)

        if (this.method == Method.GET || this.method == Method.DELETE)
            path += "/${_id!!}"

        return path
    }

    protected override fun response(response: String): String {

        return response
    }
}