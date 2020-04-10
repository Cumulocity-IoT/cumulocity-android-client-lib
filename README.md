
<!-- README.md is generated from README.Rmd. Please edit that file -->

# cumulocityr <img src='man/figures/logo.png' align="right" height="138" />

<!-- badges: start -->

[![Travis build
status](https://travis-ci.org/SoftwareAG/cumulocityr.svg?branch=master)](https://travis-ci.org/SoftwareAG/cumulocityr)
[![Codecov test
coverage](https://codecov.io/gh/SoftwareAG/cumulocityr/branch/master/graph/badge.svg)](https://codecov.io/gh/SoftwareAG/cumulocityr?branch=master)
<!-- badges: end -->

Android client library for the Cumulocity API.

## Installation

You can download the .aar library as follows

[<img src="./assets/direct-apk-download.png"
      alt="Direct apk download"
      height="80">](https://github.com/johnpcarter/cumulocity-android-client-lib/cumulocity_apiservices/build/outputs/aar/jc-cumulocity-release-latest.aar)


If you want to download the source code to build the library yourself

``` sh

$ git clone https://github.com/johnpcarter/cumulocity-android-client-lib

```

The library was built using Android Studio 3.6.2

## Example

``` kotlin
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

## Documentation

Detailed documentation is available <a href="https://github.com/johnpcarter/cumulocity-android-client-lib/cumulocity_apiservices/build/dokka/-cumulocity\ -a-p-i\ -services/index.html">here</a>

-----

Please note that this project is released with a [Contributor Code of
Conduct](https://github.com/SoftwareAG/cumulocityr/blob/master/.github/CODE_OF_CONDUCT.md).
By contributing to this project, you agree to abide by its terms.

These tools are provided as-is and without warranty or support. They do
not constitute part of the Software AG product suite. Users are free to
use, fork and modify them, subject to the license agreement. While
Software AG welcomes contributions, we cannot guarantee to include every
contribution in the master project.