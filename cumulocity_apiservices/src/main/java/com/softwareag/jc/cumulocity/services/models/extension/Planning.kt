package com.softwareag.jc.cumulocity.services.models.extension

import com.softwareag.jc.common.kotlin.extensions.DateTools.Companion.iSO861StringToDate
import org.json.JSONObject
import java.util.*

const val JC_MANAGED_OBJECT_PLANNING = "xPlanning"
const val JC_MANAGED_OBJECT_PLANNING_IS_DEPLOYED = "xPlanningIsDeployed"
const val JC_MANAGED_OBJECT_PLANNING_DATE = "xPlanningDate"
const val JC_MANAGED_OBJECT_ATTACHMENTS = "xAttachmentIds"
const val JC_MANAGED_OBJECT_PLANNING_OWNER = "xPlanningProjectOwner"

data class Planning(val isDeployed: Boolean, val date: Date?, val projectOwner: String?) {

    constructor(o: JSONObject) : this(o.getBoolean(JC_MANAGED_OBJECT_PLANNING_IS_DEPLOYED), planningDate(o), projectOwner(o)) {
    }

    companion object {

        fun planningDate(o: JSONObject): Date? {

            return if (o.has(JC_MANAGED_OBJECT_PLANNING_DATE))
                try {
                    o.getString(JC_MANAGED_OBJECT_PLANNING_DATE).iSO861StringToDate()
                } catch (e: Exception) {
                    // ignore it

                    null
                }
            else
                null
        }

        fun projectOwner(o: JSONObject): String? {

            return if (o.has(JC_MANAGED_OBJECT_PLANNING_OWNER))
                try {
                    o.getString(JC_MANAGED_OBJECT_PLANNING_OWNER)
                } catch (e: Exception) {
                    // ignore it

                    null
                }
            else
                null
        }
    }
}