package com.otcengineering.em.views.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Toast
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivityMyProfileBinding
import com.otcengineering.em.model.ProfileViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.em.utils.showAlertDialog
import com.otcengineering.em.views.components.TitleBar
import java.text.SimpleDateFormat
import java.util.*

class MyProfileActivity : BaseActivity(), SlideDatePickerDialogCallback {

    private val binding: ActivityMyProfileBinding by lazy { ActivityMyProfileBinding.inflate(layoutInflater) }

    private val viewModel: ProfileViewModel by lazy { ProfileViewModel() }

    private var units = ""

    private var username = false
    private var name = false
    private var surname = false
    private var birthday = false
    private var email = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //dades del cloud del profile a la pantalla
        viewModel.getProfile().subscribe ({
            runOnMainThread {
                binding.username.setText(it.profile.username)
                binding.name.setText(it.profile.name)
                binding.surname.setText(it.profile.surname)
                binding.birthday.setText(it.profile.birthdayDate.substring(0, 10))
                binding.email.setText(it.profile.email)

                username = false
                name = false
                surname = false
                birthday = false
                email = false

                binding.saveTxt.setTextColor(this.getColor(R.color.background_card_2))
            }
        }, {

        })

        binding.homeTitleBar.setListener(object: TitleBar.TitleBarListener {
            override fun onLeftClick() {
                finish()
            }

            override fun onRight1Click() {

            }

            override fun onRight2Click() {

            }
        })


        binding.editText.setOnClickListener {
            SlideDatePickerDialog.Builder().build().show(supportFragmentManager, "TAG")
        }

        binding.country.setText(Common.sharedPreferences.getString(Constants.Preferences.COUNTRY))

        //escollir si son kmh o mph
        binding.metric.setOnClickListener {
            binding.metric.background = this.getDrawable(R.drawable.buttonshape_unit)
            binding.txtmetric.setTextColor(this.getColor(R.color.colorPrimary))
            binding.imperial.background = this.getDrawable(R.drawable.buttonshap_unit_click)
            binding.txtimperial.setTextColor(this.getColor(R.color.white))
            units = "km/h"
        }

        binding.imperial.setOnClickListener {
            binding.imperial.background = this.getDrawable(R.drawable.buttonshape_unit)
            binding.txtimperial.setTextColor(this.getColor(R.color.colorPrimary))
            binding.metric.background = this.getDrawable(R.drawable.buttonshap_unit_click)
            binding.txtmetric.setTextColor(this.getColor(R.color.white))
            units ="mph"
        }

        binding.username.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                username = true
                runOnMainThread {
                    binding.saveTxt.setTextColor(this@MyProfileActivity.getColor(R.color.colorPrimary))
                }
            }
        })

        binding.name.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                name = true
                runOnMainThread {
                    binding.saveTxt.setTextColor(this@MyProfileActivity.getColor(R.color.colorPrimary))
                }
            }
        })

        binding.surname.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                surname = true
                runOnMainThread {
                    binding.saveTxt.setTextColor(this@MyProfileActivity.getColor(R.color.colorPrimary))
                }
            }
        })

        binding.email.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Do something before the text is changed
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Do something when the text is changed
            }

            override fun afterTextChanged(s: Editable?) {
                email = true
                runOnMainThread {
                    binding.saveTxt.setTextColor(this@MyProfileActivity.getColor(R.color.colorPrimary))
                }
            }
        })

        viewModel.getCountry(Common.sharedPreferences.getString(Constants.Preferences.COUNTRY)).subscribe ({

        }, {

        })

        binding.save.setOnClickListener {

            //depenent si l'usuari a canviat alguna dada es fa un save profile
            if(username || name || surname || birthday || email) {
                runOnMainThread {
                    binding.bkgSave.visibility = View.VISIBLE
                    binding.saveAlert.visibility = View.VISIBLE

                    binding.yes.setOnClickListener {
                        viewModel.putProfile(
                            if(binding.username.text != null){ binding.username.text.toString() } else { String() },
                            if(binding.name.text != null){ binding.name.text.toString() } else { String() },
                            if(binding.surname.text != null){ binding.surname.text.toString() } else { String() },
                            if(binding.birthday.text != null){ binding.birthday.text.toString() } else { String() },
                            if(binding.email.text != null){ binding.email.text.toString() } else { String() }).subscribe ({
                            runOnMainThread {
                                Toast.makeText(this, "SUCCESS", Toast.LENGTH_LONG)
                            }
                        }, {
                            runOnMainThread {
                                this.showAlertDialog(layoutInflater, "Failed", "Failed")
                            }
                        })

                        if(units == "km/h") {
                            Common.sharedPreferences.putString(Constants.Preferences.UNIT,"km/h")
                        } else {
                            Common.sharedPreferences.putString(Constants.Preferences.UNIT,"mph")
                        }

                        binding.bkgSave.visibility = View.GONE
                        binding.saveAlert.visibility = View.GONE

                    }

                    binding.no.setOnClickListener {
                        binding.bkgSave.visibility = View.GONE
                        binding.saveAlert.visibility = View.GONE
                    }
                }
            } else {

            }

        }

    }

    companion object {
        fun newInstance(ctx: Context) = ctx.startActivity(Intent(ctx, MyProfileActivity::class.java))
    }

    override fun onPositiveClick(day: Int, month: Int, year: Int, calendar: Calendar) {
        binding.birthday.text = SimpleDateFormat("yyyy-MM-dd").format(calendar.time)
        birthday = true
        runOnMainThread {
            binding.saveTxt.setTextColor(this.getColor(R.color.colorPrimary))
        }
    }

}