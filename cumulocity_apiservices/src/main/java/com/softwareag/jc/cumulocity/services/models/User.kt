package com.softwareag.jc.cumulocity.services.models

import org.json.JSONObject

const val C8Y_USER_API = "/user/currentUser"

const val CY8_USER_ID = "id"
const val CY8_USER_UNAME = "userName"
const val CY8_USER_FIRST_NAME = "firstName"
const val CY8_USER_LAST_NAME = "lastName"
const val CY8_USER_EMAIL = "email"

/**
 * Represents a Cumulocity user profile
 * This attribute becomes available as an attribute of the [CumulocityConnection] instance
 * after successfully calling its connect() method
 *
 * Makes no sense to instantiate this class yourself
 *
 * @constructor called internally to parse results after successful connection
 */
data class User(private val o: JSONObject) {

    /**
     * Internal id of the object given by Cumulocity
     */
    val id: String

    /**
     * username or alias used by account holder to connect to Cumulocity
     */
    val userName: String

    /**
     * Optional, only available after successful connection
     */
    val lastName: String?

    /**
     * Optional, only available after successful connection
     */
    val firstName: String?

    /**
     * Optional, only available after successful connection
     */
    val email: String?

    init {
        id = o.get(CY8_USER_ID) as String
        userName = o.get(CY8_USER_UNAME) as String
        lastName = o.get(CY8_USER_LAST_NAME) as String
        firstName = o.get(CY8_USER_FIRST_NAME) as String
        email = o.get(CY8_USER_EMAIL) as String
    }
}