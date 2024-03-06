package app.module.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import app.module.databinding.DialogConfirmToPlayBinding
import app.module.databinding.ItemMoreAppBinding
import app.module.databinding.MoreAppActivityBinding
import app.module.lang.timerInterval
import app.module.objecs.taymayGetJsonFromUrlByKtor
import app.module.utils.MyFile
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.taymayLog
import com.bumptech.glide.Glide
import com.frogobox.recycler.core.FrogoRecyclerNotifyListener
import com.frogobox.recycler.core.IFrogoBindingAdapter
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File

class MoreAppActivity : Activity() {

    lateinit var binding: MoreAppActivityBinding

    class MoreApp(
        var package_app: String,
        var category: String,
        var feature_image: String,
        var icon_app: String,
        var tag: String,
        var install_text: String,
        var play_url: String,
        var score_text: Double,
        var screenshots: String,
        var short_description: String,
        var title: String
    ) {
        fun getShorts(): MutableList<String> {
            try {
                var link = mutableListOf<String>()
                link.add(feature_image)
//                link.addAll(
//                    Gson().fromJson<MutableList<String>>(
//                        screenshots, object : TypeToken<MutableList<String>>() {}.type
//                    ).map { it + "=w1052-h320-rw" }.toMutableList().subList(0, 7)
//                )

                return link
            } catch (e: Exception) {
                return mutableListOf()
            }
        }

    }

    var moreApps = mutableListOf<MoreApp>()
    override fun onResume() {
        taymayFirebaseScreenTracking( "more_app_view", "MoreAppActivity")
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Firebase.analytics.logEvent("cross_open", Bundle())
        taymayLog("request", "more_apps")

        initView()


    }

    private fun initList() {
        binding.appList.injectorBinding<MoreApp, ItemMoreAppBinding>()
            .addData(moreApps)
            .addCallback(object : IFrogoBindingAdapter<MoreApp, ItemMoreAppBinding> {
                override fun onItemClicked(
                    binding: ItemMoreAppBinding,
                    data: MoreApp,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<MoreApp>
                ) {

                }

                override fun onItemLongClicked(
                    binding: ItemMoreAppBinding,
                    data: MoreApp,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<MoreApp>
                ) {

                }

                override fun setViewBinding(parent: ViewGroup): ItemMoreAppBinding {
                    return ItemMoreAppBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                }

                override fun setupInitComponent(
                    binding: ItemMoreAppBinding,
                    data: MoreApp,
                    position: Int,
                    notifyListener: FrogoRecyclerNotifyListener<MoreApp>
                ) {

                    binding.imvIcon.setRadius(16)
                    binding.imvTitle.text = data.title
                    binding.imvTitle.isSelected = true
                    binding.imvShort.text = data.short_description
                    binding.imvShort.isSelected = true
                    binding.tvAdTag.text =
                        " ${if (data.score_text > 4) "⭐ " + data.score_text else data.category} ↓ ${data.install_text}"
                    Glide.with(this@MoreAppActivity).load(data.icon_app).into(binding.imvIcon)
                    binding.imvHeader.setRadius(16)
                    Glide.with(this@MoreAppActivity).load(data.feature_image)
                        .into(binding.imvHeader)
                    binding.btnCheck.setOnClickListener {
                        showDialogConfimGoPlay(data)

                    }


                }
            }).createLayoutLinearVertical(true).build()
        if (moreApps.isEmpty()) {
            binding.llEmpty.visibility = View.VISIBLE
            binding.appList.visibility = View.GONE

        } else {
            binding.appList.visibility = View.VISIBLE
            binding.llEmpty.visibility = View.GONE

        }
    }

