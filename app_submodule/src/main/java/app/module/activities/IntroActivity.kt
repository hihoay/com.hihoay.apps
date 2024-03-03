package app.module.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import app.module.databinding.LayoutIntroActivityBinding
import app.module.databinding.LayoutIntroPageBinding
import app.module.objecs.loadAndShowAdInLayout
import app.module.utils.MyCache
import app.module.utils.taymayFirebaseScreenTracking
import app.module.utils.taymayGetAppVersionName
import com.bumptech.glide.Glide

class IntroFragment : Fragment() {
    lateinit var bidding: LayoutIntroPageBinding
    var pageIndex: Int = 0
    lateinit var introPager: IntroPager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val bundle = arguments
        pageIndex = bundle!!.getInt("p")

        when (IntroActivity.tutorialSetUp.pageFrom) {
            PageFrom.View -> {
                var viewContent = IntroActivity.tutorialSetUp.viewPages[pageIndex]
                if (viewContent.getParent() != null)
                    (viewContent.getParent() as ViewGroup).removeView(viewContent) // <- fix
                return viewContent
            }

            PageFrom.Page -> {
                bidding = LayoutIntroPageBinding.inflate(layoutInflater)
                try {
                    introPager = IntroActivity.tutorialSetUp.introPages[pageIndex]
                    bidding.tvBody.text = introPager.body
                    bidding.tvTitle.text = introPager.title

                    bidding.tvBody.setTextColor(resources.getColor(introPager.bodyColor))
                    bidding.tvTitle.setTextColor(resources.getColor(introPager.titleColor))

                    if (!introPager.isAnim) {
                        bidding.imvTut.setImageResource(introPager.image)
                        bidding.imvTut.visibility = View.VISIBLE
                        bidding.ltAnimation.visibility = View.GONE
                    } else {
                        bidding.ltAnimation.setAnimation(introPager.image)
                        bidding.imvTut.visibility = View.GONE
                        bidding.ltAnimation.visibility = View.VISIBLE
                    }
                } catch (e: Exception) {

                }

                return bidding.root
            }

            else -> {
                return View(requireContext())
            }
        }

    }


    companion object {
        fun newInstance(index: Int): IntroFragment {
            val fragment = IntroFragment()
            val args = Bundle()
            args.putInt("p", index)
            fragment.setArguments(args)
            return fragment
        }


    }

}

enum class ShowType {
    Once, Multi
}




enum class PageFrom {
    Page, View
}

class TutorialSetUp(
    var pageFrom: PageFrom,
    var ad_top_name: String,
    var ad_bottom_name: String,
    var showType: ShowType,
    var backgroundDrawable: Int,
    var introPages: MutableList<IntroPager>,
    var viewPages: MutableList<View>
)

class IntroPager(
    var isAnim: Boolean,
    var image: Int,
    var title: String,
    var titleColor: Int,
    var body: String,
    var bodyColor: Int
)

class IntroActivity : FragmentActivity() {
    lateinit var binding: LayoutIntroActivityBinding
    override fun onResume() {
        taymayFirebaseScreenTracking("intro_view", "IntroActivity")
        super.onResume()
    }

    override fun onBackPressed() {

    }

