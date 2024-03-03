package app.module.enums

enum class AdFormat(private val text: String) {
    Banner("banner"), Native("native"), Reward("reward"), Interstitial("interstitial"), OpenAd("open_ad"), Null(
        "null"
    ),
    Interstitial_Reward("interstitial_reward");

    fun toText(): String {
        return text
    }

    override fun toString(): String {
        return text
    }
}