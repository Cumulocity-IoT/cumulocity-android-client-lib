package com.softwareag.jc.cumulocity.apiservices

import com.softwareag.jc.common.api.Response
import com.softwareag.jc.cumulocity.services.api.CumulocityConnectionFactory
import com.softwareag.jc.cumulocity.services.api.ManagedObjectsService
import com.softwareag.jc.cumulocity.services.models.ManagedObject
import com.softwareag.jc.cumulocity.services.models.ManagedObjectQuery
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
class ManagedObjectsServiceUnitTest {

    private val _sync: ResultsWrapper = ResultsWrapper()

    @Test
    fun testGetManagedObjectGroups() {

        CumulocityConnectionFactory.connection("", "cumulocity.com").connect("", "") { c, r ->

            ManagedObjectsService(c).managedObjectsForType(0, "c8y_DeviceGroup") {

                _sync.setResults(it)
            }
        }

        // wait for query

        val results = _sync.results

        assertEquals(results.message, 200, results.status)
    }

    @Test
    fun testGetManagedObjectsForQuery() {

        CumulocityConnectionFactory.connection("", "cumulocity.com").connect("", "") { c, r ->

            ManagedObjectsService(c).managedObjectsForQuery(0, ManagedObjectQuery().add("bygroupid", null, "15718501")) {

                _sync.setResults(it)
            }
        }

        // wait for query

        val results = _sync.results

        assertEquals(results.message, 200, results.status)
        assertEquals("Expected 2 subgroups", 2, results.content!!.size)
    }

    class ResultsWrapper(): Object() {

        private var _results: Response<List<ManagedObject>>? = null

        private val lock = ReentrantLock()
        private val condition = lock.newCondition()

        val results: Response<List<ManagedObject>>
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

        fun setResults(results: Response<List<ManagedObject>>) {

            _results = results

            lock.withLock {
                condition.signal()
            }
        }
    }
}
