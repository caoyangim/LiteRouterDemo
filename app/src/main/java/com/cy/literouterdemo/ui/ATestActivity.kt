package com.cy.literouterdemo.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cy.literouter.anno.Router
import com.cy.literouterdemo.ui.ATestActivity.Companion.PATH_TEST_A

@Router(PATH_TEST_A)
class ATestActivity : AppCompatActivity() {

    companion object {
        const val PATH_TEST_A = "/test/A"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "This is A Test Activity"
        setContentView(LinearLayout(this).apply {

            addView(tv)
        })
    }
}