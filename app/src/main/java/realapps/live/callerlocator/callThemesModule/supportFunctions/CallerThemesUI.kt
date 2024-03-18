package realapps.live.callerlocator.callThemesModule.supportFunctions

import android.content.Context
import android.content.res.Resources
import realapps.live.callerlocator.databinding.FragmentCallThemesBinding
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils

class CallerThemesUI(
    val context: Context,
    private val binding: FragmentCallThemesBinding
) {

    fun setTopPadding(resources: Resources) {
        val topPadding = StatusBarUtils.getStatusBarHeight(resources) + 12

        binding.mainLayout.setPadding(
            binding.mainLayout.paddingLeft,
            topPadding,
            binding.mainLayout.paddingRight,
            binding.mainLayout.paddingBottom
        )
    }
}