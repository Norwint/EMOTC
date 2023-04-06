package com.otcengineering.em.views.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentCommunityBinding
import com.otcengineering.em.databinding.FragmentServicesBinding
import com.otcengineering.em.model.DiagnosticUpdateViewModel
import com.otcengineering.em.model.ServicesViewModel
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants

class CommunityFragment : Fragment() {

    private val binding: FragmentCommunityBinding by lazy {
        FragmentCommunityBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        Handler(Looper.getMainLooper()).postDelayed({
            binding.loading.visibility = View.GONE
        }, 1500)

        binding.web.loadUrl(Common.sharedPreferences.getString(Constants.Preferences.URL))

        return binding.root
    }

}