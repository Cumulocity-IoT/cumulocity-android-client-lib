package com.softwareag.jc.cumulocity.services.models


import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

const val C8Y_OPERATIONS_API = "/devicecontrol/operations/"

const val C8Y_OPERATIONS_DEVICE_ID = "deviceId"

/**
 * Represents a Cumulocity operation to be executed or that has been executed on a device
 *
 * Formal definition is given in [Cumulocity REST API Guide](https://cumulocity.com/guides/reference/device-control/#operation)
 *
 * @constructor Create a new operation associated with given device via its [ManagedObject] id
 */
class Operation(val deviceId: String, val type: String, val operationDetails: OperationDetails): JsonSerializable {

    enum class Status {
        SUCCESSFUL,
        FAILED,
        EXECUTING,
        PENDING
    }

    constructor(o: JSONObject): this(o.getString(C8Y_OPERATIONS_DEVICE_ID), operationType(o), details(o)) {

    }

    private var _id: String? = null

    val id: String?
        get() {
            return _id
        }

    private var _date: Date? = null

    val creationTime: Date?
        get() {
            return _date
        }

    private var _bulkOperationId: String? = null

    val bulkOperationId: String?
        get() {
            return _bulkOperationId
        }

    var _status: Status = Status.PENDING

    val status: Status
        get() {
            return _status
        }

    var _failureReason: String? = null

    val failureReason: String?
        get() {
            return _failureReason
        }

    val deviceExternalIDs: List<ExternalId> = ArrayList()

    fun flagFailed(reason: String) {
        _status = Status.FAILED
        _failureReason = reason
    }

    override fun toJSONString(): kotlin.String {
        return JsonSerializable.toJSONString(this)
    }

    data class OperationDetails(val name: String, val parameters:  Map<String, String>): JsonSerializable {

        override fun toJSONString(): kotlin.String {
            return JsonSerializable.toJSONString(this)
        }
    }

    companion object {

        fun operationType(o: JSONObject): String {

            return ""
        }

        fun details(o: JSONObject): OperationDetails {

            return OperationDetails("Beep", HashMap())
        }
    }
}