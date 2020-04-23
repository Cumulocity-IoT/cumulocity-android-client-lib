package com.softwareag.jc.cumulocity.services.models

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.iSO861StringToDate
import com.softwareag.jc.common.kotlin.extensions.JsonSerializable
import com.softwareag.jc.cumulocity.services.models.DataPoints.Value.Companion.make
import com.softwareag.jc.cumulocity.services.models.Position.Companion.make
import com.softwareag.jc.cumulocity.services.models.extension.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

const val C8Y_MANAGED_OBJECT_API = "/inventory/managedObject"

const val C8Y_MANAGED_OBJECT = "managedObject"
const val C8Y_MANAGED_OBJECT_ID = "id"
const val C8Y_MANAGED_OBJECT_TYPE = "type"
const val C8Y_MANAGED_OBJECT_NAME = "name"
const val C8Y_MANAGED_OBJECT_CREATED = "createdTime"
const val C8Y_MANAGED_OBJECT_LAST_UPDATED = "lastUpdated"
const val C8Y_MANAGED_OBJECT_OWNER = "owner"

const val C8Y_MANAGED_OBJECT_APP_OWNER = "applicationOwner"
const val C8Y_MANAGED_OBJECT_APP_ID = "applicationId"
const val C8Y_MANAGED_OBJECT_NOTES = "c8y_Notes"
const val C8Y_MANAGED_OBJECT_DEVICES = "childDevices"
const val C8Y_MANAGED_OBJECT_ASSETS = "childAssets"

const val C8Y_MANAGED_OBJECT_AVAILABILITY = "c8y_Availability"
const val C8Y_MANAGED_OBJECT_AVAILABILITY_STATUS = "status"
const val C8Y_MANAGED_OBJECT_AVAILABILITY_MESSAGE = "lastMessage"

const val C8Y_MANAGED_OBJECT_FIRMWARE = "c8y_Firmware"
const val C8Y_MANAGED_OBJECT_FIRMWARE_VERSION = "version"

const val C8Y_MANAGED_OBJECT_ALARMS = "c8y_ActiveAlarmsStatus"
const val C8Y_MANAGED_OBJECT_ALARMS_WARNING = "warning"
const val C8Y_MANAGED_OBJECT_ALARMS_MINOR = "minor"
const val C8Y_MANAGED_OBJECT_ALARMS_MAJOR = "major"
const val C8Y_MANAGED_OBJECT_ALARMS_CRITICAL = "critical"

const val C8Y_MANAGED_OBJECT_TYPE_DETAILS = "c8y_IsDevice"

const val C8Y_MANAGED_OBJECT_REQ_AVAILABILITY = "c8y_RequiredAvailability"
const val C8Y_MANAGED_OBJECT_REQ_AVAILABILITY_INTERVAL = "responseInterval"

const val C8Y_MANAGED_OBJECT_CONNECTION = "c8y_Connection"
const val C8Y_MANAGED_OBJECT_CONNECTION_STATUS = "status"

const val C8Y_MANAGED_OBJECT_OPERATIONS = "c8y_SupportedOperations"

const val C8Y_MANAGED_OBJECT_POSITION = "c8y_Position"
const val C8Y_MANAGED_OBJECT_POSITION_LONG = "lng"
const val C8Y_MANAGED_OBJECT_POSITION_LAT = "lat"
const val C8Y_MANAGED_OBJECT_POSITION_ALT = "alt"

const val C8Y_MANAGED_OBJECT_LPWAN_DEVICE = "c8y_LpwanDevice"
const val C8Y_MANAGED_OBJECT_LPWAN_DEVICE_PROVISIONED = "provisioned"

const val C8Y_MANAGED_OBJECT_LNSTYPE = "lNSType" // TODO: IS THIS IS A HACK FOR CYRIL

