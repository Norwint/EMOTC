package com.otcengineering.em.views.fragment

import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.otc.alice.api.model.General
import com.otc.alice.api.model.VehicleSettings
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentSettingsBinding
import com.otcengineering.em.model.SettingsViewModel

class SettingsFragment : Fragment() {

    private val binding: FragmentSettingsBinding by lazy {
        FragmentSettingsBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        setFragment(GeneralSettingFragment())

        binding.general.setOnClickListener {
            binding.general.background = requireContext().getDrawable(R.drawable.button_invite_background)
            binding.generalText.setTextColor(requireContext().getColor(R.color.colorPrimary))
            binding.map1.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map1Text.setTextColor(requireContext().getColor(R.color.white))
            binding.map2.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map2Text.setTextColor(requireContext().getColor(R.color.white))
            binding.map3.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map3Text.setTextColor(requireContext().getColor(R.color.white))
            setFragment(GeneralSettingFragment())
        }

        binding.map1.setOnClickListener {
            binding.general.background = requireContext().getDrawable(R.drawable.no_background)
            binding.generalText.setTextColor(requireContext().getColor(R.color.white))
            binding.map1.background = requireContext().getDrawable(R.drawable.button_invite_background)
            binding.map1Text.setTextColor(requireContext().getColor(R.color.colorPrimary))
            binding.map2.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map2Text.setTextColor(requireContext().getColor(R.color.white))
            binding.map3.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map3Text.setTextColor(requireContext().getColor(R.color.white))
            setFragment(Map1Fragment())
        }

        binding.map2.setOnClickListener {
            binding.general.background = requireContext().getDrawable(R.drawable.no_background)
            binding.generalText.setTextColor(requireContext().getColor(R.color.white))
            binding.map1.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map1Text.setTextColor(requireContext().getColor(R.color.white))
            binding.map2.background = requireContext().getDrawable(R.drawable.button_invite_background)
            binding.map2Text.setTextColor(requireContext().getColor(R.color.colorPrimary))
            binding.map3.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map3Text.setTextColor(requireContext().getColor(R.color.white))
            setFragment(Map2Fragment())
        }

        binding.map3.setOnClickListener {
            binding.general.background = requireContext().getDrawable(R.drawable.no_background)
            binding.generalText.setTextColor(requireContext().getColor(R.color.white))
            binding.map1.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map1Text.setTextColor(requireContext().getColor(R.color.white))
            binding.map2.background = requireContext().getDrawable(R.drawable.no_background)
            binding.map2Text.setTextColor(requireContext().getColor(R.color.white))
            binding.map3.background = requireContext().getDrawable(R.drawable.button_invite_background)
            binding.map3Text.setTextColor(requireContext().getColor(R.color.colorPrimary))
            setFragment(Map3Fragment())
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