package com.cy.literouter.uitl

import android.content.Context
import android.widget.Toast
import com.cy.literouter.LiteRouter

fun toast(msg: String) {
    val context = LiteRouter.getContext()
    context?.toast(msg)
}

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}