package com.londonx.li.util

import splitties.preferences.Preferences

object UserDefaults : Preferences("user_defaults") {
    var receiver by stringOrNullPref()
    var payer by stringOrNullPref()
    var item by stringOrNullPref()
    var rawAmount by stringOrNullPref()
    var signatureFile by stringOrNullPref()
}