const val C8Y_MANAGED_OBJECT_HARDWARE = "c8y_Hardware"
const val C8Y_MANAGED_OBJECT_HARDWARE_SERIAL = "serialNumber"
const val C8Y_MANAGED_OBJECT_HARDWARE_MODEL = "model"

// JC CUSTOM FIELDS

const val C8Y_MANAGED_OBJECT_HARDWARE_SUPPLIER = "supplier" //TODO - verify this with Cyril
const val JC_MANAGED_OBJECT_IS_VERIFIED = "xIsVerified"
const val JC_MANAGED_OBJECT_IS_VERIFIED_DATE = "xIsVerifiedDate"

// END OF CUSTOM FIELDS

class IsDeviceType: JsonSerializable {

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }
}

/**
 * Collection of alarms associated with a [ManagedObject]/device
 */
data class Alarms(var critical: Int = 0, var major: Int = 0, var minor: Int = 0, var warning: Int = 0) {

}

/**
 * Firmware of device represented by [ManagedObject]
 */
data class Firmware(val version: String):
    JsonSerializable {

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }
}

/**
 * Represents last status received by Cumulocity of associated device
 */
data class Availability(val lastMessage: String?, val status: String){
}

/**
 * Used by Cumulocity to determine [Availability], the status will be assumed to 'UNAVAILABLE'
 * if no message or event is received by Cumulocity within the time period (minutes) represented
 * by the response interval property.
 *
 * Set the value to -1 to set the device to 'MAINTENANCE' mode. This will ensure that Cumulocity will
 * not trigger any unnecessary alarms whilst the device is being updated etc.
 *
 * @property responseInterval measured in minutes, used by Cumulocity to determine device status
 */
data class RequiredAvailability(val responseInterval: Int):
    JsonSerializable {

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }
}

/**
 * Represents the GPS coordinated of the device associated with this [ManagedObject]
 */
data class Position(val lng: Double, val lat: Double, val alt: Double?):
    JsonSerializable {

    companion object {
        fun make(o: JSONObject): Position {
            var alt: Double = 0.0

            if (o.has(C8Y_MANAGED_OBJECT_POSITION_ALT) && o.get(C8Y_MANAGED_OBJECT_POSITION_ALT).toString() != "null") {
                var b = o.get(C8Y_MANAGED_OBJECT_POSITION_ALT).toString()
                alt = o.getDouble(C8Y_MANAGED_OBJECT_POSITION_ALT)
            }

            return Position(o.getDouble(C8Y_MANAGED_OBJECT_POSITION_LONG), o.getDouble(C8Y_MANAGED_OBJECT_POSITION_LAT), alt)
        }
    }

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }
}

/**
 * Collection of properties to identify the device type
 */
class Hardware(json: JSONObject?): PropertiesBase(json, false),
    JsonSerializable {

    private var _serialNumber: String? = null
    private var _model: String? = null
    private var _supplier: String?
    private var _revision: String?

    val serialNumber: String?
        get() {
            return _serialNumber
        }
    val model: String?
        get() {
            return _model
        }
    val supplier: String?
        get() {
            return _supplier
        }
    val revision: String?
        get() {
            return _revision
        }

    init {

        if (json != null) {

            if (json.has(C8Y_MANAGED_OBJECT_HARDWARE_SERIAL))
                _serialNumber = json.getString(C8Y_MANAGED_OBJECT_HARDWARE_SERIAL)

            if (json.has(C8Y_MANAGED_OBJECT_HARDWARE_MODEL))
                _model = json.getString(C8Y_MANAGED_OBJECT_HARDWARE_MODEL)

            _supplier = if (json.has(C8Y_MANAGED_OBJECT_HARDWARE_SUPPLIER))
                json.getString(C8Y_MANAGED_OBJECT_HARDWARE_SUPPLIER)
            else
                null

            _revision = null
        } else {

            _serialNumber = null
            _model = null
            _supplier = null
            _revision = null
        }
    }

    fun updateHardware(serialNumber: String?, supplier: String?, model: String?, revision: String?) {

        _serialNumber = serialNumber
        _supplier = supplier
        _model = model
        _revision = revision
    }

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }
}

