package com.londonx.li

import android.content.Context
import android.os.Bundle
import android.print.PrintManager
import android.text.format.DateFormat
import androidx.appcompat.app.AppCompatActivity
import com.londonx.li.util.digitUppercase
import kotlinx.android.synthetic.main.activity_preview.*
import splitties.dimensions.dp

class PreviewActivity : AppCompatActivity() {
    private val receiver by lazy { intent.getStringExtra("receiver") }
    private val payer by lazy { intent.getStringExtra("payer") }
    private val item by lazy { intent.getStringExtra("item") }
    private val amount by lazy { intent.getStringExtra("amount") }
    private val signatureFile by lazy { intent.getStringExtra("signatureFile") }
    private val isDraft by lazy { intent.getBooleanExtra("isDraft", true) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preview)

        val viewWidth = resources.displayMetrics.widthPixels - dp(32)//左右margin
        web.setInitialScale((viewWidth / 820f * 100).toInt())
        web.settings.allowFileAccess = true
        web.settings.textZoom = 100

        val html = String(assets.open("template.html").readBytes())
            .replace("\${payer}", payer)
            .replace("\${item}", item)
            .replace("\${cappedAmount}", digitUppercase(amount))
            .replace("\${rawAmount}", amount)
            .replace("\${receiver}", receiver)
            .replace("\${signature}", "file://$signatureFile")
            .replace(
                "\${date}",
                DateFormat.format("yyyy/MM/dd", System.currentTimeMillis()).toString()
            )
            .replace("\${draftVisibility}", if (isDraft) "visible" else "hidden")

        web.loadDataWithBaseURL(null, html, "text/html", "utf-8", null)

        viewPrint.setOnClickListener {
            val docName = "收据${DateFormat.format("yyyyMMddHHmmss", System.currentTimeMillis())}.pdf"
            val adapter = web.createPrintDocumentAdapter(docName)
            val printManager =
                this@PreviewActivity.getSystemService(Context.PRINT_SERVICE) as PrintManager
            printManager.print(docName, adapter, null)
        }
    }
}
