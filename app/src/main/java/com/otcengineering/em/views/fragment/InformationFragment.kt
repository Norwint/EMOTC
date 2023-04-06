package com.otcengineering.em.views.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableArrayList
import androidx.fragment.app.Fragment
import com.otc.alice.api.model.Service
import com.otcengineering.em.R
import com.otcengineering.em.data.DashboardData
import com.otcengineering.em.databinding.FragmentDashboardBinding
import com.otcengineering.em.databinding.FragmentInformationBinding
import com.otcengineering.em.model.DashboardViewModel
import com.otcengineering.em.model.ServicesViewModel
import com.otcengineering.em.service.BluetoothService
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.otcble.ble.BleSDK
import java.io.InputStream
import java.util.*

class InformationFragment  : Fragment() {

    private val viewModel: ServicesViewModel by lazy { ServicesViewModel() }

    private val binding: FragmentInformationBinding by lazy {
        FragmentInformationBinding.inflate(
            layoutInflater
        )
    }

    private val array = ObservableArrayList<Long>()

    @SuppressLint("CheckResult")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        binding.picture.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        binding.video.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "video/*"
            startActivityForResult(intent, 102)
        }

        binding.send.setOnClickListener {
            viewModel.postTicket(binding.text.text.toString(), Service.TicketType.INFORMATION, array).subscribe({

            }, {

            })

            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
        }

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            val selectedImageUri = data?.data
            val inputStream: InputStream? = selectedImageUri?.let {
                this.context?.contentResolver?.openInputStream(
                    it
                )
            }
            val byteArray = inputStream?.readBytes()
            if (byteArray != null) {
                viewModel.uploadBulk(byteArray, "test.png").subscribe({
                    array.add(it.fileId)
                }, {

                })
            }
        }
        if (requestCode == 102 && resultCode == Activity.RESULT_OK) {
            val selectedVideoUri = data?.data
            val inputStream: InputStream? = selectedVideoUri?.let {
                this.context?.contentResolver?.openInputStream(
                    it
                )
            }
            val byteArray = inputStream?.readBytes()
            if (byteArray != null) {
                viewModel.uploadBulk(byteArray, "test.mp4").subscribe({
                    array.add(it.fileId)
                }, {

                })
            }
        }
    }

}