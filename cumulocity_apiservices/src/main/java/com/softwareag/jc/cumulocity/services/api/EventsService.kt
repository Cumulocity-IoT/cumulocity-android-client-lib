package com.softwareag.jc.cumulocity.services.api

import com.softwareag.jc.common.api.ConnectionRequest
import com.softwareag.jc.common.api.Method
import com.softwareag.jc.common.api.RequestResponder
import com.softwareag.jc.common.kotlin.extensions.DateTools
import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.dateToISO861
import com.softwareag.jc.cumulocity.services.models.Event
import com.softwareag.jc.cumulocity.services.models.Measurement
import com.softwareag.jc.cumulocity.services.models.User
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder
import java.util.*
import kotlin.collections.ArrayList

const val C8Y_EVENTS_API = "/event/events"
const val C8Y_EVENTS_LIST = "events"

/**
 * Access point for retrieving and sending events to/from Cumulocity, data represented as [Event] and accessed via
 * API endpoint /event/events
 *
 * @property connection Connection referencing cumulocity tenant, instance and credentials to use
 * @constructor Creates a single use instance that cab be used to launch a query, do not reuse the same
 * instance for multiple queries
 **/
class EventsService(override val connection: CumulocityConnectionFactory.CumulocityConnection) : ConnectionRequest<User, List<Event>>(connection) {

    private var _ref: String? = null
    private var _source: String? = null

    private var _pageSize: Int = 100
    private var _revert: Boolean = false

    /**
     * Retrieves the event for the given id
     *
     * @param id internal id of the event to be retrieved
     * @param responder a single item list containing the required [Event]
     */
    fun get(id: String, responder: RequestResponder<List<Event>>) {

        _ref = id

        super.execute(responder)
    }

    /**
     *  Retrieves the 100 events emitted for the given object
     *  @param id internal id of the device/object for which we want to retrieve events
     *  @param responder List of the last 100 events
     */
    fun getAllForDevice(id: String,  responder: RequestResponder<List<Event>>) {

        _source = id
        super.execute(responder)
    }

    /**
     * Submits the events to cumulocity for the given managed object
     * @param events A list of new [Event]s to be submitted for which the ids must be null
     * @param responder callback to be called with Cumulocity response i.e. the id's of the
     * newly create events
     */
    fun post(events: Array<Event>, responder: RequestResponder<List<Event>>) {

        events.forEach { e ->
            super.execute(Method.POST, "application/json", e.toJSONString(), responder)
        }
    }

    protected override fun path(): String {

        var path = if (_source != null) {
             "$C8Y_EVENTS_API?source=$_source&pageSize=$_pageSize&revert=$_revert"
        } else if (_ref != null) {
             "$C8Y_EVENTS_API/$_ref?revert=$_revert"
        } else {
             "C8Y_EVENTS_API?revert=$_revert"
        }

        path += "&dateFrom=${URLEncoder.encode(DateTools.dateTimeAtStartOfDay().dateToISO861(), "UTF8")}&dateTo=${URLEncoder.encode(Date().dateToISO861(), "UTF8")}"

        return path
    }

    protected override fun response(response: String): List<Event> {

        val l: JSONArray = JSONObject(response).getJSONArray(C8Y_EVENTS_LIST)
        val ml: ArrayList<Event> = ArrayList()

        for (i in 0 until l.length()) {
            ml.add(Event(l.getJSONObject(i)))
        }

        return ml
    }

    private fun _toJsonList(l: Array<Event>): String {

        val out: StringBuilder = StringBuilder()

        out.append("{\"$C8Y_EVENTS_LIST\": [")

        l.forEach { m ->
            out.append(m.toJSONString()).append(",")
        }

        if (out.endsWith(","))
            out.deleteCharAt(out.length-1)


        out.append("]}")

        return out.toString()
    }
}