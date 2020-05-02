package com.londonx.li

import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import com.londonx.li.util.UserDefaults
import com.londonx.li.util.compressToFile
import com.londonx.li.util.digitUppercase
import com.londonx.li.util.startActivity
import kotlinx.android.synthetic.main.activity_main.*
import splitties.views.onClick
import java.io.File

class MainActivity : AppCompatActivity() {
    private val lastSavedSign by lazy {
        UserDefaults.signatureFile?.let { File(it) }?.takeIf { it.exists() }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                tilAmount.helperText = digitUppercase(etAmount.text?.toString() ?: "")
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
        viewClear.onClick { signaturePad.clear() }
        viewPreview.onClick { saveAndPreview() }

        etReceiver.setText(UserDefaults.receiver)
        etPayer.setText(UserDefaults.payer)
        etItem.setText(UserDefaults.item)
        etAmount.setText(UserDefaults.rawAmount)
        lastSavedSign?.also {
            signaturePad.signatureBitmap = BitmapFactory.decodeFile(it.absolutePath)
        }
    }

    private fun saveAndPreview() {
        lastSavedSign?.delete()
        val receiver = etReceiver.text?.toString()
        val payer = etPayer.text?.toString()
        val item = etItem.text?.toString()
        val amount = etAmount.text?.toString()

        tilReceiver.error = ""
        tilPayer.error = ""
        tilAmount.error = ""
        if (receiver.isNullOrBlank()) {
            tilReceiver.error = "此项必填"
            return
        }
        if (payer.isNullOrBlank()) {
            tilPayer.error = "此项必填"
            return
        }
        if (item.isNullOrBlank()) {
            tilItem.error = "此项必填"
            return
        }
        if (amount.isNullOrBlank()) {
            tilAmount.error = "此项必填"
            return
        }

        val signatureFile =
            signaturePad.transparentSignatureBitmap.compressToFile(this).absolutePath

        UserDefaults.receiver = receiver
        UserDefaults.payer = payer
        UserDefaults.item = item
        UserDefaults.rawAmount = amount
        UserDefaults.signatureFile = signatureFile

        startActivity<PreviewActivity>(
            "receiver" to receiver,
            "payer" to payer,
            "item" to item,
            "amount" to amount,
            "signatureFile" to signatureFile,
            "isDraft" to cbDraft.isChecked
        )
    }
}