/**
 * This is the single most important asset type referenced by Cumulocity. Principally identifies the
 * devices to be managed, but can be used to define any required asset type, such as groups, buildings,
 * rooms etc. etc.
 *
 * It's formal definition is given in the [Cumulocity REST API Guide](https://cumulocity.com/guides/reference/inventory/#managed-object)

 *
 * _Customisation_
 *
 * ManagedObject's can be easily enriched with custom attributes, however this is problematic for a
 * typed language such as Kotlin or Java. In this implementation custom attributes are added to a
 * properties attribute of ManagedObject and identified by the name of the value. Complex structures
 * are flattened into a single key/value pair
 * e.g.
 * ```
 * {
 *  "my_custom_structure": {
 *      "name": "this is an example"
 *      "sub_elements": {
 *              "value": "Can have as many sub-levels as you want"
 *          }
 *      }
 *  }
 * ```
 *
 * becomes
 *```
 * properties["my_custom_structure.name"] == "this is an example"
 * properties["my_custom_structure.name"] == "this is an example"
 * properties["my_custom_structure.sub_elements.value"] == "Can have as many sub-levels as you want"
 *```
 * You can also add single custom attributes to [ManagedObject] and reference them too, however they
 * have to be prefaced with 'x' in order to be included
 * e.g.
 *```
 * {
 *  "xMy_Custom_value": "123456"
 * }
 *```
 *
 * becomes
 *```
 * properties["xMy_Custom_value"] == "this is an example"
 *```
 *
 * The above is required because there is no formal description available for a [ManageObject] and
 * hence no way to formally distinguish between standard and custom attributes, hence the 'x'
 *
 * This could put forward the argument that all custom values should be placed in complex structure
 * as above. Unfortunately this is not always possible, due to the fact that only simple top level
 * custom attributes can be included in Cumulocity dashboards.
 *
 * You can also register your own classes to replace the flattened structure with a proper object
 * representation if you wish. Refer to the documentation for [PropertiesBase] for details.
 */
data class ManagedObject(private val o: JSONObject?): PropertiesBase(o, true), JsonSerializable {

    private var _id: String? = null
    private var _name: String? = null
    private var _type: String? = null
    private var _subType: String? = null

    public val id: String?
        get() {
            return _id
        }
    val name: String?
        get() {
            return _name
        }

    val type: String?
        get() {
            return _type
        }

    val subType: String?
        get() {
            return _subType
        }

    val createdDate: Date?
    val updatedDate: Date?
    val appId: String?
    val owner: String?
    val appOwner: String?

    val isLPWANProvisioned: Boolean

    var c8y_SupportedOperations: List<String> = ArrayList()
    var c8y_DataPoint: DataPoints = DataPoints()
    val c8y_ActiveAlarmsStatus: Alarms = Alarms(0, 0, 0, 0)

    var c8y_IsDevice: IsDeviceType? = null

    var c8y_Notes: String? = null

    var c8y_Availability: Availability? = Availability(null, "UNAVAILABLE")
    var c8y_Firmware: Firmware? = null

    var c8y_RequiredAvailability: RequiredAvailability? = null
    var c8y_ConnnectionStatus: String? = null
    var c8y_Hardware: Hardware = Hardware(null)
    var c8y_Position: Position? = null

    var lNSType: String? = null

