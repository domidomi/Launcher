package dominika.launcher;

import android.graphics.Bitmap;
import android.view.View;

/**
 * Created by Domi on 22.01.2017.
 */

public class FragmentsBackgroundEffects {

    public FragmentsBackgroundEffects() {
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
        return croppedBitmap;
    }
}
