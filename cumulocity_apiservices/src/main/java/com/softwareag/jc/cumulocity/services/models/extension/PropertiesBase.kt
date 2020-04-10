package com.softwareag.jc.cumulocity.services.models.extension

import com.softwareag.jc.common.kotlin.extensions.StringTools.Companion.indexOfUpper
import com.softwareag.jc.cumulocity.services.models.*
import org.json.JSONArray
import org.json.JSONObject
import java.lang.reflect.Constructor

/**
 * Manages custom attributes found in [ManageObject] and allows them to be referenced via a properties
 * map keyed by the name of the found custom attribute
 * e.g.
 *
 *```
 * {
 *      ...
 *      "xSimple_custom_attribute": "12345",
 *      "complex_custom_attribute: {
 *          "name": "test",
 *          "value": {
 *              "label": "Can be nested too!"
 *              }
 *          }
 *      }
 *      ...
 * }
 * ```
 *
 * becomes
 *
 *```
 * properties["xSimple_custom_attribute"] = "12345"
 * properties["complex_custom_attribute.name"] = "test"
 * properties["complex_custom_attribute.value.label"] = "Can be nested too!"
 * ```
 *
 * You can create your own custom classes and register them here if your structures are too complex
 * to be easily managed as above
 * e.g.
 *
 *```
 * PropertiesBase.registerCustomAttributeClass("complex_custom_attribute", "your.package.class")
 *
 * val myCustomAttrib: <class> = properties["complex_custom_attribute"] as <class>
 *```
 *
 * Your class will need to provide a constructor that takes a single JSON object as input, with JSON
 * object being the root element representing the custom structure. Your constructor can then decide
 * how to decompose the values in the structure and what properties to expose and how.
 * e.g.
 *```
 * class MyCustomAttribClass(o: JSONObject): JsonSerializable {
 *
 *  val name: String?
 *
 *  init {
 *      name = o.getString("name")
 *      ...
 *  }
 *  ...
 *  /**
 *  Only required if you want to send this custom attribute for update when sending ManagedObjects
 *  to Cumulocity
 *  */
 *  override fun toJSONString(): String {
 *      return JsonSerializable.toJSONString(this)
 *  }
 * }
 *```
 *
 * You can also group simple custom attributes together via your own custom classes by prefixing them
 * with the name of the custom label
 * e.g.
 *```
 * {
 *  ...
 *      "xPlanningDate": "11-12-20",
 *      "xPlanningInfo": "this is a test"
 *  ...
 *  }
 *```
 *
 * could be processed as
 *
 *```
 * PropertiesBase.registerCustomAttributeClass("xPlanning", "your.package.class")
 *
 * val myCustomAttrib: <class> = properties["xPlanning"] as <class>
 *```
 *
 * As above your class needs to provide a constructor to take the JSONObject, however in this case
 * it will be provided with the JSON root node containing the first attribute starting with "xPlanning"...
 * Your constructor will then have to extract the relevant attributes and assign them to the appropriate
 * properties such as date() and info()
 */
open class PropertiesBase(json: JSONObject?, excludeTopLevel: Boolean) {

    /**
     * Collection of any custom attributes found in the ManageObject structure
     */
    val properties: HashMap<String, Any> = HashMap()

    init {

        if (json != null)
            convertToMap(json, properties,"", excludeTopLevel)
    }

    private fun convertToMap(o: JSONObject, props: HashMap<String, Any>, path: String, excludeTopLevel: Boolean): Map<String, Any> {

        o.keys().forEach { k ->

            if (!exclude.contains(k)) {
                var v = o.get(k)

                if (v is JSONObject) {
                    processJSONObject(path, props, k , v, -1)
                } else if (v is JSONArray) {
                    for (i in 0 until v.length()) {
                        var x = v.get(i)

                        if (x is JSONObject)
                            processJSONObject("$path.$k", props, k, x, i)
                        else
                            props["$path.$k"] = v
                    }
                } else if (!excludeTopLevel){
                    (props as HashMap)["$path.$k"] = v
                } else if (k.startsWith("x")) {
                    // custom property

                    var kk: String = if (k.indexOfUpper(2) != -1)
                        k.substring(0, k.indexOfUpper(2))
                    else
                        k

                    if (customClasses.keys.contains(kk)) {

                        if (props[kk] == null) {
                            props[kk] = defaultConstructorForClass(kk)!!.newInstance(o)
                        }
                    } else {
                        if (path.isNullOrEmpty())
                            props[k] = v
                        else
                            props["$path.$k"] = v
                    }
                }
            }
        }

        return props
    }

    private fun processJSONObject(path: String, props: HashMap<String, Any>, k: String, o: JSONObject, index: Int) {

        var list: ArrayList<Any>? = null

        if (index != -1) {
            list = props[k] as ArrayList<Any>?

            if (list == null) {
                list = ArrayList()
                props[k] = list
            }
        }

        if (customClasses.keys.contains(k)) {

            if (list != null) {
                list.add(defaultConstructorForClass(k)!!.newInstance(o))

            } else {
               props[k] = defaultConstructorForClass(k)!!.newInstance(o)
           }
        } else {

            if (list != null) {
                list.add(convertToMap(o, HashMap(), "", false))
            } else {
                convertToMap(o, props, path, false)
            }
        }
    }

    private fun defaultConstructorForClass(className: String): Constructor<*>? {
        var c = Class.forName(customClasses[className]!!).constructors
        var constructor: Constructor<*>? = null
        for (i in c.indices) {
            if (c[i].parameterCount == 1) {
                constructor = c[i]
                break
            }
        }

        return constructor
    }

    companion object {

        private val customClasses: HashMap<String, String> = HashMap<String, String>()
        private val exclude: ArrayList<String>

        /**
         * Allows you to register your own custom classes to manage custom structures found in
         * Cumulocity assets.
         *
         * @param name Must match the name of the complex structure referenced by the JSON object
         * returned by Cumulocity. Alternatively if you want to use this to encapsulate simple values,
         * specify a prefix. The first value found starting with this prefix call the constructor of
         * your class providing the JSONObject containing the custom attribute. Prefix must start
         * with 'x' e.g. 'xPlanning'
         * @param namespaceForPackageAndClass full namespace of your class including its package location
         * Your class must provide a constructor that take a single JSONObject parameter.
         */
        fun registerCustomAttributeClass(name: String, namespaceForPackageAndClass: String) {

            customClasses[name] = namespaceForPackageAndClass
        }

        init {

            customClasses[JC_MANAGED_OBJECT_INFO] = "com.softwareag.jc.cumulocity.services.models.extension.Info"
            customClasses[JC_MANAGED_OBJECT_PLANNING] = "com.softwareag.jc.cumulocity.services.models.extension.Planning"
            customClasses[JC_MANAGED_OBJECT_CONTACT] = "com.softwareag.jc.cumulocity.services.models.extension.Contact"
            customClasses[JC_MANAGED_OBJECT_SUPPLIER] = "com.softwareag.jc.cumulocity.services.models.extension.Supplier"
            customClasses[JC_MANAGED_OBJECT_MODEL] = "com.softwareag.jc.cumulocity.services.models.extension.Model"

            exclude = ArrayList()

            exclude.add(C8Y_MANAGED_OBJECT_DEVICES)
            exclude.add(C8Y_MANAGED_OBJECT_ASSETS)
            exclude.add("additionParents")
            exclude.add("childAdditions")
            exclude.add("assetParents")
            exclude.add("deviceParents")
            exclude.add(C8Y_MANAGED_OBJECT_FIRMWARE)
            exclude.add(C8Y_MANAGED_OBJECT_ALARMS)
            exclude.add(C8Y_MANAGED_OBJECT_AVAILABILITY)
            exclude.add(C8Y_MANAGED_OBJECT_REQ_AVAILABILITY)
            exclude.add(C8Y_MANAGED_OBJECT_CONNECTION)
            exclude.add(C8Y_MANAGED_OBJECT_DATA_POINTS)
            exclude.add(C8Y_MANAGED_OBJECT_OPERATIONS)
            exclude.add(C8Y_MANAGED_OBJECT_POSITION)
            exclude.add(C8Y_MANAGED_OBJECT_HARDWARE)
            exclude.add(C8Y_MANAGED_OBJECT_HARDWARE_MODEL)
            exclude.add(C8Y_MANAGED_OBJECT_HARDWARE_SERIAL)
        }
    }
}