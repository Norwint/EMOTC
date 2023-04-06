package com.otcengineering.em.views.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivityOtpBinding
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.utils.*
import com.otcengineering.white_app.utils.MyProgressDialog

class OTPActivity : BaseActivity() {

    private val binding: ActivityOtpBinding by lazy { ActivityOtpBinding.inflate(layoutInflater) }

    var OTP = "0"

    private val viewModel: WelcomeViewModel by lazy { WelcomeViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //detectar si estem en registre otp o login
        if(Common.sharedPreferences.getString(Constants.Preferences.OTPCHECK) == "REGISTER"){
            binding.imgLogin.visibility = View.INVISIBLE
            binding.imgSignup.visibility = View.VISIBLE
            binding.textLogin.setTextColor(Color.WHITE)
            binding.textSignup.setTextColor(Color.rgb(0,1,70))
        } else if (Common.sharedPreferences.getString(Constants.Preferences.OTPCHECK) == "LOGIN"){
            binding.imgLogin.visibility = View.VISIBLE
            binding.imgSignup.visibility = View.INVISIBLE
            binding.textLogin.setTextColor(Color.rgb(0,1,70))
            binding.textSignup.setTextColor(Color.WHITE)
        }

        binding.mobileTxt.text = getString(R.string.mobile_verification, Common.sharedPreferences.getString(
            Constants.Legal.NUMTLF))

        binding.otpOneEdtxt.addTextChangedListener(GenericTextWatcher(binding.otpOneEdtxt, binding.otpTwoEdtxt))
        binding.otpTwoEdtxt.addTextChangedListener(GenericTextWatcher(binding.otpTwoEdtxt, binding.otpThreeEdtxt))
        binding.otpThreeEdtxt.addTextChangedListener(GenericTextWatcher(binding.otpThreeEdtxt, binding.otpFourEdtxt))
        binding.otpFourEdtxt.addTextChangedListener(GenericTextWatcher(binding.otpFourEdtxt, binding.otp5Edtxt))
        binding.otp5Edtxt.addTextChangedListener(GenericTextWatcher(binding.otp5Edtxt, binding.otp6Edtxt))
        binding.otp6Edtxt.addTextChangedListener(GenericTextWatcher(binding.otp6Edtxt, binding.otp6Edtxt))

        binding.otpOneEdtxt.setOnKeyListener(GenericKeyEvent(binding.otpOneEdtxt, null))
        binding.otpTwoEdtxt.setOnKeyListener(GenericKeyEvent(binding.otpTwoEdtxt, binding.otpOneEdtxt))
        binding.otpThreeEdtxt.setOnKeyListener(GenericKeyEvent(binding.otpThreeEdtxt, binding.otpTwoEdtxt))
        binding.otpFourEdtxt.setOnKeyListener(GenericKeyEvent(binding.otpFourEdtxt, binding.otpThreeEdtxt))
        binding.otp5Edtxt.setOnKeyListener(GenericKeyEvent(binding.otp5Edtxt, binding.otpFourEdtxt))
        binding.otp6Edtxt.setOnKeyListener(GenericKeyEvent(binding.otp6Edtxt, binding.otp5Edtxt))

        //torna a enviar el sms amb otp
        binding.resendSms.setOnClickListener {
            if(Common.sharedPreferences.getString(Constants.Preferences.OTPCHECK) == "REGISTER"){
                viewModel.registerAsk(Common.sharedPreferences.getString(Constants.Preferences.PHONE_NUMBER)).subscribe({
                }, {
                })
            } else if (Common.sharedPreferences.getString(Constants.Preferences.OTPCHECK) == "LOGIN"){
                viewModel.loginAsk(Common.sharedPreferences.getString(Constants.Preferences.PHONE_NUMBER)).subscribe({
                }, {
                })
            }
        }

        //fer tant register-check com login-check
        binding.otp6Edtxt.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // This method is called before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.otpOneEdtxt.text.isEmpty() || binding.otpTwoEdtxt.text.isEmpty() || binding.otpThreeEdtxt.text.isEmpty() || binding.otpFourEdtxt.text.isEmpty() || binding.otp5Edtxt.text.isEmpty() || binding.otp6Edtxt.text.isEmpty()) {
                    binding.otpText.setTextColor(this@OTPActivity.getColor(R.color.background_card_2))
                } else {
                    binding.otpText.setTextColor(this@OTPActivity.getColor(R.color.colorPrimary))
                    binding.otpButton.setOnClickListener {

                        OTP = binding.otpOneEdtxt.text.toString() + binding.otpTwoEdtxt.text.toString() + binding.otpThreeEdtxt.text.toString() + binding.otpFourEdtxt.text.toString() + binding.otp5Edtxt.text.toString() + binding.otp6Edtxt.text.toString()

                        runOnMainThread {
                            binding.loading.visibility = View.VISIBLE
                        }

                        Common.sharedPreferences.putString(Constants.Preferences.UNIT,"km/h")

                        if(Common.sharedPreferences.getString(Constants.Preferences.OTPCHECK) == "REGISTER"){
                            viewModel.registerCheck(OTP).subscribe({
                                runOnMainThread {
                                    binding.loading.visibility = View.GONE
                                }

                                viewModel.getVehicleList().subscribe({ list ->
                                    val first = list.first()
                                    Common.sharedPreferences.putLong(Constants.Preferences.VEHICLE_ID, first.id)
                                    HomeActivity.newInstance(this@OTPActivity)
                                }, {
                                    LinkVehicleActivity.newInstance(this@OTPActivity)
                                })

                            }, { error ->
                                runOnMainThread {
                                    binding.loading.visibility = View.GONE
                                    this@OTPActivity.showAlertDialog(layoutInflater, "Invalid phone activation code", "Invalid phone activation code")
                                }

                            })
                        } else if (Common.sharedPreferences.getString(Constants.Preferences.OTPCHECK) == "LOGIN"){
                            viewModel.loginCheck(OTP).subscribe({
                                runOnMainThread {
                                    binding.loading.visibility = View.GONE
                                }

                                viewModel.getVehicleList().subscribe({ list ->
                                    val first = list.first()
                                    Common.sharedPreferences.putLong(Constants.Preferences.VEHICLE_ID, first.id)
                                    HomeActivity.newInstance(this@OTPActivity)
                                }, {
                                    LinkVehicleActivity.newInstance(this@OTPActivity)
                                })

                            }, { error ->
                                runOnMainThread {
                                    binding.loading.visibility = View.GONE
                                    this@OTPActivity.showAlertDialog(layoutInflater, "Invalid phone activation code", "Invalid phone activation code")
                                }

                            })
                        }

                    }
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // This method is called after the text is changed
            }
        })

    }

    companion object {
        fun newInstance(ctx: Context) = ctx.startActivity(Intent(ctx, OTPActivity::class.java))
    }

}