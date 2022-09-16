package com.example.wsamad6

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.example.wsamad6.core.Constants
import com.example.wsamad6.core.networkInfo
import com.example.wsamad6.data.post
import com.example.wsamad6.data.signIn
import com.example.wsamad6.databinding.ActivityLoginBinding
import com.example.wsamad6.databinding.ActivityMainBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        clicks()


    }

    private fun clicks() {
        binding.btnSignIn.setOnClickListener { validateData() }
    }

    private fun validateData() {
        val results = arrayOf(validateEmail(), validatePassword())
        if (false in results) return

        if (!networkInfo(applicationContext)){
            alertMessage("We can’t find an internet connection")
            return
        }
        setVisibleProgress(true)
        sendSignIn()
    }

    private fun sendSignIn() {
        Constants.OKHTTP.newCall(post("signin", signIn(binding.edtEmail.text.toString(),binding.edtPassword.text.toString()))).enqueue(object  : Callback{
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
                runOnUiThread {
                    setVisibleProgress(false)
                    alertMessage("Server Error!")
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val json  = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                if (json.getBoolean("success")){
                    val data = json.getJSONObject("data")
                    val sharedPreferences = getSharedPreferences(Constants.USER,Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()){
                        putString("id",data.getString("id"))
                        putString("name",data.getString("name"))
                        putString("token",data.getString("token"))
                        apply()
                    }
                    runOnUiThread {
                        setVisibleProgress(false)
                    }
                    val i = Intent(this@LoginActivity,HomeActivity::class.java)
                    startActivity(i)
                }else{
                    runOnUiThread {
                        setVisibleProgress(false)
                        alertMessage("We can’t find account with this credentials")
                    }
                }
            }
        })
    }

    private fun setVisibleProgress(b: Boolean) {
        if (b){
            binding.progress.visibility = View.VISIBLE
            binding.btnSignIn.visibility = View.GONE
        }else{
            binding.progress.visibility = View.GONE
            binding.btnSignIn.visibility = View.VISIBLE
        }
    }

    private fun validatePassword(): Boolean {
        val regex = Pattern.compile("^([0-9]{1,5})")
        return if (binding.edtPassword.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else if (!regex.matcher(binding.edtPassword.text.toString()).matches()) {
            alertMessage("The Password field have a wrong format")
            false
        } else {
            true
        }
    }

    private fun validateEmail(): Boolean {
        val regex = Pattern.compile("^([a-zA-Z]{1,}@wsa[.]com)")
        return if (binding.edtEmail.text.toString().isNullOrEmpty()) {
            alertMessage("Any field can't be empty")
            false
        } else if (!regex.matcher(binding.edtEmail.text.toString()).matches()) {
            alertMessage("The email field have a wrong format")
            false
        } else {
            true
        }
    }

    private fun alertMessage(s: String) {
        binding.txtAlert.text = s
        binding.btnSignIn.animate().translationY(300f).setDuration(300).withEndAction {
            binding.llAlert.visibility = View.VISIBLE
            binding.llAlert.animate().alpha(1f).setDuration(200).withEndAction {
                binding.llAlert.animate().alpha(1f).setDuration(800).withEndAction {
                    binding.btnSignIn.animate().translationY(0f).setDuration(200)
                    binding.llAlert.animate().alpha(0f).setDuration(200)
                    binding.llAlert.visibility = View.GONE
                }
            }
        }
    }
}