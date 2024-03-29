package com.wolkowiczmateusz.securitysocialoffline

import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.wolkowiczmateusz.securitysocialoffline.authentication.SystemServices

open class BaseSecureActivity : AppCompatActivity() {

    val systemServices by lazy(LazyThreadSafetyMode.NONE) { SystemServices(this) }

    private var deviceSecurityAlert: AlertDialog? = null

    override fun onStart() {
        super.onStart()
        if (!systemServices.isDeviceSecure()) {
            deviceSecurityAlert = systemServices.showDeviceSecurityAlert()
        }
    }

    override fun onStop() {
        super.onStop()
        deviceSecurityAlert?.dismiss()
    }
}