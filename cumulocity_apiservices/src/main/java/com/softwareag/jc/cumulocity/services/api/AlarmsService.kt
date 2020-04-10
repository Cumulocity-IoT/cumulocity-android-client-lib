package com.softwareag.jc.cumulocity.services.api

import com.softwareag.jc.common.api.ConnectionRequest
import com.softwareag.jc.common.api.Method
import com.softwareag.jc.common.api.RequestResponder
import com.softwareag.jc.cumulocity.services.models.Alarm
import com.softwareag.jc.cumulocity.services.models.User
import org.json.JSONArray
import org.json.JSONObject
import kotlin.collections.ArrayList

const val C8Y_ALARMS_API = "/alarm/alarms"
const val C8Y_ALARMS_LIST = "alarms"

/**
 * Allows [Alarm]s to be posted and queried from Cumulocity
 *
 * @property connection Connection referencing cumulocity tenant, instance and credentials to use
 * @constructor Creates a single use instance that cab be used to launch a query, do not reuse the same
 * instance for multiple queries
 **/
class AlarmsService(override val connection: CumulocityConnectionFactory.CumulocityConnection) : ConnectionRequest<User, List<Alarm>>(connection) {

    private var _ref: String? = null
    private var _source: String? = null

    /**
     * Retrieves the [Alarm] details for the given id
     *
     * @param id the id of the [Alarm]
     * @param responder: the callback to be called with a one item list
     */
    fun get(id: String, responder: RequestResponder<List<Alarm>>) {

        _ref = id

        super.execute(responder)
    }

    /**
     * Retrieves the alarm details for the given managed object
     *
     * @param id the id of the managed object that is to be queried
     * @param responder: the callback to be called with the list of alarms
     */
    fun getAllForDevice(deviceId: String,  responder: RequestResponder<List<Alarm>>) {

        _source = deviceId
        super.execute(responder)
    }

    /**
     * Creates new [Alarm]s in Cumulocity
     *
     * @param alarms List of alarms to be sent to Cumulocity. id should be null
     * @param responder: the callback to be called with the updated alarms from Cumulocity
     */
    fun post(alarms: Array<Alarm>, responder: RequestResponder<List<Alarm>>) {

        alarms.forEach { a ->
            super.execute(Method.POST, "application/json", a.toJSONString(), responder)
        }
    }

    protected override fun path(): String {

        if (_source != null) {
            return "C8Y_ALARMS_API?source=$_source"
        } else if (_ref != null) {
            return "$C8Y_ALARMS_API/$_ref"
        } else {
            return C8Y_ALARMS_API
        }
    }

    protected override fun response(response: String): List<Alarm> {

        val l: JSONArray = JSONObject(response).getJSONArray(C8Y_ALARMS_LIST)
        val ml: ArrayList<Alarm> = ArrayList()

        for (i in 0 until l.length()) {
            ml.add(Alarm(l.getJSONObject(i)))
        }

        return ml
    }

    private fun _toJsonList(l: Array<Alarm>): String {

        val out: StringBuilder = StringBuilder()

        out.append("{\"$C8Y_ALARMS_LIST\": [")

        l.forEach { m ->
            out.append(m.toJSONString()).append(",")
        }

        if (out.endsWith(","))
            out.deleteCharAt(out.length-1)


        out.append("]}")

        return out.toString()
    }
}