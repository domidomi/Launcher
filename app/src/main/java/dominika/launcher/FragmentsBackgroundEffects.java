package dominika.launcher;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;

/**
 * Created by Domi on 22.01.2017.
 */

public class FragmentsBackgroundEffects {

    Context context;
    RenderScript renderScript;

    public FragmentsBackgroundEffects(Context context) {
        this.context = context;
        renderScript = RenderScript.create(context);
    }

    public Bitmap getScreenShot(View view) {
        View screenView = view.getRootView();
        screenView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(screenView.getDrawingCache());
        screenView.setDrawingCacheEnabled(false);
        return bitmap;
    }

    public Bitmap cropBitmap(Bitmap bitmap, Integer marginTop, Integer marginBottom) {
        Integer margin = marginTop + marginBottom;
        Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, 0, marginTop, bitmap.getWidth(), bitmap.getHeight() - margin);
        croppedBitmap = blurBitmap(croppedBitmap);
        return croppedBitmap;
    }

    public Bitmap blurBitmap(Bitmap bitmapOriginal) {
        //this will blur the bitmapOriginal with a radius of 8 and save it in bitmapOriginal
        final Allocation input = Allocation.createFromBitmap(renderScript, bitmapOriginal); //use this constructor for best performance, because it uses USAGE_SHARED mode which reuses memory
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        script.setRadius(16f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(bitmapOriginal);

        return bitmapOriginal;
    }
}
