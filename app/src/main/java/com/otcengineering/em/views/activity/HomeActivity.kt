package com.otcengineering.em.views.activity

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.otcengineering.em.BuildConfig
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivityHomeBinding
import com.otcengineering.em.model.ServicesViewModel
import com.otcengineering.em.model.SettingsViewModel
import com.otcengineering.em.model.WelcomeViewModel
import com.otcengineering.em.service.BluetoothService
import com.otcengineering.em.service.LocationService
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.startActivity
import com.otcengineering.em.views.fragment.*
import com.otcengineering.em.views.components.TitleBar
import com.otcengineering.otcble.ble.BleSDK
import com.otcengineering.otcble.remote.NetworkSDK
import pub.devrel.easypermissions.EasyPermissions


class HomeActivity : BaseActivity() {

    private val binding: ActivityHomeBinding by lazy { ActivityHomeBinding.inflate(layoutInflater) }
    private val welcomeViewModel: WelcomeViewModel = WelcomeViewModel()
    private val network: NetworkSDK by lazy { Common.network }

    private val viewModel: SettingsViewModel by lazy { SettingsViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //detectar si te ble connectat
        BleSDK.enableBluetooth(this)

        binding.navBar.navigationTabDashboardSelector.setColorFilter(this.getColor(R.color.colorRed))

        //guardar general settings al cache
        viewModel.getGeneralSettings().subscribe({ settings ->

            val serialized = settings.toByteArray()
            Common.sharedPreferences.putRaw(Constants.Preferences.GENERAL_SETTINGS, serialized)

        }, {
        })

        //send push token
        welcomeViewModel.putNotificationToken().subscribe({

        }, {

        })

        //guardar url del community
        ServicesViewModel().getURL().subscribe({
            Common.sharedPreferences.putString(Constants.Preferences.URL, it.url)
        }, {
            Common.sharedPreferences.putString(Constants.Preferences.URL, "https://www.electric-motion.fr/en/")
        })

        setFragment(GeneralDashboardFragment())

        binding.menuHome.navigationLayoutMore.visibility = View.GONE
        binding.menuHome.version = "EM ${BuildConfig.VERSION_NAME}"


        if (BluetoothService.checkPermissions(this)) {
            LocationService.getService(this)
            BluetoothService.getService(this).executeService()
        }

        //detectar vehicle
        welcomeViewModel.getVehicleList().subscribe({ list ->
            val first = list.first()
            Common.macAddress = first.tcuMacAddress
            Common.sharedPreferences.putLong(Constants.Preferences.VEHICLE_ID, first.id)
            Common.serialNumber = first.tcuSerialNumber
            Common.sharedPreferences.putString(Constants.Preferences.SERIAL_NUMBER, first.tcuSerialNumber)
            Common.sharedPreferences.putString(Constants.Preferences.VIN, first.vin)
        }, {})

        binding.menuHome.txtLogout.setOnClickListener {
            Common.sharedPreferences.clear()
            BluetoothService.getService(this).bluetoothDisconnect()
            BluetoothService.getService(this).bluetoothRemoveSerialNumber()
            WelcomeActivity.newInstance(this)
            network.tokenSession.setAuthToken(null)
            finish()
        }

        binding.menuHome.closeMenu.setOnClickListener {
            binding.menuHome.navigationLayoutMore.visibility = View.GONE
        }

        binding.menuHome.txtProfile.setOnClickListener {
            binding.menuHome.navigationLayoutMore.visibility = View.GONE
            MyProfileActivity.newInstance(this)
            binding.homeTitleBar.title = "My Profile"
        }

        binding.menuHome.txtNotifications.setOnClickListener {
            binding.menuHome.navigationLayoutMore.visibility = View.GONE
            NotificationActivity.newInstance(this)
            binding.homeTitleBar.title = "Notification History"
        }


        binding.navBar.navigationTabDashboard.setOnClickListener(createListenerForTab())
        binding.navBar.navigationTabLocation.setOnClickListener(createListenerForTab())
        binding.navBar.navigationTabNotifications.setOnClickListener(createListenerForTab())
        binding.navBar.navigationTabTracklog.setOnClickListener(createListenerForTab())

        binding.homeTitleBar.setListener(object: TitleBar.TitleBarListener {
            override fun onLeftClick() {
                binding.menuHome.navigationLayoutMore.visibility = View.VISIBLE
            }

            override fun onRight1Click() {

            }

            override fun onRight2Click() {

            }
        })

//        binding.menuHome.profileImageButton.setOnClickListener { showPictureDialog() }

        onBackPressedDispatcher.addCallback(object: OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.menuHome.navigationLayoutMore.visibility == View.VISIBLE) {
                    binding.menuHome.navigationLayoutMore.visibility = View.GONE
                    return
                }

                finish()
            }
        })
    }

