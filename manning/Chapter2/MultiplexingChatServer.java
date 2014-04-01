package Chapter2;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;

public class MultiplexingChatServer implements Runnable
{
  private int port;
  private Vector sockets = new Vector();
  private Set closedSockets = new HashSet();

  public MultiplexingChatServer( int port ) {
    this.port = port;
    Thread t = new Thread( this, "MultiplexingChatServer" );
    t.start();
  }

  public void run() {
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      ssc.configureBlocking( false );
      ServerSocket ss = ssc.socket();
      InetSocketAddress isa = new InetSocketAddress( port );
      ss.bind( isa );

      Selector selector = Selector.open();
      ssc.register( selector, SelectionKey.OP_ACCEPT );
      System.out.println( "Listening on port "+port );

      ByteBuffer buffer = ByteBuffer.allocate( 4096 );

      while (true) {
        int numKeys = selector.select();
        if (numKeys>0) {
          Set skeys = selector.selectedKeys();
          Iterator it = skeys.iterator();
          while (it.hasNext()) {
            SelectionKey rsk = (SelectionKey)it.next();
            int rskOps = rsk.readyOps();
            if ((rskOps & SelectionKey.OP_ACCEPT) ==
                SelectionKey.OP_ACCEPT) {
              Socket socket = ss.accept();
              System.out.println( "Connection from "+socket );
              sockets.addElement( socket );
              SocketChannel sc = socket.getChannel();
              sc.configureBlocking( false );
              sc.register( selector, SelectionKey.OP_READ );
              it.remove();
            } else if ((rskOps & SelectionKey.OP_READ) ==
                       SelectionKey.OP_READ) {
              SocketChannel ch = (SocketChannel)rsk.channel();
              it.remove();
              buffer.clear();
              ch.read( buffer );
              buffer.flip();
              System.out.println( "Read "+buffer.limit()+
                                  " bytes from "+ch.socket() );
              if (buffer.limit()==0) {
                System.out.println( "closing on 0 read" );
                rsk.cancel();
                Socket socket = ch.socket();
                close( socket );
              } else {
                sendToAll( buffer );
              }
            }
          }

          removeClosedSockets();
        }
      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  // This method is identical to
  // PollingChatServer.sendToAll()
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

  private void close( Socket socket ) {
    closedSockets.add( socket );
  }

  // This method is identical to
  // PollingChatServer.removeClosedSockets()
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
    new MultiplexingChatServer( port );
  }
}