    fun showDialogConfimGoPlay(data: MoreApp) {
        var buider = AlertDialog.Builder(this)
        var dialog: AlertDialog
        var dialogConfirmToPlayBinding =
            DialogConfirmToPlayBinding.inflate(LayoutInflater.from(this))
        buider.setView(dialogConfirmToPlayBinding.root)
        dialog = buider.create()

        dialogConfirmToPlayBinding.tvOpen.setOnClickListener {
            Firebase.analytics.logEvent("dialog_cross_click_open", Bundle())
            taymayLog("open_play", data.package_app)

            val browserIntent = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://play.google.com/store/apps/details?id=" + data.package_app
                )
            )
            startActivity(browserIntent)
            if (dialog.isShowing) dialog.dismiss()
        }
        Glide.with(this@MoreAppActivity).load(data.icon_app)
            .into(dialogConfirmToPlayBinding.imvIcon)
        dialogConfirmToPlayBinding.imvIcon.setRadius(24)
        dialogConfirmToPlayBinding.tvCancel.setOnClickListener {
            Firebase.analytics.logEvent("dialog_cross_click_cancel", Bundle())

            if (dialog.isShowing) dialog.dismiss()
        }
        dialogConfirmToPlayBinding.tvTitle.text = data.title
        dialog.show()
        Firebase.analytics.logEvent("dialog_cross_show", Bundle())

    }

    companion object {
        fun initMoreAppData(context: Context, callback: (data: MutableList<MoreApp>) -> Unit) {
            try {
                var moreApps = mutableListOf<MoreApp>()
                var pathCached = File(context.filesDir, "more_app.json")
                var jsonData = "[]"
                if (pathCached.exists()) {
                    jsonData = MyFile.getStringFromFile(pathCached.absoluteFile)
                    taymayGetJsonFromUrlByKtor(
                        "https://bot.taymay.io/my_apps/${context.packageName}", "[]"
                    ) {
                        pathCached.parentFile.mkdirs()
                        MyFile.writeToFile(it, pathCached)

                    }
                    moreApps.clear()
                    moreApps.addAll(
                        Gson().fromJson<MutableList<MoreApp>?>(
                            jsonData, object : TypeToken<MutableList<MoreApp>>() {}.type
                        )
                    )
                    if (moreApps.size > 0) moreApps.removeAll { it.package_app.toString() == context.packageName }
//                    elog("callback exsits more app")

                    callback(moreApps)
                    return

                } else {
                    taymayGetJsonFromUrlByKtor(
                        "https://bot.taymay.io/my_apps/${context.packageName}", "[]"
                    ) {
                        MainScope().launch {
                            pathCached.parentFile.mkdirs()
                            MyFile.writeToFile(it, pathCached)
                            moreApps.clear()
                            moreApps.addAll(
                                Gson().fromJson<MutableList<MoreApp>?>(
                                    jsonData, object : TypeToken<MutableList<MoreApp>>() {}.type
                                )
                            )
                            if (moreApps.size > 0) moreApps.removeAll { it.package_app.toString() == context.packageName }
//                            elog("callback init more app")
                            callback(moreApps)
                            return@launch
                        }
                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        fun Open(activity: Activity) {
            activity.startActivity(Intent(activity, MoreAppActivity::class.java))

        }

    }

    fun loadAndShowData() {


        binding.llLoading.visibility = View.VISIBLE
        binding.appList.visibility = View.GONE
        binding.llEmpty.visibility = View.GONE
        initMoreAppData(this) {
            timerInterval(500) {
                MainScope().launch {
                    moreApps = it
                    initList()
                    binding.llLoading.visibility = View.GONE
                    binding.swipeRefresh.isRefreshing = false
                }
            }

        }
    }

    private fun initView() {
        binding = MoreAppActivityBinding.inflate(layoutInflater)
        binding.swipeRefresh.setOnRefreshListener {

            taymayLog("request", "more_apps")

            loadAndShowData()
        }
        loadAndShowData()
        binding.icBackMoreApp.setOnClickListener {
            onBackPressed()
        }
        binding.icMoreAppInfo.setOnClickListener {
            PolicyActivity.Open_Policy(this, "policy.html", PolicyActivity.calHome)
        }
        setContentView(binding.root)
    }
}