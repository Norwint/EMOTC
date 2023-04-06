package com.otcengineering.em.views.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import com.otcengineering.em.databinding.ActivityLinkVehicleBinding
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread

class LinkVehicleActivity : BaseActivity() {

    private val binding: ActivityLinkVehicleBinding by lazy { ActivityLinkVehicleBinding.inflate(layoutInflater) }

    private val viewModel: WelcomeViewModel by lazy { WelcomeViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

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

        binding.linkVehicle.setOnClickListener {

            runOnMainThread {
                binding.loading.visibility = View.VISIBLE
            }

            //vincula el vehicle amb l'usuari depenent del que hi ha al EditText
            viewModel.putVehicleLink(binding.vin.text.toString()).subscribe({
                runOnMainThread {
                    binding.loading.visibility = View.GONE
                }
                Common.sharedPreferences.putLong(Constants.Preferences.VEHICLE_ID, it.id)
                HomeActivity.newInstance(this)
            }, {
                runOnMainThread {
                    binding.loading.visibility = View.GONE
                }
            })

        }


    }

    companion object {
        fun newInstance(ctx: Context) = ctx.startActivity(Intent(ctx, LinkVehicleActivity::class.java))
    }

}