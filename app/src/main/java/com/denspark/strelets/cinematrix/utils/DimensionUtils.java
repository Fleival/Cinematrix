package com.denspark.strelets.cinematrix.utils;

import android.content.Context;

public class DimensionUtils {

    private static boolean isInitialised = false;
    private static float pixelsPerOneDp;

    private static void initialise(Context context) {
        pixelsPerOneDp = context.getResources().getDisplayMetrics().densityDpi / 160f;
        isInitialised = true;
    }

    public static int pxToDp(Context context, int px) {
        if (!isInitialised) {
            initialise(context);
        }
        return (int) (px / pixelsPerOneDp);
    }

    public static int dpToPx(Context context, float dp) {
        if (!isInitialised) {
            initialise(context);
        }
        return (int) (dp * pixelsPerOneDp);
    }
}
