package com.otcengineering.em.views.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import androidx.fragment.app.Fragment
import com.otc.alice.api.model.VehicleSettings
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentGeneralSettingsBinding
import com.otcengineering.em.databinding.FragmentMap1Binding
import com.otcengineering.em.databinding.FragmentMap2Binding
import com.otcengineering.em.databinding.FragmentMap3Binding
import com.otcengineering.em.model.SettingsViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread

class Map3Fragment : Fragment() {

    private val viewModel: SettingsViewModel by lazy { SettingsViewModel() }

    private val binding: FragmentMap3Binding by lazy {
        FragmentMap3Binding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        if(Common.sharedPreferences.getString(Constants.Preferences.MODEL) == "Escape S") {
            binding.switchTko.visibility = View.GONE
            binding.idleSpeed.visibility = View.GONE
            binding.idleTorque.visibility = View.GONE
            binding.textView3.visibility = View.GONE
            binding.textIdleSpeed.visibility = View.GONE
            binding.textIdleTorque.visibility = View.GONE
            binding.percent1.visibility = View.GONE
            binding.percent2.visibility = View.GONE
            binding.resetIdleSpeed.visibility = View.GONE
            binding.resetIdleTorque.visibility = View.GONE
            binding.infoIdleSpeed.visibility = View.GONE
            binding.infoIdleTorque.visibility = View.GONE
        }

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

        val throttle = (settings.vehicleSettings.mapsList[2].settingsList[0].value - settings.settingsRangeList[3].minValue) / (settings.settingsRangeList[3].maxValue - settings.settingsRangeList[3].minValue) * 100
        val power = (settings.vehicleSettings.mapsList[2].settingsList[4].value - settings.settingsRangeList[7].minValue) / (settings.settingsRangeList[7].maxValue - settings.settingsRangeList[7].minValue) * 100
        val rpm = (settings.vehicleSettings.mapsList[2].settingsList[3].value - settings.settingsRangeList[6].minValue) / (settings.settingsRangeList[6].maxValue - settings.settingsRangeList[6].minValue) * 100

        val throttleSpeed = (settings.vehicleSettings.mapsList[2].settingsList[1].value - settings.settingsRangeList[4].minValue) / (settings.settingsRangeList[4].maxValue - settings.settingsRangeList[4].minValue) * 100
        val throttleTorque = (settings.vehicleSettings.mapsList[2].settingsList[2].value - settings.settingsRangeList[5].minValue) / (settings.settingsRangeList[5].maxValue - settings.settingsRangeList[5].minValue) * 100

        runOnMainThread {

            binding.throttleReactivity.currentValue =  throttle.toInt()
            binding.power.currentValue = power.toInt()
            binding.rpm.currentValue = rpm.toInt()

            if(throttleSpeed > 0 || throttleTorque > 0) {
                binding.switchTko.isChecked = true
                binding.idleSpeed.currentValue = throttleSpeed.toInt()
                binding.idleTorque.currentValue = throttleTorque.toInt()

                binding.textView3.setTextColor(Color.WHITE)
                binding.textIdleSpeed.setTextColor(Color.WHITE)
                binding.textIdleTorque.setTextColor(Color.WHITE)
                binding.percent1.setTextColor(Color.WHITE)
                binding.percent2.setTextColor(Color.WHITE)
                binding.idleSpeed.baseColor = Color.WHITE
                binding.idleSpeed.circleFillColor = requireContext().getColor(R.color.colorRed)
                binding.idleSpeed.circleTextColor = Color.WHITE
                binding.idleSpeed.fillColor = requireContext().getColor(R.color.colorRed)
                binding.idleTorque.baseColor = Color.WHITE
                binding.idleTorque.circleFillColor = requireContext().getColor(R.color.colorRed)
                binding.idleTorque.circleTextColor = Color.WHITE
                binding.idleTorque.fillColor = requireContext().getColor(R.color.colorRed)
                binding.resetIdleSpeed.setColorFilter(Color.WHITE)
                binding.resetIdleTorque.setColorFilter(Color.WHITE)

            } else {
                binding.switchTko.isChecked = false
            }

            binding.resetThrottleReactivity.setOnClickListener {
                binding.throttleReactivity.currentValue =  throttle.toInt()
            }

            binding.resetPower.setOnClickListener {
                binding.power.currentValue = power.toInt()
            }

            binding.resetRpm.setOnClickListener {
                binding.rpm.currentValue = rpm.toInt()
            }

            binding.resetIdleSpeed.setOnClickListener {
                binding.idleSpeed.currentValue = throttleSpeed.toInt()
            }

            binding.resetIdleTorque.setOnClickListener {
                binding.idleTorque.currentValue = throttleTorque.toInt()
            }

        }

        val switchListener = object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                if(isChecked) {
                    binding.idleSpeed.currentValue = throttleSpeed.toInt()
                    binding.idleTorque.currentValue = throttleTorque.toInt()
                    binding.textView3.setTextColor(Color.WHITE)
                    binding.textIdleSpeed.setTextColor(Color.WHITE)
                    binding.textIdleTorque.setTextColor(Color.WHITE)
                    binding.percent1.setTextColor(Color.WHITE)
                    binding.percent2.setTextColor(Color.WHITE)
                    binding.idleSpeed.baseColor = Color.WHITE
                    binding.idleSpeed.circleFillColor = requireContext().getColor(R.color.colorRed)
                    binding.idleSpeed.circleTextColor = Color.WHITE
                    binding.idleSpeed.fillColor = requireContext().getColor(R.color.colorRed)
                    binding.idleTorque.baseColor = Color.WHITE
                    binding.idleTorque.circleFillColor = requireContext().getColor(R.color.colorRed)
                    binding.idleTorque.circleTextColor = Color.WHITE
                    binding.idleTorque.fillColor = requireContext().getColor(R.color.colorRed)
                    binding.resetIdleSpeed.setColorFilter(Color.WHITE)
                    binding.resetIdleTorque.setColorFilter(Color.WHITE)
                } else {
                    binding.textView3.setTextColor(requireContext().getColor(R.color.disabled))
                    binding.textIdleSpeed.setTextColor(requireContext().getColor(R.color.disabled))
                    binding.textIdleTorque.setTextColor(requireContext().getColor(R.color.disabled))
                    binding.percent1.setTextColor(requireContext().getColor(R.color.disabled))
                    binding.percent2.setTextColor(requireContext().getColor(R.color.disabled))
                    binding.idleSpeed.baseColor = requireContext().getColor(R.color.disabled)
                    binding.idleSpeed.circleFillColor = requireContext().getColor(R.color.disabled)
                    binding.idleSpeed.circleTextColor = requireContext().getColor(R.color.disabled)
                    binding.idleSpeed.fillColor = requireContext().getColor(R.color.disabled)
                    binding.idleSpeed.currentValue = 0
                    binding.idleTorque.baseColor = requireContext().getColor(R.color.disabled)
                    binding.idleTorque.circleFillColor = requireContext().getColor(R.color.disabled)
                    binding.idleTorque.circleTextColor = requireContext().getColor(R.color.disabled)
                    binding.idleTorque.fillColor = requireContext().getColor(R.color.disabled)
                    binding.idleTorque.currentValue = 0
                    binding.resetIdleSpeed.setColorFilter(requireContext().getColor(R.color.disabled))
                    binding.resetIdleTorque.setColorFilter(requireContext().getColor(R.color.disabled))
                }
            }
        }

        binding.switchTko.setOnCheckedChangeListener(switchListener)

        return binding.root
    }

}