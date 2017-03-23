/**
 * Created by upraj singh bhullar on 18-Mar-17.
 */
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.uprajsinghbhullar.myapplication.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

import static android.content.ContentValues.TAG;

public class mainactivity extends AppCompatActivity implements View.OnClickListener {
    // class lass level declarations
    private static int DEF_VALUE = 500;
    private static int MIN_VALUE = 0;
    private static int MAX_VALUE = 1000;
    Button sendBTN;
    TextView serverText;
    EditText usermsg;
    private static final  String SERVER_IP = "137.207.82.52";
    private static final int SERVERPORT = 6677;
    private Socket socket;
    Thread ClientThread = null;
    String responseText = null;
    String clientText = null;
    private boolean isRunning = false;
    BufferedReader in;
    private AtomicInteger i = new AtomicInteger(DEF_VALUE);
    private Thread t1 = null;
    private Thread t2 = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sendBTN=(Button)findViewById(R.id.next_button);
        sendBTN.setOnClickListener(this);

    }
    mainactivity(){
        initThreads();
    }
    private void initThreads() {
        Log.i(TAG, "Initializing Threads...");

        t1 = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Starting T1.");
                while (true) {

                        System.out.println("Inside Run" + responseText);
                        try {
                            socket = new Socket(SERVER_IP, SERVERPORT);
                            System.out.println("Connection DONE");
                            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            responseText = in.readLine();
                            if(responseText != null) {
                                System.out.println("Message From Server" + responseText);
                                serverText = (TextView) findViewById(R.id.server_text);
                                serverText.append("Server:" + responseText);
                            }
                            in.close();
                            socket.close();
                            System.out.println("Closing socket");

                        } catch (IOException e) {
                            return;
                        } catch (Exception e) {
                            return;
                        }
                    }

            }
        });

        t2 = new Thread(new Runnable() {

            @Override
            public void run() {
                Log.i(TAG, "Starting T1.");
                while (true) {
                   if(usermsg != null){
                       serverText = (TextView) findViewById(R.id.server_text);
                       serverText.append("Client:" + clientText);
                       usermsg = null;
                   }
                }
            }
        });

        t1.start();
        t2.start();
    }
    @Override
    public void onClick(View v) {
        Thread t = new Thread(){
            @Override
            public void run(){
                try {

                    usermsg = (EditText)findViewById(R.id.msg_description);
                    clientText = usermsg.getText().toString();
                    System.out.println("Message: "+clientText);
                    System.out.println("Starting Connection");
                    socket = new Socket( SERVER_IP,SERVERPORT );
                    System.out.println("Connection DONE");
                    OutputStream outstream = socket.getOutputStream();
                    PrintWriter out = new PrintWriter(outstream);
                    out.print(clientText );
                    out.flush();
                    out.close();
                    outstream.close();
                    socket.close();
                    System.out.println("Closing socket");
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
}
