package com.mcmaster.wiser.idyll.view;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Define method for each fragment into to link the search bar with the navigation drawer
 * Created by Eric on 6/13/17.
 */

public abstract class BaseFragment extends Fragment{

    private BaseFragmentCallbacks mCallbacks;

    public interface BaseFragmentCallbacks{
        void onAttachSearchViewToDrawer (FloatingSearchView searchView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseFragmentCallbacks){
            mCallbacks = (BaseFragmentCallbacks) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement BaseFragmentCallbacks");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    protected void attachSearchViewActivityDrawer(FloatingSearchView searchView){
        if (mCallbacks != null){
            mCallbacks.onAttachSearchViewToDrawer(searchView);
        }
    }

    public abstract boolean onActivityBackPress();

}
