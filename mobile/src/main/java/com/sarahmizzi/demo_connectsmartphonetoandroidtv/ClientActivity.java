package com.sarahmizzi.demo_connectsmartphonetoandroidtv;

import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class ClientActivity extends Activity {
    public static final int SERVERPORT = 8080;

    private EditText serverIp;
    private Button connectPhones;
    private String serverIpAddress = "";
    private boolean connected = false;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clientactivity);

        serverIp = (EditText) findViewById(R.id.server_ip);
        connectPhones = (Button) findViewById(R.id.connect_phones);
        connectPhones.setOnClickListener(connectListener);
    }

    private View.OnClickListener connectListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(!connected){
                serverIpAddress = serverIp.getText().toString();
                if(!serverIpAddress.equals("")){
                    Thread cThread = new Thread(new ClientThread());
                    cThread.start();
                }
            }
        }
    };

    public class ClientThread implements Runnable{
        @Override
        public void run() {
            try{
                InetAddress serverAddr = InetAddress.getByName(serverIpAddress);
                Log.d("ClientActivity", "C: Connecting...");
                Socket socket = new Socket(serverAddr, SERVERPORT);
                connected = true;
                while (connected){
                    try{
                        Log.d("ClientActivity", "C: Sending command.");
                        PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                        // Issue Commands
                        out.println("Hey Server!");
                        Log.d("ClientActivity", "C: Sent.");
                    }
                    catch (Exception e){
                        Log.e("ClientActivity", "S: Error", e);
                    }
                }
                socket.close();
                Log.d("ClientActivity", "C: Closed.");
            }
            catch (Exception e){
                Log.e("ClientActivity", "C: Error", e);
                connected = false;
            }
        }
    }
}
