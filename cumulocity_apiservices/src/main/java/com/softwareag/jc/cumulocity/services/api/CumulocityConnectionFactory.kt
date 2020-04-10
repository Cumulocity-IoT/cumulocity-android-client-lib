package com.softwareag.jc.cumulocity.services.api

import android.os.AsyncTask
import com.softwareag.jc.common.api.Connection
import com.softwareag.jc.common.api.ConnectionFactory
import com.softwareag.jc.common.api.Credentials
import com.softwareag.jc.common.api.OnConnectionResult
import com.softwareag.jc.cumulocity.services.models.C8Y_USER_API
import com.softwareag.jc.cumulocity.services.models.User
import org.json.JSONObject
import java.net.URL

/**
 * Establish your connection instances to Cumulocity through this class.
 *
 * e.g.
 *  ```kotlin
 *  CumulocityConnectionFactory.connection(<tenant>, <instance e.g. cumulocity.com>).connect(<user>, <password>) { connection, responseInfo ->
 *
 *      if (conn.isConnected) {
 *
 *          Log.i("success", "Hello ${conn.userProfile().firstName}, ${conn.userProfile().lastName}")
 *
 *          ManagedObjectService(connection).get("12345") { result ->
 *
 *              if (status == 500) {
 *
 *                  val failureReason: String? = results.reason
 *                  ...
 *
 *              } else {
 *
 *                  val device: ManagedObject = results.content
 *                  ...
 *
 *              }
 *          }
 *      } else {
 *
 *          Log.e("error", "Login Refused - ${conn.failureReason}")
 *     }
 *  }
 *  ```
 *
 *   The connection can be reused without having to re-authenticate if required.
 */
class CumulocityConnectionFactory: ConnectionFactory<User>() {

    /**
     * Represents a connection. Call the connect() method to test and/or retrieve the [User] profile
     * of the cumulocity account associated with given credentials. The implementation of this interface
     * is returned via the [CumulocityConnectionFactory connection] method.
     *
     * The connection object can then be used instantiate the API Services to interrogate Cumulocity
     *
     * @param endPoint The endpoint that will be used to connect to your cumulocity tenant minus the resource path
     * @param isConnected: Boolean result of call to connect() method
     * @param failureReason: String? If is isConnected is false this might give you an idea of why
     * @param credentials: Credentials the user/password used in the call to connect()
     * @param headers: Map<String, String> The headers returned by the call to connect()
     */
    interface CumulocityConnection: Connection<User> {

        /**
         * User profile returned by Cumulocity for the given connection
         */
        fun userProfile(): User?

        /**
         * Verifies connection with Cumulocity and calls the callback function with the results
         * The connection given in the callback should be the one used when using the Cumulocity services
         * to retrieve/update assets.
         *
         * @param user user id to authenticate with
         * @param password password of the given user
         * @param responder callback function to confirm connection, use this to call any of the
         * cumulocity services.
         * @return AsynTask the kotlin task where the http request is being called from. Not generally useful
         * but allows request to be cancelled if so required.
         */
        override fun connect(
            user: String,
            password: String,
            responder: OnConnectionResult<User>
        ): AsyncTask<String, Unit, Unit>
    }

    companion object {

        private var _c: CumulocityConnection? = null
        private var _t: String? = null
        private var _s: String? = null

        /**
         * Returns a connection object for the given tenant and instance
         * @param tenant the tenant that you registered with cumulocity e.g. frpresales
         * @param serverInstance the url of the instance to which you want to connect e.g. cumulocity.com
         * @return CumulocityConnection an instance that you can use to connect and run the services as shown above
         */
        fun connection(tenant: String, serverInstance: String): CumulocityConnection {

            if (_c == null || _t != tenant.replace("_", "%2d") || _s != serverInstance) {

                _t = tenant.replace("_", "%2d")
                _s = serverInstance

                val url = URL("https://$_t.$_s")
                _c = CumulocityConnectionImpl(CumulocityAuthConnection(url, C8Y_USER_API))
            }

            return _c!!
        }
    }

    internal class CumulocityAuthConnection(url: URL, a: String): BasicAuthConnection<User>(url, a, null, null) {

        override fun connect(listener: OnConnectionResult<User>): AsyncTask<String, Unit, Unit> {

           return super.connect() { _, r ->

               listener(this, r)
           }
        }
    }

    internal class CumulocityConnectionImpl(private val c: Connection<User>): CumulocityConnection {

        private var _user: User? = null

        override val endPoint: URL
            get() {
                return c.endPoint
            }

        override val isConnected: Boolean
            get() {
                return c.isConnected
            }

        override val failureReason: String?
            get() {
                return c.failureReason
            }

        override val credentials: Credentials
            get() {
                return c.credentials
            }

        override val headers: Map<String, String>
            get() {
                return headers
            }

        override fun connect(user: String, password: String, listener: OnConnectionResult<User>): AsyncTask<String, Unit, Unit> {

            return c.connect(user, password) { c, r ->

                if (c.isConnected && r.content != null) {
                    _user = User(JSONObject(r.content as String))
                }

                listener(this, r)
            }
        }

        override fun connect(listener: OnConnectionResult<User>): AsyncTask<String, Unit, Unit> {

            return c.connect() { c, r ->

                if (c.isConnected && r.content != null) {
                    _user = User(JSONObject(r.content as String))
                }

                listener(this, r)
            }
        }

        override fun userProfile(): User? {

            return _user
        }
    }
}