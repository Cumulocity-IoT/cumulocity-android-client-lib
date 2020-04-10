package com.softwareag.jc.common.api

import android.os.AsyncTask
import java.net.URL

enum class AuthType {
    Basic, Bearer, Token, Anonymous
}

typealias OnConnectionResult<T> = (Connection<T>, Response<T>) -> Unit

interface Credentials {
    var user: String?
    var password: String?
    var authType: AuthType
}

interface Connection<T> {

    val endPoint: URL
    val isConnected: Boolean
    val failureReason: String?
    val credentials: Credentials

    val headers: Map<String, String>

    fun connect(user: String, password: String, listener: OnConnectionResult<T>): AsyncTask<String, Unit, Unit>
    fun connect(listener: OnConnectionResult<T>): AsyncTask<String, Unit, Unit>
}