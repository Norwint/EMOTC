package com.otcengineering.em.views.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.os.postDelayed
import com.google.firebase.messaging.FirebaseMessaging
import com.otcengineering.em.BuildConfig
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivitySplashBinding
import com.otcengineering.em.model.OtcException
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.service.BluetoothService
import com.otcengineering.em.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pub.devrel.easypermissions.EasyPermissions

class SplashActivity : BaseActivity() {

    private val binding: ActivitySplashBinding by lazy { ActivitySplashBinding.inflate(layoutInflater) }
    private val viewModel: WelcomeViewModel by lazy { WelcomeViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        Handler(Looper.getMainLooper()).postDelayed({
            refreshLogin()
        }, 3000)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
            Common.sharedPreferences.putString(Constants.Preferences.FIREBASE_TOKEN, token)
        }
    }

    override fun onResume() {
        super.onResume()
        GlobalScope.launch(Dispatchers.IO) {
            val videoUri = Uri.parse("android.resource://" + packageName + "/" + R.raw.logo_splash_vid)
            withContext(Dispatchers.Main) {
                binding.videoView.setVideoURI(videoUri)
                binding.videoView.setOnPreparedListener {
                    it.setOnInfoListener { mp, what, extra ->
                        if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                            binding.placeholder.visibility = View.GONE
                            return@setOnInfoListener true
                        }

                        return@setOnInfoListener false
                    }
                }
                binding.videoView.start()
                Handler(Looper.getMainLooper()).postDelayed(200) {
                    binding.videoView.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun refreshLogin() {
        viewModel.refreshLogin().subscribe({
            Common.serialNumber = Common.sharedPreferences.getString(Constants.Preferences.SERIAL_NUMBER)
            HomeActivity.newInstance(this)
            finish()
        }, {
            if (it is OtcException) {
                WelcomeActivity.newInstance(this)
            } else {
                HomeActivity.newInstance(this)
            }
            finish()
        })
    }

}