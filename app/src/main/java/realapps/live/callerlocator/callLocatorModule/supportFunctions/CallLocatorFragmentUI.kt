package realapps.live.callerlocator.callLocatorModule.supportFunctions

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.animation.LinearInterpolator
import realapps.live.callerlocator.callLocatorModule.modalClass.MyFriendsDataItem
import realapps.live.callerlocator.databinding.FragmentCallLocatorBinding
import realapps.live.callerlocator.zCommonFuntions.StatusBarUtils
import realapps.live.callerlocator.zDatabase.BlockedContactsDBHelper


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

        if(findIsNumberBlocked(phoneNumber))
            binding.tvBlock.text="UnBlock"
        else
            binding.tvBlock.text="Block"
    }

    fun toggleContactLayout(show: Boolean)
    {
        binding.contactLayout.visibility= if(show) View.VISIBLE else View.GONE
    }

    // Creating the ValueAnimator for rotation
    val rotationAnimator = ValueAnimator.ofFloat(0f, 360f)

    fun startRotateAnimation()
    {

        // Setting up rotation properties
        rotationAnimator.duration = 1000 // Duration in milliseconds
        rotationAnimator.repeatCount = ValueAnimator.INFINITE // Infinite rotation
        rotationAnimator.interpolator = LinearInterpolator() // Linear interpolator for smooth rotation

        // Update listener for rotating the ImageView
        rotationAnimator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            binding.btReload.rotation = animatedValue // Update the rotation of the ImageView
        }

        // Start the rotation animation
        rotationAnimator.start()
    }

    fun stopRotateAnimation()
    {
        // Start the rotation animation
        rotationAnimator.cancel()
    }

    private var dbHelper = BlockedContactsDBHelper(context)

    private fun findIsNumberBlocked(phoneNumber: String):Boolean
    {
        return dbHelper.findNumber(phoneNumber)
    }

    fun showMyFriendsPB()
    {
        binding.shimmerViewContainer.visibility=View.VISIBLE
        binding.rvFriend.visibility=View.GONE
        binding.noFriendsLt.visibility=View.GONE

        binding.shimmerViewContainer.startShimmerAnimation()
    }

    fun hideMyFriendsPB()
    {
        binding.shimmerViewContainer.stopShimmerAnimation()
        binding.shimmerViewContainer.visibility=View.GONE
        binding.rvFriend.visibility=View.VISIBLE
        binding.noFriendsLt.visibility=View.VISIBLE
    }

    fun showContactPopUp(myFriendsDataItem: MyFriendsDataItem)
    {
        if(myFriendsDataItem.friendName==null)
            binding.tvContactName.text="Unknown"
        else
            binding.tvContactName.text=myFriendsDataItem.friendName

        if(findIsNumberBlocked(myFriendsDataItem.friendNumber!!))
            binding.tvBlockNumberPU.text="UnBlock"
        else
            binding.tvBlockNumberPU.text="Block"

        binding.tvPhoneNumberPU.text=myFriendsDataItem.friendNumber

        binding.contactPu.visibility=View.VISIBLE
        binding.contactLayout.visibility=View.GONE
    }

    fun hideContactPopUp()
    {
        binding.contactPu.visibility=View.GONE

    }
}

object EnteredNumber{
    var phoneNumber = ""
}