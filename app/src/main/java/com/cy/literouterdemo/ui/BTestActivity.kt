package com.cy.literouterdemo.ui

import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.cy.literouter.anno.Router
import com.cy.literouterdemo.ui.BTestActivity.Companion.PATH_TEST_B

@Router(PATH_TEST_B)
class BTestActivity : AppCompatActivity() {

    companion object {
        const val PATH_TEST_B = "/test/B"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "This is B Test Activity"
        setContentView(LinearLayout(this).apply {

            addView(tv)
        })
    }
}