package com.otcengineering.em.views.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.niwattep.materialslidedatepicker.SlideDatePickerDialog
import com.niwattep.materialslidedatepicker.SlideDatePickerDialogCallback
import com.otcengineering.em.R
import com.otcengineering.em.data.PushNotification
import com.otcengineering.em.databinding.ActivityMyProfileBinding
import com.otcengineering.em.databinding.ActivityNotificationBinding
import com.otcengineering.em.model.ProfileViewModel
import com.otcengineering.em.model.PushNotificationViewModel
import com.otcengineering.em.utils.Common
import com.otcengineering.em.utils.Constants
import com.otcengineering.em.utils.interfaces.OnClickListener
import com.otcengineering.em.utils.runOnMainThread
import com.otcengineering.em.utils.showAlertDialog
import com.otcengineering.em.views.components.TitleBar
import com.otcengineering.em.views.components.addSource
import java.text.SimpleDateFormat
import java.util.*

class NotificationActivity : BaseActivity() {

    private val binding: ActivityNotificationBinding by lazy { ActivityNotificationBinding.inflate(layoutInflater) }

    private val viewModel : PushNotificationViewModel by lazy {
        PushNotificationViewModel()
    }

    var page = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.homeTitleBar.setListener(object: TitleBar.TitleBarListener {
            override fun onLeftClick() {
                finish()
            }

            override fun onRight1Click() {

            }

            override fun onRight2Click() {

            }
        })

        //notificacions igual que a vitesco
        viewModel.getNotificationList(this, page)

        with(binding.recyclerView) {
            addSource(
                R.layout.row_push_notification,
                viewModel.routes,
                object : OnClickListener<PushNotification> {
                    override fun onItemClick(view: View, t: PushNotification) {
                        t.toggleSelected()
                        binding.invalidateAll()
                    }
                }
            )
        }

        binding.recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (!recyclerView.canScrollVertically(1)) {
                    page++
                    viewModel.getNotificationList(this@NotificationActivity, page)
                }
            }
        })

    }

    companion object {
        fun newInstance(ctx: Context) = ctx.startActivity(Intent(ctx, NotificationActivity::class.java))
    }

}