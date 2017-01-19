package dominika.launcher;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import dominika.launcher.AppsByCategory.CategoriesGridFragment;


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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_two, container, false);

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

        return view;
    }

    private void loadFolder(String category) {
        clickedFolder = category;
        Fragment fragment = new CategoriesGridFragment();
        replaceFragment(fragment);
    }

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
