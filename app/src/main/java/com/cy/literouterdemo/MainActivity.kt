package com.cy.literouterdemo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.cy.literouter.LiteRouter
import com.cy.literouter.anno.Router
import com.cy.literouterdemo.ui.ATestActivity.Companion.PATH_TEST_A

@Router("/main")
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_jump_test_a).setOnClickListener {
            LiteRouter.routeTo(this, PATH_TEST_A)
        }
    }
}