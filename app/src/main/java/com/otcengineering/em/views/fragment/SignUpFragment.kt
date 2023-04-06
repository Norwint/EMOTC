package com.otcengineering.em.views.fragment

import android.app.Activity
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.databinding.FragmentSignupBinding
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.em.utils.showAlertDialog
import com.otcengineering.em.views.activity.LegalActivity
import com.otcengineering.em.views.activity.OTPActivity
import com.otcengineering.white_app.utils.MyProgressDialog

class SignUpFragment: Fragment() {

    private val binding: FragmentSignupBinding by lazy {
        FragmentSignupBinding.inflate(
            layoutInflater
        )
    }

    private val viewModel: WelcomeViewModel by lazy { WelcomeViewModel() }

    private var privacy = false
    private var services = false
    private var disclaimer = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding.fragment = this

        binding.privacy.setOnClickListener(View.OnClickListener { v: View? ->
            binding.privacy.requestFocus()
            val intent = Intent(requireContext(), LegalActivity::class.java)
            intent.putExtra("num", 5)
            openSomeActivityForResult(intent)
        })

        binding.service.setOnClickListener(View.OnClickListener { v: View? ->
            binding.service.requestFocus()
            val intent = Intent(requireContext(), LegalActivity::class.java)
            intent.putExtra("num", 6)
            openSomeActivityForResult(intent)
        })

        binding.disclaimer.setOnClickListener(View.OnClickListener { v: View? ->
            binding.disclaimer.requestFocus()
            val intent = Intent(requireContext(), LegalActivity::class.java)
            intent.putExtra("num", 7)
            openSomeActivityForResult(intent)
        })

        return binding.root
    }

    fun openSomeActivityForResult(intent: Intent) {
        resultLauncher.launch(intent)
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val num = data?.getIntExtra("num", 0)
            if (num == 5) {
                privacy = true
                binding.privacyTick.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    ), PorterDuff.Mode.MULTIPLY
                )
            } else if (num == 6) {
                services = true
                binding.serviceTick.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    ), PorterDuff.Mode.MULTIPLY
                )
            } else if (num == 7) {
                disclaimer = true
                binding.discalimerTick.setColorFilter(
                    ContextCompat.getColor(
                        requireContext(),
                        R.color.black
                    ), PorterDuff.Mode.MULTIPLY
                )
            }

            if (privacy && services && disclaimer) {
                binding.continuTxt.setTextColor(requireContext().getColor(R.color.colorPrimary))
                binding.registerButton.setOnClickListener {

                    if (binding.mobile.text.length > 9 || binding.mobile.text.length < 9) {
                        requireContext().showAlertDialog(layoutInflater, "Phone length error", "Phone must have 9 digits")
                    } else {
                        runOnMainThread {
                            binding.loading.visibility = View.VISIBLE
                        }

                        viewModel.getCountry(binding.ccp.selectedCountryName).subscribe({

                        }, {

                        })

                        val phoneNumber = "+" + binding.ccp.selectedCountryCode + binding.mobile.text.toString()
                        Common.sharedPreferences.putString(Constants.Legal.NUMTLF, phoneNumber)
                        Common.sharedPreferences.putString(Constants.Preferences.OTPCHECK, "REGISTER")

                        viewModel.registerAsk(phoneNumber).subscribe({
                            runOnMainThread {
                                binding.loading.visibility = View.GONE
                            }
                            OTPActivity.newInstance(requireContext())
                        }, {
                            runOnMainThread {
                                binding.loading.visibility = View.GONE
                                if (viewModel.error == "PHONE_ALREADY_EXISTS") {
                                    requireContext().showAlertDialog(layoutInflater, "Phone already exist", "Phone already exist")
                                    binding.invalidateAll()
                                } else {
                                    requireContext().showAlertDialog(layoutInflater, "Unexpected error", "Please try again or contact technical support")
                                    binding.invalidateAll()
                                }
                            }
                        })
                    }
                }
            }
        }
    }
}
fun changeColor(editText: EditText, color: Int) {
    editText.backgroundTintList = ColorStateList.valueOf(color)
}