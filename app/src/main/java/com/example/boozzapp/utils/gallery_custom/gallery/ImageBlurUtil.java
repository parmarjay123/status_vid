package com.example.boozzapp.utils.gallery_custom.gallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

public class ImageBlurUtil {

    public static Bitmap applyGaussianBlur(Context context, Bitmap inputBitmap, float radius) {
        Bitmap blurredBitmap = Bitmap.createBitmap(
                inputBitmap.getWidth(), inputBitmap.getHeight(), Bitmap.Config.ARGB_8888
        );

        RenderScript renderScript = RenderScript.create(context);
        Allocation inputAllocation = Allocation.createFromBitmap(renderScript, inputBitmap);
        Allocation outputAllocation = Allocation.createFromBitmap(renderScript, blurredBitmap);

        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(inputAllocation);
        scriptIntrinsicBlur.setRadius(radius);
        scriptIntrinsicBlur.forEach(outputAllocation);

        outputAllocation.copyTo(blurredBitmap);

        inputAllocation.destroy();
        outputAllocation.destroy();
        scriptIntrinsicBlur.destroy();
        renderScript.destroy();

        return blurredBitmap;
    }
}
