package com.example.wsamad6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.wsamad6.databinding.ActivityHomeBinding
import com.example.wsamad6.databinding.ActivityLoginBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()
    }
}