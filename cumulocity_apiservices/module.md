# Module cumulocity_apiservices

__Author__ John Carter,
__Date__ March 2020,
__version__ 1.0,

Android v28 Kotlin developed library (.aar) giving access to Cumulocity via its REST API. Use this
library when developing Android apps to access your back-end Cumlocity tenant.

Refer to this [Cumulocity Developer Reference Site](https://cumulocity.com/guides/reference/rest-implementation)
for more information on the underlying API implementation.

Access the different Cumulocity assets using the service classes defined in the package [com.softwareag.jc.cumulocity.services.api]
and interrogate the results via objects defined by classes in the [com.softwareag.jc.cumulocity.services.models]
package.

 e.g.
 ```kotlin
    CumulocityConnectionFactory.connection(<tenant>, <instance e.g. cumulocity.com>).connect(<user>, <password>) { connection, responseInfo ->

       ManagedObjectsService(connection).managedObjectsForType(0, "c8y_DeviceGroup") { results ->

           val status: Int = results.status

           val failureReason: String? = if (status == 500)
               results.reason
           else
               null

           val objects: List<ManagedObject> = results.content
       }
   }
   ```

   Connections can be reused, but services should not be reused and are not thread-safe, hence
   should not be used in parallel by different threads.

  _Assets Types_

  Classes exist for all major assets types; [ManagedObject], [Alarm], [Binary], [DataPoints], [Event],
  [Measurement], [Operation] and [User]

  _API Services_

  To retrieve these assets use one of the api service classes in the [api] package such as
  [ManagedObjectsService], [ManagedObjectService], [AlarmsService], [EventsService],
  [MeasurementsService] etc.


# Package com.softwareag.jc.cumulocity.services.api

Query/update Cumulocity assets via well defined classes

Each class represents a specific resource type provided by Cumulocity. [ManagedObject] represents
the most heavily referenced type of data, of which the model can be heavily customised. Both devices
and groups are represented as [ManagedObject]s.
Identified [ManagedObject]s can be queried and updated via the [ManagedObjectService] class either 
using the Cumulocity internal id or a registered external id. 

Complex queries for multiple [ManagedObject]s can be implemented via [ManagedObjectsService].

# Package com.softwareag.jc.cumulocity.services.models

Classes defining the model for all of the cumulocity asset types.

API Services will provide your callback function with a RequestResponder object representing the API
response. If the response status attribute is 200.201 then content attribute if set will be either a
single or list of one of the following types.


# Package com.softwareag.jc.cumulocity.services.models.extension

Represents a custom structure added to your Cumulocity Managed Object instances

Cumulocity is highly extensible and allows you to customise the assets with any number of attributes. 
The [ManagedObject] class provides a properties map attribute that allows you to reference any of
your custom attributes without having to redevelop this library.

However there are two concerns 

