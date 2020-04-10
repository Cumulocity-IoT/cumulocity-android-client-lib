package com.softwareag.jc.cumulocity.services.models.extension

import org.json.JSONObject

const val JC_MANAGED_OBJECT_CONTACT = "xContact"

const val JC_MANAGED_OBJECT_CONTACT_NAME = "xContactName"
const val JC_MANAGED_OBJECT_CONTACT_PHONE = "xContactPhone"
const val JC_MANAGED_OBJECT_CONTACT_EMAIL = "xContactEmail"

data class Contact(val contact: String?, val contactPhone: String?, val contactEmail: String?) {

    constructor(o: JSONObject): this(name(o), phone(o), email(o) ) {
    }

    companion object {

        fun phone(o: JSONObject): String? {

            return if ( o.has(JC_MANAGED_OBJECT_CONTACT_PHONE))
                o.getString(JC_MANAGED_OBJECT_CONTACT_PHONE)
            else
                null
        }

        fun email(o: JSONObject): String? {

            return if ( o.has(JC_MANAGED_OBJECT_CONTACT_EMAIL))
                o.getString(JC_MANAGED_OBJECT_CONTACT_EMAIL)
            else
                null
        }

        fun name(o: JSONObject): String? {

            return if ( o.has(JC_MANAGED_OBJECT_CONTACT_NAME))
                o.getString(JC_MANAGED_OBJECT_CONTACT_NAME)
            else
                null
        }
    }
}