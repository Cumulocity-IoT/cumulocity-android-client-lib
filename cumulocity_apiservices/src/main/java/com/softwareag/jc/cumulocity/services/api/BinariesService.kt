package com.softwareag.jc.cumulocity.services.api

import android.util.Log
import com.softwareag.jc.common.api.ConnectionMultipartRequest
import com.softwareag.jc.common.api.ContentPart
import com.softwareag.jc.common.api.Method
import com.softwareag.jc.common.api.Response
import com.softwareag.jc.cumulocity.services.models.User
import org.json.JSONObject
import java.lang.Exception

const val C8Y_BINARIES_API = "/inventory/binaries"
const val C8Y_BINARIES_ID = "id"
const val C8Y_BINARIES_TYPE = "type"
const val C8Y_BINARIES_NAME = "name"
const val C8Y_BINARIES_OBJECT = "object"
const val C8Y_BINARIES_FILE = "file"
const val C8Y_BINARIES_FILE_SIZE = "filesize"

const val JC_HEADER_LOCATION = "Location"
const val JC_HEADER_CONTENT_DISPOSITION = "Content-Disposition"

/**
 * Allows files to be uploaded/downloaded from Cumulocity using the /inventory/binaries api
 *
 * @property connection Connection referencing cumulocity tenant, instance and credentials to use
 * @constructor Creates a single use instance that can be used to fetch/send binary content to
 * Cumulocity
 **/
class BinariesService(connection: CumulocityConnectionFactory.CumulocityConnection) : ConnectionMultipartRequest<User, ByteArray>(connection) {

    /**
     * Wrapper for the content to uploaded/downloaded from Cumulocity
     *
     * @property id null of the internal id assigned by Cumulocity
     * @property name name/label of the of the data and shown in Cumulocity -> Administration
     * -> Management -> File Repository
     * @property type Content type of the data e.g. application/png etc
     * @property content byte array of the file contents to be sent or received from Cumulocity
     */
    data class Attachment(val id: String, val name: String, val type: String?, var content: ByteArray?) {

        constructor(o: JSONObject): this(o.getString(C8Y_BINARIES_ID),  o.getString(
            C8Y_BINARIES_NAME
        ), o.getString(C8Y_BINARIES_TYPE), null) {

        }
    }

    private var _ref: String? = null

    /**
     * Fetch file contents using Cumulocity internal id of the file
     *
     * @param id internal id of the stored file
     * @param responder callback function to be called with the file content
     */
    fun get(id: String, responder: (Response<Attachment>) -> Unit) {

        _ref = id

        super.execute() {
            responder(ResponseImpl(id, it))
        }
    }

    /**
     * Sends the file to Cumulocity to be stored
     *
     * @param name label of the file to be shown in Cumulocity -> Administration
     * -> Management -> File Repository
     * @param contentType content type representing the type of data to be stored
     * @param content  ByteArray representing raw data to be stored
     * @param responder Callback function to be called with response from Cumulocity, principally
     * the internal id of the newly stored file
     */
    fun send(name: String, contentType: String, content: ByteArray, responder: (Response<Attachment>) -> Unit) {

        val request: ArrayList<ContentPart> = ArrayList()
        val contentPartObject: ContentPart =
            ContentPart(
                "$C8Y_BINARIES_OBJECT", null, null, ("{\n" +
                        "  \"$C8Y_BINARIES_NAME\":\"$name\",\n" +
                        "  \"$C8Y_BINARIES_TYPE\":\"$contentType\"\n" + "}").toByteArray()
            )

        val contentPartFileSize: ContentPart =
            ContentPart(
                "$C8Y_BINARIES_FILE_SIZE",
                null,
                null,
                "${content.size}".toByteArray()
            )

        val contentPartFile: ContentPart =
            ContentPart(
                "$C8Y_BINARIES_FILE",
                name,
                contentType,
                content
            )

        request.add(contentPartObject)
        request.add(contentPartFileSize)
        request.add(contentPartFile)

        super.execute(Method.POST, request) {

            responder(ResponseImpl(name, content, it))
        }
    }

    protected override fun path(): String {

        return if (_ref != null) {
            "$C8Y_BINARIES_API/$_ref"
        } else {
            C8Y_BINARIES_API
        }
    }

    inner class ResponseImpl(private val r: Response<ByteArray>):
        Response<Attachment> {

        private var _attachment: Attachment? = null

        init {
            try {
                if (r.content != null)
                    _attachment =
                        Attachment(
                            JSONObject(String(r.content!!))
                        )
            } catch(e: Exception) {
                // do now't

            }
        }

        constructor(name: String, content: ByteArray, r: Response<ByteArray>): this(r) {

            var id: String? = null
            var loc: List<String>? = r.headers?.get(JC_HEADER_LOCATION)

            if (loc != null) {
                id = loc[0].substring(loc[0].lastIndexOf("/")+1)
            }

            if (id != null)
                _attachment =
                    Attachment(
                        id,
                        name,
                        null,
                        content
                    )
        }

        constructor(id: String, r: Response<ByteArray>): this(r) {

            // get

            var name: String? = null
            var disposition: List<String>? = r.headers?.get(JC_HEADER_CONTENT_DISPOSITION)

            if (disposition != null) {
                name = disposition[0].substring(disposition[0].indexOf("\""), disposition[0].length-2)
            }

            if (r.status in 200..299)
                _attachment =
                    Attachment(
                        id,
                        name!!,
                        type,
                        r.content
                    )
        }

        override val status: Int
            get(){
                return r.status
            }

        override val message: String
            get() {
                return r.message
            }

        override val headers: Map<String, MutableList<String>>?
            get() {
                return r.headers
            }

        override val type: String?
            get() {
                return r.type
            }

        override var content: Attachment? = null
            get() {
                return _attachment
            }
    }
}