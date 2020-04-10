package com.softwareag.jc.cumulocity.services.api

import com.softwareag.jc.common.api.Connection
import com.softwareag.jc.common.api.ConnectionRequest
import com.softwareag.jc.common.api.RequestResponder
import com.softwareag.jc.cumulocity.services.models.*
import org.json.JSONObject
import java.net.URLEncoder

/**
 * Principal access point for all Cumulocity data represented as [ManagedObject]s such as devices
 * and groups and implemented through the API endpoint /inventory/managedObjects.
 *
 * It translates the response content into a list of [ManagedObject] instances to simplify usage
 *
 *  e.g.
 *  ```kotlin
 *  CumulocityConnectionFactory.connection(<tenant>, <instance e.g. cumulocity.com>).connect(<user>, <password>) { connection, responseInfo ->
 *
 *      ManagedObjectsService(connection).managedObjectsForType(0, "c8y_DeviceGroup") { results ->
 *
 *          val status: Int = results.status
 *
 *          val failureReason: String? = if (status == 500)
 *              results.reason
 *          else
 *              null
 *
 *          val objects: List<ManagedObject> = results.content
 *      }
 *  }
 *  ```
 *
 *  The results instance consists of the following properties
 *
 *  **status** - HTTP response code
 *
 *  **message** - HTTP response message, most often null unless the request failed
 *
 *  **headers** - HTTP response headers
 *
 *  **type** - Indicates the content type that was returned
 *
 *  **content** - Optional content provided by Cumulocity and will be a list of 0 or [ManagedObject] objects.
 *
 *
 *
 * @property connection Connection referencing cumulocity tenant, instance and credentials to use
 * @constructor Creates a single use instance that cab be used to launch a query, do not reuse the same
 * instance for multiple queries
 **/
class ManagedObjectsService(override val connection: Connection<User>) : ConnectionRequest<User, List<ManagedObject>>(connection) {

    /**
     * default is 50, ensures large queries can be broken down into separate requests via paged
     * results. Each request will limited by this value and the request will have to be repeated along
     * with incrementing the pageNum attribute.
     */
    var pageSize: Int = 50

    /**
     * Allows the last query results to be retrieved without having to resubmit the query
     */
    var lastPage: ManagedObjects? = null

    private var _pageNum: Int = 0
    private var _type: String? = null
    private var _query: ManagedObjectQuery? = null

    /**
     * Returns all managed objects for the given page with the page size specified by the [pageSize]
     * property of your instance. Invoke this method for successive page whilst incrementing the pageNum
     * You will get a empty list once you go past the last page.
     *
     * @param pageNum The page to be fetched
     * @param responder The callback to be invoked for the results, content will be a list of [ManagedObject]s
     *
     */
    fun allManagedObject(pageNum: Int, responder: RequestResponder<List<ManagedObject>>) {

        _pageNum = pageNum

        super.execute(responder)
    }

    /**
     * Returns all managed objects for the given page and type with the page size specified by the @see pageSize
     * property of your instance. Invoke this method for each successive page whilst incrementing the pageNum
     * You will get a empty list once you go past the last page.
     *
     * @param pageNum The page to be fetched
     * @param type The type of managed object to be fetched e.g. "c8y_DeviceGroup"
     * @param responder The callback to be invoked for the results, content will be a list of [ManagedObject]s
     */
    fun managedObjectsForType(pageNum: Int, type: String, responder: RequestResponder<List<ManagedObject>>) {

        _pageNum = pageNum
        _type = type

        super.execute(responder)
    }

    /**
     * Returns all managed objects for the given page and queries with the page size specified by the @see pageSize
     * property of your instance. Invoke this method for each successive page whilst incrementing the pageNum
     * You will get a empty list once you go past the last page.
     *
     * @param pageNum The page to be fetched
     * @param query [ManagedObjectQuery] representing a collection of queries to be applied
     * @param responder The callback to be invoked with the results
     */
    fun managedObjectsForQuery(pageNum: Int, query: ManagedObjectQuery, responder: RequestResponder<List<ManagedObject>>) {

        _pageNum = pageNum
        _query = query

        super.execute(responder)
    }

    protected override fun path(): String {

        if (_type != null) {
            return C8Y_MANAGED_OBJECTS_API + "?pageNum=$_pageNum&pageSize=$pageSize&type=" + URLEncoder.encode(_type, "utf-8")
        } else if (_query != null) {
            return C8Y_MANAGED_OBJECTS_API + "?pageNum=$_pageNum&pageSize=$pageSize&query=" + _query!!.build()
        } else {
            return C8Y_MANAGED_OBJECTS_API
        }
    }

    protected override fun response(response: String): List<ManagedObject> {

        _type = null
        _query = null

        lastPage =  ManagedObjects(JSONObject(response))

        return lastPage!!.objects()
    }

}