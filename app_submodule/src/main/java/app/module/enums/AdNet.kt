package app.module.enums

enum class AdNet(private val text: String) {
    //    Facebook("facebook"),
    Admob("admob"),  //    Unity("unity"),

    //    Applovin("applovin"),
    Netlink("netlink"), Pangle("pangle"), Cross("cross"), Null("null");

    override fun toString(): String {
        return text
    }

    fun toText(): String {
        return text
    }

}