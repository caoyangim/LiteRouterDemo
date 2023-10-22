package com.cy.literouter.uitl;

import static com.cy.literouter.uitl.ExtensionsKt.toast;

import android.util.Log;

import com.cy.literouter.LiteRouter;

/**
 * @author Duckbb
 * @description TODO
 * @date 2023年10月19日 23:02
 */
public class LogHelper {

    private static final int LOG_NORMAL = 3;
    private static final int LOG_ERROR = 5;

    public static void tip(String tag, String msg) {
        log(tag, msg, LOG_NORMAL);
    }

    public static void errorTip(String tag, String msg) {
        LogHelper.log(tag, msg, LOG_ERROR);
    }

    public static void log(String tag, String msg, int level) {
        if (!LiteRouter.INSTANCE.isDebug()) {
            return;
        }
        if (level >= LOG_ERROR) {
            toast(msg);
            Log.e(tag, msg);
            return;
        }
        Log.d(tag, msg);
    }
}
