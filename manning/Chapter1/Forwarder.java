package Chapter1;
import java.io.*;
import java.net.*;
import java.nio.*;
import java.nio.channels.*;
import java.util.*;
import java.util.regex.*;

public class Forwarder
{
  static private final int bufferSize = 0x4000;

  // Defines the format of the config file lines
  private static final int LOCAL_PORT_POS = 0;
  private static final int HOSTNAME_POS = 1;
  private static final int PORT_POS = 2;
  private static final int SOURCES_POS = 3;

  public Forwarder() {
  }

  public void readConfig( String configFilename )
      throws IOException {
    FileReader fr = new FileReader( configFilename );
    LineNumberReader lnr = new LineNumberReader( fr );
    while (true) {
      String line = lnr.readLine();
      if (line==null)
        break;
      Pattern pattern = Pattern.compile( "\\s+" );
      String strings[] = pattern.split( line );
      if (strings.length < SOURCES_POS) {
        System.err.println( "Config file syntax error at "+
                            configFilename+":"+
                            lnr.getLineNumber() );
        System.exit( 1 );
      }

      // First, the local forwarding port
      int forwardingPort = Integer.parseInt( strings[LOCAL_PORT_POS] );

      // Then the destination address
      InetAddress destAddress =
        InetAddress.getByName( strings[HOSTNAME_POS] );

      // Then the destination port
      int destPort = Integer.parseInt( strings[PORT_POS] );

      // Finally, zero or more permitted sources
      InetAddress sources[] = new InetAddress[strings.length-SOURCES_POS];
      for (int i=SOURCES_POS; i<strings.length; ++i) {
        sources[i-SOURCES_POS] = InetAddress.getByName( strings[i] );
      }
      AddressSet allowedSources = new AddressSet( sources );

      addForward( forwardingPort, destAddress,
                  destPort, allowedSources );
    }
  }

  public void addForward( int forwardingPort,
                                       InetAddress destAddress,
                                       int destPort,
                                       AddressSet allowedSources ) {
    InetSocketAddress destSocketAddress =
      new InetSocketAddress( destAddress, destPort );
    ForwarderListenerThread flt =
      new ForwarderListenerThread( forwardingPort,
                                   destSocketAddress,
                                   allowedSources );
    flt.start();
  }

  class ForwarderListenerThread extends Thread
  {
    private int forwardingPort;
    private SocketAddress destAddress;
    private AddressSet allowedSources;
    private HashSet forwardsConnections = new HashSet();
    private Object connectionsLock = new Object();
    private boolean shutdown = false;
    private ServerSocketChannel ssc;

    public ForwarderListenerThread( int forwardingPort,
                                    SocketAddress destAddress,
                                    AddressSet allowedSources ) {
      this.forwardingPort = forwardingPort;
      this.destAddress = destAddress;
      this.allowedSources = allowedSources;
    }

    public void run() {
      try {
        ssc = ServerSocketChannel.open();
        ssc.configureBlocking( true );
        ServerSocket ss = ssc.socket();
        byte anyIP[] = { 0, 0, 0, 0 };
        InetAddress forwardingHost =
          InetAddress.getByAddress( anyIP );
        InetSocketAddress isa =
          new InetSocketAddress( forwardingHost, forwardingPort );
        ss.bind( isa );
        synchronized( connectionsLock ) {
          System.out.println( "Listening on "+isa );
          while (true) {
            SocketChannel source = ssc.accept();
            InetAddress connectingAddress =
              source.socket().getInetAddress();
            if (allowedSources.contains( connectingAddress )) {
              SocketChannel dest = SocketChannel.open();
              source.configureBlocking( true );
              dest.configureBlocking( true );
              dest.connect( destAddress );
              ForwarderThread forwardsThread =
                new ForwarderThread( this, source, dest );
              ForwarderThread backwardsThread =
                new ForwarderThread( this, dest, source );
              forwardsThread.start();
              backwardsThread.start();
              forwardsConnections.add( forwardsThread );
            } else {
              System.out.println( "Connection from "+
                                  connectingAddress+
                                  " refused" );
              try {
                source.close();
              } catch( IOException ie ) {
                System.err.println( "Problem disconnecting "+
                                    "rejected connection from "+
                                    connectingAddress );
                ie.printStackTrace();
              }
            }
          }
        }
      } catch( AsynchronousCloseException ace ) {
        System.err.println( "Closed forward "+this );
        // We don't call shutdown here, because this
        // exception is triggered by the close() call
        // inside shutdown -- this exception is a *result*
        // of shutting down, not an instigation to do so.
      } catch( IOException ie ) {
        System.err.println( "Exception forwarding "+this+": "+ie );
        ie.printStackTrace();
        shutdown();
      }
    }

