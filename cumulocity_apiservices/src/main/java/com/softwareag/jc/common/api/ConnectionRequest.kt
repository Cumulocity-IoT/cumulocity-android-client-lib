package com.softwareag.jc.common.api

import android.os.AsyncTask
import android.util.Base64
import android.util.Log
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

enum class Method {
    GET, POST, PUT, PATCH, DELETE
}
interface Response<T> {

    val status: Int
    val message: String
    val headers: Map<String, MutableList<String>>?
    val type: String?
    var content: T?
}

typealias RequestResponder<T> = (Response<T>) -> Unit

abstract class ConnectionRequest<C,T>(open val connection: Connection<C>): AsyncTask<String, Unit, Unit>() {

    val headers: HashMap<String, String> = HashMap()

    private var _delegate: RequestResponder<T>? = null
    private var _method: Method =
        Method.GET
    private var _body: String? = null
    private var _response: Response<T>? = null
    private var _contentType: String? = null

    protected abstract fun path(): String
    protected abstract fun response(response: String): T

    val method: Method
        get() {
            return _method
        }

    fun execute(responder: RequestResponder<T>?): AsyncTask<String, Unit, Unit> {

        _delegate = responder

        return super.execute()
    }

    fun execute(method: Method, responder: RequestResponder<T>?): AsyncTask<String, Unit, Unit> {
        _method = method
        _delegate = responder

        return super.execute()
    }

    fun execute(method: Method, contentType: String, request: String, responder: RequestResponder<T>?): AsyncTask<String, Unit, Unit> {

        _method = method
        _delegate = responder
        _contentType = contentType
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
                it(_response ?: noResponse())
            }
        }
    }

    private fun http(url: URL, method: Method, headers: Map<String, String>, body: String?): Int {

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

            if (body != null) {
                request(conn, body)
            }

            Log.i("http", "Making request to $method - ${url.toExternalForm()}")

            if (body != null)
                Log.i("http", "$body")

            val responseCode = conn.responseCode

            Log.i("debug", "response code is $responseCode")

            _response =
                ResponseImpl(
                    conn.responseCode,
                    conn.headerFields,
                    conn.responseMessage
                )

            if (responseCode in 200..299) {
                (_response as ResponseImpl).setContent(conn.getHeaderField("Content-Type"), _response(conn))
            }

        } catch (e: Exception) {

            val m: String = e.message ?: "No error given"

           _response =
               ResponseImpl(
                   500,
                   null,
                   m
               )
            Log.e("ERROR", m, e)
        }

        return _response?.status ?: 504
    }

    private fun request(urlConnection: HttpURLConnection, body: String) {

       // try {
            val bufferedWriter = BufferedWriter(OutputStreamWriter(urlConnection.outputStream))
            bufferedWriter.write(body)
            bufferedWriter.close()
    }

    private fun _response(urlConnection: HttpURLConnection): T? {

        try {
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))

            val stringBuilder = StringBuilder()
            var line: String?
            while (bufferedReader.readLine().also {
                    line = it } != null) {
                stringBuilder.append(line).append("\n")
            }

            bufferedReader.close()

            if (stringBuilder.isNotEmpty())
                return response(stringBuilder.toString())
            else
                return null

        } finally {
            urlConnection.disconnect()
        }
    }

    private fun noResponse(): Response<T> {

        return ResponseImpl<T>(
            504,
            null,
            "No response from server at " + connection.endPoint + path()
        )
    }

    private fun valueInHeaders(key: String, headers: Map<String, String>): String? {

        var found: String? = null

        headers.forEach { (k, v) ->
            if (k == key)
                found = v
        }

        return found;
    }

    internal class ResponseImpl<T>(override val status: Int, override val headers: Map<String, MutableList<String>>?, override val message: String) :
        Response<T> {

        override var type: String? = null
        override var content: T? = null

        fun setContent(t: String?, c: T?) {

            type = t
            content = c
        }
    }
}