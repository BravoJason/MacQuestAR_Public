package com.mcmaster.wiser.idyll.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.mcmaster.wiser.idyll.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by wiserlab on 7/11/17.
 */

public class EmergencyServicesFragment extends BaseFragment {

    @BindView(R.id.emergency_efrt_button)
    Button emergencyEfrtButton;

    @BindView(R.id.emergency_contacts_button)
    Button emergencyContactsButton;

    @BindView(R.id.emergency_swhat_button)
    Button emergencySwhatButton;

    @OnClick({ R.id.emergency_efrt_button , R.id.emergency_contacts_button , R.id.emergency_swhat_button })
    public void onEmergencyCallButtonClick(Button b){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        String telephoneNumber = "";
        switch (b.getId()){
            case R.id.emergency_efrt_button:
                telephoneNumber = "tel:9055224135";
                break;
            case R.id.emergency_contacts_button:
                telephoneNumber = "tel:9055224315";
                break;
            case R.id.emergency_swhat_button:
                telephoneNumber = "tel:905522431527500";
                break;
        }
        intent.setData(Uri.parse(telephoneNumber));
        startActivity(intent);
    }

    @Override
    public boolean onActivityBackPress() {
        return false;
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
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

}
