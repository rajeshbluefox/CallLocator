package realapps.live.callerlocator.callLocatorModule.supportFunctions

import android.content.Context
import android.content.res.Resources
import android.view.View
import realapps.live.callerlocator.databinding.FragmentCallLocatorBinding
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils


class CallLocatorFragmentUI(
    val context: Context,
    private val binding: FragmentCallLocatorBinding
) {

     fun setTopPadding(resources: Resources) {
        val topPadding = StatusBarUtils.getStatusBarHeight(resources) + 12

//         val layoutParams = binding.searchBarMainLt.layoutParams as ConstraintLayout.LayoutParams
//         layoutParams.setMargins(0, topPadding, 0, 0) // Left, Top, Right, Bottom
//         binding.searchBarMainLt.layoutParams = layoutParams

         binding.searchBarMainLt.setPadding(
             binding.searchBarMainLt.paddingLeft,
             topPadding,
             binding.searchBarMainLt.paddingRight,
             binding.searchBarMainLt.paddingBottom
         )
    }

    fun clearSearch()
    {
        binding.etSearch.text=null
    }

    fun setContactNumber(phoneNumber: String)
    {
        EnteredNumber.phoneNumber =phoneNumber

        toggleContactLayout(true)

        binding.tvPhoneNumber.text=phoneNumber
    }

    fun toggleContactLayout(show: Boolean)
    {
        binding.contactLayout.visibility= if(show) View.VISIBLE else View.GONE
    }
}

object EnteredNumber{
    var phoneNumber = ""
}