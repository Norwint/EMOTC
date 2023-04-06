package com.otcengineering.em.views.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otc.alice.api.model.Service
import com.otcengineering.em.databinding.FragmentAfterSalesBinding
import com.otcengineering.em.model.DashboardViewModel
import com.otcengineering.em.model.ServicesViewModel

class AfterSalesFragment  : Fragment() {

    private val viewModel: ServicesViewModel by lazy { ServicesViewModel() }

    private val binding: FragmentAfterSalesBinding by lazy {
        FragmentAfterSalesBinding.inflate(
            layoutInflater
        )
    }

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
//            viewModel.postTicket(binding.text.text.toString(), Service.TicketType.AFTER_SALES).subscribe({
//
//            }, {
//
//            })
            val fragmentManager = parentFragmentManager
            fragmentManager.beginTransaction().remove(this).commit()
        }

        return binding.root
    }

}