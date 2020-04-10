package com.softwareag.jc.common.kotlin.extensions

import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class StringTools {

    companion object {

        fun String.tokenize(): Map<String, String> {

            var results: HashMap<String, String> = HashMap()

            var tk: StringTokenizer = if (this.indexOf("\n") != -1)
                StringTokenizer(this, "\n")
            else if (this.indexOf("/") != -1)
                StringTokenizer(this, "/")
            else
                StringTokenizer(this, "\\")

            var z: Int = 0
            while (tk.hasMoreTokens()) {
                var pair: Pair<String, String?> = tk.nextToken().splitLine()

                if (pair.second != null)
                    results.put(pair.first, pair.second!!)
                else
                    results.put("${z++}", pair.first)
            }

            return results
        }

        fun String.splitLine(): Pair<String, String?> {

            var index: Int = if (this.indexOf("/") != -1 && this.indexOf("/") == this.lastIndexOf("/"))
                this.indexOf("/")
            else if (this.indexOf(",") != -1 && this.indexOf(",") == this.lastIndexOf(","))
                this.indexOf(",")
            else if (this.indexOf(":") != -1 && (this.indexOf(":") == this.lastIndexOf(":") || this.toLowerCase().contains("http")))
                this.indexOf(":")
            else
                -1

            if (index != -1)
                return Pair(this.substring(0,index).trim(),
                    clean(
                        this,
                        index
                    )
                )
            else
                return Pair(
                    clean(
                        this,
                        index
                    ), null)
        }

        fun String.indexOfUpper(index: Int): Int {

            var count: Int = 1
            var z: Int = -1
            for (i in this.indices) {

                if (this[i].isUpperCase() && count++ >= index) {
                    z = i
                    break
                }
            }

            return z
        }

        fun String.toStringList(separator: String): List<String> {

            var tk: StringTokenizer = StringTokenizer(this, separator)
            var out: ArrayList<String> = ArrayList()
            while (tk.hasMoreTokens()) {
                out.add(tk.nextToken())
            }

            return out
        }

        fun List<String>.flattenToString(): String {

            val builder = StringBuilder()

            this.forEach { s->
                builder.append("$s, ")

            }

            if (builder.length > 0)
                builder.deleteCharAt(builder.length-2)

            return builder.toString()
        }

        fun String.padRight( n: Int): String? {
            return String.format("%-" + n + "s", this)
        }

        fun String.padLeft(n: Int): String? {
            return String.format("%" + n + "s", this)
        }

        private fun clean(str: String, index: Int): String {

            if (str.toLowerCase().contains("http"))
                return str.substring(index+1).trim()
            else
                return str.substring(index+1).trim().replace("\\s+".toRegex(),"").replace("\\:".toRegex(),"").trim()
        }
    }
}