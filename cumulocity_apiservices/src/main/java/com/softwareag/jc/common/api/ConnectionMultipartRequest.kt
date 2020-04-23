package com.softwareag.jc.common.api

import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import java.io.*
import java.net.HttpURLConnection
import java.net.URL

const val JC_MULTIPART_BOUNDARY = "--fileBoundary"
const val JC_MULTIPART_CONTENT_TYPE = "multipart/form-data; boundary=fileBoundary"

data class ContentPart(val name: String, val fileName: String?, val contentType: String?, val content: ByteArray) {
}

abstract class ConnectionMultipartRequest<C,ByteArray>(open val connection: Connection<C>): AsyncTask<String, Unit, Unit>() {

    val headers: HashMap<String, String> = HashMap()

    private var _delegate: RequestResponder<ByteArray>? = null
    private var _method: Method =
        Method.GET
    private var _body:  List<ContentPart>? = null
    private var _response: Response<ByteArray>? = null
    private var _contentType: String? = null

    protected abstract fun path(): String

    fun execute(responder: RequestResponder<ByteArray>?): AsyncTask<String, Unit, Unit> {

        _delegate = responder

        return super.execute()
    }

    fun execute(method: Method, responder: RequestResponder<ByteArray>?): AsyncTask<String, Unit, Unit> {
        _method = method
        _delegate = responder

        return super.execute()
    }

    fun execute(method: Method, request: List<ContentPart>, responder: RequestResponder<ByteArray>?): AsyncTask<String, Unit, Unit> {

        _method = method
        _delegate = responder
        _contentType = JC_MULTIPART_CONTENT_TYPE
        _body = request

        return super.execute()
    }

    override fun onPreExecute() {

    }

    override fun doInBackground(vararg params: String?): Unit {

        this.http(URL(connection.endPoint.toExternalForm() + path()), _method, headers, _body)
    }

    override fun onPostExecute(result: Unit?) {

        if (_delegate != null) {
            _delegate?.let {
                it((_response ?: noResponse()) as Response<ByteArray>)
            }
        }
    }

    private fun http(url: URL, method: Method, headers: Map<String, String>, body: List<ContentPart>?): Int {

        try {
            val conn: HttpURLConnection = url.openConnection() as HttpURLConnection

            conn.requestMethod = method.toString()

            if (connection.credentials.authType != AuthType.Anonymous) {

                var creds = "Bearer " + connection.credentials.password!!.toByteArray()

                if (connection.credentials.authType == AuthType.Basic) {

                    val str: String = "${connection.credentials.user}:${connection.credentials.password}"
                    creds = "Basic " + Base64.encodeToString(str.toByteArray(), Base64.NO_WRAP);
                }

                conn.setRequestProperty("Authorization", creds)
            }

            if (_contentType != null)
                conn.setRequestProperty("Content-Type", _contentType!!)

            headers.forEach { (k, v) ->
                conn.setRequestProperty(k, v)
            }

            Log.i("http", "Making m/p request to $method - ${url.toExternalForm()}")

            if (body != null) {
                request(conn, body)
            }

            val responseCode = conn.responseCode

            Log.i("debug", "response code is $responseCode / ${conn.responseMessage}")

            _response =
                ResponseImpl<ByteArray>(
                    conn.responseCode,
                    conn.headerFields,
                    conn.responseMessage
                )

            if (responseCode in 200..299) {
                (_response as ResponseImpl<ByteArray>).setContent(conn.getHeaderField("Content-Type"), _response(conn.inputStream))
            } else {
                (_response as ResponseImpl<ByteArray>).setContent(conn.getHeaderField("Content-Type"), _response(conn.errorStream))
            }

            conn.disconnect()
        } catch (e: Exception) {

            val m: String = e.message ?: "No error given"

           _response =
               ResponseImpl<ByteArray>(
                   500,
                   null,
                   m
               )
            Log.e("ERROR", m, e)
        }

        return _response?.status ?: 504
    }

    private fun request(urlConnection: HttpURLConnection, body: List<ContentPart>) {

        val raw = BufferedOutputStream(urlConnection.outputStream)
        var output = BufferedWriter(OutputStreamWriter(raw))

        body.forEach { v ->

            output.write(JC_MULTIPART_BOUNDARY)
            output.write("\r")
            output.newLine()

            if (v.fileName != null)
                output.write("Content-Disposition: form-data; name=\"${v.name}\"; filename=\"${v.fileName}\"")
            else
                output.write("Content-Disposition: form-data; name=\"${v.name}\"")

            output.write("\r")
            output.newLine()

            if (v.contentType != null) {
                output.write("Content-Type: ${v.contentType}")
                output.write("\r")
                output.newLine()
            }

            output.write("\r")
            output.newLine()
            output.flush()

            raw.write(v.content)
            raw.flush()
            output.write("\r")
            output.newLine()
        }

        output.write("$JC_MULTIPART_BOUNDARY--")
        output.write("\r")
        output.newLine()

        output.close()
    }

    private fun _response(input: InputStream): ByteArray? {

        try {

            val input = BufferedInputStream(input)
            val bytes = input.readBytes()
            input.close()

           // Log.i("debug", String(bytes))

            return bytes as ByteArray
        } catch(e: Exception) {
            Log.e("http", "no reply: " + e.localizedMessage)
            return null
        }
    }

    private fun noResponse(): ResponseImpl<ByteArray> {

        return ResponseImpl(
            504,
            null,
            "No response from server at " + connection.endPoint + path()
        )
    }

    internal class ResponseImpl<ByteArray>(override val status: Int, override val headers: Map<String, MutableList<String>>?, override val message: String) :
        Response<ByteArray> {

        override var type: String? = null
        override var content: ByteArray? = null

        fun setContent(t: String?, c: ByteArray?) {

            type = t
            content = c
        }
    }
}