//    private fun showPictureDialog() {
//        val pictureDialog = AlertDialog.Builder(this)
//        pictureDialog.setTitle(R.string.select_action)
//        val pictureDialogItems = arrayOf(
//            getString(R.string.select_from_gallery),
//            getString(R.string.capture_from_camera),
//            "Remove photo"
//        )
//        pictureDialog.setItems(
//            pictureDialogItems
//        ) { dialog: DialogInterface?, which: Int ->
//            when (which) {
//                0 -> choosePhotoFromGallary()
//                1 -> takePhotoFromCamera()
//                2 -> removeImage()
//            }
//        }
//        pictureDialog.show()
//    }
//
//    private fun removeImage() {
//        viewModel.removeImage().subscribe({
//            if (it) {
//                binding.menuHome.profileImage.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.user_placeholder_correct))
//            }
//        }, {})
//    }
//
//    private fun takePhotoFromCamera() {
//        uriForCamera = FileProvider.getUriForFile(this, "$packageName.provider", File(externalCacheDir, "photo.png"))
//        startForResultForCamera.launch(uriForCamera!!)
//    }
//
//    private var uriForCamera: Uri? = null
//
//    private val startForResultForCamera = registerForActivityResult(ActivityResultContracts.TakePicture()) { result ->
//        if (result) {
//            val bitmap = ImageUtils.getBitmapExifCorrected(this, uriForCamera!!)
//            binding.menuHome.profileImage.setImageBitmap(bitmap)
//
//            viewModel.putImage(bitmap, "${System.currentTimeMillis()}.jpg").subscribe({
//                viewModel.getUserProfile().subscribe({ profile ->
//                    binding.menuHome.user = profile
//                    if (profile.image != 0L) {
//                        viewModel.getImage(profile.image, this).subscribe({
//                            binding.menuHome.profileImage.setImageDrawable(it)
//                        }, {
//
//                        })
//                    }
//                }, {
//                    println(it)
//                })
//            }, {})
//        }
//    }
//
//    private val startForResultFromGallery = registerForActivityResult(
//        ActivityResultContracts.StartActivityForResult()
//    ) { result ->
//        if (result != null) {
//            if (result.resultCode == Activity.RESULT_OK) {
//                try {
//                    if (result.data != null) {
//                        val selectedImageUri: Uri? = result.data!!.data
//                        val bitmap = BitmapFactory.decodeStream(
//                            selectedImageUri?.let {
//                                baseContext.contentResolver.openInputStream(
//                                    it
//                                )
//                            }
//                        )
//                        binding.menuHome.profileImage.setImageBitmap(bitmap)
//                        viewModel.putImage(bitmap, "${System.currentTimeMillis()}.jpg").subscribe({
//                            viewModel.getUserProfile().subscribe({ profile ->
//                                binding.menuHome.user = profile
//                                if (profile.image != 0L) {
//                                    viewModel.getImage(profile.image, this).subscribe({
//                                        binding.menuHome.profileImage.setImageDrawable(it)
//                                    }, {
//
//                                    })
//                                }
//                            }, {
//                                println(it)
//                            })
//                        }, {})
//                    }
//                } catch (exception: java.lang.Exception) {
//                    Log.d("TAG", "" + exception.localizedMessage)
//                }
//            }
//        }
//    }
//
//    fun choosePhotoFromGallary() {
//        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//        if (galleryIntent.resolveActivity(packageManager) != null) {
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startForResultFromGallery.launch(intent)
//        } else {
//            val filesIntent =
//                Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
//            startForResultFromGallery.launch(intent)
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        try {
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults)
        } catch (e: Exception) {
            Log.e("HomeActivity", "Exception", e)
        }
        LocationService.getService(this)
        BluetoothService.getService(this).executeService()
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun createListenerForTab(): View.OnClickListener {
        return View.OnClickListener { view: View ->
            binding.navBar.navigationTabDashboardSelector.setColorFilter(Color.WHITE)
            binding.navBar.navigationTabLocationSelector.setColorFilter(Color.WHITE)
            binding.navBar.navigationTabNotificationSelector.setColorFilter(Color.WHITE)
            binding.navBar.navigationTabTracklogSelector.setColorFilter(Color.WHITE)
            when (view.id) {
                R.id.navigation_tabDashboard -> {
                    setFragment(GeneralDashboardFragment())
                    binding.homeTitleBar.title = "Dashboard"
                    binding.navBar.navigationTabDashboardSelector.setColorFilter(this.getColor(R.color.colorRed))
                }
                R.id.navigation_tabLocation -> {
                    setFragment(SettingsFragment())
                    binding.homeTitleBar.title = "Settings"
                    binding.navBar.navigationTabLocationSelector.setColorFilter(this.getColor(R.color.colorRed))
                }
                R.id.navigation_tabNotifications -> {
                    setFragment(DiagnosticUpdateFragment())
                    binding.homeTitleBar.title = "Services"
                    binding.navBar.navigationTabNotificationSelector.setColorFilter(this.getColor(R.color.colorRed))
                }
                R.id.navigation_tabTracklog -> {
                    setFragment(ServicesFragment())
                    binding.homeTitleBar.title = "Update & diagnostic"
                    binding.navBar.navigationTabTracklogSelector.setColorFilter(this.getColor(R.color.colorRed))
                }
            }
        }
    }

    companion object {
        fun newInstance(ctx: Context) {
            ctx.startActivity(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK, HomeActivity::class.java)
        }
    }
}