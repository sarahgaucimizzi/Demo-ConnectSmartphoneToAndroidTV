package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends Activity implements ConnectFragment.OnConnectListener {
    public static final int SERVERPORT = 8080;
    public static String SERVERIP = null;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    public Socket client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getFragmentManager().beginTransaction()
                .replace(R.id.container, new ConnectFragment(), "connectFragment")
                .commit();
    }

    public class ServerThread implements Runnable {
        public void run() {
            try {
                if (SERVERIP != null) {
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // Listen for incoming clients
                        client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                // Open content fragment
                                getFragmentManager().beginTransaction()
                                        .replace(R.id.container, new ContentFragment())
                                        .commit();
                            }
                        });

                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(serverSocket != null) {
                                    listenForCommand();
                                    handler.postDelayed(this, 500);
                                }
                            }
                        }, 500);

                    }
                }
            } catch (final Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        ConnectFragment connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");
                        if (connectFragment == null){
                            getFragmentManager().beginTransaction()
                                    .replace(R.id.container, new ConnectFragment(), "connectFragment")
                                    .commit();

                            connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");
                        }
                        if(connectFragment != null) {
                            connectFragment.updateText("Error "+ e.getMessage());
                            Log.e("Server Activity", e.getMessage(), e);
                        }
                    }
                });
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onConnectDevices(String serverIP) {
        SERVERIP = serverIP;
        Thread thread = new Thread(new ServerThread());
        thread.start();
    }

    public void listenForCommand(){
        try {
            // Listen for remote commands and execute them
            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            final String line = in.readLine();
            if (line != null) {
                Log.d("ServerActivity", line);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        switch (line){
                            case "UP":
                                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                                break;

                            case "DOWN":
                                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                                break;

                            case "LEFT":
                                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                                break;

                            case "RIGHT":
                                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                                break;

                            case "OK":
                                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                                break;

                            case "EXIT":
                                if (serverSocket != null) {
                                    try {
                                        serverSocket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                                serverSocket = null;

                                getFragmentManager().beginTransaction()
                                        .replace(R.id.container, new ConnectFragment(), "connectFragment")
                                        .commit();
                                break;
                        }
                    }
                });
            }
        } catch (final Exception e) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    ConnectFragment connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");
                    if (connectFragment == null){
                        getFragmentManager().beginTransaction()
                                .replace(R.id.container, new ConnectFragment(), "connectFragment")
                                .commit();

                        connectFragment = (ConnectFragment) getFragmentManager().findFragmentByTag("connectFragment");
                    }
                    if(connectFragment != null) {
                        connectFragment.updateText("Oops. Connection interrupted. Please reconnect your devices. " + e.getMessage());
                    }
                }
            });
            e.printStackTrace();
        }
    }
}