    init {

        if (o != null) {

            _id = if (o.has(C8Y_MANAGED_OBJECT_ID))
                o.getString(C8Y_MANAGED_OBJECT_ID)
            else
                null

            _name =  if (o.has(C8Y_MANAGED_OBJECT_NAME))
                o.getString(C8Y_MANAGED_OBJECT_NAME)
            else
                ""

            _type =  if (o.has(C8Y_MANAGED_OBJECT_TYPE))
                o.getString(C8Y_MANAGED_OBJECT_TYPE)
            else
                ""

            createdDate = if (o.has(C8Y_MANAGED_OBJECT_CREATED))
                o.getString(C8Y_MANAGED_OBJECT_CREATED).iSO861StringToDate()
            else
                null

            updatedDate = if (o.has(C8Y_MANAGED_OBJECT_LAST_UPDATED))
                o.getString(C8Y_MANAGED_OBJECT_LAST_UPDATED).iSO861StringToDate()
            else
                null

            appId = if (o.has(C8Y_MANAGED_OBJECT_APP_ID))
                o.getString(C8Y_MANAGED_OBJECT_APP_ID)
            else
                null

            owner = if (o.has(C8Y_MANAGED_OBJECT_OWNER))
                o.getString(C8Y_MANAGED_OBJECT_OWNER)
            else
                null

            appOwner = if (o.has(C8Y_MANAGED_OBJECT_APP_OWNER))
                o.getString(C8Y_MANAGED_OBJECT_APP_OWNER)
            else
                null

            c8y_Notes = if (o.has(C8Y_MANAGED_OBJECT_NOTES))
                o.getString(C8Y_MANAGED_OBJECT_NOTES)
            else
                null

            c8y_RequiredAvailability = if (o.has(C8Y_MANAGED_OBJECT_REQ_AVAILABILITY))
                RequiredAvailability(o.getJSONObject(C8Y_MANAGED_OBJECT_REQ_AVAILABILITY).getInt(
                    C8Y_MANAGED_OBJECT_REQ_AVAILABILITY_INTERVAL
                ))
            else
                null

            c8y_ConnnectionStatus = if (o.has(C8Y_MANAGED_OBJECT_CONNECTION))
                o.getJSONObject(C8Y_MANAGED_OBJECT_CONNECTION).getString(
                    C8Y_MANAGED_OBJECT_CONNECTION_STATUS
                )
            else
                null

            c8y_Position = if (o.has(C8Y_MANAGED_OBJECT_POSITION))
                Position.make(o.getJSONObject(C8Y_MANAGED_OBJECT_POSITION))
            else
                null

            c8y_Hardware = if (o.has(C8Y_MANAGED_OBJECT_HARDWARE))
                Hardware(o.getJSONObject(C8Y_MANAGED_OBJECT_HARDWARE))
            else
                Hardware(null)

            if (o.has(C8Y_MANAGED_OBJECT_AVAILABILITY)) {
                var a: JSONObject = o.getJSONObject(C8Y_MANAGED_OBJECT_AVAILABILITY)

                var status = a.getString(C8Y_MANAGED_OBJECT_AVAILABILITY_STATUS)
                var lastMessage = a.getString(C8Y_MANAGED_OBJECT_AVAILABILITY_MESSAGE)

                c8y_Availability = Availability(lastMessage, status)
            }

            if (o.has(C8Y_MANAGED_OBJECT_FIRMWARE)) {
                var a: JSONObject = o.getJSONObject(C8Y_MANAGED_OBJECT_FIRMWARE)

                c8y_Firmware = Firmware(a.getString(C8Y_MANAGED_OBJECT_FIRMWARE_VERSION))
            }

            if (o.has(C8Y_MANAGED_OBJECT_ALARMS)) {
                var a: JSONObject = o.getJSONObject(C8Y_MANAGED_OBJECT_ALARMS)

                var c: Int = if (a.has(C8Y_MANAGED_OBJECT_ALARMS_CRITICAL)) a.getInt(
                    C8Y_MANAGED_OBJECT_ALARMS_CRITICAL
                ) else 0
                var m: Int = if (a.has(C8Y_MANAGED_OBJECT_ALARMS_MAJOR)) a.getInt(
                    C8Y_MANAGED_OBJECT_ALARMS_MAJOR
                ) else 0
                var n: Int = if (a.has(C8Y_MANAGED_OBJECT_ALARMS_MINOR)) a.getInt(
                    C8Y_MANAGED_OBJECT_ALARMS_MINOR
                ) else 0
                var w: Int = if (a.has(C8Y_MANAGED_OBJECT_ALARMS_WARNING)) a.getInt(
                    C8Y_MANAGED_OBJECT_ALARMS_WARNING
                ) else 0

                c8y_ActiveAlarmsStatus.critical = c
                c8y_ActiveAlarmsStatus.major = m
                c8y_ActiveAlarmsStatus.minor = n
                c8y_ActiveAlarmsStatus.warning = w
            }

            if (o.has(C8Y_MANAGED_OBJECT_TYPE_DETAILS)) {
                _subType = setType()
            } else {
                _type = setType()
                _subType = null
            }

            if (o.has(C8Y_MANAGED_OBJECT_OPERATIONS)) {
                var ops: JSONArray = o.getJSONArray(C8Y_MANAGED_OBJECT_OPERATIONS)

                for (i in 0 until ops.length()) {

                    (c8y_SupportedOperations as ArrayList).add(ops.getString(i))
                }
            }

            if (o.has(C8Y_MANAGED_OBJECT_DATA_POINTS) && o.get(C8Y_MANAGED_OBJECT_DATA_POINTS) is JSONObject) {

                c8y_DataPoint = DataPoints(o.getJSONObject(C8Y_MANAGED_OBJECT_DATA_POINTS))
            }

            isLPWANProvisioned = if (o.has(C8Y_MANAGED_OBJECT_LPWAN_DEVICE)) {
                o.getJSONObject(C8Y_MANAGED_OBJECT_LPWAN_DEVICE).getBoolean(C8Y_MANAGED_OBJECT_LPWAN_DEVICE_PROVISIONED)
            } else {
                false
            }

            // HACK FOR LORA FIELDS ADDED BY CYRIL

            if (o.has(CY_LORA_DEVICE_CODEC)) {
                (properties as HashMap)[CY_LORA_DEVICE] = LoRaDeviceInfo(o)
            }

            if (o.has(CY_LORA_NETWORK_LNS)) {
                (properties as HashMap)[CY_LORA_NETWORK] = LoRaNetorkInfo(o)
            }

            if (o.has(C8Y_MANAGED_OBJECT_LNSTYPE)) {
                this.lNSType = o.getString(C8Y_MANAGED_OBJECT_LNSTYPE)
            }

            // HACK HACK
        } else {

            createdDate = Date()
            updatedDate = null
            owner = null
            appId = null
            appOwner = null
            isLPWANProvisioned = false
        }
    }

