package com.example.uprajsinghbhullar.myapplication;

/**
 * Created by upraj singh bhullar on 19-Mar-17.
 */

        import java.io.ByteArrayOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.Socket;
        import java.lang.Runnable;
        import java.net.UnknownHostException;
        import android.os.AsyncTask;
        import android.widget.TextView;

public class Client extends AsyncTask<Void, Void, Void> {

    String dstAddress;
    int dstPort;
    String response = "";
    TextView serverText ;

    Client(String addr, int port,TextView textResponse) {
        System.out.println("inside client constructor port:"+port + "ip:"+addr);
        dstAddress = addr;
        dstPort = port;
        this.serverText=textResponse;
    }
    @Override
    protected Void doInBackground(Void... arg0) {
        System.out.println("inside background process ");

        Socket socket = null;

        try {
            socket = new Socket(dstAddress, dstPort);

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(
                    1024);
            byte[] buffer = new byte[1024];

            int bytesRead;
            InputStream inputStream = socket.getInputStream();

			/*
			 * notice: inputStream.read() will block if no data return
			 */
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                response += byteArrayOutputStream.toString("UTF-8");
            }

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        serverText.setText(response);
    }

}
