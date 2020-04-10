package com.softwareag.jc.cumulocity.services.api

import com.softwareag.jc.common.api.ConnectionRequest
import com.softwareag.jc.common.api.Method
import com.softwareag.jc.common.api.Response
import com.softwareag.jc.cumulocity.services.models.Measurement
import com.softwareag.jc.cumulocity.services.models.MeasurementSeries
import com.softwareag.jc.cumulocity.services.models.Operation
import com.softwareag.jc.cumulocity.services.models.User
import org.json.JSONObject

/**
 * Access point for posting operations to remote assets via Cumulocity
 *
 *
 * @property connection Connection referencing cumulocity tenant, instance and credentials to use
 * @constructor Creates a single use instance that cab be used to launch a query, do not reuse the same
 * instance for multiple queries
 **/
class OperationService(override val connection: CumulocityConnectionFactory.CumulocityConnection) : ConnectionRequest<User, Operation>(connection) {

    var version: String = "1"

    /**
     * Submits an operation to Cumulocity to be run on the targetted device refereneced in the managed
     * object
     *
     * @param operation []Operation] to be posted to Cumulocity
     * @param responder callback function to be called with Cumulocity response
     */
    fun post(operation: Operation, responder: (Response<Operation?>) -> Unit) {

        super.execute(Method.POST, "application/vnd.com.nsn.cumulocity.operation+json;ver=$version", operation.toJSONString()) {

        }
    }

    protected override fun path(): String {
        TODO("Not yet implemented")
    }

    protected override fun response(response: String): Operation {
        return Operation(JSONObject(response))
    }
}