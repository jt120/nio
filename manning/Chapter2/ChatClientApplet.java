package Chapter2;
// $Id$

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

import javax.swing.JFrame;

public class ChatClientApplet extends JFrame
{
  
  public static void main(String[] args) {
	  String host = "127.0.0.1";
	    int port = 8888;
	    ChatClientApplet a = new ChatClientApplet();
	    a.setLayout( new BorderLayout() );
	    a.add( "Center", new ChatClient( host, port ) );
	    a.setVisible(true);
}
}
