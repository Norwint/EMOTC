package com.otcengineering.em.views.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.view.MotionEvent
import android.view.View
import android.view.ViewTreeObserver
import com.otcengineering.em.R
import com.otcengineering.em.databinding.ActivityLegalBinding

class LegalActivity : BaseActivity(), View.OnTouchListener, ViewTreeObserver.OnScrollChangedListener {

    private val binding: ActivityLegalBinding by lazy { ActivityLegalBinding.inflate(layoutInflater) }

    private var mSharedPreferences: SharedPreferences? = null

    private var n = 0

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        val dif: Bundle = getIntent().getExtras()!!

        binding.txtDescripcion.movementMethod = ScrollingMovementMethod()

        n = 0

        if (dif.getInt("num") == 5) {
            setTitle(R.string.data_protection)
            binding.txtDescripcion.setText(getResources().getString(R.string.privacy_info))
            n = dif.getInt("num")
        } else if (dif.getInt("num") == 6) {
            setTitle(R.string.legal)
            binding.txtDescripcion.setText(getResources().getString(R.string.legal))
            n = dif.getInt("num")
        } else if (dif.getInt("num") == 7) {
            setTitle(R.string.disclaimer)
            binding.txtDescripcion.setText(getResources().getString(R.string.disclaimer))
            n = dif.getInt("num")
        }
//        if (ConnectionUtils.isOnline(getApplicationContext())) {
//            val getTermsByUserLanguage: TypedTask<TermsAcceptanceResponse> =
//                TypedTask(Endpoints.GET_TERMS_ACCEPTANCE_USER_LANG,
//                    null,
//                    true,
//                    TermsAcceptanceResponse::class.java,
//                    object : TypedCallback<TermsAcceptanceResponse?>() {
//                        fun onSuccess(@Nonnull value: TermsAcceptanceResponse) {
//                            descripcion!!.text = Html.fromHtml(value.getTerms(n - 5).text)
//                        }
//
//                        fun onError(@Nonnull status: OTCStatus, message: String?) {
//                            if (status == OTCStatus.USER_PROFILE_REQUIRED) {
//                                GetTerms().execute()
//                            }
//                        }
//                    })
//            getTermsByUserLanguage.execute()
//        } else {
//            ConnectionUtils.showOfflineToast()
//        }
        binding.txtDescripcionLegal.text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque quis urna ut elit dignissim tincidunt. Pellentesque fermentum convallis vehicula. Nulla ac posuere felis. Aenean tincidunt dictum arcu. Etiam tempus orci sit amet porta mattis. Integer dignissim dolor vel metus feugiat sagittis. Duis elementum lacus ac risus lobortis dapibus. Nunc eu iaculis dolor. Nullam nec aliquam massa, eget viverra mi.\n" +
                "\n" +
                "In dapibus massa non leo bibendum vestibulum. Morbi quis ante erat. Quisque pharetra ligula felis, a sagittis justo pulvinar a. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Sed et diam ipsum. Morbi vitae ex eu lorem ullamcorper pretium vitae et leo. Duis eu leo id erat euismod imperdiet. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Vivamus rutrum arcu sed mattis ornare. Praesent sed diam a est rutrum accumsan sit amet vitae magna. Suspendisse urna purus, molestie a semper ut, ullamcorper eu lacus. Nullam tellus ante, commodo sed ex vitae, eleifend cursus orci.\n" +
                "\n" +
                "Integer non pulvinar velit. Maecenas velit nunc, ullamcorper sed porta et, euismod ac turpis. Nullam dapibus interdum blandit. Morbi a tristique urna. Suspendisse luctus velit ut enim lacinia, eget vehicula leo tempor. Quisque non congue nunc. Ut felis felis, rhoncus nec viverra eu, auctor ac arcu. Curabitur placerat tristique eros, nec porttitor ante fringilla id.\n" +
                "\n" +
                "Donec lobortis tortor nisl, hendrerit condimentum est laoreet id. Proin ullamcorper tortor eu pretium iaculis. Sed euismod ligula at urna pulvinar, nec tempus mauris vulputate. Lorem ipsum dolor sit amet, consectetur adipiscing elit. Maecenas ut euismod felis, quis sagittis lacus. Integer at nisl non lorem dignissim congue nec eget elit. Nunc vitae rhoncus est. Sed at magna et ante porta gravida vitae ut nulla. Maecenas pretium, nulla vel vehicula tincidunt, eros libero suscipit neque, non pharetra ex purus vel massa. Ut eget sem sit amet quam tempor consectetur a eu tellus. Maecenas scelerisque odio diam, at elementum turpis dapibus eu. Vivamus eu porta orci, sed malesuada magna. Sed elementum maximus purus, quis scelerisque elit varius in. Integer at congue turpis. In turpis urna, dapibus ut sapien sed, accumsan lobortis lacus.\n" +
                "\n" +
                "Orci varius natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Sed lorem augue, tincidunt sed elit in, pulvinar placerat ante. Donec sagittis sem id dui iaculis, a auctor mi ullamcorper. Curabitur pharetra cursus turpis, ac tincidunt urna suscipit vel. Ut quis varius velit. Duis pellentesque, turpis sit amet consectetur imperdiet, quam ex consequat arcu, pharetra volutpat risus sapien lacinia est. Pellentesque leo ipsum, imperdiet non auctor pretium, venenatis maximus ipsum. Nam fermentum, enim id eleifend aliquam, arcu urna interdum libero, non vehicula urna augue non nisi. Vestibulum tempus, sapien venenatis elementum laoreet, arcu leo suscipit urna, eget egestas dui metus scelerisque erat. Fusce ex turpis, aliquam nec nulla vel, fringilla lobortis mauris. Vestibulum egestas velit urna, a posuere risus gravida non. Proin in metus quis nulla iaculis blandit."
//        metodoAceptar(n)

