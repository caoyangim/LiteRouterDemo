package com.cy.literouterdemo

import android.app.Application
import com.cy.literouter.LiteRouter

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        LiteRouter.init(this, true)
    }
}