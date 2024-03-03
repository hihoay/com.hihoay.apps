package app.module.enums

enum class AdState(private val text: String) {
    Init("Init"),
    Loaded("Loaded"),
    Cached("Cached"),
    Timeout("Timeout"),
    Error("Error"),
    Show("Show"),
    Close("Close"),
    Done("Done"),
    AdClick("AdClick");

    override fun toString(): String {
        return text
    }

    fun toText(): String {
        return text
    }
}