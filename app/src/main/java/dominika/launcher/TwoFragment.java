package dominika.launcher;

import android.app.Service;
import android.app.WallpaperManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dominika.launcher.AllAppsGrid.AppsGridFragment;
import dominika.launcher.AppsByCategory.CategoriesGridFragment;
import dominika.launcher.LastContacts.Call;
import dominika.launcher.LastContacts.CallManager;
import dominika.launcher.LastContacts.Sms;
import dominika.launcher.LastContacts.SmsManager;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TwoFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TwoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    public static String clickedFolder;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    View view;

    static List<Sms> listOfSms = new ArrayList<Sms>();
    static List<Call> listOfCalls= new ArrayList<Call>();

    FragmentsBackgroundEffects fragmentsBackgroundEffects;
    private OnFragmentInteractionListener mListener;

    public TwoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TwoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TwoFragment newInstance(String param1, String param2) {
        TwoFragment fragment = new TwoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        callsFilter.addAction("android.intent.action.NEW_OUTGOING_CALL");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_two, container, false);

        // Listen to button which calls category
        LinearLayout mBtnGames = (LinearLayout) view.findViewById(R.id.btnCategoryGames);
        mBtnGames.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadFolder("games");
            }

        });

        LinearLayout mBtnHobby = (LinearLayout) view.findViewById(R.id.btnCategoryHobby);
        mBtnHobby.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadFolder("hobby");
            }

        });

        LinearLayout mBtnMultimedia = (LinearLayout) view.findViewById(R.id.btnCategoryMultimedia);
        mBtnMultimedia.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadFolder("multimedia");
            }

        });

        LinearLayout mBtnSocial = (LinearLayout) view.findViewById(R.id.btnCategorySocial);
        mBtnSocial.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadFolder("social");
            }

        });

        LinearLayout mBtnUtility = (LinearLayout) view.findViewById(R.id.btnCategoryUtility);
        mBtnUtility.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadFolder("utility");
            }

        });

        LinearLayout mBtnOther = (LinearLayout) view.findViewById(R.id.btnCategoryOther);
        mBtnOther.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                loadFolder("other");
            }

        });


        SmsManager.getListOfSms(getActivity(), view, getContext());
        CallManager.getListOfCalls(getActivity(), view, getContext());

        view.invalidate();

        //changeWallpaper(view, true);

        return view;
    }

    private IntentFilter filter =
            new IntentFilter("android.provider.Telephony.SMS_RECEIVED");

    private BroadcastReceiver smsReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub

            if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
                Bundle bundle = intent.getExtras();           //---get the SMS message passed in---
                SmsMessage[] msgs = null;
                String msgFrom = "";
                String msgBody = "";
                Long msgDate = 0L;

                if (bundle != null){
                    //---retrieve the SMS message received---
                    try{
                        Object[] pdus = (Object[]) bundle.get("pdus");

                        msgs = new SmsMessage[pdus.length];
                        for(int i=0; i<msgs.length; i++){
                            msgs[i] = SmsMessage.createFromPdu((byte[])pdus[i]);
                            msgFrom = msgs[i].getOriginatingAddress();
                            msgDate = msgs[i].getTimestampMillis();
                            msgBody += msgs[i].getMessageBody();
                        }
                        updateListOfSms(msgFrom, msgDate, msgBody);
                        //setLastSmsTextView(msgBody+";"+msg_from);
                    } catch(Exception e){
                        Log.d("Exception caught",e.getMessage());
                    }

                }
            }
        }
    };

    private IntentFilter callsFilter =
            new IntentFilter("android.intent.action.PHONE_STATE");

    private BroadcastReceiver callReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try{
                String state = intent.getStringExtra(TelephonyManager.EXTRA_STATE);

                if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                    updateListOfCalls();
                }

                if(state.equals(TelephonyManager.EXTRA_STATE_RINGING)){
                    updateListOfCalls();
                }

                if(state.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)){
                    updateListOfCalls();
                }

                if (state.equals(TelephonyManager.EXTRA_STATE_IDLE)){
                    updateListOfCalls();
                }
            }
            catch(Exception e){e.printStackTrace();}
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(smsReceiver, filter);
        getActivity().registerReceiver(callReceiver, callsFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(smsReceiver);
        getActivity().unregisterReceiver(callReceiver);
        super.onPause();
    }

    private void loadFolder(String category) {
       /* byte[] byteArray = getBitmapForBackground();

        // Save bitmap to bundle
        Bundle bundle = new Bundle();
        bundle.putByteArray("screenShot",byteArray);*/

        clickedFolder = category;
        Fragment fragment = new CategoriesGridFragment();
        //fragment.setArguments(bundle);
        replaceFragment(fragment);
    }

    /*public byte[] getBitmapForBackground() {


        // Get our bitmap and cut it
        FragmentsBackgroundEffects fragmentsBackgroundEffects = new FragmentsBackgroundEffects();
        Bitmap screenShot = fragmentsBackgroundEffects.getScreenShot(, marginTop, marginBottom);

        // Write bitmap to byte stream
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        screenShot.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        return byteArray;
    }*/

    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.two_fragment, fragment);
        transaction.addToBackStack(null);

        // Commit the transaction
        transaction.commit();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Nullable
    @Override
    public View getView() {
        return view;
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static List<Sms> getListOfSms() {
        return listOfSms;
    }

    public static void setListOfSms(List<Sms> listOfSms) {
        TwoFragment.listOfSms = listOfSms;
    }

    public static void setListOfCalls(List<Call> listOfCalls) {
        TwoFragment.listOfCalls = listOfCalls;
    }

    public void updateListOfSms(String msgFrom, Long dateInMilis, String msgBody) {
        Sms objSms = new Sms();
        objSms.setAddress(msgFrom);
        objSms.setPerson(SmsManager.getContactName(objSms.getAddress(), getContext()));
        objSms.setMsg(msgBody);
        objSms.setTime(dateInMilis.toString());
        objSms.setTime(SmsManager.convertDate(objSms.getTime(),"HH:mm dd/MM"));

        listOfSms.add(0, objSms);

        for (int i =1; i < listOfSms.size(); i++) {
            if (listOfSms.get(i).getAddress().equals(objSms.getAddress())) {
                listOfSms.remove(i);
            }
        }

        if (listOfSms.size() > 3) {
            listOfSms.remove(listOfSms.size()-1);
        }

        SmsManager.setListOfSmsView(listOfSms, getView(), getContext());
    }

    public void updateListOfCalls() {
        CallManager.getListOfCalls(getActivity(), getView(), getContext());
    }


    // Gets current system wallpaper
    private Drawable getWallpaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        return wallpaperDrawable;
    }

    private void changeWallpaper(View view, Boolean change) {

        final View v = view;

        if (change == true) {
            final ImageView imageView = (ImageView) v.findViewById(R.id.fragment_wallpaper);
            imageView.setVisibility(View.VISIBLE);

            fragmentsBackgroundEffects = new FragmentsBackgroundEffects(getContext());
            Bitmap screenBitmap = ((BitmapDrawable) getWallpaper()).getBitmap();

            loadBackground loader = new loadBackground(new taskComplete() {
                @Override
                public void complete(Bitmap resultBitmap) {
                    if (v.getVisibility() == View.VISIBLE) {
                        // Its visible
                        Drawable drawable = new BitmapDrawable(getResources(), resultBitmap);
                        //drawable.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
                        v.setBackground(drawable);
                    } else {
                        // Either gone or invisible
                    }
                }
            });
            loader.execute(screenBitmap);
        } else {
            ImageView imageView = (ImageView) v.findViewById(R.id.fragment_wallpaper);
            imageView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            SmsManager.getListOfSms(getActivity(), getView(), getContext());
            CallManager.getListOfCalls(getActivity(), getView(), getContext());
            //changeWallpaper(getView(), true);
        } else {
            //changeWallpaper(getView(), false);
        }
    }


    public class loadBackground extends AsyncTask<Bitmap, Void, Bitmap> {

        taskComplete done;

        public loadBackground(taskComplete task) {
            done = task;
        }

        @Override
        protected Bitmap doInBackground(Bitmap... params) {
            // Calculate how much of bitmap we have to cut
            ContextWrapper contextWrapper = new ContextWrapper(getContext());
            ScreenSpace mScreenSpace = new ScreenSpace(getActivity());

            Integer marginBottom = mScreenSpace.getBottomMargin(contextWrapper);
            Integer marginTop =  mScreenSpace.getTopMargin(contextWrapper);

            return fragmentsBackgroundEffects.cropBitmap(params[0], marginTop, marginBottom);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            done.complete(bitmap);
            super.onPostExecute(bitmap);

            Drawable d = new BitmapDrawable(getResources(), bitmap);
            final ImageView imageView = (ImageView) view.findViewById(R.id.fragment_wallpaper);
            imageView.setImageDrawable(d);
        }
    }

    interface taskComplete{
        void complete (Bitmap resultBitmap);
    }


    /*public class CallReceiver extends BroadcastReceiver {

        private static final String TAG = "PhoneStatReceiver";

        private boolean incomingFlag = false;

        private String incoming_number = null;


        @Override

        public void onReceive(Context context, Intent intent) {

            if(intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)){

                incomingFlag = false;

                String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

                CallManager.getListOfCalls(getActivity(), getView(), getContext());

            } else{

                TelephonyManager tm = (TelephonyManager)context.getSystemService(Service.TELEPHONY_SERVICE);



                switch (tm.getCallState()) {

                    case TelephonyManager.CALL_STATE_RINGING:

                        incomingFlag = true;

                        incoming_number = intent.getStringExtra("incoming_number");


                        Log.i(TAG, "incoming RINGING :"+ incoming_number);

                        CallManager.getListOfCalls(getActivity(), getView(), getContext());

                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        if(incomingFlag){

                            Log.i(TAG, "incoming ACCEPT :"+ incoming_number);

                            CallManager.getListOfCalls(getActivity(), getView(), getContext());
                        }

                        break;



                    case TelephonyManager.CALL_STATE_IDLE:

                        if(incomingFlag){

                            Log.i(TAG, "incoming IDLE");

                            CallManager.getListOfCalls(getActivity(), getView(), getContext());
                        }

                        break;

                }

            }

        }

    }*/



}
