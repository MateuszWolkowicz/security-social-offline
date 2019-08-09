package com.wolkowiczmateusz.securitysocialoffline

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.wolkowiczmateusz.securitysocialoffline.authentication.EncryptionServices
import com.wolkowiczmateusz.securitysocialoffline.authentication.SystemServices
import com.wolkowiczmateusz.securitysocialoffline.extentions.hideKeyboard
import com.wolkowiczmateusz.securitysocialoffline.extentions.openSecuritySettings
import com.wolkowiczmateusz.securitysocialoffline.extentions.startHomeActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import android.content.Intent
import com.facebook.AccessToken
import com.wolkowiczmateusz.securitysocialoffline.extentions.makeVisible
import com.wolkowiczmateusz.securitysocialoffline.extentions.startSignInActivity

/**
 * Sign up with password screen.
 */
class SignUpActivity : BaseSecureActivity() {

    private var callbackManager: CallbackManager? = null

    companion object {
        const val EMAIL = "email"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)
        callbackManager = CallbackManager.Factory.create()
        initViews()
    }

    private fun initViews() {
        cb_offline_password.makeVisible()
        cb_offline_password.isChecked = true
        appTitleView.text = getString(R.string.app_name) + " " + getString(R.string.registration)
        confirmPasswordView.setOnEditorActionListener { _, id, _ -> onEditorActionClick(id) }
        doneView.setOnClickListener { attemptToSignUp() }

        if (systemServices.isFingerprintHardwareAvailable()) {
            allowFingerprintView.makeVisible()
        }
        allowFingerprintView.setOnCheckedChangeListener { _, checked -> onAllowFingerprint(checked) }

        login_button.setPermissions(listOf(EMAIL))
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                startHomeActivity()
            }

            override fun onCancel() {
                // App code
            }

            override fun onError(exception: FacebookException) {
                // App code
            }
        })

        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        if (isLoggedIn) {
            startHomeActivity()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun onAllowFingerprint(checked: Boolean) {
        if (checked && !systemServices.hasEnrolledFingerprints()) {
            allowFingerprintView.isChecked = false
            Snackbar.make(signUpRootView, R.string.sign_up_snack_message, Snackbar.LENGTH_LONG)
                    .setAction(R.string.sign_up_snack_action, { openSecuritySettings() })
                    .show()
        }
    }

    private fun onEditorActionClick(id: Int): Boolean = when (id) {
        EditorInfo.IME_ACTION_DONE, EditorInfo.IME_NULL -> {
            attemptToSignUp()
            true
        }
        else -> false
    }

    /**
     * Attempts to sign up with password specified by the sing up form.
     * If there are form errors errors are presented and no actual sing up attempt is made.
     */
    private fun attemptToSignUp() {
        passwordHolderView.error = null
        confirmPasswordHolderView.error = null

        val passwordString = passwordView.text.toString()
        val confirmPasswordString = confirmPasswordView.text.toString()

        var cancel = false
        var focusView: View? = null

        if (!isPasswordValid(passwordString)) {
            passwordHolderView.error = getString(R.string.sign_up_error_invalid_password)
            focusView = passwordView
            cancel = true
        } else if (passwordString != confirmPasswordString) {
            confirmPasswordHolderView.error = getString(R.string.sign_up_error_incorrect_password)
            focusView = confirmPasswordView
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {
            createKeys(passwordString, allowFingerprintView.isChecked)

            with(Storage(this)) {
                val encryptedPassword = EncryptionServices(applicationContext).encrypt(passwordString, passwordString)
                logi("Original password is: $passwordString")
                logi("Saved password is: $encryptedPassword")

                savePassword(encryptedPassword)
                saveFingerprintAllowed(allowFingerprintView.isChecked)
            }
            focusView?.hideKeyboard()

            startHomeActivity()
        }
    }


    /**
     * Create master, fingerprint and confirm credentials keys.
     */
    private fun createKeys(password: String, isFingerprintAllowed: Boolean) {
        val encryptionService = EncryptionServices(applicationContext)
        encryptionService.createMasterKey(password)

        if (SystemServices.hasMarshmallow()) {
            if (isFingerprintAllowed && systemServices.hasEnrolledFingerprints()) {
                encryptionService.createFingerprintKey()
            }
            encryptionService.createConfirmCredentialsKey()
        }
    }

    private fun isPasswordValid(password: String) = !TextUtils.isEmpty(password) && password.length >= 6

    override fun onBackPressed() {
        super.onBackPressed()
        startSignInActivity()
    }
}
