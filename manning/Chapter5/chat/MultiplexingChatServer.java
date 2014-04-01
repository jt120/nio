package Chapter5.chat;

import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.logging.*;

import Chapter5.logging.LoggerGUI;

public class MultiplexingChatServer implements Runnable
{
  private int port;
  private Vector sockets = new Vector();
  private Set closedSockets = new HashSet();
  static private Logger logger;

  static {
    logger = Logger.getLogger( "org.jdk14tut.chat" );
    //logger.setLevel( Level.ALL );
  }

  public MultiplexingChatServer( int port ) {
    new LoggerGUI();

    this.port = port;
    logger.config( "Will listen on port "+port );

    Thread t = new Thread( this, "MultiplexingChatServer" );
    t.start();
    logger.fine( "Started background I/O thread" );
  }

  public void run() {
    logger.fine( "Background thread started" );
    try {
      ServerSocketChannel ssc = ServerSocketChannel.open();
      logger.finer( "Opened server socket channel" );
      ssc.configureBlocking( false );
      ServerSocket ss = ssc.socket();
      InetSocketAddress isa = new InetSocketAddress( port );
      ss.bind( isa );
      logger.finer( "server socket channel bound to "+isa );

      Selector selector = Selector.open();
      ssc.register( selector, SelectionKey.OP_ACCEPT );
      logger.finer( "Registered "+ssc+" with selector" );

      logger.info( "Listening on port "+port );

      ByteBuffer buffer = ByteBuffer.allocate( 4096 );
      logger.finer( "Allocated buffer, "+
                    "capacity="+buffer.capacity() );

      while (true) {
        logger.finer( "Going into select()" );
        int numKeys = selector.select();
        logger.finer( "Returned from select()" );
        if (numKeys>0) {
          Set skeys = selector.selectedKeys();
          logger.finer( "select() returned with "+skeys.size()+
                        " keys in selected set" );
          Iterator it = skeys.iterator();
          while (it.hasNext()) {
            SelectionKey rsk = (SelectionKey)it.next();
            logger.finer( "Selection key: "+rsk );
            int rskOps = rsk.readyOps();
            if ((rskOps & SelectionKey.OP_ACCEPT) ==
                SelectionKey.OP_ACCEPT) {
              logger.finer( "Selection key is ACCEPT type" );
              Socket socket = ss.accept();
              logger.info( "Connection from "+socket );
              sockets.addElement( socket );
              SocketChannel sc = socket.getChannel();
              sc.configureBlocking( false );
              sc.register( selector, SelectionKey.OP_READ );
              logger.finer( "Registered "+sc+" with selector" );
              selector.selectedKeys().remove( rsk );
            } else if ((rskOps & SelectionKey.OP_READ) ==
                       SelectionKey.OP_READ) {
              logger.finer( "Selection key is READ type" );
              SocketChannel ch = (SocketChannel)rsk.channel();
              selector.selectedKeys().remove( rsk );
              buffer.clear();
              ch.read( buffer );
              buffer.flip();
              logger.finer( "Read "+buffer.limit()+
                                  " bytes from "+ch.socket() );
              if (buffer.limit()==0) {
                logger.info( "closing on 0 read" );
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
      logger.info( "Error in main I/o loop: "+ie );
    }
  }

  private void sendToAll( ByteBuffer bb ) {
    logger.finer( "Sending buffer, "+bb.limit()+" bytes" );
    for (Enumeration e=sockets.elements();
         e.hasMoreElements();) {
      Socket socket = null;
      try {
        socket = (Socket)e.nextElement();
        logger.fine( "Sending to "+socket );
        SocketChannel sc = socket.getChannel();
        logger.finer( "Starting write to "+socket );
        bb.rewind();
        while (bb.remaining()>0) {
          sc.write( bb );
        }
        logger.finer( "Finished write to "+socket );
      } catch( IOException ie ) {
        logger.info( "closing on write exception" );
        closedSockets.add( socket );
      }
    }
  }

  private void close( Socket socket ) {
    closedSockets.add( socket );
  }

  private void removeClosedSockets() {
    for (Iterator it=closedSockets.iterator(); it.hasNext();) {
      Socket socket = (Socket)it.next();
      sockets.remove( socket );
      logger.fine( "Removed closed socket "+socket );
    }
    closedSockets.clear();
  }

  static public void main( String args[] ) throws Exception {
    int port = Integer.parseInt( args[0] );
    new MultiplexingChatServer( port );
  }
}
