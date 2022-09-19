package com.example.wsamad6

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.wsamad6.databinding.ActivityHomeBinding
import com.example.wsamad6.databinding.ActivityLoginBinding

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var navController : NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.supportActionBar?.hide()

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        binding.bottomNavigation.setupWithNavController(navController)

        observeDestination()
    }

    private fun observeDestination() {
        navController.addOnDestinationChangedListener{n,d,a ->
            when(d.id){
                R.id.checkListFragment ->{binding.bottomNavigation.visibility = View.GONE}
                R.id.qrFragment ->{binding.bottomNavigation.visibility = View.VISIBLE}
                R.id.mapFragment ->{binding.bottomNavigation.visibility = View.VISIBLE}
                R.id.homeFragment ->{binding.bottomNavigation.visibility = View.VISIBLE}
            }
        }
    }
}