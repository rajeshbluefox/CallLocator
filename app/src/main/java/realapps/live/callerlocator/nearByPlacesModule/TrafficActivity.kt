package realapps.live.callerlocator.nearByPlacesModule

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import realapps.live.callerlocator.R
import realapps.live.callerlocator.databinding.ActivityTrafficBinding
import realapps.live.callerlocator.zCommonFuntions.JStatusBarUtils
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils

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