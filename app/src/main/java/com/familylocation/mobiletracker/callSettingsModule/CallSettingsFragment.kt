package com.familylocation.mobiletracker.callSettingsModule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.familylocation.mobiletracker.MyApplication
import com.familylocation.mobiletracker.R
import com.familylocation.mobiletracker.databinding.FragmentCallSettingsBinding
import com.familylocation.mobiletracker.zCommonFuntions.CallIntent


class CallSettingsFragment : Fragment() {

    private lateinit var binding: FragmentCallSettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_call_settings, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        MyApplication.getInstance().loadNativeAd(binding.rlAdplaceholder, requireActivity())


        onClickListeners()
    }

    private fun onClickListeners() {
        binding.btCallFlashLight.setOnClickListener {
            CallIntent.goToCallFlashLightActivity(requireContext(), false, requireActivity())
        }

        binding.btCallAnnouncment.setOnClickListener {
            CallIntent.goToCallAnnouncmentActivity(requireContext(),false,requireActivity())
        }

        binding.btProfile.setOnClickListener {
            CallIntent.goToProfileActivity(requireContext(),false,requireActivity())
        }

        binding.btCallBlocking.setOnClickListener {
            CallIntent.goToCallBlockingActivity(requireContext(),false,requireActivity())
        }

        binding.btHome.setOnClickListener {
            CallIntent.goToControllingActivity(requireContext(),true,requireActivity())
        }
    }



}