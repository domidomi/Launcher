package dominika.launcher.DateTimeTemperature;

import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import dominika.launcher.R;

import static android.os.Looper.getMainLooper;

/**
 * Created by Domi on 23.01.2017.
 */

public class DateChangeListener {
    final Calendar calendar = Calendar.getInstance();
    final Handler mHandler = new Handler();
    boolean runDateListener = true; //set it to false if you want to stop the listener
    View view;

    public DateChangeListener(View view) {
        this.view = view;
        setDate();
    }

    public void setDate() {
        Calendar c = Calendar.getInstance();
        int day = c.get(Calendar.DAY_OF_MONTH);
        int month = c.get(Calendar.MONTH)+1;
        String day_in_week = getDayName(c.get(Calendar.DAY_OF_WEEK));
        TextView mDateTextView = (TextView) view.findViewById(R.id.date_textView);
        mDateTextView.setText(String.valueOf(day) + "." + String.valueOf(month) + ", " + day_in_week);
    }

    public void timer() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (runDateListener) {
                    try {
                        Thread.sleep(100000);
                        mHandler.post(new Runnable() {

                            @Override
                            public void run() {
                                setDate();
                            }
                        });
                    } catch (Exception e) {
                    }
                }
            }
        }).start();}


    public static String getDayName(int day){
        switch(day){
            case 0:
                return  "Sob.";
            case 1:
                return "Nd.";
            case 2:
                return "Pon.";
            case 3:
                return "Wt.";
            case 4:
                return "Śr.";
            case 5:
                return "Czw.";
            case 6:
                return "Pt.";
            case 7:
                return  "Sob.";
        }

        return "";
    }


    public void stopListening() {
        runDateListener = false;
    }

    public void startListening() {
        runDateListener = true;
    }

}
