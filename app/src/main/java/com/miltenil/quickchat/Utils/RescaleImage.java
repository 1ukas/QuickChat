package com.miltenil.quickchat.Utils;

import android.content.res.Resources;
import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class RescaleImage {

    public byte[] decodeResource(byte[] imageByteArray, int DESIREDWIDTH, int DESIREDHEIGHT) {
        Bitmap scaledBitmap = null;
        ByteArrayOutputStream baos = null;

        try {
            // Part 1: Decode image
            Bitmap unscaledBitmap = ScalingUtilities.decodeByteArray(imageByteArray, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
                // Part 2: Scale image
                scaledBitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);
            }
            else {
                unscaledBitmap.recycle();
                return imageByteArray;
            }

            try {
                baos = new ByteArrayOutputStream();
                scaledBitmap.compress(Bitmap.CompressFormat.PNG, 75, baos);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            scaledBitmap.recycle();
        }
        catch (Throwable e) {
        }
        return baos.toByteArray();
    }
}
