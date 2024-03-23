package realapps.live.callerlocator

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

import dagger.hilt.android.AndroidEntryPoint
import realapps.live.callerlocator.zCommonFuntions.CallIntent
import realapps.live.callerlocator.zSharedPreference.LoginData

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

//    private lateinit var homeViewModel: DummyViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        homeViewModel = ViewModelProvider(this)[DummyViewModel::class.java]

        checkLoginStatusandNavigate()



    }

    private fun checkLoginStatusandNavigate() {
        Handler(Looper.getMainLooper()).postDelayed({

            if (LoginData.getUserLoginStatus(this)) {

//                val userType = LoginData.getUserType(this)

//                if (userType == 0)
                    CallIntent.goToHomeActivity(this, true, this)
//                else
//                    CallIntent.goToWareHouseHomeActivity(this, true, this)

            } else {
                CallIntent.goToLoginActivity(this, true, this)
            }

        }, 2000)
    }

//    fun observer()
//    {
//        homeViewModel.getDoctorsResponse().observe(this){
//            Log.e("Test",it.productsList.toString())
//        }
//    }






}