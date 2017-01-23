package dominika.launcher;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextClock;
import android.widget.Toast;

import java.net.URI;

import dominika.launcher.AllAppsGrid.AppsGridFragment;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OneFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link OneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OneFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;



    public OneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment OneFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static OneFragment newInstance(String param1, String param2) {
        OneFragment fragment = new OneFragment();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_one, container, false);

        // Listen to button which calls all apps
        ImageButton mBtnShowSettings = (ImageButton) view.findViewById(R.id.btnShowSettings);
        ImageButton mBtnShowDialer = (ImageButton) view.findViewById(R.id.btnShowDialer);
        ImageButton mBtnAllApps = (ImageButton) view.findViewById(R.id.btnAllApps);
        ImageButton mBtnShowMessenger = (ImageButton) view.findViewById(R.id.btnShowMessenger);
        ImageButton mBtnShowGallery = (ImageButton) view.findViewById(R.id.btnShowGallery);


        ImageButton mBtnShowMessageApp = (ImageButton) view.findViewById(R.id.btnShowMessageApp);
        ImageButton mBtnShowBrowser = (ImageButton) view.findViewById(R.id.btnShowBrowser);
        ImageButton mBtnShowCamera = (ImageButton) view.findViewById(R.id.btnShowCamera);
        ImageButton mBtnShowGmail = (ImageButton) view.findViewById(R.id.btnShowGmail);
        ImageButton mBtnShowCalendar = (ImageButton) view.findViewById(R.id.btnShowCalc);

        LinearLayout mDateTimeWidget = (LinearLayout) view.findViewById(R.id.dateTimeWidget);

        mBtnShowSettings.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent SettingsIntent = new Intent(Settings.ACTION_SETTINGS);
                startActivity(SettingsIntent);
            }
        });

        mBtnShowDialer.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent DialIntent = new Intent(Intent.ACTION_DIAL);

                try {
                    startActivity(DialIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Please Install Dialer", Toast.LENGTH_LONG).show();
                }

            }
        });

        mBtnAllApps.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Fragment fragment = new AppsGridFragment();
                replaceFragment(fragment);
            }

        });

        mBtnShowMessenger.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent MessengerIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                if (MessengerIntent == null) {
                    Toast.makeText(getContext(),"Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                } else {
                    try {
                        startActivity(MessengerIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "Please Install Facebook Messenger", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mBtnShowGallery.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent GalleryIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("content://media/internal/images/media"));

                try {
                    startActivity(GalleryIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Please Install Dialer", Toast.LENGTH_LONG).show();
                }

            }
        });

        mBtnShowMessageApp.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent MesssageAppIntent = new Intent(Intent.ACTION_MAIN);
                MesssageAppIntent.addCategory(Intent.CATEGORY_DEFAULT);
                MesssageAppIntent.setType("vnd.android-dir/mms-sms");

                try {
                    startActivity(MesssageAppIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Please Install Messaging App", Toast.LENGTH_LONG).show();
                }

            }
        });

        mBtnShowBrowser.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent BrowserIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.sec.android.app.sbrowser");
                if (BrowserIntent == null) {
                    BrowserIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.android.browser");
                    try {
                        startActivity(BrowserIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "Please Install Browser", Toast.LENGTH_LONG).show();
                    }
                } else {
                    try {
                        startActivity(BrowserIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "Please Install Browser", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        mBtnShowCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent CameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);

                try {
                    startActivity(CameraIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Please Install Camera App", Toast.LENGTH_LONG).show();
                }

            }
        });

        mBtnShowGmail.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent mailIntent = getActivity().getPackageManager().getLaunchIntentForPackage("com.google.android.gm");
                if (mailIntent != null) {
                    try {
                        startActivity(mailIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getContext(), "You should install Gmail.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    mailIntent = new Intent(Intent.ACTION_MAIN);
                    mailIntent.addCategory(Intent.CATEGORY_APP_EMAIL);
                    try {
                        startActivity(mailIntent);
                    } catch (android.content.ActivityNotFoundException exc) {
                        Toast.makeText(getContext(), "You have to install mail app.", Toast.LENGTH_LONG).show();

                    }
                }
            }
        });

        mBtnShowCalendar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                long startMillis = System.currentTimeMillis();
                Uri.Builder builder = CalendarContract.CONTENT_URI.buildUpon();
                builder.appendPath("time");
                ContentUris.appendId(builder, startMillis);
                Intent CalendarIntent = new Intent(Intent.ACTION_VIEW).setData(builder.build());

                //Intent CalendarIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                try {
                    startActivity(CalendarIntent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getContext(), "Please Install Calendar App", Toast.LENGTH_LONG).show();
                }

            }
        });




        mDateTimeWidget.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent ClockIntent = new Intent(AlarmClock.ACTION_SHOW_ALARMS);
                ClockIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(ClockIntent);
            }
        });



        // Inflate the layout for this fragment
        return view;
    }


    public void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.one_fragment, fragment);
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

}
