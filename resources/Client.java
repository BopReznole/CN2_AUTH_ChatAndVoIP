import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;
import java.net.DatagramPacket;
import java.io.IOException;
import java.net.SocketException;
import java.net.UnknownHostException;


public class Client {

	private DatagramSocket datagramSocket; /* create socket datagramSocket */
	private InetAddress inetAddress; /* create IP address inetAddress, to set it as IP of server */
	private byte[] buffer;  /* create buffer to store messages, size = 1024 bytes */
	
	public Client(DatagramSocket datagramSocket, InetAddress inetAddress) { /* constructor Client,
	 initialize datagramSocket, inetAddress */
		this.datagramSocket = datagramSocket;
		this.inetAddress = inetAddress;
	}
	
	public void sendThenReceive() { /* method, client sends message and receives it back */
		
		Scanner scanner = new Scanner(System.in); /* input from keyboard */
		while (true) { /* client always running, sending and receiving packets */
			try {
				String messageToSend = scanner.nextLine(); /* get string messageToSend from keyboard*/
				buffer = messageToSend.getBytes(); /* convert messageToSend to bytes and put to buffer */
				DatagramPacket datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, 1234);
				/* get all data from buffer, create a datagramPacket, send to IP inetAddress and port of the server */
				datagramSocket.send(datagramPacket); /* send datagramPacket */
				datagramSocket.receive(datagramPacket); /* wait until server sends back and get that to buffer */
				String messageFromServer = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
				/* create string from datagramPacket byte array by server, offset=0 */
				System.out.println("The server says you said " + messageFromServer); /* print message messageFromSever */
			    }
			catch (IOException e) { /* in case of error */
				e.printStackTrace();
				break;
			    }
		}
	}
	
	public static void main(String[] args) throws SocketException, UnknownHostException  {
    	DatagramSocket datagramSocket = new DatagramSocket(); /* set datagramSocket */
    	InetAddress inetAddress = InetAddress.getByName("Localhost"); /* set to inetAddress the IP of server Localhost */
    	Client client = new Client(datagramSocket, inetAddress); /* pass datagramSocket and inetAddress
    	 to constructor Client */
    	System.out.println("Send datagram packets to a server");
    	client.sendThenReceive(); /* call method receiveThenSend */
    }
	
	
	
	
}
