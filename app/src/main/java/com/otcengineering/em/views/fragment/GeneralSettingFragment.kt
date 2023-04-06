package com.otcengineering.em.views.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otc.alice.api.model.VehicleSettings
import com.otcengineering.em.databinding.FragmentGeneralSettingsBinding
import com.otcengineering.em.model.SettingsViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.em.views.activity.HomeActivity

class GeneralSettingFragment : Fragment() {

    private val binding: FragmentGeneralSettingsBinding by lazy {
        FragmentGeneralSettingsBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        binding.save.setOnClickListener {
            binding.bkgSave.visibility = View.VISIBLE
            binding.saveAlert.visibility = View.VISIBLE
        }

        binding.yes.setOnClickListener {
            binding.bkgSave.visibility = View.GONE
            binding.saveAlert.visibility = View.GONE
        }

        binding.no.setOnClickListener {
            binding.bkgSave.visibility = View.GONE
            binding.saveAlert.visibility = View.GONE
        }

        val serialized = Common.sharedPreferences.getRaw(Constants.Preferences.GENERAL_SETTINGS)
        val settings = VehicleSettings.VehicleSettingsResponse.parseFrom(serialized)

        val throttle = (settings.vehicleSettings.general.settingsList[0].value - settings.settingsRangeList[0].minValue) / (settings.settingsRangeList[0].maxValue - settings.settingsRangeList[0].minValue) * 100
        val dead = (settings.vehicleSettings.general.settingsList[1].value - settings.settingsRangeList[1].minValue) / (settings.settingsRangeList[1].maxValue - settings.settingsRangeList[1].minValue) * 100
        val force = 100 - (settings.vehicleSettings.general.settingsList[2].value - settings.settingsRangeList[2].minValue) / (settings.settingsRangeList[2].maxValue - settings.settingsRangeList[2].minValue) * 100

        runOnMainThread {

            binding.throttleRange.currentValue =  throttle.toInt()
            binding.throttleDeadZone.currentValue = dead.toInt()
            binding.brakeForce.currentValue = force.toInt()

            binding.resetThrottleRange.setOnClickListener {
                binding.throttleRange.currentValue =  throttle.toInt()
            }

            binding.resetThrottleDeadZone.setOnClickListener {
                binding.throttleDeadZone.currentValue = dead.toInt()
            }

            binding.resetBrakeForce.setOnClickListener {
                binding.brakeForce.currentValue = force.toInt()
            }

        }

        return binding.root
    }

}