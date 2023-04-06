package com.otcengineering.em.views.fragment

import android.graphics.Outline
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import androidx.core.view.isEmpty
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentCommunityBinding
import com.otcengineering.em.databinding.FragmentContactBinding
import com.otcengineering.otcble.ble.BleSDK
import java.util.*

class ContactFragment : Fragment() {

    private val binding: FragmentContactBinding by lazy {
        FragmentContactBinding.inflate(
            layoutInflater
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        //seleccionar si vols fer el ticket de information o aftersales
        binding.information.setOnClickListener {
            binding.content.visibility = View.VISIBLE
            setFragment(InformationFragment())
        }

        binding.ticket.setOnClickListener {
            binding.content.visibility = View.VISIBLE
            setFragment(AfterSalesFragment())
        }

        return binding.root
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = childFragmentManager.beginTransaction()
        transaction.replace(R.id.content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}