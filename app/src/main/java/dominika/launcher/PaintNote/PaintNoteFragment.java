package dominika.launcher.PaintNote;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import dominika.launcher.Constants;
import dominika.launcher.CustomViewPager;
import dominika.launcher.FragmentsBackgroundEffects;
import dominika.launcher.MainActivity;
import dominika.launcher.R;
import dominika.launcher.ScreenSpace;

import static android.R.attr.width;
import static dominika.launcher.R.attr.height;
import static dominika.launcher.R.attr.layout;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PaintNoteFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PaintNoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PaintNoteFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    FragmentsBackgroundEffects fragmentsBackgroundEffects;

    public Paint mPaint;
    RelativeLayout layout;

    DrawView drawView;
    DrawView savedDrawView = null;
    DrawView clearDrawing;
    View view;

    public PaintNoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PaintNoteFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PaintNoteFragment newInstance(String param1, String param2) {
        PaintNoteFragment fragment = new PaintNoteFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Returns Instance of PaintNoteFragment
     *
     * @return Instance of PaintNoteFragment
     */
    public static PaintNoteFragment getInstance() {
        PaintNoteFragment fragment = new PaintNoteFragment();
        fragment.setRetainInstance(true);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        Log.d("TWORZĘ AKTYWNOŚĆ", "bang");


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_paint_note, container, false);

        Log.d("TWORZĘ WIDOK", "bang");

        if (savedDrawView == null) {
            drawView = new DrawView(getActivity());
            drawView.setDrawingCacheEnabled(true);
            clearDrawing = drawView;
        } else {
            drawView = savedDrawView;
        }


        //dv = new DrawView(getActivity());
        //drawView.invalidate();


        layout = (RelativeLayout) view.findViewById(R.id.drawing_layout);
        layout.addView(drawView);

        //layout.addView(new DrawView(getActivity()));

        //drawView = (DrawView) view.findViewById(R.id.drawing_view);

        // Set buttons and listeners
        setButtons(view);

        // Inflate the layout for this fragment
        //changeWallpaper(view, true);

        return view;
    }

    public void setButtons(View view) {
        ImageButton btnClearDrawing = (ImageButton) view.findViewById(R.id.btnClearDrawing);
        ImageButton btnExitDrawing = (ImageButton) view.findViewById(R.id.btnExitDrawing);

        btnClearDrawing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDrawing();
            }
        });

        btnExitDrawing.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                exitDrawing();
            }
        });
    }

    public void exitDrawing() {
        MainActivity activity = (MainActivity) getActivity();
        CustomViewPager vp = (CustomViewPager) activity.getViewPager();
        vp.setCurrentItem(1);
        vp.setPagingEnabled(true);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            MainActivity activity = (MainActivity) getActivity();
            CustomViewPager vp = (CustomViewPager) activity.getViewPager();
            vp.setPagingEnabled(false);
        }
        else {
        }
    }

    public void clearDrawing() {
        drawView.clearDrawing();
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

    @Nullable
    @Override
    public View getView() {
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // save view
        savedDrawView = drawView;

        // remove view
        layout.removeView(drawView);
    }

    // Gets current system wallpaper
    private Drawable getWallpaper() {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(getContext());
        final Drawable wallpaperDrawable = wallpaperManager.getDrawable();

        return wallpaperDrawable;
    }

    private void changeWallpaper(final View view, Boolean change) {
        fragmentsBackgroundEffects = new FragmentsBackgroundEffects(getContext());
        Bitmap screenBitmap = ((BitmapDrawable) getWallpaper()).getBitmap();
        screenBitmap = fragmentsBackgroundEffects.blurBitmap(screenBitmap);
        Drawable drawable = new BitmapDrawable(getResources(), screenBitmap);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.drawing_layout);
        layout.setBackgroundDrawable(drawable);

        /*if (change) {
            fragmentsBackgroundEffects = new FragmentsBackgroundEffects(getContext());
            Bitmap screenBitmap = fragmentsBackgroundEffects.getScreenShot(view.findViewById(R.id.activity_main));

            loadBackground loader = new loadBackground(new taskComplete() {
                @Override
                public void complete(Bitmap resultBitmap) {
                    if (view.getVisibility() == View.VISIBLE) {
                        // Its visible
                        Drawable drawable = new BitmapDrawable(getResources(), resultBitmap);
                        //drawable.setColorFilter(Color.rgb(123, 123, 123), android.graphics.PorterDuff.Mode.MULTIPLY);
                        view.setBackground(drawable);
                    } else {
                        // Either gone or invisible
                    }
                }
            });
            loader.execute(screenBitmap);
        }*/
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

        }
    }

    interface taskComplete{
        void complete (Bitmap resultBitmap);
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



    public class DrawView extends View {
        //DrawView drawView;
        ImageView imageView;
        Bitmap bitmap; Bitmap bit;
        Canvas canvas;

        private static final float MINP = 0.25f;
        private static final float MAXP = 0.75f;
        private Bitmap  mBitmap;
        private Canvas  mCanvas;
        private Path mPath;
        private Paint   mPaint;
        Context context;

        //private int stateToSave;


        Paint paint = new Paint();

        public DrawView(Context context) {
            super(context);
            this.context = context;
            mPath = new Path();
            setPaint();
        }
        public DrawView(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.context = context;
            mPath = new Path();
            setPaint();
        }
        public DrawView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
            this.context = context;
            mPath = new Path();
            setPaint();
        }

        public void setPaint() {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setDither(true);
            mPaint.setColor(Color.parseColor(Constants.COLOR_ACCENT_LIGHT_VIOLET));
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);

            Point p = ScreenSpace.availableSpace;

            if (p.x <= 720) {
                mPaint.setStrokeWidth(10);
            } else {
                mPaint.setStrokeWidth(18);
            }

        }

        /*@Override
        public Parcelable onSaveInstanceState()
        {
            Bundle bundle = new Bundle();
            bundle.putParcelable("superState", super.onSaveInstanceState());
            bundle.putInt("stateToSave", this.stateToSave); // ... save stuff
            return bundle;
        }

        @Override
        public void onRestoreInstanceState(Parcelable state)
        {
            if (state instanceof Bundle) // implicit null check
            {
                Bundle bundle = (Bundle) state;
                this.stateToSave = bundle.getInt("stateToSave"); // ... load stuff
                state = bundle.getParcelable("superState");
            }
            super.onRestoreInstanceState(state);
        }*/



        @Override
        protected void onSizeChanged(int w, int h, int oldw, int oldh) {
            super.onSizeChanged(w, h, oldw, oldh);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            canvas.drawBitmap(mBitmap, 0, 0, mPaint);
            canvas.drawPath(mPath, mPaint);
        }

        public void clearDrawing() {
            //setDrawingCacheEnabled(false);
            // don't forget that one and the match below,
            // or you just keep getting a duplicate when you save.

            //onSizeChanged(width, height, width, height);
            /*int i = Integer.parseInt("000000");
            drawView.canvas.drawColor(i);*/

            layout.removeViews(0, layout.getChildCount());

            drawView = null;

            drawView = new DrawView(getActivity());
            drawView.setDrawingCacheEnabled(true);
            layout.addView(drawView);

            layout.invalidate();

            savedDrawView = drawView;
            //invalidate();

            //setDrawingCacheEnabled(true);
        }

        private float mX, mY;
        private static final float TOUCH_TOLERANCE = 4;

        private void touch_start(float x, float y) {
            //showDialog();
            mPath.reset();
            mPath.moveTo(x, y);
            mX = x;
            mY = y;

        }
        private void touch_move(float x, float y) {
            float dx = Math.abs(x - mX);
            float dy = Math.abs(y - mY);
            if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
                mX = x;
                mY = y;
            }
        }

        private void touch_up() {
            mPath.lineTo(mX, mY);
            // commit the path to our offscreen
            mCanvas.drawPath(mPath, mPaint);
            // kill this so we don't double draw
            mPath.reset();
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
            //mPaint.setMaskFilter(null);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    touch_start(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_MOVE:

                    touch_move(x, y);
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    touch_up();
                    invalidate();
                    break;
            }
            return true;
        }

    }




}
