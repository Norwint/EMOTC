package com.otcengineering.em.views.fragment

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentLoginBinding
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.em.utils.showAlertDialog
import com.otcengineering.em.views.activity.OTPActivity
import com.otcengineering.white_app.utils.MyProgressDialog

class LoginFragment : Fragment() {

    private val viewModel: WelcomeViewModel by lazy { WelcomeViewModel() }

    private val binding: FragmentLoginBinding by lazy {
        FragmentLoginBinding.inflate(
            layoutInflater
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        binding.loginButton.setOnClickListener {

            runOnMainThread {
                binding.loading.visibility = View.VISIBLE
            }

            viewModel.getCountry(binding.ccp.selectedCountryName).subscribe({

            }, {

            })

            val phoneNumber = "+" + binding.ccp.selectedCountryCode + binding.mobile.text.toString()
            Common.sharedPreferences.putString(Constants.Legal.NUMTLF, phoneNumber)
            Common.sharedPreferences.putString(Constants.Preferences.OTPCHECK, "LOGIN")

            viewModel.loginAsk(phoneNumber).subscribe({
                runOnMainThread {
                    binding.loading.visibility = View.GONE
                }
                OTPActivity.newInstance(requireContext())
            }, {
                runOnMainThread {
                    binding.loading.visibility = View.GONE
                    if (viewModel.error == "PHONE_NOT_FOUND") {
                        requireContext().showAlertDialog(layoutInflater, "Phone not found", "Phone not found")
                        binding.invalidateAll()
                    } else {
                        requireContext().showAlertDialog(layoutInflater, "Unexpected error", "Please try again or contact technical support")
                        binding.invalidateAll()
                    }
                }
            })

        }
        return binding.root
    }

    fun changeColor(editText: EditText, color: Int) {
        editText.backgroundTintList = ColorStateList.valueOf(color)
    }

}