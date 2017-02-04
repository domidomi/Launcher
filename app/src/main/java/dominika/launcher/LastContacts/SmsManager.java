package dominika.launcher.LastContacts;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.LinearLayout;
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

public final class SmsManager {

    private SmsManager () { // private constructor

    }

    public static void getListOfSms(Activity activity, View view, Context context) {

        List<Sms> listOfSms = new ArrayList<Sms>();
        Sms objSms;

        // Get all messages - sent and received
        Uri message = Uri.parse("content://sms/");

        Cursor cursor = activity.getContentResolver().query(message,
                new String[]{"DISTINCT address","body", "date"}, //DISTINCT
                "address IS NOT NULL) GROUP BY (address", //GROUP BY
                null, "date desc limit 3");

        int totalSMS = 0;
        if (cursor != null) {
            totalSMS = cursor.getCount();
        }

        if (totalSMS == 0) {
            listOfSms = new ArrayList<Sms>();
        } else {
            if (cursor.moveToFirst()) {
                for (int i = 0; i < totalSMS; i++) {

                    objSms = new Sms();
                    objSms.setAddress(cursor.getString(cursor
                            .getColumnIndexOrThrow("address")));
                    objSms.setMsg(cursor.getString(cursor.getColumnIndexOrThrow("body")));
                    objSms.setTime(cursor.getString(cursor.getColumnIndexOrThrow("date")));
                    objSms.setTime(convertDate(objSms.getTime(),"HH:mm dd/MM"));
                    objSms.setPerson(getContactName(objSms.getAddress(), context));

                    listOfSms.add(objSms);
                    cursor.moveToNext();
                }
            } else {
                listOfSms = new ArrayList<Sms>();
            }
        }


        // else {
        // throw new RuntimeException("You have no SMS");
        // }
        cursor.close();

        setListOfSmsView(listOfSms, view, context);

        /*setLastSmsTextView(listOfSms.get(0).getMsg() + "; " + listOfSms.get(0).getId() + "; " + listOfSms.get(0).getAddress());*/


        /*String msgData = "";
        if (cursor != null) {
            try {

                if (cursor.moveToFirst()) { // must check the result to prevent exception
                    do {
                        for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                            msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                        }
                        // use msgData
                        setLastSmsTextView(msgData);
                    } while (cursor.moveToNext());
                } else {
                    // empty box, no SMS
                }

                *//*
            Cursor cursor = getActivity().getContentResolver().query(SMS_INBOX, new String[] {body, address},
                    null, null, "date desc limit 3");*//*
            } finally {
                cursor.close();
            }
        }*/
    }

    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        android.text.format.DateFormat df = new android.text.format.DateFormat();
        return df.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public static void setListOfSmsView(final List<Sms> listOfSms, View view, final Context context) {
        if (view != null) {

            // Find the ScrollView
            ScrollView scrollView = (ScrollView) view.findViewById(R.id.last_contacts_view);

            RelativeLayout layouts[] = new RelativeLayout[3];

            RelativeLayout message1 = (RelativeLayout) scrollView.findViewById(R.id.message1);
            RelativeLayout message2 = (RelativeLayout) scrollView.findViewById(R.id.message2);
            RelativeLayout message3 = (RelativeLayout) scrollView.findViewById(R.id.message3);

            layouts[0] = message1;
            layouts[1] = message2;
            layouts[2] = message3;


            message1.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", listOfSms.get(0).getAddress());
                    smsIntent.putExtra("sms_body","");
                    context.startActivity(smsIntent);
                }

            });

            message2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", listOfSms.get(1).getAddress());
                    smsIntent.putExtra("sms_body","");
                    context.startActivity(smsIntent);
                }

            });

            message3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                    smsIntent.setType("vnd.android-dir/mms-sms");
                    smsIntent.putExtra("address", listOfSms.get(2).getAddress());
                    smsIntent.putExtra("sms_body","");
                    context.startActivity(smsIntent);
                }

            });


            if (listOfSms.size() == 0) {
                for (int i = 0; i < 3; i++) {
                    layouts[i].setVisibility(View.GONE);
                }
            } else {
                for (int i = 0; i < listOfSms.size(); i++) {
                    layouts[i].setVisibility(View.VISIBLE);
                    Sms sms = listOfSms.get(i);
                    TextView contactName = (TextView) layouts[i].findViewById(R.id.contact_name);
                    TextView contactDate = (TextView) layouts[i].findViewById(R.id.contact_date);
                    TextView contactMessage = (TextView) layouts[i].findViewById(R.id.contact_message);

                    contactName.setText(sms.getPerson());
                    contactDate.setText(sms.getTime());
                    contactMessage.setText(sms.getMsg());
                }
            }

            // Display the view
            view.invalidate();
        }
        TwoFragment.setListOfSms(listOfSms);
    }


    public static String getContactName(String phoneNumber, Context context) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri,
                new String[] { ContactsContract.PhoneLookup.DISPLAY_NAME }, null, null, null);
        if (cursor == null) {
            return phoneNumber;
        }
        String contactName = null;
        if (cursor.moveToFirst()) {
            contactName = cursor.getString(cursor
                    .getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        if (contactName == null) {
            contactName = phoneNumber;
        }

        return contactName;
    }

}
