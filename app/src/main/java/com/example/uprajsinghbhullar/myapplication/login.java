package com.example.uprajsinghbhullar.myapplication;

import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.Socket;
import java.io.PrintWriter;
import java.io.BufferedReader;
import java.net.UnknownHostException;
import java.io.IOException;
import android.widget.Toast;
import android.widget.TextView;
import android.widget.EditText;
public class login extends AppCompatActivity implements View.OnClickListener,Runnable {

    Button sendBTN;
    TextView serverText;
    EditText usermsg;
    private static final  String SERVER_IP = "137.207.82.53";
    private static final int SERVERPORT = 6668;
    public  Socket socket;
    String clientText;
    String response;
    Boolean setFlag = Boolean.FALSE;
    BufferedReader in;
    OutputStream outstream;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sendBTN=(Button)findViewById(R.id.next_button);
        sendBTN.setOnClickListener(this);
        serverText = (TextView) findViewById(R.id.server_text);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

    }
    @Override
    public void run() {


        }


    public void onClick( View v) {

        Thread t = new Thread(){
            @Override
            public void run(){
                try {
                       usermsg = (EditText) findViewById(R.id.msg_description);
                       clientText = usermsg.getText().toString();
                       String tempText = "";
                       tempText = tempText + clientText + "\n";
                        response = "Client:" + tempText;
                        setFlag = Boolean.TRUE;
                       System.out.println("Starting Connection");
                        socket = new Socket(SERVER_IP, SERVERPORT);
                       System.out.println("Connection DONE");
                    System.out.println("Message: " + clientText);
                    outstream =socket.getOutputStream();
                       PrintWriter out = new PrintWriter(outstream);
                       out.print(clientText);
                       out.flush();
                       //out.close();
                       //outstream.close();
                       //System.out.println("Closing socket");
                        Client myClient = new Client("137.207.82.53", 6668,socket);
                        myClient.execute();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.d("Server",response);
                            serverText.append(response);
                        }
                    });
                } catch (UnknownHostException e){
                    System.out.println("There was an Unknown Erorr:");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("There was an IOException:");
                    e.printStackTrace();
                }
            }
        };
        t.start();

        Toast.makeText(this, "Message Sent...", Toast.LENGTH_SHORT).show();
    }

    class Client extends AsyncTask<String, String, String> {

        String dstAddress;
        int dstPort;
        Socket socket;

        Client(String addr, int port,Socket socket) {
            System.out.println("inside client constructor port:"+port + "ip:"+addr);
            dstAddress = addr;
            dstPort = port;
            this.socket = socket;
        }
        @Override
        protected String doInBackground(String... arg0) {
            System.out.println("inside background process ");
            try {
                byte[] buffer = new byte[255];
                //ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(255);
                ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(buffer);
                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                System.out.println("isInputShutdown"+socket.isInputShutdown());
                socket.setKeepAlive(true);
                socket.setOOBInline(true);
                while ((bytesRead = inputStream.read(buffer)) != -1) {

                        System.out.println("inside while ");
                        //byteArrayInputStream.read(buffer, 0, bytesRead);
                        //byteArrayInputStream.read(buffer);
                        response = new String(buffer);
                        System.out.println("Server Response" + response);
                    inputStream.close();
                    outstream.close();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                serverText.append("Server:"+response);
                            }
                        });

                    }

               /* while(true) {
                    System.out.println("inside while ");
                    bytesRead = inputStream.read(buffer);
                        System.out.println("waiting for server message");
                        //byteArrayInputStream.read(buffer, 0, bytesRead);
                    response = new String(buffer);
                    System.out.println("Server Response" + response);
                        System.out.println("Server Response" + response);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                serverText.append(response);
                            }
                        });

                } */

            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();

                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.d("error", e.getMessage());
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        protected void onProgressUpdate(String... progress) {
            serverText.append("Progress:"+response);
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            serverText.append("Server Closed! Bye Bye");
            //serverText.append("Server:"+response);

        }

    }


}
