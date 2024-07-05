package com.familylocation.mobiletracker.nearByPlacesModule

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.databinding.ActivityTrafficBinding
import com.familylocation.mobiletracker.zCommonFuntions.StatusBarUtils

class TrafficActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTrafficBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityTrafficBinding.inflate(layoutInflater)
        setContentView(binding.root)

        StatusBarUtils.transparentStatusBar(this)
        StatusBarUtils.setTopPadding(resources,binding.titleBarLayout)

        supportFragmentManager.beginTransaction()
            .replace(R.id.containerFragment, TrafficFragment())
            .commit()


        binding.btBack.setOnClickListener {
            finish()
        }

    }



}