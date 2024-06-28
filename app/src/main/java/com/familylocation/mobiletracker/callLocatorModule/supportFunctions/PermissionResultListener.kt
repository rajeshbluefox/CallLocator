package com.familylocation.mobiletracker.callLocatorModule.supportFunctions

interface PermissionResultListener {
    fun onPermissionResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray)
}
