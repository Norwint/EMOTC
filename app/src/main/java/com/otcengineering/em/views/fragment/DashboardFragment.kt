package com.otcengineering.em.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.data.DashboardData
import com.otcengineering.em.databinding.FragmentDashboardBinding
import com.otcengineering.em.model.DashboardViewModel
import com.otcengineering.em.service.BluetoothService
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.otcble.ble.BleSDK
import java.lang.Math.round
import java.util.*
import kotlin.math.roundToInt


class DashboardFragment : Fragment() {

    private val viewModel: DashboardViewModel by lazy { DashboardViewModel() }

    private val binding: FragmentDashboardBinding by lazy {
        FragmentDashboardBinding.inflate(
            layoutInflater
        )
    }

    private var timer = Timer()

    private fun updateData() {
        viewModel.getDashboardData().subscribe({ dd ->
            if (!BleSDK.isConnected()) {
                runOnMainThread {
                    binding.constraintLayout.visibility = View.VISIBLE
                    dataInitialize(dd.dashboardData)
                }
            } else {
                binding.constraintLayout.visibility = View.GONE
                val status = BluetoothService.getService(requireContext()).getData()
                runOnMainThread {
                    dataInitialize(status)
                }
            }
            binding.invalidateAll()
        }, {
            binding.invalidateAll()
        })
    }

    override fun onResume() {
        super.onResume()
        timer = Timer()
        timer.scheduleAtFixedRate(object: TimerTask() {
            var times = 0
            override fun run() {
                if (BleSDK.isConnected() || times % 5 == 0) {
                    updateData()
                }
                times += 1
            }
        }, 5000, 5000)
    }

    override fun onPause() {
        super.onPause()
        timer.cancel()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        binding.bkgLeft.setImageDrawable(requireContext().getDrawable(R.drawable.background_left))
        binding.lockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.lock_black))
        binding.lockedUnlocked.setImageDrawable(requireContext().getDrawable(R.drawable.lock))
        binding.bkgRight.setImageDrawable(requireContext().getDrawable(R.drawable.background_right_black))
        binding.unlockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.unlock))

        updateData()

        binding.units.text = Common.sharedPreferences.getString(Constants.Preferences.UNIT)

        return binding.root
    }

    private fun dataInitialize(data: DashboardData) {
        runOnMainThread {

            if(data.map == 0) {
                binding.infoThrottleReactivity.setImageDrawable(requireContext().getDrawable(R.drawable.forme_neutral))
                binding.map.visibility = View.INVISIBLE
                binding.textView9.visibility = View.INVISIBLE
                binding.map0.visibility = View.VISIBLE
            } else if (data.map == 1) {
                binding.infoThrottleReactivity.setImageDrawable(requireContext().getDrawable(R.drawable.forme))
                binding.map.text = data.map.toString()
                binding.map0.visibility = View.INVISIBLE
            } else if (data.map == 2) {
                binding.infoThrottleReactivity.setImageDrawable(requireContext().getDrawable(R.drawable.forme_blue))
                binding.map.text = data.map.toString()
                binding.map0.visibility = View.INVISIBLE
            } else {
                binding.infoThrottleReactivity.setImageDrawable(requireContext().getDrawable(R.drawable.forme_red))
                binding.map.text = data.map.toString()
                binding.map0.visibility = View.INVISIBLE
            }

            if(data.speed.toInt() < 10) {
                binding.speed.text = "0"
            } else {
                if(Common.sharedPreferences.getString(Constants.Preferences.UNIT) == "km/h"){
                    binding.speed.text = (data.speed.toInt()).toString()
                } else {
                    binding.speed.text = (data.speed * 0.621371).roundToInt().toString()
                }
            }

            if(data.map == 0) {
                runOnMainThread {
                    binding.bkgLeft.setOnClickListener {
                        binding.bkgLeft.setImageDrawable(requireContext().getDrawable(R.drawable.background_left))
                        binding.lockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.lock_black))
                        binding.lockedUnlocked.setImageDrawable(requireContext().getDrawable(R.drawable.lock))
                        binding.bkgRight.setImageDrawable(requireContext().getDrawable(R.drawable.background_right_black))
                        binding.unlockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.unlock))
                    }

                    binding.bkgRight.setOnClickListener {
                        binding.bkgLeft.setImageDrawable(requireContext().getDrawable(R.drawable.background_left_black))
                        binding.lockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.lock))
                        binding.lockedUnlocked.setImageDrawable(requireContext().getDrawable(R.drawable.unlock))
                        binding.bkgRight.setImageDrawable(requireContext().getDrawable(R.drawable.background_right))
                        binding.unlockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.unlock_black))
                    }
                }
            }

            binding.batteryNumber.text = (data.soc.toInt()).toString() + " %"
            binding.lastConnection.text = data.date

            val battery = 900 - ((data.soc.toInt()*1000)/100)
            binding.batteryPercent.layoutParams.height = battery
            val rpm = 900 - ((data.rpm.toInt()*1000)/8000)
            binding.rpmPercent.layoutParams.height = rpm

            if(data.limitationActive <= 150) {
                binding.limitationActive.visibility = View.VISIBLE
            } else {
                binding.limitationActive.visibility = View.INVISIBLE
            }

            if(data.tko){
                binding.tko.visibility = View.VISIBLE
            } else {
                binding.tko.visibility = View.INVISIBLE
            }

            if(data.error > 0){
                binding.error.visibility = View.VISIBLE
                if(data.limitationActive <= 150){
                    binding.error.setImageDrawable(requireContext().getDrawable(R.drawable.triangle))
                } else {
                    binding.error.setImageDrawable(requireContext().getDrawable(R.drawable.triangle_red))
                    binding.limitationActive.text = "ERROR 000"
                }

            } else {
                binding.error.visibility = View.INVISIBLE
            }

            if(data.hotBattery > 50) {
                binding.temperature.visibility = View.VISIBLE
            } else {
                binding.temperature.visibility = View.INVISIBLE
            }

            if(data.coldBattery < 10) {
                binding.flocon.visibility = View.VISIBLE
            } else {
                binding.flocon.visibility = View.INVISIBLE
            }


            if(!data.lockStatus) {
                binding.bkgLeft.setImageDrawable(requireContext().getDrawable(R.drawable.background_left))
                binding.lockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.lock_black))
                binding.lockedUnlocked.setImageDrawable(requireContext().getDrawable(R.drawable.lock))
                binding.bkgRight.setImageDrawable(requireContext().getDrawable(R.drawable.background_right_black))
                binding.unlockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.unlock))
            } else {
                binding.bkgLeft.setImageDrawable(requireContext().getDrawable(R.drawable.background_left_black))
                binding.lockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.lock))
                binding.lockedUnlocked.setImageDrawable(requireContext().getDrawable(R.drawable.unlock))
                binding.bkgRight.setImageDrawable(requireContext().getDrawable(R.drawable.background_right))
                binding.unlockedButton.setImageDrawable(requireContext().getDrawable(R.drawable.unlock_black))
            }

            if(data.optionIndicator == 0){
                binding.tko.visibility = View.INVISIBLE
                binding.tc.visibility = View.INVISIBLE
            } else if(data.optionIndicator == 1) {
                binding.tko.visibility = View.VISIBLE
                binding.tc.visibility = View.INVISIBLE
            } else if(data.optionIndicator == 2) {
                binding.tko.visibility = View.INVISIBLE
                binding.tc.visibility = View.VISIBLE
            } else {
                binding.tko.visibility = View.VISIBLE
                binding.tc.visibility = View.VISIBLE
            }

        }

    }

}