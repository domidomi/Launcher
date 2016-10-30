package dominika.launcher.AllAppsGrid;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import dominika.launcher.R;

/**
 * Created by Domi on 28.10.2016.
 *
 * Adapter for displaying icons and names for each app.
 */

public class AppListAdapter extends ArrayAdapter<AppModel> {

    private final LayoutInflater mLayoutInflater;

    // Simple_list_item_2 contains a TwoLineListItem containing two TextViews.
    public AppListAdapter(Context context) {
        super(context, android.R.layout.simple_list_item_2);

        mLayoutInflater = LayoutInflater.from(context);
    }

    public void setData(ArrayList<AppModel> appsList) {
        clear();
        if (appsList != null) {
            addAll(appsList);
        }
    }



    //If the platform version supports it add all items in one step, otherwise add in loop
    @Override
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void addAll(Collection<? extends AppModel> appsCollection) {
        // If internal version of system is higher or equal to lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            super.addAll(appsCollection);
        }else{
            for(AppModel app: appsCollection){
                super.add(app);
            }
        }
    }

    /**
     * Put new apps items in the list.
     */
    @Override public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            view = mLayoutInflater.inflate(R.layout.list_item_icon_text, parent, false);
        } else {
            view = convertView;
        }

        AppModel item = getItem(position);
        ((ImageView)view.findViewById(R.id.icon)).setImageDrawable(item.getIcon());
        ((TextView)view.findViewById(R.id.text)).setText(item.getLabel());

        return view;
    }

}