    companion object {

        var tutorialSetUp: TutorialSetUp =
            TutorialSetUp(
                PageFrom.Page,
                "",
                "",
                ShowType.Once,
                0,
                mutableListOf(),
                mutableListOf()
            )

        /**
         * isCanShowAd thể hiện đã show Intro và người dùng đã click Done thì có thể hiển thị được quảng cáo sau Intro trả về là true, còn nếu Intro đã hiển thị thì sẽ trả về false
         */
        var runAfterDone = fun(isCanShowAd: Boolean) {}

        fun taymayShowIntroFromPages(
            context: Context,
            showType: ShowType,
            ad_top_name: String,
            ad_bottom_name: String,
            backgroundDrawable: Int,
            vararg introPager: IntroPager,
            runAfterDone: (isCanShowAd: Boolean) -> Unit
        ) {

            if (introPager.isEmpty() || MyCache.getBooleanValueByName(
                    context,
                    taymayGetAppVersionName(context),
                    false
                )
                && showType == ShowType.Once
            ) {
                runAfterDone(false)
                return
            }

            IntroActivity.runAfterDone = runAfterDone
            IntroActivity.tutorialSetUp =
                TutorialSetUp(
                    PageFrom.Page,
                    ad_top_name,
                    ad_bottom_name,
                    showType,
                    backgroundDrawable,
                    introPager.toMutableList(),
                    mutableListOf()
                )
            context.startActivity(Intent(context, IntroActivity::class.java))
        }

        fun taymayShowIntroFromViews(
            context: Context,
            showType: ShowType,
            ad_top_name: String,
            ad_bottom_name: String,
            backgroundDrawable: Int,
            vararg view: View,
            runAfterDone: (isCanShowAd: Boolean) -> Unit
        ) {
            if (view.isEmpty() || MyCache.getBooleanValueByName(
                    context,
                    taymayGetAppVersionName(context),
                    false
                )
                && showType == ShowType.Once
            ) {
                runAfterDone(false)
                return
            }
            IntroActivity.runAfterDone = runAfterDone
            IntroActivity.tutorialSetUp =
                TutorialSetUp(
                    PageFrom.View,
                    ad_top_name,
                    ad_bottom_name,
                    showType,
                    backgroundDrawable,
                    mutableListOf(),
                    view.toMutableList()
                )
            context.startActivity(Intent(context, IntroActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LayoutIntroActivityBinding.inflate(layoutInflater)
        var pageAdaptor = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getCount(): Int {
                if (tutorialSetUp.pageFrom == PageFrom.Page)
                    return tutorialSetUp.introPages.size
                else
                    return tutorialSetUp.viewPages.size
            }

            override fun getItem(position: Int): Fragment {
                return IntroFragment.newInstance(position)
            }
        }

        Glide.with(this).load(tutorialSetUp.backgroundDrawable).into(binding.imvBackgroud)

        if (tutorialSetUp.showType == ShowType.Once)
            binding.tvDone.text = getString(app.module.R.string.start)
        else
            binding.tvDone.text = getString(app.module.R.string.done)
        binding.viewpagerTutorial.adapter = pageAdaptor
        binding.viewpagerTutorial.addOnPageChangeListener(object : OnPageChangeListener {

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                if (binding.spinKit.visibility == View.GONE) {
                    var pageSize = 0
                    if (tutorialSetUp.pageFrom == PageFrom.Page)
                        pageSize = tutorialSetUp.introPages.size
                    else
                        pageSize = tutorialSetUp.viewPages.size
                    if (position >= pageSize - 1) {
                        binding.tvDone.visibility = View.VISIBLE
                        binding.tvNext.visibility = View.GONE
                    } else {
                        binding.tvDone.visibility = View.GONE
                        binding.tvNext.visibility = View.VISIBLE
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })
        binding.tvDone.visibility = View.GONE
        binding.tvNext.visibility = View.GONE
        binding.spinKit.visibility = View.GONE


        loadAndShowAdInLayout(this, tutorialSetUp.ad_top_name, binding.llAdTop)
        loadAndShowAdInLayout(this, tutorialSetUp.ad_bottom_name, binding.llAdBottom)

        initAfterShowAd()
        binding.indicator.setViewPager(binding.viewpagerTutorial)
        setContentView(binding.root)
    }

    fun initAfterShowAd() {
        binding.tvDone.visibility = View.GONE
        binding.tvNext.visibility = View.VISIBLE


        binding.tvNext.setOnClickListener {
            binding.viewpagerTutorial.setCurrentItem(
                binding.viewpagerTutorial.currentItem + 1,
                true
            )
        }

        binding.tvDone.setOnClickListener {
            MyCache.putBooleanValueByName(this, taymayGetAppVersionName(this), true)
            runAfterDone(true)
            finish()
        }
    }

}