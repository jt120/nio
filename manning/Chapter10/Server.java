package Chapter10;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.prefs.*;
import java.util.regex.*;

public class Server implements Runnable
{
  static private final int defaultPort = 5555;
  private int port;
  private Preferences prefs =
    Preferences.userNodeForPackage( getClass() );
  private ServerSocketChannel ssc;

  public Server() {
    port = getPort();
    addPrefsListener();
    new Thread( this ).start();
  }

  public Preferences prefs() {
    return prefs;
  }

  private int getPort() {
    int p = prefs.getInt( "port", defaultPort );
    return p;
  }

  private void addPrefsListener() {
    prefs.addPreferenceChangeListener(
      new PreferenceChangeListener() {
        public void preferenceChange( PreferenceChangeEvent pce ) {
          System.out.println( "Change: ("+pce.getNode()+") key="+
                              pce.getKey()+" value="+
                              pce.getNewValue() );
          if (pce.getKey().equals( "port" )) {
            try {
              updatePort();
            } catch( IOException ie ) {
              ie.printStackTrace();
            }
          }
        }
      } );
  }

  private void updatePort() throws IOException {
    int p = getPort();
    if (p != port) {
      changePort( p );
    }
  }

  private void changePort( int port ) throws IOException {
    this.port = port;
    ssc.close();
  }

  public void run() {
    try {
      while (true) {
        // Listen on port <port>, all addresses
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking( true );
        byte anyIP[] = { 0, 0, 0, 0 };
        InetAddress localhost = InetAddress.getByAddress( anyIP );
        InetSocketAddress isa =
          new InetSocketAddress( localhost, port );
        ssc.socket().bind( isa );

        // Accept connections
        while (true) {
          try {
            System.out.println( "Listening on "+ssc );
            SocketChannel sc = ssc.accept();
            dealWithConnection( sc );
          } catch( AsynchronousCloseException ace ) {
            System.out.println( "Reopening...." );
            break;
          }
        }
      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  protected void dealWithConnection( SocketChannel sc )
      throws IOException {
    System.out.println( "Got connection "+sc );
    sc.close();
  }

  static public void main( String args[] ) throws IOException {
    Server server = new Server();

    new ServerCommandLine( System.in, System.out );
  }
}
