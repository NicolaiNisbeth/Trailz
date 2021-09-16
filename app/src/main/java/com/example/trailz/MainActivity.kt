package com.example.trailz

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.trailz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bottomNavigationView = DataBindingUtil
            .setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .navView

        bottomNavigationView.setupWithNavController(
            findNavController(R.id.nav_host_fragment_activity_main)
        )
    }
}
