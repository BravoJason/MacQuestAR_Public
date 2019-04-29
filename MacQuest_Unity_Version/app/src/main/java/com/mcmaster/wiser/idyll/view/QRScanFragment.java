package com.mcmaster.wiser.idyll.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.mcmaster.wiser.idyll.R;
import com.mcmaster.wiser.idyll.presenter.NoScanResultException;
import com.mcmaster.wiser.idyll.presenter.QRScanResultReceiver;

import java_cup.Main;


public class QRScanFragment extends BaseFragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public String codeContent;
    public String codeFormat;
    IntentIntegrator integrator;


    public QRScanFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ARScanFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QRScanFragment newInstance(String param1, String param2) {
        QRScanFragment fragment = new QRScanFragment();
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
        integrator = new IntentIntegrator(this.getActivity()).forSupportFragment(this);
        // use forSupportFragment or forFragment method to use fragments instead of activity
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("");

        //integrator.setResultDisplayDuration(0); // milliseconds to display result on screen after scan
        integrator.setCameraId(0);  // Use a specific camera of the device

        integrator.initiateScan();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_qrscan, container, false);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();



    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        QRScanResultReceiver parentActivity = (QRScanResultReceiver) this.getActivity();

        if (scanningResult != null) {
            //we have a result
            codeContent = scanningResult.getContents();
            codeFormat = scanningResult.getFormatName();
            // send received data
            parentActivity.QRScanResultData(codeFormat,codeContent);

        }else{
            // send exception
            parentActivity.QRScanResultData(new NoScanResultException(this.getString(R.string.scan_qr_code_no_result)));
        }



    }




    @Override
    public boolean onActivityBackPress() {
        return false;
    }
}

