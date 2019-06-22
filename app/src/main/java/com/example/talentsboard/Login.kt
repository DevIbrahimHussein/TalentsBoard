package com.example.talentsboard

import android.os.Bundle
import android.support.v4.app.Fragment
import android.widget.Button
import android.widget.EditText
import android.content.Intent
import android.support.design.widget.TextInputLayout
import android.util.Log
import android.view.*
import android.widget.Toast
//import com.facebook.CallbackManager
//import com.facebook.FacebookCallback
//import com.facebook.FacebookException
//import com.facebook.login.LoginManager
//import com.facebook.login.LoginResult
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import java.lang.Exception
import java.security.MessageDigest
import java.util.*


class Login : Fragment() {

    private var email: EditText?= null
    private var password: EditText?= null
    private var go: Button?= null
    private var registerLink: Button?= null
    private var layoutEmail: TextInputLayout?= null
    private var layoutPassword: TextInputLayout?= null
    private var facebookLogin: Button?= null
//    private var mCallbackManager: CallbackManager? = null


    fun hash(input: String): String {
        val bytes = input.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("", { str, it -> str + "%02x".format(it) })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        this.email = view!!.findViewById(R.id.login_email) as EditText
        this.password = view!!.findViewById(R.id.password_login) as EditText
        this.layoutEmail = view!!.findViewById(R.id.emailLayout) as TextInputLayout
        this.layoutPassword = view!!.findViewById(R.id.password_Layout) as TextInputLayout
        this.go = view!!.findViewById(R.id.go_login) as Button
        this.registerLink= view!!.findViewById(R.id.register_link) as Button
//        this.facebookLogin = view!!.findViewById(R.id.login_button) as Button

        this.registerLink!!.setOnClickListener {

            val fragment = Registration()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_main, fragment)
            transaction.commit()

        }

        this.go!!.setOnClickListener {

                if(this.email!!.text.isNotEmpty() && this.email!!.text.isNotBlank() && this.email!!.text.isNullOrBlank().not() && this.email!!.text.isNullOrEmpty().not()){
                    this.layoutEmail!!.isErrorEnabled = false
                    if(this.password!!.text.isNotEmpty() && this.password!!.text.isNotBlank() && this.password!!.text.isNullOrBlank().not() && this.password!!.text.isNullOrEmpty().not()) {
                        this.layoutPassword!!.isErrorEnabled = false

                        try{
                            Fuel.get("http://10.0.2.2/Android/TalentsBoard/loginProcess.php?" +
                                    "Email=${this.email!!.text!!.trim()}&" +
                                    "Password=${hash(this.password!!.text.trim().toString())}")
                                .responseJson { _, _, result ->

                                    try{
                                        if (result.get().obj().getString("res").toLowerCase() == "success") {

                                            val userId = result.get().obj().getString("userId")
                                            val intent = Intent(context, MainActivity::class.java)
                                            intent.putExtra("Email Address", this.email!!.text.trim().toString())
                                            intent.putExtra("userId",userId)
                                            startActivity(intent)
                                            activity!!.finish()

                                        } else {
                                            Toast.makeText(context, "Wrong email or password", Toast.LENGTH_LONG).show()
                                            this.email!!.requestFocus()
                                        }
                                    } catch (e: Exception){
                                        Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
                                    }
                                } // end of fuel
                        } catch (e: Exception){
                            Toast.makeText(context,e.toString(),Toast.LENGTH_LONG).show()
                        }

                    } else {
                        this.layoutPassword!!.error = "Password can't be empty"
                        this.password!!.requestFocus()
                    }
                } else {
                    this.layoutEmail!!.error = "Email can't be empty"
                    this.email!!.requestFocus()
                }
        } // end of listener


//        this.facebookLogin?.setOnClickListener{
//            // Login
//            var mCallbackManager = CallbackManager.Factory.create()
//            /* Set the initial permissions to request from the user while logging in */
//            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email","user_status"))
//            LoginManager.getInstance().registerCallback(
//                this.mCallbackManager,
//
//                object : FacebookCallback<LoginResult> {
//                    override fun onSuccess(loginResult: LoginResult) {
//                        Log.d("MainActivity", "Facebook token: " + loginResult.accessToken.token)
//                        startActivity(Intent(context, MainActivity::class.java))
//                    }
//
//                    override fun onCancel() {
//                        Log.d("MainActivity", "Facebook onCancel.")
//
//                    }
//
//                    override fun onError(error: FacebookException) {
//                        Log.d("MainActivity", "Facebook onError.")
//                        Toast.makeText(context,error.toString(),Toast.LENGTH_LONG).show()
//                    }
//                }
//            )
//        } // end facebook login


    } // end of class

}
