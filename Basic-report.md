## Report

### 1. Περιγραφή κώδικα
1. Chat με UDP
2. VoIP
3. Chat με TCP
4. App.java
	- Σε αυτήν την κλάση υπάρχει η main συνάρτηση του προγράμματος. Εδώ αρχικοποιείται και τρέχει το πρόγραμματα.
	- Συγκεκριμένα:
		1. Εισάγουμε τις απαραίτητες βιβλιοθήκες και πακέτα:
			```
			package com.cn2.communication;

			import java.io.*;
			import java.net.*;

			import javax.swing.JFrame;
			import javax.swing.JTextField;
			import javax.sound.sampled.LineUnavailableException;
			import javax.swing.JButton;
			import javax.swing.JTextArea;
			import javax.swing.JScrollPane;

			import java.awt.event.ActionListener;
			import java.awt.event.ActionEvent;
			import java.awt.*;
			import java.awt.event.*;
			import java.awt.Color;
			import java.lang.Thread;
			```
			- το πακέτο της εφαρμογής μας
			- την βασική βιβλιθήκη java.io
			- την βιβλιθήκη java.net για networking λειτουργίες
			- τις βιβλιοθήκες javax.swing και java.awt για το GUI	
			- την βιβλιθήκη java.lang.Thread για την χρήση Threads (νημάτων) ώστε να γίνονται παράλληλα διάφορες διεργασίες
		2. Στην συνέχεια ορίζουμε την κλάση της εφαρμογής που περιέχει:
			- Το gui και τις μεταβλητές του (όπως τα κουμπιά)
			- Τις απαραίτητες μεταβλητές για την χρήση του διαδικτύου
			- Την αρχικοποίηση των μεταβλητών αυτών.
			```
			{ // initialize network variables using non-static initialization block
			
			try {
				remoteAddress = InetAddress.getByName("192.168.1.20"); // initialize to remoteAddress the IP of remote 
				chatUDP = new UDPChat(new DatagramSocket(1234), remoteAddress); /* initialize chatUDP, pass DatagramSocket from port 1234 and
				remoteAddress to constructor UDPChat */ 
				voip = new VoIP(new DatagramSocket(1243), remoteAddress); /* initialize voip, pass DatagramSocket from port 1243 and
				remoteAddress to constructor VoIP */
				
				//	chatTCP = new TCPChatSender(new Socket("192.168.1.20", 2345)); /* initialize chatTCP, pass Socket from port 2345 and 
				//	IP to constructor TCPChatSender */
				//	chatTCP = new TCPChatReceiver(new ServerSocket(2345)); //initialize chatTCP, pass ServerSocket from port 2345 to constructor TCPChatReceiver 
			}
			catch (Exception e) { // in case of error
				e.printStackTrace();
				System.exit(1);
			}
			}
			```
				- Εισάγουμε στην αρχή την διεύθυνση IP του peer μας στην μεταβλητή `remoteAddress`
				- Κατασκευάζουμε τις μεταβλητές `chatUDP` και `voip` περνώντας #todo
				- Σε σχόλιο έχουμε την μεταβλητή `chatTCP` επειδή θα την χρησιμοποιήσουμε μόνο στην περίπτωση που ενεργοποιήσουμε την χρήση TCP για το chat.
				- Έχουμε και ένα μέτρο στο τέλος για να εντοπίζει σφάλματα και να τερματίζει την εφαρμογή.
				

			
### 2. Απεικόνιση πακέτων μέσω Wireshark
1. Πακέτα κειμένου
2. Πακέτα φωνής
