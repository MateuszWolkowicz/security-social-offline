package com.wolkowiczmateusz.securitysocialoffline.extentions

import android.app.Activity
import android.content.Intent
import android.opengl.Visibility
import android.view.View
import com.wolkowiczmateusz.securitysocialoffline.HomeActivity
import com.wolkowiczmateusz.securitysocialoffline.SecretActivity
import com.wolkowiczmateusz.securitysocialoffline.SignInActivity
import com.wolkowiczmateusz.securitysocialoffline.SignUpActivity
import com.wolkowiczmateusz.securitysocialoffline.Storage

fun Activity.startSecretActivity(requestCode: Int, mode: Int = SecretActivity.MODE_CREATE, password: String? = null, secretData: Storage.SecretData? = null) {
    val intent = Intent(this, SecretActivity::class.java)
    intent.putExtra("mode", mode)
    password?.let { intent.putExtra("password", password) }
    secretData?.let { intent.putExtra("secret", secretData) }
    startActivityForResult(intent, requestCode)
}

fun Activity.startHomeActivity(finishCallingActivity: Boolean = true) = startActivity(HomeActivity::class.java, finishCallingActivity)

fun Activity.startSignUpActivity(finishCallingActivity: Boolean = true) = startActivity(SignUpActivity::class.java, finishCallingActivity)
fun Activity.startSignInActivity(finishCallingActivity: Boolean = true) = startActivity(SignInActivity::class.java, finishCallingActivity)

private fun Activity.startActivity(cls: Class<*>, finishCallingActivity: Boolean = true) {
    val intent = Intent(this, cls)
    startActivity(intent)
    finishCallingActivity.let { finish() }
}

fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeInvisible() {
    visibility = View.INVISIBLE
}

fun View.makeGone() {
    visibility = View.GONE
}

fun View.makeEnabled() {
    isEnabled = true
}

fun View.makeDisabled() {
    isEnabled = false
}
