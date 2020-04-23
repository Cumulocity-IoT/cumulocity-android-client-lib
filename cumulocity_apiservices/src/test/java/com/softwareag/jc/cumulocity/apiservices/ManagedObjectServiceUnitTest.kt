package com.softwareag.jc.cumulocity.apiservices

import com.softwareag.jc.common.api.Response
import com.softwareag.jc.cumulocity.services.api.CumulocityConnectionFactory
import com.softwareag.jc.cumulocity.services.api.ManagedObjectService
import com.softwareag.jc.cumulocity.services.models.C8Y_MANAGED_OBJECT_TYPE_DETAILS
import com.softwareag.jc.cumulocity.services.models.IsDeviceType
import com.softwareag.jc.cumulocity.services.models.ManagedObject
import com.softwareag.jc.cumulocity.services.models.extension.Planning
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(RobolectricTestRunner::class)
class ManagedObjectServiceUnitTest {

    private val _sync: ResultsWrapper = ResultsWrapper()

    @Test
    fun postMangedObject() {

        CumulocityConnectionFactory.connection("", "cumulocity.com").connect("", "") { c, r ->

            val obj = ManagedObject(null, "12345","test device", "jc", "acs", "1", "SensorPhone", "parp", null, "test")
            (obj.properties as HashMap)[C8Y_MANAGED_OBJECT_TYPE_DETAILS] = IsDeviceType()

            ManagedObjectService(c).add(obj) {

                _sync.setResults(it)
            }
        }

        // wait for query

        val results = _sync.results

        assertEquals(results.message, 200, results.status)
    }

    @Test
    fun testGetManagedObject() {

        CumulocityConnectionFactory.connection("", "cumulocity.com").connect("", "") { c, r ->

            ManagedObjectService(c).get("15718501") {

                _sync.setResults(it)
            }
        }

        // wait for query

        val results = _sync.results

        assertEquals(results.message, 200, results.status)
    }

    @Test
    fun testGetManagedObjectWithCustomExtensions() {

        CumulocityConnectionFactory.connection("", "cumulocity.com").connect("", "") { c, r ->

            ManagedObjectService(c).get("15718501") {

                _sync.setResults(it)
            }
        }

        // wait for query

        val results = _sync.results

        assertEquals(results.message, 200, results.status)
        assertNotNull("Expecting to find xIsVerified attribute from group", results.content!!.properties["xIsVerified"])
        assertNotNull("Expecting to find complex property xPlanning", (results.content!!.properties["xPlanning"] as Planning))

    }

    class ResultsWrapper(): Object() {

        private var _results: Response<ManagedObject>? = null

        private val lock = ReentrantLock()
        private val condition = lock.newCondition()

        val results: Response<ManagedObject>
            get() {

                lock.withLock {

                    if (_results != null) {
                        return _results!!
                    } else {
                        condition.await()
                    }
                }

                return _results!!
            }

        fun setResults(results: Response<ManagedObject>) {

            _results = results

            lock.withLock {
                condition.signal()
            }
        }
    }
}