    synchronized public void shutdown() {
      if (shutdown)
        return;

      try {
        System.out.println( "Closing "+ssc );
        ssc.close();
      } catch( IOException ie ) {
        System.err.println( "Error closing "+ssc );
        ie.printStackTrace();
      }

      synchronized( connectionsLock ) {
        for (Iterator it = forwardsConnections.iterator();
             it.hasNext();) {
          ForwarderThread ft = (ForwarderThread)it.next();
          System.out.println( "Closing "+ft );
          ft.shutdown();
        }
      }

      shutdown = true;
    }

    public void remove( ForwarderThread ft ) {
      if (forwardsConnections.contains( ft ))
        forwardsConnections.remove( ft );
    }

    public String toString() {
      return forwardingPort+"-->"+destAddress;
    }
  }

  class ForwarderThread extends Thread {
    private ForwarderListenerThread flt;
    private String description;
    private SocketChannel from;
    private SocketChannel to;
    private boolean shutdown = false;

    public ForwarderThread( ForwarderListenerThread flt,
                            SocketChannel from, SocketChannel to ) {
      this.flt = flt;
      this.from = from;
      this.to = to;

      Socket fromSocket = from.socket();
      Socket toSocket = to.socket();
      description =
        fromSocket.getInetAddress()+":"+fromSocket.getPort()+
        "-->"+
        toSocket.getInetAddress()+":"+toSocket.getPort();
    }

    public void run() {
      try {
        ByteBuffer buffer = ByteBuffer.allocateDirect( bufferSize );
        while (true) {
          from.read( buffer );
          if (buffer.position()==0) {
            System.out.println( "Closing on zero read: "+this );
            break;
          }
          System.out.println( this+" read "+buffer.position() );
          buffer.flip();
          while (buffer.remaining()>0) {
            int r = to.write( buffer );
            System.out.println( this+" wrote "+r+", remaining "+
                                buffer.remaining() );
          }
          buffer.clear();
        }
        shutdown();
      } catch( AsynchronousCloseException ace ) {
        System.err.println( "Closed forward "+this+": "+ace );
        shutdown();
      } catch( IOException ie ) {
        System.err.println( "Exception forwarding "+this+": "+ie );
        ie.printStackTrace();
        shutdown();
      }
    }

    public void shutdown() {
      if (shutdown)
        return;

      try {
        from.close();
      } catch( IOException ie ) {
        System.err.println( "Error closing from of "+this );
        ie.printStackTrace();
      }

      try {
        to.close();
      } catch( IOException ie ) {
        System.err.println( "Error closing to of "+this );
        ie.printStackTrace();
      }

      shutdown = true;
      flt.remove( this );
      System.err.println( "Closed forward "+this );
    }

    public String toString() {
      return description;
    }
  }

  static class AddressSet {
    private Set addresses = new HashSet();

    public AddressSet( InetAddress ias[] ) {
      for (int i=0; i<ias.length; ++i) {
        System.out.println( "as "+ias[i] );
        addresses.add( ias[i] );
      }
      System.out.println( "address set size "+addresses.size() );
    }

    public boolean contains( InetAddress ia ) {
      if (addresses.size()==0)
        return true;
      return addresses.contains( ia );
    }
  }

  static public void main( String args[] ) throws IOException {
    String configFilename = args[0];

    Forwarder forwarder = new Forwarder();
    forwarder.readConfig( configFilename );
  }
}
