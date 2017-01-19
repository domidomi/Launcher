package dominika.launcher.SharedPreferencesHelper;

/**
 * Created by Domi on 19.01.2017.
 */

public class SharedPreferencesAppModel {

    private String mAppLabel;
    private String mCategory;

    public SharedPreferencesAppModel(String mAppLabel, String mCategory) {
        this.mAppLabel = mAppLabel;
        this.mCategory = mCategory;
    }

    public String getmAppLabel() {
        return mAppLabel;
    }

    public void setmAppLabel(String mAppLabel) {
        this.mAppLabel = mAppLabel;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }
}
