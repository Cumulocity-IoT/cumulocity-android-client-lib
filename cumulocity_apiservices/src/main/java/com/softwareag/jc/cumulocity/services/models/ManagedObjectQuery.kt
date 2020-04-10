package com.softwareag.jc.cumulocity.services.models

import java.net.URLEncoder

/**
 * Class representing a set of query arguments to used when using the query method of the [ManagedObjectsService]
 * to fetch [ManagedObject]s.
 * Currently all queries have to be true i.e. they are ANDED together
 *
 */
class ManagedObjectQuery {

    /**
     * Represents the operator to be applied to the value of the query i.e. equals, not equals etc.
     */
    enum class Operator {
        eq, ne, lt, gt, le, ge
    }

    /**
     * Represents an individual query to be applied, consisting of a key (left hand), an operator
     * and a value (right hand).
     * If the operator is blank, then the key is assumed to be a function e.g.
     * ```
     * val q = Query("bygroupid", null, "12345")
     * Log.i("example", "${q.toString()}")
     * ```
     *
     * would output
     *      example - bygroupd(12345)
     *
     */
    data class Query(val key: String, val operator: Operator?, var value: String) {

        override fun toString(): String {

            if (operator != null)
                return "$key $operator '$value'"
            else // if no operator, assume key is function
                return "$key($value)"
        }
    }

    private val queries: List<Query> = ArrayList()

    /**
     * Adds a new query to the existing set
     */
    fun add(query: Query) {
        (queries as ArrayList).add(query)
    }

    /**
     * Adds a new query to the existing set based on the individual values
     * @param key left hand operator
     * @param operator the operator to be applied e.g. 'eq' 'ne' etc. or blank if key is a function
     * @param value right hand operator or value of function is operator is null
     */
    fun add(key: String, operator: Operator?, value: String): ManagedObjectQuery {

        (queries as ArrayList).add(Query(key, operator, value))

        return this
    }

    fun build(): String {

        var b: StringBuilder = StringBuilder()

        this.queries.forEach { q ->

            if (b.isEmpty())
                b.append(q.toString())
            else
                b.append(" and ").append(q.toString())
        }

        return URLEncoder.encode(b.toString(), "UTF8")
    }
}