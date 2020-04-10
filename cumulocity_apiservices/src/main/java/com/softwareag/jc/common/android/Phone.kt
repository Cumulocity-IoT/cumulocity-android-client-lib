package com.softwareag.jc.common.android

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.BatteryManager
import android.telephony.*
import androidx.core.app.ActivityCompat


class Phone {

    companion object {
        fun getLastBestLocation(context: Context): Location? {

            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null
            } else {

                var locationManager =
                    context.getSystemService(android.content.Context.LOCATION_SERVICE) as LocationManager?

                val locationGPS: Location? =
                    locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                val locationNet: Location? =
                    locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

                var NetLocationTime: Long = 0

                if (null != locationNet) {
                    NetLocationTime = locationNet.time
                }

                var GPSLocationTime: Long = 0

                if (null != locationGPS) {
                    GPSLocationTime = locationGPS.time
                }

                return if (0 < GPSLocationTime - NetLocationTime) {
                    locationGPS
                } else {
                    locationNet
                }
            }
        }

        fun getSignalStrength(context: Context): Int? {

            val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val celldbm = if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return null
            } else {
                if (telephonyManager.allCellInfo[0] is CellInfoWcdma)
                    (telephonyManager.allCellInfo[0] as CellInfoWcdma).cellSignalStrength.dbm
                else if (telephonyManager.allCellInfo[0] is CellInfoGsm)
                    (telephonyManager.allCellInfo[0] as CellInfoGsm).cellSignalStrength.dbm
                else if (telephonyManager.allCellInfo[0] is CellInfoCdma)
                    (telephonyManager.allCellInfo[0] as CellInfoCdma).cellSignalStrength.dbm
                else
                    null
            }

           // val cellSignalStrengthdBm = cellinfo.cellSignalStrength

            return celldbm
        }

        fun getBatteryLevel(context: Context): Float {
            val batteryStatus: Intent? = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            )
            var batteryLevel = -1
            var batteryScale = 1
            if (batteryStatus != null) {
                batteryLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, batteryLevel)
                batteryScale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, batteryScale)
            }
            return batteryLevel / batteryScale.toFloat() * 100
        }

        fun getBatteryState(context: Context, intent: Intent?) {
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus: Intent? = context.registerReceiver(null, ifilter)

// Are we charging / charged?

// Are we charging / charged?
            val status = batteryStatus?.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL

// How are we charging?

// How are we charging?
            val chargePlug = batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
            val usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB
            val acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC
        }

        fun getOsVersion(context: Context): String? {

            val localPackageManager: PackageManager = context.getPackageManager()
            val localPackageInfo: PackageInfo =
                localPackageManager.getPackageInfo(context.getPackageName(), 0)

            return localPackageInfo.versionName
        }
    }
}