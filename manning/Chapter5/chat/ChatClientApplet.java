package Chapter5.chat;

import java.applet.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class ChatClientApplet extends Applet
{
  public void init() {
    String host = getParameter( "host" );
    int port = Integer.parseInt( getParameter( "port" ) );
    setLayout( new BorderLayout() );
    add( "Center", new ChatClient( host, port ) );
  }
}
