package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

/*
    Tutorial: https://thinkandroid.wordpress.com/2010/03/27/incorporating-socket-programming-into-your-applications/
 */

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

public class ServerActivity extends Activity {
    public static final int SERVERPORT = 8080;
    public static String SERVERIP;

    private TextView serverStatus;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_serveractivity);
        serverStatus = (TextView) findViewById(R.id.server_status);

        SERVERIP = getLocalIpAddress();

        Thread thread = new Thread(new ServerThread());
        thread.start();
    }

    public class ServerThread implements Runnable {
        public void run() {
            try {
                if (SERVERIP != null) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Listening on IP: " + SERVERIP);
                        }
                    });
                    serverSocket = new ServerSocket(SERVERPORT);
                    while (true) {
                        // Listen for incoming clients
                        Socket client = serverSocket.accept();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                serverStatus.setText("Connected");
                            }
                        });

                        try {
                            BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                            final String line = in.readLine();
                            while (line != null) {
                                Log.d("ServerActivity", line);
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // TODO
                                        if(line.equals("HI")){
                                            serverStatus.setText("Hello there!");
                                        }
                                        else if(line.equals("How are you?")){
                                            serverStatus.setText("Fine thanks.");
                                        }
                                        else if(line.equals("code123")){
                                            serverStatus.setText("Password correct. Welcome back.");
                                        }
                                    }
                                });
                            }
                            break;
                        } catch (final Exception e) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    serverStatus.setText("Oops. Connection interrupted. Please reconnect your devices. " + e.getMessage());
                                }
                            });
                            e.printStackTrace();
                        }
                    }
                } else {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            serverStatus.setText("Couldn't detect internet connection.");
                        }
                    });
                }
            } catch (final Exception e) {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        serverStatus.setText("Error " + e.getMessage());
                    }
                });
                e.printStackTrace();
            }
        }
    }

    private String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("ServerActivity", ex.toString());
        }

        return null;
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
}
