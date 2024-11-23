import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.SocketException;
import java.io.IOException;


public class Server {
	
	private DatagramSocket datagramSocket; /* set socket datagramSocket */
	private byte[] buffer = new byte[1024]; /* store messages buffer, size = 1024 bytes */
	
	public Server(DatagramSocket datagramSocket) { /* constructor Server, initialize datagramSocket */
		this.datagramSocket = datagramSocket;
	}
	
	public void receiveThenSend() { /* method, server receives message and sends one back */
		while (true) { /* server always running, receiving and sending packets */
			try {
				DatagramPacket datagramPacket= new DatagramPacket(buffer, buffer.length); /* get 
				packet datagramPacket to buffer */
				datagramSocket.receive(datagramPacket); /* datagramPacket received from datagramSocket*/	
				InetAddress inetAddress = datagramPacket.getAddress(); /* get IP address inetAddress */
				int port = datagramPacket.getPort(); /* get port */
				String messageFromClient = new String(datagramPacket.getData(), 0, datagramPacket.getLength()); 
				/* create string from datagramPacket byte array by client, offset=0 */
				System.out.println("Message from client: " + messageFromClient); /* print message messageFromClient */
				datagramPacket = new DatagramPacket(buffer, buffer.length, inetAddress, port); /* reassign datagramPacket to */
				datagramSocket.send(datagramPacket); /* send it to the inetAddress and port of the client */
				}
			catch (IOException e) { /* in case of error */
				e.printStackTrace();
				break;
			    }
		}
	}

    public static void main(String[] args) throws SocketException {
    	DatagramSocket datagramSocket = new DatagramSocket(1234); /* create datagramSocket, server 
    	communicating from application port 1234 */
    	Server server = new Server(datagramSocket); /* pass datagramSocket to constructor Server */
    	server.receiveThenSend(); /* call method receiveThenSend */
    }
}






