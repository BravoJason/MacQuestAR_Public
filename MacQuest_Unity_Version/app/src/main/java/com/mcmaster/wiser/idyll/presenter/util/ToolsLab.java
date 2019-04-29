package com.mcmaster.wiser.idyll.presenter.util;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.View;


/*
Some tools function implementation.
Created by Jason.
*/

public class ToolsLab {

    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Rect mBounds;

    public void initIfNeeded() {
        if(mBitmap == null) {
            mBitmap = Bitmap.createBitmap(1,1, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
            mBounds = new Rect();
        }
    }
    //Function to get a view color.
    public int getBackgroundColor(View view) {
        //The actual color, not the id.
        int color = Color.BLACK;
        if(view.getBackground() instanceof ColorDrawable) {
            if(Build.VERSION.SDK_INT <Build.VERSION_CODES.HONEYCOMB) {
                initIfNeeded();
                //If the ColorDrawable makes use of its bounds in the draw method,
                //we may not be able to get the color we want. This is not the usual
                //case before Ice Cream Sandwich (4.0.1 r1).
                //Yet, we change the bounds temporarily, just to be sure that we are
                //successful.
                ColorDrawable colorDrawable = (ColorDrawable)view.getBackground();
                mBounds.set(colorDrawable.getBounds());//Save the original bounds.
                colorDrawable.setBounds(0, 0, 1, 1);//Change the bounds.
                colorDrawable.draw(mCanvas);
                color = mBitmap.getPixel(0, 0);
                colorDrawable.setBounds(mBounds);//Restore the original bounds.
            }
            else {
                color = ((ColorDrawable)view.getBackground()).getColor();
            }
        }
        return color;
    }
}