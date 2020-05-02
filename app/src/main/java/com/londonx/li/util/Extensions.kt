package com.londonx.li.util

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Parcelable
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.Serializable
import java.security.MessageDigest

/**
 * 数字金额大写转换
 * 要用到正则表达式
 */
fun digitUppercase(money: String): String {
    if (money.isBlank()) {
        return ""
    }
    if (money.toDouble() == 0.0) return "零圆整"
    val fraction = arrayOf("角", "分")
    val digit =
        arrayOf("零", "壹", "贰", "叁", "肆", "伍", "陆", "柒", "捌", "玖")
    val unit = arrayOf(
        arrayOf("圆", "万", "亿"),
        arrayOf("", "拾", "佰", "仟")
    )
    val numArray = money.split(".").toTypedArray()
    var amountInWords = ""
    var integerPart = money.toDouble().toInt()
    var i = 0
    while (i < unit[0].size && integerPart > 0) {
        var temp = ""
        val tempNum = integerPart % 10000
        if (tempNum != 0 || i == 0) {
            var j = 0
            while (j < unit[1].size && integerPart > 0) {
                temp = digit[integerPart % 10] + unit[1][j] + temp
                integerPart /= 10
                j++
            }
            /*
                 *正则替换，加上单位
                 *把零佰零仟这种去掉，再去掉多余的零
                 */amountInWords = temp.replace("(零.)+".toRegex(), "零").replace("^$".toRegex(), "零")
                .replace("(零零)+".toRegex(), "零") + unit[0][i] + amountInWords
        } else {
            integerPart /= 10000
            temp = "零"
            amountInWords = temp + amountInWords
        }
        amountInWords =
            amountInWords.replace("零" + unit[0][i], unit[0][i] + "零")
        if (i > 0) amountInWords = amountInWords.replace(
            "零" + unit[0][i - 1],
            unit[0][i - 1] + "零"
        )
        i++
    }
    var fWordsStr = ""
    if (numArray.size > 1) {
        val fStr = numArray[1]
        val iLen = if (fraction.size < fStr.length) fraction.size else fStr.length
        for (ii in 0 until iLen) {
            val numInt = fStr.substring(ii, ii + 1).toInt()
            if (numInt == 0) continue
            if (amountInWords.isNotBlank() && fWordsStr.isBlank() && ii > 0) {
                fWordsStr = "零"
            }
            fWordsStr += digit[numInt] + fraction[ii]
        }
    }
    if (fWordsStr.isBlank()) fWordsStr = "整"
    amountInWords += fWordsStr
    amountInWords = amountInWords.replace("(零零)+".toRegex(), "零").replace("零整", "整")
    return amountInWords
}

fun Bitmap.compressToFile(
    context: Context,
    fmt: Bitmap.CompressFormat = Bitmap.CompressFormat.PNG,
    file: File? = null
): File {
    val extension = when (fmt) {
        Bitmap.CompressFormat.JPEG -> "jpg"
        Bitmap.CompressFormat.PNG -> "png"
        Bitmap.CompressFormat.WEBP -> "webp"
    }
    val bos = ByteArrayOutputStream()
    this.compress(fmt, 100, bos)
    val bytes = bos.toByteArray()

    val actualFile = if (file == null) {
        val md5 = bytes.md5
        var f = File(context.filesDir, "$md5.$extension")
        var retry = 1
        while (f.exists()) {
            f = File(context.filesDir, "${md5}_$retry.$extension")
            retry++
        }
        f
    } else {
        file
    }
    actualFile.writeBytes(bytes)
    return actualFile
}

val ByteArray.md5: String
    get() {
        return MessageDigest
            .getInstance("MD5")
            .digest(this)
            .joinToString("") { "%02x".format(it) }
    }

inline fun <reified T> Context.startActivity(vararg params: Pair<String, *>) {
    val intent = assembleIntent(*params)
    intent.setClass(this, T::class.java)
    startActivity(intent)
}

fun assembleIntent(vararg params: Pair<String, *>): Intent {
    val intent = Intent()
    params.forEach {
        when (it.second) {
            null -> return@forEach
            is Bundle -> intent.putExtra(it.first, it.second as Bundle)
            is Parcelable -> intent.putExtra(it.first, it.second as Parcelable)
            is Serializable -> intent.putExtra(it.first, it.second as Serializable)
            is Array<*> -> intent.putExtra(it.first, it.second as Array<*>)
            is Boolean -> intent.putExtra(it.first, it.second as Boolean)
            is BooleanArray -> intent.putExtra(it.first, it.second as BooleanArray)
            is Byte -> intent.putExtra(it.first, it.second as Byte)
            is ByteArray -> intent.putExtra(it.first, it.second as ByteArray)
            is Char -> intent.putExtra(it.first, it.second as Char)
            is CharArray -> intent.putExtra(it.first, it.second as CharArray)
            is CharSequence -> intent.putExtra(it.first, it.second as CharSequence)
            is Double -> intent.putExtra(it.first, it.second as Double)
            is DoubleArray -> intent.putExtra(it.first, it.second as DoubleArray)
            is Float -> intent.putExtra(it.first, it.second as Float)
            is FloatArray -> intent.putExtra(it.first, it.second as FloatArray)
            is Int -> intent.putExtra(it.first, it.second as Int)
            is IntArray -> intent.putExtra(it.first, it.second as IntArray)
            is Long -> intent.putExtra(it.first, it.second as Long)
            is LongArray -> intent.putExtra(it.first, it.second as LongArray)
            is Short -> intent.putExtra(it.first, it.second as Short)
            is ShortArray -> intent.putExtra(it.first, it.second as ShortArray)
            is String -> intent.putExtra(it.first, it.second as String)
            else -> throw IllegalArgumentException("Context#startActivity with illegal param '${it.first}' value ${it.second}")
        }
    }
    return intent
}