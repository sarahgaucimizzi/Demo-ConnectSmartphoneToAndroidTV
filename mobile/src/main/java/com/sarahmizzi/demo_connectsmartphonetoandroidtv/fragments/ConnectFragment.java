package com.sarahmizzi.demo_connectsmartphonetoandroidtv.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sarahmizzi.demo_connectsmartphonetoandroidtv.R;
import com.sarahmizzi.demo_connectsmartphonetoandroidtv.utilities.MobileNsdHelper;

import java.net.InetAddress;

public class ConnectFragment extends Fragment {
    private EditText serverIp;
    private Button connectPhones;
    private String serverIpAddress = "";

    private static OnConnectListener mListener;

    public ConnectFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_connect, container, false);
        connectPhones = (Button) view.findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobileNsdHelper nsdHelper = new MobileNsdHelper(getActivity().getBaseContext());
                nsdHelper.initializeDiscoveryListener();
                nsdHelper.initializeResolveListener();
                nsdHelper.discoverServices();
            }
        });

        return view;
    }

    public void startConnection(InetAddress host, int port){
        mListener.onConnectTo(host, port);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnConnectListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnConnectListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnConnectListener {
        public void onConnectTo(InetAddress host, int port);
    }
}
