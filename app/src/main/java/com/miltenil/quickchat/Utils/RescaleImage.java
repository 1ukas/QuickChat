package com.miltenil.quickchat.Utils;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;

public class RescaleImage {

    public byte[] decodeResource(byte[] imageByteArray, int DESIREDWIDTH, int DESIREDHEIGHT) {
        Bitmap scaledBitmap = null;
        ByteArrayOutputStream baos = null;

        try {
            Bitmap unscaledBitmap = ScalingUtilities.decodeByteArray(imageByteArray, DESIREDWIDTH, DESIREDHEIGHT, ScalingUtilities.ScalingLogic.FIT);

            if (!(unscaledBitmap.getWidth() <= DESIREDWIDTH && unscaledBitmap.getHeight() <= DESIREDHEIGHT)) {
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
