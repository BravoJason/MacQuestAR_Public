package com.mcmaster.wiser.idyll.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.presenter.util.MapUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wiserlab on 7/11/17.
 */

public class InfoFragment extends BaseFragment {

    @Override
    public boolean onActivityBackPress() {
        return false;
    }

    @BindView(R.id.macquest_info_card)
    CardView macquestInfoCard;


    @BindView(R.id.info_close_button)
    Button infoCloseButton;

    @OnClick(R.id.info_close_button)
    public void closeInfoFragment(){
        MapUtils.staticHideCardView(macquestInfoCard ,getContext());
        ((MainActivity) getActivity()).navigationView.getMenu().getItem(0).setChecked(true);
//        getFragmentManager().popBackStack(MapFragment.class.getSimpleName(), 0);
    }

    //When the cardview closes, reset the sidebar to highlight the right option.
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) getActivity()).resetSidebarHighlight();
    }


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
