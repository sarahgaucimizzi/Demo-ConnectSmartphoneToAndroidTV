package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class MainActivity extends Activity implements ConnectFragment.OnConnectListener, RemoteFragment.OnButtonPressedListener{
    public static final int SERVERPORT = 8080;
    private boolean connected = false;
    String serverIpAddress = "";
    String command = "";
    Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ConnectFragment(), "connectFragment")
                .commit();
    }

    public class ClientThread implements Runnable {
        @Override
        public void run() {
            try {
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                mSocket = new Socket(serverAddr, SERVERPORT);
                connected = true;
                if (connected) {
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new RemoteFragment(), "remoteFragment")
                            .commit();

                }
                if(!connected) {
                    mSocket.close();
                    Log.d("ClientActivity", "C: Closed.");
                }
            } catch (Exception e) {
                ConnectFragment connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");

                if(connectFragment == null){
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new ConnectFragment(), "connectFragment")
                            .commit();

                    connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");
                }

                if(connectFragment != null){
                    connectFragment.showSnackbar("Error: Something went very very wrong.");
                }
                connected = false;
            }
        }
    }

    @Override
    public void onConnectTo(String ip) {
        serverIpAddress = ip;
        if (!connected) {
            if (!serverIpAddress.equals("")) {
                Thread cThread = new Thread(new ClientThread());
                cThread.start();
            }
        }
    }

    @Override
    public void buttonPressed(String s) {
        command = s;
        if(connected){
            try {
                Log.d("ClientActivity", "C: Sending command.");
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(mSocket.getOutputStream())), true);
                // Issue Commands
                if(!command.equals("")) {
                    out.println(command);
                    Log.d("ClientActivity", "C: Sent.");
                }
            } catch (Exception e) {
                Log.e("ClientActivity", "S: Error" + e.getMessage(), e);
                RemoteFragment remoteFragment = (RemoteFragment) getFragmentManager().findFragmentByTag("remoteFragment");

                if(remoteFragment == null){
                    getFragmentManager().beginTransaction()
                            .replace(R.id.container, new RemoteFragment(), "remoteFragment")
                            .commit();

                    remoteFragment = (RemoteFragment) getFragmentManager().findFragmentByTag("remoteFragment");
                }

                if(remoteFragment != null){
                    remoteFragment.showSnackbar("S: Error" + e.getMessage());
                }
            }
        }
    }
}
