package Chapter2;
// $Id$

import java.applet.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;

public class ChatClient extends Panel implements Runnable
{
  // Components for the visual display of the chat windows
  private TextField tf = new TextField();
  private TextArea ta = new TextArea();

  // The socket connecting us to the server
  private Socket socket;

  // The streams we communicate to the server; these come
  // from the socket
  private OutputStream out;
  private InputStream in;

  // Constructor
  public ChatClient( String host, int port ) {

    // Set up the screen
    setLayout( new BorderLayout() );
    add( "North", tf );
    add( "Center", ta );

    // We want to receive messages when someone types a line
    // and hits return, using an anonymous class as
    // a callback
    tf.addActionListener( new ActionListener() {
      public void actionPerformed( ActionEvent e ) {
        processMessage( e.getActionCommand() );
      }
    } );

    // Connect to the server
    try {

      // Initiate the connection
      socket = new Socket( host, port );

      // We got a connection!  Tell the world
      System.out.println( "connected to "+socket );

      // Let's grab the streams and create DataInput/Output streams
      // from them
      in = socket.getInputStream();
      out = socket.getOutputStream();

      // Start a background thread for receiving messages
      new Thread( this ).start();
    } catch( IOException ie ) { System.out.println( ie ); }
  }

  // Gets called when the user types something
  private void processMessage( String message ) {
    try {

      // Send it to the server
      out.write( message.getBytes() );

      // Clear out text input field
      tf.setText( "" );
    } catch( IOException ie ) { System.out.println( ie ); }
  }

  // Background thread runs this: show messages from other window
  public void run() {
    try {

      byte buffer[] = new byte[4096];

      // Receive messages one-by-one, forever
      while (true) {

        // Get the next message
        int r = in.read( buffer );
        String message = new String( buffer, 0, r );

        // Print it to our text window
        ta.append( message+"\n" );
      }
    } catch( IOException ie ) { System.out.println( ie ); }
  }
}
