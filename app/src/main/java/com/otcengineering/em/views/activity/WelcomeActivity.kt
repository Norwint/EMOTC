package com.otcengineering.em.views.activity


import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivityWelcomeBinding
import com.otcengineering.em.utils.startActivity
import com.otcengineering.em.views.fragment.LoginFragment
import com.otcengineering.em.views.fragment.SignUpFragment

class WelcomeActivity : BaseActivity() {

    private val binding: ActivityWelcomeBinding by lazy { ActivityWelcomeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setFragment(LoginFragment())

        binding.signUp.setOnClickListener {
            binding.imgLogin.visibility = View.INVISIBLE
            binding.imgSignup.visibility = View.VISIBLE
            binding.textLogin.setTextColor(Color.WHITE)
            binding.textSignup.setTextColor(Color.rgb(0,1,70))
            setFragment(SignUpFragment())
        }

        binding.login.setOnClickListener {
            binding.imgLogin.visibility = View.VISIBLE
            binding.imgSignup.visibility = View.INVISIBLE
            binding.textLogin.setTextColor(Color.rgb(0,1,70))
            binding.textSignup.setTextColor(Color.WHITE)
            setFragment(LoginFragment())
        }
    }

    private fun setFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.content, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    companion object {
        fun newInstance(ctx: Context) = ctx.startActivity(WelcomeActivity::class.java)
    }
}