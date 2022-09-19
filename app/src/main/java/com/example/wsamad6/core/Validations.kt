package com.example.wsamad6.core

import java.util.regex.Pattern

object Validations {

    fun validateEmail(s: String?): Boolean {
        val regex = Pattern.compile("^([a-zA-Z]{1,}@wsa[.]com)")
        return if (s.isNullOrEmpty()) {
            false
        } else regex.matcher(s).matches()
    }

    fun validatePassword(s: String?): Boolean {
        val regex = Pattern.compile("^([0-9]{1,5})")
        return if (s.isNullOrEmpty()) {
            false
        } else regex.matcher(s).matches()
    }

}