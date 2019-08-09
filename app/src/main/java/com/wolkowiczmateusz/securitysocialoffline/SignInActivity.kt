package com.wolkowiczmateusz.securitysocialoffline

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.inputmethod.EditorInfo
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginResult
import com.wolkowiczmateusz.securitysocialoffline.extentions.startHomeActivity
import kotlinx.android.synthetic.main.activity_sign_up.*
import android.content.Intent
import com.facebook.AccessToken
import com.wolkowiczmateusz.androidextensions.makeGone
import com.wolkowiczmateusz.androidextensions.makeVisible
import com.wolkowiczmateusz.securitysocialoffline.extentions.startSignUpActivity

/**
 * Sign up with password screen.
 */
class SignInActivity : BaseSecureActivity() {

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
        confirmPasswordView.makeGone()
        btn_signUp.makeVisible()
        singUpHintView.makeGone()
        allowFingerprintView.makeGone()
        confirmPasswordView.setOnEditorActionListener { _, id, _ -> onEditorActionClick(id) }
        doneView.setOnClickListener { attemptToSignIn() }

        login_button.setPermissions(listOf(EMAIL))
        // If you are using in a fragment, call loginButton.setFragment(this);

        btn_signUp.setOnClickListener {
            startSignUpActivity()
        }

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

    private fun onEditorActionClick(id: Int): Boolean = when (id) {
        EditorInfo.IME_ACTION_DONE, EditorInfo.IME_NULL -> {
            attemptToSignIn()
            true
        }
        else -> false
    }

    /**
     * Attempts to sign up with password specified by the sing up form.
     * If there are form errors errors are presented and no actual sing up attempt is made.
     */
    private fun attemptToSignIn() {
        passwordHolderView.error = null
        confirmPasswordHolderView.error = null
        usernameHolderView.error = null

        val passwordString = passwordView.text.toString()
        val userNameString = userNameView.text.toString()

        var cancel = false
        var focusView: View? = null


        if (userNameString.isEmpty()) {
            usernameHolderView.error = getString(R.string.empty_user_name)
            focusView = userNameView
            cancel = true
        }

        if (!isPasswordValid(passwordString)) {
            passwordHolderView.error = getString(R.string.sign_up_error_invalid_password)
            focusView = passwordView
            cancel = true
        }

        if (passwordString.isEmpty() ) {
            passwordHolderView.error = getString(R.string.password_empty)
            focusView = passwordView
            cancel = true
        }

        if (cancel) {
            focusView?.requestFocus()
        } else {

            startHomeActivity()
        }
    }


    private fun isPasswordValid(password: String) = !TextUtils.isEmpty(password) && password.length >= 6
}