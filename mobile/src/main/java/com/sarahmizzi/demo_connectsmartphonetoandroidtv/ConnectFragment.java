package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

public class ConnectFragment extends Fragment {
    private EditText serverIp;
    private Button connectPhones;
    private String serverIpAddress = "";

    private OnConnectListener mListener;

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
        serverIp = (EditText) view.findViewById(R.id.server_ip);
        connectPhones = (Button) view.findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);

        return view;
    }

    private View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            serverIpAddress = serverIp.getText().toString();
            mListener.onConnectTo(serverIpAddress);
        }
    };

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

    public void showSnackbar(String s){
        Snackbar.make(connectPhones, s, Snackbar.LENGTH_LONG).show();
    }

    public interface OnConnectListener {
        public void onConnectTo(String ip);
    }
}
