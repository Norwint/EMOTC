package com.otcengineering.em.views.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentGeneralDashboardBinding

class GeneralDashboardFragment : Fragment() {

    private val binding: FragmentGeneralDashboardBinding by lazy {
        FragmentGeneralDashboardBinding.inflate(
            layoutInflater
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        setFragment(GeneralFragment())

        binding.general.setOnClickListener {
            binding.general.background = requireContext().getDrawable(R.drawable.button_invite_background)
            binding.generalText.setTextColor(requireContext().getColor(R.color.colorPrimary))
            binding.dashboard.background = requireContext().getDrawable(R.drawable.no_background)
            binding.dashboardText.setTextColor(requireContext().getColor(R.color.white))
            setFragment(GeneralFragment())
        }

        binding.dashboard.setOnClickListener {
            binding.general.background = requireContext().getDrawable(R.drawable.no_background)
            binding.generalText.setTextColor(requireContext().getColor(R.color.white))
            binding.dashboard.background = requireContext().getDrawable(R.drawable.button_invite_background)
            binding.dashboardText.setTextColor(requireContext().getColor(R.color.colorPrimary))
            setFragment(DashboardFragment())
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