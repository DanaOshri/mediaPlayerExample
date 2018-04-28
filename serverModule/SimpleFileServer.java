import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class SimpleFileServer {

	public final static int SOCKET_PORT = 8000;  // you may change this
	public final static String FILE_TO_SEND = "C:\\Users\\ido16\\Desktop\\tudo2.mp3";  // you may change this

	public static void main (String [] args ) throws IOException {
		BufferedInputStream file_reader = null;
		OutputStream socket_write = null;
		ServerSocket servsock = null;
		Socket sock = null;
		File myFile = new File (FILE_TO_SEND);

		try {
			servsock = new ServerSocket(SOCKET_PORT);
			
			while (true) {
				System.out.println("Waiting...");
				try {
					//get connection
					sock = servsock.accept();
					socket_write = sock.getOutputStream();
					System.out.println("Accepted connection : " + sock);

					// read file from path
					byte [] mybytearray  = new byte [(int)myFile.length()];
					file_reader = new BufferedInputStream(new FileInputStream(myFile));
					file_reader.read(mybytearray,0,mybytearray.length);

					//send file_size to socket
					socket_write.write(ByteBuffer.allocate(4).putInt(mybytearray.length).array(),0,4);          
					System.out.println("send size : " + mybytearray.length + "\n");

					//send file to socket
					socket_write.write(mybytearray,0,mybytearray.length);
					socket_write.flush();
					System.out.println("Done.");
					break;
				}
				finally {
					if (file_reader != null) file_reader.close();
				}
			}
			
			DataOutputStream data_out = new DataOutputStream(socket_write);
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while(true)	{													// receive file
				data_out.writeUTF(br.readLine());
				System.out.println("send");
			} 

		}
		finally {
			if (servsock != null) servsock.close();
			if (file_reader != null) file_reader.close();
			if (servsock != null) servsock.close();
		}
		
	}
}



