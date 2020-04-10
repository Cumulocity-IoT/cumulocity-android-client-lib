package com.softwareag.jc.cumulocity.services.api

import com.softwareag.jc.common.api.ConnectionRequest
import com.softwareag.jc.common.api.Method
import com.softwareag.jc.common.api.RequestResponder
import com.softwareag.jc.cumulocity.services.models.C8Y_MANAGED_EXTIDS_API
import com.softwareag.jc.cumulocity.services.models.ExternalId
import com.softwareag.jc.cumulocity.services.models.ExternalIds
import com.softwareag.jc.cumulocity.services.models.User
import org.json.JSONObject

/**
 * Allows lookup of external id's associated with [ManageObject]s
 */
class ExternalIdService(connection: CumulocityConnectionFactory.CumulocityConnection) : ConnectionRequest<User, List<ExternalId>>(connection) {

    var lastPage: ExternalIds? = null
    private var _id: String? = null

    private var _pageNum: Int = 0

    fun allManagedObject(pageNum: Int, responder: RequestResponder<List<ExternalId>>?) {

        _pageNum = pageNum

        super.execute(responder)
    }

    fun externalIDsForManagedObject(pageNum: Int, id: String, responder: RequestResponder<List<ExternalId>>?) {

        _pageNum = pageNum
        _id = id

        super.execute(responder)
    }

    /**
     * Register an external id for an existing [ManagedObject]
     */
    fun registerExternalIDsForManagedObject(id: String, type: String, value: String) {

        _id = id

        super.execute(
            Method.POST, "application/json", "{\n" +
                "    \"type\" : \"$type\",\n" +
                "    \"externalId\" : \"$value\"\n" +
                "}", null)
    }

    protected override fun path(): String {

        var path: String = C8Y_MANAGED_EXTIDS_API

        path = path.replace("<DEVICE>", _id!!)

        return path
    }

    protected override fun response(response: String): List<ExternalId> {

        lastPage =  ExternalIds(JSONObject(response))

        return lastPage!!.objects()
    }

}