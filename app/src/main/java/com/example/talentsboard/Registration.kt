package com.example.talentsboard

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.android.extension.responseJson
import java.lang.Exception
import java.security.MessageDigest
import java.util.regex.Matcher
import java.util.regex.Pattern

class Registration : Fragment() {

    private var email: EditText? = null
    private var emailLayout: TextInputLayout?= null
    private var firstName: EditText?= null
    private var firstNameLayout: TextInputLayout?= null
    private var lastName: EditText?= null
    private var lastNameLayout: TextInputLayout?= null
    private var password: EditText?= null
    private var passwordLayout: TextInputLayout?= null
    private var login: Button?= null
    private var go: Button?= null

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isValidPassword(password: String): Boolean {

        val pattern: Pattern
        val matcher: Matcher
        val PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{4,}$"
        pattern = Pattern.compile(PASSWORD_PATTERN)
        matcher = pattern.matcher(password)
        return matcher.matches()
    }

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
        return inflater.inflate(R.layout.fragment_registration, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        this.emailLayout = view!!.findViewById(R.id.emailLayout) as TextInputLayout
        this.passwordLayout = view!!.findViewById(R.id.passwordLayout) as TextInputLayout
        this.firstNameLayout = view!!.findViewById(R.id.firstNameLayout) as TextInputLayout
        this.lastNameLayout = view!!.findViewById(R.id.lastNameLayout) as TextInputLayout
        this.email = view!!.findViewById(R.id.emailText) as EditText
        this.password = view!!.findViewById(R.id.passwordText) as EditText
        this.firstName = view!!.findViewById(R.id.firstNameText) as EditText
        this.lastName = view!!.findViewById(R.id.lastNameText) as EditText
        this.login = view!!.findViewById(R.id.login_link) as Button
        this.go = view!!.findViewById(R.id.go_register) as Button

        this.login!!.setOnClickListener {

            val fragment = Login()
            val transaction = activity!!.supportFragmentManager.beginTransaction()
            transaction.replace(R.id.frame_main, fragment)
            transaction.commit()

        }

        this.go!!.setOnClickListener {

            if(this.email!!.text.isNotEmpty() && this.email!!.text.isNotBlank() && !this.email!!.text.isNullOrEmpty() && this.email!!.text.isNullOrBlank().not()){
                this.emailLayout!!.isErrorEnabled =  false
                    if(isEmailValid(this.email!!.text.trim().toString())){
                        if(this.firstName!!.text.isNotEmpty() && this.firstName!!.text.isNotBlank() && !this.firstName!!.text.isNullOrEmpty() && this.firstName!!.text.isNullOrBlank().not()) {
                            this.firstNameLayout!!.isErrorEnabled = false
                            if(this.lastName!!.text.isNotEmpty() && this.lastName!!.text.isNotBlank() && !this.lastName!!.text.isNullOrEmpty() && this.lastName!!.text.isNullOrBlank().not()) {
                                this.lastNameLayout!!.isErrorEnabled = false
                                if(this.password!!.text.isNotEmpty() && this.password!!.text.isNotBlank() && !this.password!!.text.isNullOrEmpty() && !this.password!!.text.isNullOrBlank()) {
                                    this.passwordLayout!!.isErrorEnabled = false
                                    if(isValidPassword(this.password!!.text.trim().toString())){
                                        Fuel.get(
                                            "http://10.0.2.2/Android/TalentsBoard/registrationProcess.php?" +
                                                    "email=${this.email!!.text!!.trim()}&" +
                                                    "firstName=${this.firstName!!.text.trim()}&" +
                                                    "lastName=${this.lastName!!.text.trim()}&" +
                                                    "Password=${hash(this.password!!.text.trim().toString())}"
                                            )
                                                .responseJson { _, _, result ->

                                                try{
                                                    if(result.get().obj().getString("res").toLowerCase()=="success"){

                                                        val intent = Intent(context,MainActivity::class.java)
                                                        intent.putExtra("Email Address",this.email!!.text.trim().toString())
                                                        intent.putExtra("userId",result.get().obj().getString("userId"))
                                                        startActivity(intent)
                                                        activity!!.finish()

                                                    } else {
                                                        this.emailLayout!!.error = "Email Already Exists"
                                                        this.email!!.requestFocus()
                                                    }
                                                } catch (e: Exception){
                                                    Toast.makeText(context,e.toString(), Toast.LENGTH_LONG).show()
                                                }
                                            }
                                    } else {
                                        this.passwordLayout!!.error = "Password is not at correct form"
                                        this.password!!.requestFocus()
                                    }
                                } else {
                                    this.passwordLayout!!.error = "Password must not be empty"
                                    this.password!!.requestFocus()
                                }
                            } else {
                                this.lastNameLayout!!.error = "Last name must not be empty"
                                this.lastName!!.requestFocus()
                            }
                        } else {
                            this.firstNameLayout!!.error = "First name must not be empty"
                            this.firstName!!.requestFocus()
                        }
                    } else {
                        this.emailLayout!!.error = "Email not in correct form"
                        this.email!!.requestFocus()
                    }
            } else {
                this.emailLayout!!.error = "Email must not be empty"
                this.email!!.requestFocus()
            }
        } // end of listener
    } // end of main
} // end of class
