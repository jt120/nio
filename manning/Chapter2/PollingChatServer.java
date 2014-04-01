package Chapter2;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class PollingChatServer implements Runnable
{
  static private final int sleepTime = 100; //SLEEP_TIME
  private int port;
  private Vector sockets = new Vector();
  private Set closedSockets = new HashSet();

  public PollingChatServer( int port ) {
    this.port = port;
    Thread t = new Thread( this, "PollingChatServer" );
    t.start();
  }

  public void run() {
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.configureBlocking( false );
      ServerSocket ss = ssc.socket();
      InetSocketAddress isa = new InetSocketAddress( port );
      ss.bind( isa );

      ByteBuffer buffer = ByteBuffer.allocate( 4096 );

      System.out.println( "Listening on port "+port );

      while (true) {
        SocketChannel sc = ssc.accept();

        if (sc != null) {
          Socket newSocket = sc.socket();
          System.out.println( "Connection from "+newSocket );
          newSocket.getChannel().configureBlocking( false );
          sockets.addElement( newSocket );
        }

        for (Enumeration e = sockets.elements();
             e.hasMoreElements();) {
          Socket socket = null;
          try {
            socket = (Socket)e.nextElement();
            SocketChannel sch = socket.getChannel();
            buffer.clear();
            sch.read( buffer );
            if (buffer.position() > 0) {
              buffer.flip();
              System.out.println( "Read "+buffer.limit()+
                                  " bytes from "+sch.socket() );
              sendToAll( buffer );
            }
          } catch( IOException ie ) {
            closedSockets.add( socket );
          }
        }

        removeClosedSockets();

        try {
          Thread.sleep( sleepTime );
        } catch( InterruptedException ie ) {}
      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  private void sendToAll( ByteBuffer bb ) {
    for (Enumeration e=sockets.elements();
         e.hasMoreElements();) {
      Socket socket = null;
      try {
        socket = (Socket)e.nextElement();
        SocketChannel sc = socket.getChannel();
        bb.rewind();
        while (bb.remaining()>0) {
          sc.write( bb );
        }
      } catch( IOException ie ) {
        closedSockets.add( socket );
      }
    }
  }

  private void removeClosedSockets() {
    for (Iterator it=closedSockets.iterator(); it.hasNext();) {
      Socket socket = (Socket)it.next();
      sockets.remove( socket );
      System.out.println( "Removed "+socket );
    }
    closedSockets.clear();
  }

  static public void main( String args[] ) throws Exception {
    int port = Integer.parseInt( args[0] );
    new PollingChatServer( port );
  }
}
