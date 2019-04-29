package com.mcmaster.wiser.idyll.view;

import android.Manifest;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.connection.MySingleton;
import com.mcmaster.wiser.idyll.connection.ServerUtils;
import com.github.paolorotolo.appintro.AppIntro;

import java.util.HashMap;
import java.util.Map;

/**
 * First time user's introduction to the app
 * Created by Eric on 7/18/17.
 */

public class IntroActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().hide();
        RegisterUser();
//        getActionBar().hide();

        // Note here that we DO NOT use setContentView();

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        addSlide(SampleSlide.newInstance(R.layout.intro_slide_1));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide_2));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide_3));
        addSlide(SampleSlide.newInstance(R.layout.intro_slide_4));

        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
//        addSlide(AppIntroFragment.newInstance(getString(R.string.welcome), "Your campus tool", R.drawable.logo_transparent, getResources().getColor(R.color.colorPrimary)));
//        addSlide(AppIntroFragment.newInstance("Using MacQuest", "Use the search bar to find any lecture hall, building, or washroom on campus", R.drawable.ic_people_white_24dp, getResources().getColor(R.color.colorBlue)));
//        addSlide(AppIntroFragment.newInstance("Navigation", "Click the navigation button to find the optimal route to any room", R.drawable.ic_my_location_white_24dp, getResources().getColor(R.color.colorWhite)));

        setDoneText("Lets go!");

        askForPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 3);

        // OPTIONAL METHODS
        // Override bar/separator color.
//        setBarColor(Color.parseColor("#3F51B5"));
//        setSeparatorColor(Color.parseColor("#2196F3"));

        showSeparator(false);

        setIndicatorColor(R.color.colorPrimaryLight, R.color.colorPrimary);
        setColorSkipButton(getResources().getColor(R.color.colorPrimaryLight));
        setNextArrowColor(getResources().getColor(R.color.colorPrimaryLight));

        // Hide Skip/Done button.
//        showSkipButton(false);
//        setProgressButtonEnabled(false);

        // Turn vibration on and set intensity.
        // NOTE: you will probably need to ask VIBRATE permission in Manifest.
        setVibrate(true);
        setVibrateIntensity(30);
    }

    private void RegisterUser() {
        final String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        final String localTime = String.valueOf(System.currentTimeMillis());
        final RequestQueue requestQueue = Volley.newRequestQueue(IntroActivity.this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtils.API_REGISTRATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uuid", id);
                params.put("timeinstalled", localTime);
                return params;
            }
        };
        MySingleton.getInstance(this).addToRequestQue(stringRequest);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        finish();
        // Do something when users tap on Skip button.
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        finish();
        // Do something when users tap on Done button.
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
