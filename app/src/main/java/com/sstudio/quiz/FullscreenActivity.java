package com.sstudio.quiz;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.sstudio.quiz.Models.Quiz;
import com.sstudio.quiz.Models.Result;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity implements View.OnClickListener {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 0;
    private static final String TAG = "Fullscreen_Activity";
    private final Handler mHideHandler = new Handler();
    private LinearLayout mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
   // private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            //mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };


    RequestQueue queue;
    String token="";
    Quiz q;
    Context context;
    int i=0;
    boolean clickable=true;
    int count=0;
    CuDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        mVisible = true;
        mContentView = findViewById(R.id.fullscreen_content);
        context=this;
        queue= Volley.newRequestQueue(context);

        final JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET,
                "https://opentdb.com/api.php?amount=10&category=9&type=multiple&token="+token, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading().dismiss();
                        Gson gson=new Gson();
                        q=gson.fromJson(String.valueOf(response),Quiz.class);
                        load(q.getResults().get(i));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        JsonObjectRequest sessionToken=new JsonObjectRequest(Request.Method.GET, "https://opentdb.com/api_token.php?command=request",
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Gson gson=new Gson();
                try {
                    token=response.getString("token");
                    queue.add(request);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "onResponse: token= "+token);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(sessionToken);
        ((findViewById(R.id.button))).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i<q.getResults().size()){
                    load(q.getResults().get(i));
                    clickable=true;
                    reset();
                }else {
                    dialog=new CuDialog(context);
                    dialog.setTitle("Completed");
                            dialog.setMessage("Total Questions : "+i+"\nRight answers : "+count);
                            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Load more", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialog.dismiss();
                                    loadMore();
                                }
                            });
                    dialog.show();
                }
            }
        });
        (findViewById(R.id.option4)).setOnClickListener(this);
        (findViewById(R.id.option3)).setOnClickListener(this);
        (findViewById(R.id.option2)).setOnClickListener(this);
        (findViewById(R.id.option1)).setOnClickListener(this);
        loading().show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(0);
    }
    public CuDialog loading(){
        CuDialog dialog=new CuDialog(this);
        View c=View.inflate(this,R.layout.loading,null);
        Glide.with(this).load(R.raw.wedges).into(((ImageView)c.findViewById(R.id.loading)));
        dialog.setView(c);
     return dialog;
    }
    public void loadMore(){
        i=0;
        JsonObjectRequest request= new JsonObjectRequest(Request.Method.GET,
                "https://opentdb.com/api.php?amount=10&category=9&type=multiple&token="+token, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        loading().dismiss();
                        Gson gson=new Gson();
                        q=gson.fromJson(String.valueOf(response),Quiz.class);
                        reset();
                        load(q.getResults().get(i));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(request);
    }

    public void load(Result res){
        TextView textView = (TextView) findViewById(R.id.section_label);
        TextView option1 = (TextView) findViewById(R.id.option1);
        TextView option2 = (TextView) findViewById(R.id.option2);
        TextView option3 = (TextView) findViewById(R.id.option3);
        TextView option4 = (TextView) findViewById(R.id.option4);
        //textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
        Log.d(TAG, "onCreateView: result= "+res);

        List<String> r=new ArrayList<>();
        r.add(res.getCorrectAnswer());
        r.add(res.getIncorrectAnswers().get(0));
        r.add(res.getIncorrectAnswers().get(2));
        r.add(res.getIncorrectAnswers().get(1));
        Log.d(TAG, "onCreateView: correct ans :"+r.get(0));
        Log.d(TAG, "onCreateView: list : "+r);
        Collections.shuffle(r);
        Log.d(TAG, "onCreateView: list shuffled :"+r);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            textView.setText(Html.fromHtml(res.getQuestion(),Html.FROM_HTML_MODE_LEGACY));
            option1.setText(Html.fromHtml(r.get(0),Html.FROM_HTML_MODE_LEGACY));
            option2.setText(Html.fromHtml(r.get(1),Html.FROM_HTML_MODE_LEGACY));
            option3.setText(Html.fromHtml(r.get(2),Html.FROM_HTML_MODE_LEGACY));
            option4.setText(Html.fromHtml(r.get(3),Html.FROM_HTML_MODE_LEGACY));
        }else {
            textView.setText(Html.fromHtml(res.getQuestion()));
            option1.setText(Html.fromHtml(r.get(0)));
            option2.setText(Html.fromHtml(r.get(1)));
            option3.setText(Html.fromHtml(r.get(2)));
            option4.setText(Html.fromHtml(r.get(3)));
        }
        Log.d(TAG, "load: option gettext: "+option1.getText());

    }
    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
       // mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in delay milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void reset(){
        (findViewById(R.id.option4)).setBackgroundResource(R.drawable.ic_options);
        (findViewById(R.id.option1)).setBackgroundResource(R.drawable.ic_options);
        (findViewById(R.id.option2)).setBackgroundResource(R.drawable.ic_options);
        (findViewById(R.id.option3)).setBackgroundResource(R.drawable.ic_options);
    }
    @Override
    public void onClick(View view) {
        try {
            if (clickable){
                clickable=false;
                String right;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    right= String.valueOf(Html.fromHtml(q.getResults().get(i).getCorrectAnswer(),Html.FROM_HTML_MODE_LEGACY));
                }else {
                    right= String.valueOf(Html.fromHtml(q.getResults().get(i).getCorrectAnswer()));
                }
                Log.d(TAG, "onClick: right: "+right);
                if (((TextView)(findViewById(view.getId()))).getText().toString().equals(right)){
                    count++;
                }
                if (((TextView)findViewById(R.id.option1)).getText().toString().equals(right)){
                    ((findViewById(R.id.option1))).setBackgroundResource(R.drawable.ic_right_answer);
                }else {
                    ((findViewById(R.id.option1))).setBackgroundResource(R.drawable.ic_wrong_answer);
                }
                if (((TextView)findViewById(R.id.option2)).getText().toString().equals(right)){
                    ((findViewById(R.id.option2))).setBackgroundResource(R.drawable.ic_right_answer);
                }else {
                    ((findViewById(R.id.option2))).setBackgroundResource(R.drawable.ic_wrong_answer);
                }
                if (((TextView)findViewById(R.id.option3)).getText().toString().equals(right)){
                    ((findViewById(R.id.option3))).setBackgroundResource(R.drawable.ic_right_answer);
                }else {
                    ((findViewById(R.id.option3))).setBackgroundResource(R.drawable.ic_wrong_answer);
                }
                if (((TextView)findViewById(R.id.option4)).getText().toString().equals(right)){
                    ((findViewById(R.id.option4))).setBackgroundResource(R.drawable.ic_right_answer);
                }else {
                    ((findViewById(R.id.option4))).setBackgroundResource(R.drawable.ic_wrong_answer);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
