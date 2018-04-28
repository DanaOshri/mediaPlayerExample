package com.example.idoabu.test1;

import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

import android.os.AsyncTask;


public class MainActivity extends AppCompatActivity {
    public final static int SOCKET_PORT = 8000;      // you may change this
    public final static String SERVER = "10.0.2.2";  // localhost
    MediaPlayer player = new MediaPlayer();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void play(View v) {
        new Download_mp3_Task().execute();
    }

    public void pause(View v) {
        if (player != null) {
            player.pause();
        }
    }

    private class Download_mp3_Task extends AsyncTask<String, Void, Socket> {
        File tempMp3;
        protected Socket doInBackground(String... urls) {
            int file_size;
            Socket sock = null;

            try {
                //connecting to server
                sock = new Socket(SERVER, SOCKET_PORT);
                System.out.println("Connecting... : " + sock);

                //initial socket_input_stream and file_write
                InputStream socket_input_stream = sock.getInputStream();

                tempMp3 = File.createTempFile("current", "mp3", getCacheDir());
                tempMp3.deleteOnExit();
                BufferedOutputStream file_write = new BufferedOutputStream(new FileOutputStream(tempMp3));

                // read file size from socket - first 4 bytes
                file_size = getSize(socket_input_stream);
                System.out.println("read size : " + file_size + "\n");

                //read file to memory
                getAudioFile(socket_input_stream,file_write,file_size);

                playerLoader(tempMp3);
                listen(socket_input_stream);
            }

            catch (Exception e) {
                Log.i("error", "1");
                System.out.println(e.getMessage()); // prints "hu?"
            }
            return sock;
        }
    }

    public void playerLoader(File file) throws IOException{
        FileInputStream fis = new FileInputStream(file);
        player.setDataSource(fis.getFD());
        player.prepare();
    }

    public int getSize (InputStream socket_input_stream) throws IOException{
        byte[] file_size_bytes = new byte[4];
        int bytesRead = 0;
        bytesRead = socket_input_stream.read(file_size_bytes, 0, 4);
        return ByteBuffer.wrap(file_size_bytes).getInt();
    }
    public void getAudioFile(InputStream socket_input_stream, BufferedOutputStream file_write, int file_size) throws IOException{
        //read from socket
        byte[] mybytearray = new byte[file_size + 1];
        int bytesRead = 0;
        int current = 0;
        do {                                                            // receive file
            bytesRead = socket_input_stream.read(mybytearray, current, (file_size - current));
            if (bytesRead > 0) current += bytesRead;
        } while (bytesRead > 0);

        System.out.println("File downloaded (" + current + " bytes read)");
        //write to file
        file_write.write(mybytearray, 0, file_size);
        file_write.close();
    }
    public void listen (InputStream socket_input_stream){
        try {
            //connecting to server
            //Socket sock = new Socket(SERVER, SOCKET_PORT);
            //Log.i("success","---------------------------------open socket---------------------------------");

            //initial socket_input_stream and file_write
            DataInputStream data_in = new DataInputStream(socket_input_stream);
            System.out.println(data_in == null);
            // read from socket
            String msg = "";
            while(true)	{
                // receive file
                msg = data_in.readUTF();
                System.out.println("success");
                if(msg.equals("play")) {
                    Log.i("success", "---------------------------------get play---------------------------------");
                    player.start();
                }
                else if(msg.equals("pause")) {
                    Log.i("success", "---------------------------------get pause---------------------------------");
                    player.pause();
                }
            }

        }
        catch (Exception e) {
            Log.i("error", "1213123");
            System.out.println(e.getMessage()); // prints "hu?"
        }
    }
}

