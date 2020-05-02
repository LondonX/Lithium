package com.londonx.li.view

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import com.github.gcacace.signaturepad.views.SignaturePad

class SafeSignaturePad(context: Context?, attrs: AttributeSet?) : SignaturePad(context, attrs) {
    override fun onSaveInstanceState(): Parcelable? {
        // return super.onSaveInstanceState()
        super.onSaveInstanceState()
        return Bundle()

    }
}