        binding.scroll.setOnTouchListener(this)
        binding.scroll.viewTreeObserver.addOnScrollChangedListener(this)

    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouch(p0: View?, p1: MotionEvent?): Boolean {
        return false
    }

    override fun onScrollChanged() {
        val view = binding.scroll.getChildAt(binding.scroll.childCount - 1)
        val topDetector = binding.scroll.scrollY
        val bottomDetector: Int = view.bottom - (binding.scroll.height + binding.scroll.scrollY)
        if (bottomDetector == 0) {
            binding.btnTxtAceptar.setTextColor(getColor(R.color.black))
            binding.btnAceptar.setOnClickListener {
                setResult(RESULT_OK, Intent().putExtra("num",n))
                finish()
            }
        }
    }


//    internal inner class GetTerms :
//        AsyncTask<Any?, Any?, TermsAcceptanceResponse?>() {
//        protected override fun doInBackground(vararg params: Any): TermsAcceptanceResponse? {
//            return try {
//                var lang: String = MyApp.getUserLocale().getLanguage()
//                lang = if (lang == "in") {
//                    "ba"
//                } else {
//                    "en"
//                }
//                ApiCaller.doCall(
//                    Endpoints.GET_TERMS_ACCEPTANCE_LANG + lang, false, null,
//                    TermsAcceptanceResponse::class.java
//                )
//                //return ApiCaller.doCall(Endpoints.GET_TERMS_ACCEPTANCE, null, Welcome.TermsAcceptanceResponse.class);
//            } catch (e: Exception) {
//                null
//            }
//        }
//
//        override fun onPostExecute(response: TermsAcceptanceResponse?) {
//            if (response == null) {
//                runOnUiThread {
//                    Toast.makeText(
//                        getApplicationContext(),
//                        "Cannot load Terms.",
//                        Toast.LENGTH_LONG
//                    ).show()
//                }
//                if (!Utils.isActivityFinish(this@LegalActivity)) {
//                    finish()
//                }
//            } else {
//                if (n == 5) {
//                    descripcion!!.text = Html.fromHtml(response.getTerms(0).text)
//                } else if (n == 6) {
//                    descripcion!!.text = Html.fromHtml(response.getTerms(1).text)
//                } else if (n == 7) {
//                    descripcion!!.text = Html.fromHtml(response.getTerms(2).text)
//                }
//            }
//        }
//    }

    companion object {
        private var legalTerm = true
        private var dataTerm = true
        private var disclaTerm = true
    }
}