    /**
     * Creates new ManageObject to be posted to Cumlocity via [ManageObjectService]
     */
    constructor(id: String?, serialNumber: String?, name: String?, supplier: String?, model: String?, revision: String?, type: String, subType: String?, location: Position?, notes: String?):this(null) {

        c8y_Hardware.updateHardware(serialNumber, supplier, model, revision)

        _id = id
        _name = name
        _type = type
        _subType = subType

        this@ManagedObject.c8y_Notes = notes

        if (location != null)
            this@ManagedObject.c8y_Position = Position(location!!.lng, location!!.lat, location!!.alt)
    }

    /**
     * Creates new ManageObject to be posted to Cumulocity via [ManageObjectService]
     */
    constructor(name: String?, notes: String?):this(null) {

        _name = name
        this@ManagedObject.c8y_Notes = notes
    }

    /**
     * Updates the internal id with that of the parameter, used internally to set the id of a newly
     * posted ManageObject with the new id returned from Cumulocity
     */
    fun updateId(id: String) {
        _id = id
    }

    override fun toJSONString(): String {

        return JsonSerializable.toJSONString(this)
    }

    private fun setType(): String? {
        return if (o!!.has(C8Y_MANAGED_OBJECT_TYPE))
            o!!.getString(C8Y_MANAGED_OBJECT_TYPE)
        else
            null
    }
}