<p id="top"/>

# Module cho Android - Hihoay.io

<img src="https://play-lh.googleusercontent.com/kYGVCEZebP5uG0oJeC6u7KJJj3WhYBBqv-k4o4gxHxZO_oHSM2AIlaPDpZEg2FkSfj8" width=10% height=10%>
</img>

| Các Mục                                                                                                               | Mô tả |
| --------------------------------------------------------------------------------------------------------------------- | ----- |
| [I, Tích hợp vào dự án (Android Studio)](#i-tích-hợp-vào-dự-án-android-studio)                                        |       |
| [II, Cài đặt tải cấu hình quảng cáo](#ii-cài-đặt-tải-cấu-hình-quảng-cáo)                                              |       |
| [III, Cài đặt hiển thị các vị trí quảng cáo trong App](#iii-cài-đặt-hiển-thị-các-vị-trí-quảng-cáo-trong-app)          |       |
| [IV, Quảng cáo màn hình Intro/Tutorial và Cấu hình cài đặt](#iv-quảng-cáo-màn-hình-introtutorial-và-cấu-hình-cài-đặt) |       |
| [V, Các tiện ích khác](#v-các-tiện-ích-khác)                                                                          |       |
| [VI, Hệ thống cấu hình dữ liệu trong App](#v-các-tiện-ích-khác)                                                       |       |
| [VII, Hệ thống In App Purchase (Thanh toán trong App)](#vii-hệ-thống-in-app-purchase-thanh-toán-trong-app)            |       |

## I, Tích hợp vào dự án (Android Studio)

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

- Tích hợp vào Android Studio:

1. Thêm vô `build.gradle`:

- Phiên bản: [![](https://jitpack.io/v/com.github.hihoay/com.hihoay.apps.svg)](https://jitpack.io/private#hihoay/com.hihoay.apps)

```kotlin
  implementation 'com.github.hihoay:com.hihoay.apps:<version_name>'
```

2. Thêm vô `settings.gradle`:

```groovy
 dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        ...
        maven {
            url "https://jitpack.io"
            credentials { username  "jp_9m3ub8bd9bkaudepcup9k83n37"}
        }
    }
 }
```

3. Thêm các tệp cấu hình mặc định vào thư mục `assets`:

> `ads.json` là tệp cấu hình mặc định các vị trí quảng cáo.

- Lấy nội dung tệp `ads.json` bằng cách kiểm tra `Logcat` trên `Android Studio` sau khi chạy ứng
  dụng sẽ in ra đường dẫn chứa nội dung của tệp `copy` nội dung từ đường dẫn và `dán` vào
  tệp `ads.json`, đường dẫn định dạng sau:

```json
ad_version   https://bot.taymay.io/ad_version/<app_package_name>.json
```

> `data.json` là tập cấu hình mặc định các dữ liệu sử dụng.

- Lấy nội dung tệp `data.json` bằng cách kiểm tra `Logcat` trên `Android Studio` sau khi chạy ứng
  dụng sẽ in ra đường dẫn chứa nội dung của tệp `copy` nội dung từ đường dẫn và `dán` vào
  tệp `data.json`, đường dẫn có định dạng sau:

```json
data_version   https://bot.taymay.io/data_version/<app_package_name>.json
```

## II, Cài đặt tải cấu hình quảng cáo

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

4.**(Phần này trờ xuống thì App đã được cài rồi nên mọi người có thể bỏ qua)** Thêm vào Manifest
thông tin App ID của Admob nếu chưa có (không sẽ bị crash, dưới đây là ID test
khi nào có ID thật thì thay thế) (meta-data được thêm ở vị trí trong thẻ `application`)

```xml

    <!-- Sử dụng ID này để test nếu chưa có: ca-app-pub-3940256099942544~3347511713 -->

<meta-data android:name="com.google.android.gms.ads.APPLICATION_ID"
    android:value="<id của app trên Admob>" />
```

- Thêm vào trong thẻ `application` trong `manifest`

```xml
  <application
    ...
      tools:replace="android:theme">
  </application>
```

5. Gắn Firebase vào App nếu chưa gắn:

- Thêm filre `google-services.json` vào thư mục `app` của project.

- Trên Android Studio vào
  menu `Tools` > `Firebase` > `Analytics` > `Get started with Google Analytics [Kotlin]` > chọn
  module `app`

- Trên Android Studio vào
  menu `Tools` > `Firebase` > `Crashlytics` > `Get started with Firebase Crashlytics [Kotlin]` >
  chọn module `app`

6. Cấu hình `Remote Configs`:

- Key là `ad_version` giá trị string là `default`
- Key là `data_version` giá trị string là `default`

7. Rebuild lại Project

<p id="abcd"></p>

#### 1, Thêm vào `onCreate()` `Application` _class_

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
//        AD_CONFIG_VERSION_DEFAULT = "test"
//        DATA_CONFIG_VERSION_DEFAULT = "default"
        IS_TESTING = BuildConfig.DEBUG
        HASH_UMP_TEST = "137E352EEFAF0422571EC5990F502A56"
        taymaySetupApplication(this, "remove_ad,upgrade_01")
    }
}
```

#### 2, Cài đặt màn hình Splash `SplashActivity`:

```kotlin

class SplashActivity : AppCompatActivity() {
    private val isCalled = AtomicBoolean(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(
            taymaySplashViewNoAnim(
                resources.getString(R.string.app_name), R.raw.ic_splash, layoutInflater
            ).root
        )
        taymayInitUMP(this) { b, consentInformation ->
            if (isCalled.compareAndSet(false, true)) {
                initSplash()
            }
        }
    }

    private fun initSplash() {
        taymayGetAdVersion(this) { //Tải dữ liệu quảng cáo
//            if (com.taymay.calculator.vault.BuildConfig.DEBUG) adsConfig.forEach {
//                it.ad_distance = 3
//            }
            taymayLoadAdShowCallback(
                this, "splash_full", LinearLayout(this)
            ) { myAd ->
                when (myAd.adState) {
                    AdState.Close, AdState.Done, AdState.Timeout, AdState.Error -> adShowDone()
                    AdState.Show -> setContentView(LinearLayout(this@SplashActivity))
                    else -> {}
                }
            }
        }
    }

    private fun adShowDone() {
        taymayAskFirstSetupLanguage(this, "au:language_top_small", "language_bottom_medium") {
            showIntro(this, ShowType.Once) {
                if (it) taymayActivityLoadAndShowAdCallbackFor(
                    this, "app_intro_skip_full"
                ) { ad, ac ->
                    goHomeScreen()
                }
                else goHomeScreen()
            }
        }
    }

    private fun goHomeScreen() {
            startActivity(Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            })
    }

    companion object {

        fun showIntro(activity: Activity, multi: ShowType, callback: (Boolean) -> Unit) {
            IntroActivity.taymayShowIntroFromPages(
                activity, multi, "au:intro_top_small",

                "app_intro_bottom_small", R.drawable.bg_tut, IntroPager(
                    true,
                    R.raw.set_pass_anim,
                    activity.resources.getString(R.string.title_tut_1),
                    R.color.colorWhile,
                    activity.resources.getString(R.string.body_tut_1),
                    R.color.colorWhile
                ), IntroPager(
                    true,
                    R.raw.anim_reset_pass,
                    activity.resources.getString(R.string.title_tut_2),
                    R.color.colorWhile,
                    activity.resources.getString(R.string.body_tut_2),
                    R.color.colorWhile
                ),
                IntroPager(
                    true,
                    R.raw.anim_tut_03,
                    activity.resources.getString(R.string.title_tut_3),
                    R.color.colorWhile,
                    activity.resources.getString(R.string.body_tut_3),
                    R.color.colorWhile
                )
            ) {
                callback(it)
            }
        }
    }
}
```

- Có thể kiểm tra tải được dữ liệu cấu hình quảng cáo thành công hay chưa:

```kotlin
var isAdsReady = isAdVersionReady()
```

## III, Cài đặt hiển thị các vị trí quảng cáo trong App

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

- Các tiện ích sử dụng ở những gói sau, kiểm tra để tránh bị nhầm lẫn:

```kotlin
import app.module.utils.*
```

- Hiển thị ad không có `callback` dành cho các định dạng `Banner` `Native` để không cần phải kiểm
  tra lại trạng thái của quảng cáo

```kotlin
// adName: tên quảng cáo
// adViewContainer: view chứa quảng cáo là LinearLayout
loadAndShowAdInLayout(Activity.this, adName, adViewContainer)
```

- Mẫu khai báo ViewContainer chứa quảng cáo

```xml

<LinearLayout
  android:id="@+id/ll_top_banner"
  android:layout_width="match_parent"
  android:layout_height="wrap_content"
  android:orientation="vertical"
  tools:layout_height="10dp"
/>
```

- Hiển thị quảng cáo có `callback` sử dụng cho các định dạng `Interstitial` `Open Ad` `Reward` ...
  để có thể kiểm tra lại trạng thái của quảng cáo

  - Ví dụ sử dụng cho quảng cáo màn `Splash`

```kotlin

  taymayLoadAdShowCallback( // tự động tải và hiển thị quảng cáo
    Activity.this,
    "open_app", // tên quảng cáo
    LinearLayout(this) // view chứa quảng cáo là LinearLayout
  ) { myAd -> // callback có tham số là đối tượng `MyAd`
    when (myAd.adState) { // check trạng thái của quảng cáo hiện tại
      AdState.Close, AdState.Done -> goToMain()
      AdState.Show -> setContentView(LinearLayout(Taymay.AppContext))
      AdState.Timeout, AdState.Error -> goToMain()
      else -> {}
    }
  }
```

- Các trạng thái của quảng cáo

> `Init` : quảng cáo được khởi tạo

> `Loaded` : quảng cáo đã tải thành công

> `Timeout` : quảng cáo tải quá thời gian cho phép

> `Error` : quảng cáo bị lỗi

> `Show` : quảng cáo đã hiển thị

> `Close` : quảng cáo đã đóng

> `Done` : quảng cáo đã hoàn thành (Dành cho quảng cáo Reward)

> `AdClick` : quảng cáo được click

- Khởi tạo quảng cáo quay trờ lại `App (Open Ad)` sử dụng trong Activity

```kotlin
taymayInitReturnAppAd(Activity.this,"return_app") // tên quảng cáo
```

- Kiểm tra xem quảng cáo đã được tải trước chưa:

```kotlin
taymayIsAdLoaded("ad_name_here")
```

- Tải trước quảng cáo

```kotlin
application.loadAdsToCache( "ad_name_1","ad_name_2","ad_name_3",...)
```

- Sử dụng `Dialog` tải quảng cáo và sau khi tải xong thì tự động hiển thị quảng cáo chèn giữa và thực hiện
  công việc sau đó. (ví dụ: Mở một `Activity` mới)

```kotlin
taymayDialogLoadAndShowAdInterstitial(this, "ad_name") {// tên quảng cáo
    //thực thi lệnh sau khi quảng cáo đã đóng
}
```

- Sử dụng `AdInterActivity` tải quảng cáo và sau khi tải xong thì tự động hiển thị quảng cáo chèn giữa, đóng `AdInterActivity` thực hiện
  công việc sau đó. (ví dụ: Mở một `Activity` mới) - Trường hợp này không nên sử dụng với `Navigation` vì sẽ gây ra lỗi do khác `Context`:

```kotlin
/*
callback:
- ad: Là đối tượng MyAd
- activity: là activity chèn giữa tải quảng cáo
  */
taymayActivityLoadAndShowAdCallbackFor(this, "ad_name") { ad, activity ->

}
```

- Sử dụng `AdInterActivity` tải quảng cáo và sau khi tải xong thì tự động hiển thị quảng cáo chèn giữa và gọi callback gọi lại nhưng không tự động `destroy()` `AdInterActivity`. (ví dụ: Mở một `Activity` mới) - Trường hợp này không nên sử dụng với `Navigation` vì sẽ gây ra lỗi do khác `Context`:

```kotlin
/*
callback:
- ad: Là đối tượng MyAd
- activity: là activity chèn giữa tải quảng cáo
  */
taymayActivityLoadAndShowAdCallbackWaitFor(this, "ad_name") { ad, activity ->
  activity.let {
        if (it.isDestroyed) it.finish()
    }
}
```

- Hiển thị Dialog tải quảng cáo, sử dụng để chờ tải quảng cáo về và sử dụng

```kotlin
taymayShowDialogAdLoading(
    Activity.this,
        "adName", //Tên quảng cáo
    ) { isCanShow, // Quảng cáo có thể sử dụng được không
        myAd // Quảng cáo đã tải về
        -> run {
            // thực hiện lệnh sau khi đã tải quảng cáo về
        }
    }
```

- Bật/tắt quảng cáo chèn giữa có thể hiện hoặc không hiện lên một màn hình: (lưu ý sau khi set
  là `False` thì bạn nhớ set lại là `True` sau đó để quảng cáo có thể tiếp tục hiện)

```kotlin
taymaySetCanShowAd(true/false)
```

## IV, Quảng cáo màn hình Intro/Tutorial và Cấu hình cài đặt

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

Gói sử dụng `app.module.activities`

- Hiển thị màn hình Intro/Tutorial thông qua đối tượng`IntroPager`:

- Màn hình được khỏi tạo thông qua 2 phương thức `showIntroFromPages()`, `showIntroFromViews()` sử
  dụng với các mục đích khác nhau.

##### 1. `showIntroFromPages()` sử dụng các `IntroPager` để thiết lập các dữ liệu cho các `Page`

```kotlin
/**
  * Cho phép khởi tạo bằng các đối tượng `IntroPager`
  * isCanShowAd thể hiện đã show Intro và người dùng đã click Done thì có thể hiển thị được quảng cáo sau Intro trả về là true, còn nếu Intro đã hiển thị thì sẽ trả về false
  */
fun taymayShowIntroFromPages(
          context: Context,
          showType: ShowType, //tủy chọn hiển thị một lần hoặc nhiều lần
          ad_top_name: String,// tên vị trí quảng cáo
          ad_bottom_name: String,// Vị trí quảng cáo được sử dụng trong màn hình
          backgroundDrawable: Int, // background của màn hình
          vararg introPager: IntroPager, // các IntroPager
          runAfterDone: (isCanShowAd: Boolean) -> Unit //callback được gọi lại khi kết thúc màn
      )
```

```kotlin
enum class ShowType {
    Once, Multi
}

class IntroPager(
    var isAnim: Boolean, // true là sử dụng animation thông qua file .json false là sử dụng thông qua ảnh từ drawable
    var image: Int,// true: R.raw.anim_tut_01 or false:  R.drawable.im_tut_1
    var title: String, // title
    var titleColor: Int, // Màu của Title , ex: R.color.colorTitle
    var body: String, // Mô tả
    var bodyColor: Int // Màu của mô tả, ex: R.color.colorBody
)
```

- Ví dụ `showIntroFromPages()`:

```kotlin
 IntroActivity.showIntroFromPages(
                    context,
                    ShowType.Once,
                    "au:intro_top_small",
                    "au:intro_bottom_small",
                    R.drawable.bg_tut,
                    IntroPager(
                        false,
                        R.drawable.im_tut_1,
                        context.resources.getString(R.string.title_tut_1),
                        R.color.colorTu1,
                        context.resources.getString(R.string.body_tut_1),
                        R.color.colorBody1
                    ),
                    IntroPager(
                        false,
                        R.drawable.im_tut_2,
                        context.resources.getString(R.string.title_tut_2),
                        R.color.colorTut2,
                        context.resources.getString(R.string.body_tut_2),
                        R.color.colorBody2
                    ),
                    IntroPager(
                        false,
                        R.drawable.im_tut_3,
                        context.resources.getString(R.string.title_tut_3),
                        R.color.colorTut3,
                        context.resources.getString(R.string.body_tut_3),
                        R.color.colorBody3
                    )
                ) {
                    // Sau khi kết thúc màn Intro/Tutorial thì Callback sẽ được gọi
                }

```

##### 2. `showIntroFromViews()` sử dụng các `View` để thiết lập các dữ liệu `Page`

```kotlin

enum class ShowType {
    Once, Multi
}

fun showIntroFromViews(
    context: Context,
    showType: ShowType, //tủy chọn hiển thị một lần hoặc nhiều lần
    ad_top_name: String,
    ad_bottom_name: String,
    backgroundDrawable: Int, // background của màn hình
    vararg view: View, // Các View của mỗi Page
    runAfterDone: () -> Unit
) {

}
```

- Ví dụ `showIntroFromViews`:

```kotlin

fun getImageView(context: Context, resId: Int): ImageView {
          var imv: ImageView = ImageView(context)
          imv.setImageResource(resId)
          imv.scaleType = ImageView.ScaleType.CENTER_INSIDE
          return imv
}

IntroActivity.showIntroFromViews(
    context,
    ShowType.Once,
    "au:intro_top_small",
    "au:intro_bottom_small",
    R.drawable.bg_tut,
    getImageView(context, R.drawable.im_tut_1),
    getImageView(context, R.drawable.im_tut_2),
    getImageView(context, R.drawable.im_tut_3)
) {
  // Sau khi kết thúc màn Intro/Tutorial thì Callback sẽ được gọi
}
```

## V, Các tiện ích khác

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

- `MyFile` các phương thức tiện ích sử dụng với File trong gói `app.module.utils`

```
 MyFile....
```

- `MyConnection` các phương thức tiện ích sử dụng với HTTP trong gói `app.module.utils`

```
 MyConnection....
```

- `MyCache` Tiện ich Shared Preferences trong gói `app.module.utils`

```
    MyCache.get...
    MyCache.put...
```

- Lấy tên phiên bản và mã phiên bản:

```kotlin
app.module.utils.taymayGetAppVersionName(this) // tên phiên bản

app.module.utils.taymayGetAppVersionCode(this) // mã phiên bản
```

- Hiển thị Dialog xin Rate và Feedback từ lần thứ 2 vào lại App

```kotlin
taymayAskRateAndFeedbackNextSession(Activity.this) { // hiển thị dialog xin rate từ lần thứ 2 vào app
    // được gọi sau khi dialog xin rate tắt
}
```

- Hiển thị hỏi Dialog xin Rate và Reivew

```kotlin
taymayShowDialogRateAndFeedback(this@VaultActivity) {
// được gọi sau khi dialog xin rate tắt
}
```

- Mở màn hình Policy

```kotlin
taymayOpenPolicyActivity(Activity.this,"policy.html")// tên file ở Assets

```

- Hiển thị Dialog xóa quảng cáo

```kotlin
taymayShowDialogRemoveAd(Activity.this, "remove_ad,...,...", ,MainActivity::class.java) // tên của màn hình Activity được mở sau màn Splash, remove_ad là các mã id product cách nhau bằng `,`
```

- Hiển thị Dialog xin Feedback

```kotlin
taymayShowDialogFeedback(Activity.this) {  }
```

- Hiển thị Dialog kiểm tra bản cập nhật App trên Google Play

```kotlin
taymayShowDialogCheckAppOnStore(Activity.this)
```

- Kiểm tra User đã xóa quảng cáo hay chưa

```kotlin
if (taymayIsPayRemoveAd(Activity.this)) // true là đã xóa và ngược lại
activityAboutBinding.icGoogleByeAd.visibility =
    View.GONE

```

- Kiểm tra sẽ hiển thị màn cài đặt ngôn ngữ:

```kotlin
taymayIsWillShowLanguageSetup(activity) // true nếu màn cài ngôn ngữ chưa hiển thị lần nào, false là đã cài ngôn ngữ
```

- Kiểm tra và mở màn hình chọn cài Language lần đầu tiên:

```kotlin

taymayAskFirstSetupLanguage(Activity.this, "bottom_language"){
// Thực hiện lệnh tiếp theo nếu đã kiểm tra cài đặt ngôn ngữ hoặc ngôn ngữ đã được cài xong
}
```

- Mở màn hinh cài đặt ngôn ngữ, nếu cài đặt thành công thì sẽ restart lại App

```kotlin
taymayOpenSetLangActivity(this, "au:language_top_small", "language_bottom_medium") {
        startActivity(Intent(this, AppSplash::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
}
```

- Thêm Icon vào giao diện để mở màn hình More App

```xml
<com.airbnb.lottie.LottieAnimationView
    android:id="@+id/ic_cross"
    android:layout_width="?actionBarSize"
    android:layout_height="?actionBarSize"
    app:layout_constraintEnd_toEndOf="parent"
    app:layout_constraintStart_toStartOf="parent"
    app:layout_constraintTop_toTopOf="parent"
    app:lottie_autoPlay="true"
    app:lottie_loop="true"
    app:lottie_rawRes="@raw/anim_store_cross" />
```

- Cú pháp mở màn hình More App

```kotlin
taymayOpenMoreAppActivity(Activity.this)
```

- Cú pháp mở màn hình quảng cáo thoát App

```kotlin
taymayAskExitApp(this, "au:exit_app_medium", 1500) {
    it.finishAffinity() // Thoát ExitActivity
    finishAffinity() // Thoát App
}
```

- Theo dõi các sự kiện thông qua `Firebase`

[Chi tiết LogEvent](https://firebase.google.com/docs/analytics/events?platform=android)

```kotlin
// đơn giản để track một sự kiện
taymayFirebaseEventTracking(Activity.this,event ="home_click_button")
// nếu cần thêm các tham số kèm theo
taymayFirebaseEventTracking(Activity.this,event ="home_click_button" ,Pair("arg1",""), Pair("agr2",1))
```

- Bật/Tắt chế độ DebugView để kiểm tra lại việc gắn các sự kiện:

```shell
// Bật
adb shell setprop debug.firebase.analytics.app PACKAGE_NAME

// Tắt
adb shell setprop debug.firebase.analytics.app .none.

```

[Chi tiết hơn về DebugVew](https://firebase.google.com/docs/analytics/debugview#android)

- Theo dõi các màn hình thông qua `Firebase`

```kotlin
taymayFirebaseScreenTracking(Activity.this, screen_name, screen_class)
```

- Thêm vô `Manifest` nếu sử dụng `taymayFirebaseScreenTracking` để tắt tự động Log các màn hình mặc định:

```xml
<meta-data android:name="google_analytics_automatic_screen_reporting_enabled" android:value="false" />
```

[Chi tiết Measure screenviews](https://firebase.google.com/docs/analytics/screenviews)

- `Logger` giúp log các thông tin lên server nếu cần thêm dữ liệu từ phía `User`:

Trong gói `app.module.utils`

Sự dụng để có thể lấy thêm dữ liệu từ `User` và lưu chúng lên server mysql ở bảng `logger` theo
dạng `key:value` để với `key` là dạng string và `value` thì hỗ trợ các định dạng khác nhau

```kotlin

taymayLog(key, string_value) // log lên đoạn văn bản, với value có thể là string hoặc json string
taymayLog(key, long_value) // log giá trị `long`
taymayLog(key, double_value) // log giá trị `double`
taymayLog(key, string_value, long_value, double_value) //nếu log chứa nhiều giá trị khác nhau
```

Các `log` sẽ tự động lấy thêm thông tin kèm các thông tin từ các phương thức trên:

> `uid` : mã định định danh của user

> `from` : package app tên của gói

> `version` : phiên bản của ứng dựng định dạng `version_code` (`version_name`)

- Lấy thông tin về Quốc Gia thông qua địa chỉ IP của `User`

```kotlin
taymayGetGeoIP {
  it // là đối tượng  GeoIP
}
```

```kotlin
GeoIP(
    var country_code: String = "-",
    var country_name: String = "-",
    var city: String = "-",
    var postal: String = "-",
    var latitude: String = "-",
    var longitude: String = "-",
    var IPv4: String = "-",
    var state: String = "-"
)
```

- Lấy `UserID`: trả về id của user

```kotlin
taymayGetUserID(context)
```

- Tải một File băng URL

```kotlin

taymayDownloadFileFromUrl(
   url,
    file_out
) { b ->
}
```

- Mở `Activity` mới và clear tất cả các `Activity` ở Dưới:

```kotlin
  taymayClearStackAndGoActivity(ActivityA::class.java)
```

- `POST`,`GET` `json` từ `URL`:

```kotlin

taymayGetJsonFromUrlByKtor(url: String, defaultVault: String, callback: (res: String) -> Unit)

taymayPostJsonToUrlByKtor(
    server: String,
    json: String,
    defaultVault: String,
    callback: (res: String) -> Unit
)
```



## VI, Hệ thống cấu hình dữ liệu trong App

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

- Các thông tin cấu hình được lưu định dạng `json` và được lấy từ `Remote Configs` với key
  là `data_configs` hoặc là từ tập tin trong thư mục `assets` có tên tệp `data.json`

- Tệp `ads.json` có dạng

```json
[
  {
    "ad_id": "ca-app-pub-8474106102524491/8458557130",
    "ad_name": "splash",
    "ad_network": "admob",
    "ad_format": "interstitial",
    "ad_enable": true,
    "ad_index": 1,
    "ad_ctr": 10,
    "ad_impressions": 100,
    "ad_distance": 10,
    "ad_timeout": 10,
    "ad_tag": "",
    "ad_version": "default",
    "app_version_name": "1.0.1",
    "app_id": "com.taymay.haircut",
    "ad_update": "3/26/2022 5:05:21",
    "ad_reload": false,
    "ad_mediation": "",
    "ad_note": "",
    "open_bid": ""
  },
  ...
]

```

- Tệp `data.json` có dạng

```json
[
  {
    "app_id": <str>,
    "bool_value": <bool>,
    "data_version": <str>,
    "double_value": <double>,
    "key": <str>,
    "long_value": <long>,
    "string_value": <str>
  },
    ...
]
```

```kotlin
data class DataConfig(
    var app_id: String = "",
    var bool_value: Boolean = false,
    var key: String = "",
    var data_version: String = "",
    var double_value: Double = 0.0,
    var long_value: Long = 0,
    var string_value: String = ""
)
```

- Đối tượng `DataConfig` để cấu hình các loại dữ liệu khác nhau như `String`, `Boolean`
  , `Double` `Long` với các `key` khác nhau và được cấu hình dự trên các `data_version` để tiện
  trong quá trình AB Test các tính năng trong App

- Các phương thức sử dụng

```kotlin

// lấy giá trị String từ 1 key
var dataString = taymayGetDataString("<tên key>", "<giá trị mặc định>")

// lấy giá trị Boolean từ 1 key
var dataBoolean =  taymayGetDataBoolean("<tên key>", <giá trị mặc định>)

// lấy giá trị Double từ 1 key
var dataDouble = taymayGetDataDouble("<tên key>", <giá trị mặc định>)

// lấy giá trị Long từ 1 key
var dataLong = taymayGetDataLong("<tên key>", <giá trị mặc định>)

```

- Hệ thống sẽ tùy chỉnh các `data_version` tự động trên server tuy nhiên khi `Debug` thì có thể tùy
  chỉnh lựa chọn các phiên bản để cho việc kiểm thử

  - Đối với `Data Configs` sử dụng thuộc tính `DATA_CONFIG_VERSION_DEFAULT` để thay
    đổi `data_version` cho phiên bản chạy. (Mặc định bản `release` sẽ tự động lấy các giá trị sẽ
    đổi thành `default`)
  - Đối với `Ads Configs` sử dụng thuộc tính `AD_CONFIG_VERSION_DEFAULT` để thay đổi `ad_version`
    cho phiên bản chạy. (Mặc định bản `release` sẽ tự động lấy các giá trị sẽ đổi thành `default`)

- Ví dụ muốn test data 2 phiên bản `default` và `A` bạn có thể thay đổi để test phiên bản với dữ
  liệu cấu hình phiên bản `A`:

```kotlin
DATA_CONFIG_VERSION_DEFAULT="A"
```

- Tương tự như `Ads Configs`:

```kotlin
AD_CONFIG_VERSION_DEFAULT="A"
```

[[Lên Tốp]](#top) [[Xuống Cuối]](#bottom)

## VII, Hệ thống In App Purchase (Thanh toán trong App)

- Lưu ý mặc định các App đều phải có giá trị `iap` đã cấu hình trong `Data Configs` để sử dụng được
  tính năng `IAP`.

- Sử dụng `Data Configs` để cấu hình iap với `key` là `iap` và cú pháp của giá trị
  là `"id_product_a,id_product_b,id_product_c,...."`, mỗi giá trị phân tách nhau bằng dấu `,` hỗ trợ
  cấu hình nhiều `product_id` khác nhau để hỗ trợ trong việc AB Test Giá, mặc định sẽ
  chọn `product_id` đầu tiên.

<p id="bottom"/>
