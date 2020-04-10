package com.softwareag.jc.common.api

import android.os.AsyncTask
import java.net.URL

open class ConnectionFactory<T> {

    companion object {

        private var _c: Connection<*>? = null

        fun <T> connection(baseUrl: String, authEndPoint: String, user: String, password: String): T {

            if (_c == null)
                _c =
                    BasicAuthConnection<T>(
                        URL(baseUrl),
                        authEndPoint,
                        user,
                        password
                    )

            return _c as T!!
        }
    }

    open class BasicAuthConnection<T>(override val endPoint: URL, val authEndPoint: String, val user: String?, val password: String?) :
        Connection<T> {

        private var _isConnected: Boolean = false
        private var _failureReason: String? = null

        override val isConnected: Boolean
            get() {
                return _isConnected
            }

        override val failureReason: String?
            get() {
                return _failureReason
            }

        override val credentials: Credentials =
            CredentialsImpl(
                user,
                password
            )
        override val headers: Map<String, String> = HashMap()

        override fun connect(user: String, password: String, listener: OnConnectionResult<T>): AsyncTask<String, Unit, Unit> {

            credentials.user = user
            credentials.password = password
            credentials.authType =
                AuthType.Basic

            return connect(listener)
        }

       override fun connect(listener: OnConnectionResult<T>): AsyncTask<String, Unit, Unit> {

            val connectionRequest =
                ConnectionAuthRequestImpl(
                    this,
                    authEndPoint
                )

            return connectionRequest.execute {r ->
                processResponse(r as Response<T>)
                listener(this, r)
            }
        }

        private fun processResponse(r: Response<T>) {

            _isConnected = r.status == 200 || r.status == 201
            _failureReason = r.message
        }

       internal class ConnectionAuthRequestImpl<T>(conn: Connection<T>, private val authEndPoint: String) : ConnectionRequest<T, String>(conn) {

            override fun path(): String {
                return authEndPoint
            }

            override fun response(r: String): String {
                return r
            }
        }

        internal class CredentialsImpl(private val _user: String?, private val _password: String?):
            Credentials {

            override var authType =
                AuthType.Anonymous

            override var user: String? = _user
                set(u) {

                    field = u

                    if (u != null && authType == AuthType.Anonymous)
                        authType =
                            AuthType.Basic
                    else if (u == null && authType != AuthType.Anonymous)
                        authType =
                            AuthType.Anonymous
                }

            override var password: String? = _password
                set(p) {

                    field = p

                    if (p != null && authType == AuthType.Anonymous)
                        authType =
                            AuthType.Basic
                    else if (p == null && authType != AuthType.Anonymous)
                        authType =
                            AuthType.Anonymous
                }
        }
    }
}