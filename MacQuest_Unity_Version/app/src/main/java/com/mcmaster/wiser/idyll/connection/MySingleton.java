package com.mcmaster.wiser.idyll.connection;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Ahmed on 7/8/2017.
 */

public class MySingleton {
    private static MySingleton mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;
// create an instance for the life time
    private MySingleton(Context context)
    {
        mCtx = context;
        requestQueue = getRequestQueue();

    }

    public RequestQueue getRequestQueue()
    {
        if(requestQueue==null)
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        return requestQueue;
    }

    public static synchronized MySingleton getInstance(Context context)
    {
        if(mInstance==null)
        {
            mInstance = new MySingleton(context);
        }
        return mInstance;
    }
    public<T> void addToRequestQue(Request<T> request)
    {
        getRequestQueue().add(request);
    }
}
