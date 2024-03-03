package app.module.lang

import java.io.Serializable

object Compass {
    val SHOWTUTORIAL = "SHOWTUTORIAL"
    val PERMISS = "PERMISS"
}

class Language(
    var country_name: String,
    var country_code: String,
    var flag: String,
    var lang_name: String,
    var lang_code: String,
    var flag_svg: String,
    var local: String,
    var region: String,

    ) : Obase()

open class Obase : JSONConvertable, Serializable


object CompassObject {
    var language = Language("", "", "", "", "", "", "", "")
}
