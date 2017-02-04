package dominika.launcher.LastContacts;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import dominika.launcher.R;
import dominika.launcher.TwoFragment;

/**
 * Created by Domi on 03.02.2017.
 */

public final class CallManager {

    private CallManager() { // private constructor

    }

    public static void getListOfCalls(Activity activity, View view, Context context) {

        List<Call> listOfCalls = new ArrayList<Call>();
        Call objCall;

        // Get all messages - sent and received
        Uri allCalls = Uri.parse("content://call_log/calls");

        /*String num= c.getString(c.getColumnIndex(CallLog.Calls.NUMBER));// for  number
        String name= c.getString(c.getColumnIndex(CallLog.Calls.CACHED_NAME));// for name
        String duration = c.getString(c.getColumnIndex(CallLog.Calls.DURATION));// for duration

*/
        /*Cursor cursor = activity.getContentResolver().query(allCalls,
                new String[]{"DISTINCT CallLog.Calls.NUMBER", "CallLog.Calls.CACHED_NAME", "CallLog.Calls.DURATION", "CallLog.Calls.DATE"}, //DISTINCT
                "CallLog.Calls.NUMBER IS NOT NULL) GROUP BY (CallLog.Calls.NUMBER", //GROUP BY
                null, "date desc limit 3");*/

        /*Cursor cursor = activity.getContentResolver().query(allCalls,
                new String[]{"DISTINCT CallLog.Calls.NUMBER", "CallLog.Calls.CACHED_NAME", "CallLog.Calls.DURATION", "CallLog.Calls.DATE"}, //DISTINCT
                "CallLog.Calls.NUMBER IS NOT NULL) GROUP BY (CallLog.Calls.NUMBER", //GROUP BY
                null, "date desc limit 3");
*/
        Cursor cursor = activity.getContentResolver().query(allCalls, new String[]{"number", "name", "date", "duration"}, null, null, "date desc");


        //context.getContentResolver().query(allCalls, null, null, null, CallLog.Calls.NUMBER + "," + CallLog.Calls.CACHED_NAME);

        int totalCalls = 0;
        if (cursor != null) {
            totalCalls = cursor.getCount();
        }

        List<Call> tmp = new ArrayList<Call>();

        Log.d("Ile połączeń", Integer.toString(totalCalls));

        if (totalCalls == 0) {
            listOfCalls = new ArrayList<Call>();
        } else {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < totalCalls; i++) {
                    if (tmp.size() < 3) {

                        Log.d("Sprawdzam połączenie ", Integer.toString(i));
                        objCall = new Call();
                        objCall.setNumber(cursor.getString(cursor
                                .getColumnIndexOrThrow("number")));
                        objCall.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                        objCall.setDate(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                        objCall.setDate(convertDate(objCall.getDate(), "HH:mm dd/MM"));
                        objCall.setDuration(cursor.getString(cursor.getColumnIndexOrThrow("duration")));

                        boolean contains = false;
                        for (int j = 0; j < tmp.size(); j++) {
                            if (tmp.get(j).getNumber().equals(objCall.getNumber()) || tmp.get(j).getName().equals(objCall.getName())) {
                                Log.d("Już zawiera ten numer: ", objCall.getNumber());
                                contains = true;
                            }
                        }
                        if (!contains) {
                            Log.d("Nie zawiera Tego numeru", objCall.getNumber());
                            tmp.add(objCall);
                        }

                        cursor.moveToNext();
                    } else {
                        break;
                    }
                }
            } else {
                listOfCalls = new ArrayList<Call>();
            }
        }


        listOfCalls = tmp;

        if (cursor != null) {
            cursor.close();
        }

        setListOfCallsView(listOfCalls, view, context);
    }

    public static String convertDate(String dateInMilliseconds, String dateFormat) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return df.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static void setListOfCallsView(final List<Call> listOfCalls, View view, final Context context) {
        if (view != null) {

            // Find the ScrollView
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.last_contacts_view);

            RelativeLayout layouts[] = new RelativeLayout[3];

            RelativeLayout call1 = (RelativeLayout) scrollView.findViewById(R.id.call1);
            RelativeLayout call2 = (RelativeLayout) scrollView.findViewById(R.id.call2);
            RelativeLayout call3 = (RelativeLayout) scrollView.findViewById(R.id.call3);

            layouts[0] = call1;
            layouts[1] = call2;
            layouts[2] = call3;


            call1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listOfCalls.get(0).getNumber()));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    } else {
                        context.startActivity(intent);
                    }
                }
            });

            call2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listOfCalls.get(1).getNumber()));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    } else {
                        context.startActivity(intent);
                    }
                }

            });

            call3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + listOfCalls.get(2).getNumber()));
                    if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.

                        return;
                    } else {
                        context.startActivity(intent);
                    }
                }

            });


            if (listOfCalls.size() == 0) {
                for (int i = 0; i < 3; i++) {
                    layouts[i].setVisibility(View.GONE);
                }
            } else {
                for (int i = 0; i < listOfCalls.size(); i++) {
                    layouts[i].setVisibility(View.VISIBLE);
                    Call call = listOfCalls.get(i);
                    TextView contactName = (TextView) layouts[i].findViewById(R.id.contact_name);
                    TextView contactDate = (TextView) layouts[i].findViewById(R.id.contact_date);
                    TextView contactMessage = (TextView) layouts[i].findViewById(R.id.contact_duration);

                    contactName.setText(call.getName());
                    contactDate.setText(call.getDate());
                    contactMessage.setText("rozmowa, "+ call.getDuration());
                }
            }

            // Display the view
            view.invalidate();
        }
        TwoFragment.setListOfCalls(listOfCalls);
    }

}
