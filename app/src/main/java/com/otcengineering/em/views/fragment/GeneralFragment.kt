package com.otcengineering.em.views.fragment
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.data.DashboardData
import com.otcengineering.em.databinding.FragmentDashboardBinding
import com.otcengineering.em.databinding.FragmentGeneralBinding
import com.otcengineering.em.databinding.FragmentGeneralDashboardBinding
import com.otcengineering.em.model.DashboardViewModel
import com.otcengineering.em.model.SettingsViewModel
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.service.BluetoothService
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.otcble.ble.BleSDK
import java.util.*

class GeneralFragment : Fragment() {

    private val binding: FragmentGeneralBinding by lazy {
        FragmentGeneralBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel: DashboardViewModel by lazy { DashboardViewModel() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        viewModel.getGeneralDashboard().subscribe {

        }

        viewModel.getDashboardData().subscribe({ dd ->
            if (!BleSDK.isConnected()) {
                runOnMainThread {
                    dataInitialize(dd.dashboardData)
                }
            } else {
                val status = BluetoothService.getService(requireContext()).getData()
                runOnMainThread {
                    dataInitialize(status)
                }
            }
            binding.invalidateAll()
        }, {
            binding.invalidateAll()
        })

        WelcomeViewModel().getVehicleList().subscribe({ list ->

            runOnMainThread {
                val first = list.first()
                binding.vin.text = first.vin
                binding.year.text = first.year.toString()

                val pattern = "Epure".toRegex()
                if(pattern.containsMatchIn(first.model)){
                    binding.infoThrottleReactivity.setImageDrawable(requireContext().getDrawable(R.drawable.epure))
                    binding.textView3.text = first.model
                    Common.sharedPreferences.putString(Constants.Preferences.MODEL, first.model)
                } else {
                    binding.infoThrottleReactivity.setImageDrawable(requireContext().getDrawable(R.drawable.escape))
                    binding.textView3.text = first.model
                    Common.sharedPreferences.putString(Constants.Preferences.MODEL, first.model)
                }

            }

        }, {})

        return binding.root
    }

    private fun dataInitialize(data: DashboardData) {
        runOnMainThread {
            binding.hours.text = durationTime(data.sessionTime.toInt())
            binding.battery.text = (data.batteryCycle.toInt()).toString()
        }
    }

    fun durationTime(sec: Int) : String {

        val timeSec: Int = sec

        val hours = timeSec / 3600
        var temp = timeSec - hours * 3600
        val mins = temp / 60
        temp -= mins * 60
        val secs = temp

        var time = ""

        if (hours > 0) {
            time += "${hours}h "
        }

        time += "${mins}min "

        return time
    }

}