package Chapter11;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.*;
import javax.net.ssl.*;

public class CCServer implements Runnable
{
  // The port number we will listen on
  private int port;

  // Passphrase for the authentication and trust keystores
  static private final String passphrase = "serverpass";

  // Secure random source
  static private SecureRandom secureRandom;

  /**
   * Constructor: start the server socket
   */
  public CCServer( int port ) {
    this.port = port;

    Thread thread = new Thread( this );
    thread.start();
  }

  /**
   * Listen for incoming connections, and hand them off to
   * a SocketHandler
   */
  public void run() {
    try {
      // Prepare ServerSocket
      ServerSocketFactory ssf = getServerSocketFactory();
      SSLServerSocket ss =
        (SSLServerSocket)ssf.createServerSocket( port );

      // Request client authorization
      ss.setNeedClientAuth( true );

      // Listen for incoming connections
      System.out.println( "Listening on port "+port+"..." );
      while (true) {
        Socket socket = ss.accept();
        System.out.println( "Got connection from "+
                            socket.getInetAddress() );

        // Handle the socket in a separate thread
        new SocketHandler( socket );
      }
    } catch( GeneralSecurityException gse ) {
      gse.printStackTrace();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  /**
   * Handle a transaction: read credit card information,
   * verify it, and send the verification response code
   * back to the client.  This method is called from
   * a SocketHandler object running in a separate thread
   */
  private void handleSocket( Socket socket ) throws IOException {
    // Get the streams connected to the client
    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();
    DataInputStream din = new DataInputStream( in );
    DataOutputStream dout = new DataOutputStream( out );

    while (true) {
      // Read credit card info
      String ccnumber = din.readUTF();
      String ccexp = din.readUTF();

      // Verify credit card info
      boolean verified = checkCCDatabase( ccnumber, ccexp );

      // Send the verification response code back to
      // the client
      dout.writeBoolean( verified );
      dout.flush();

      // Print a log message
      String message = verified ? "Verified." : "Not verified.";
      System.out.println( "Verify: "+ccnumber+" "+ccexp+": "+
                          message );
    }
  }

  /**
   * Verify credit card information against some database.
   * This is just a demo, so we'll always return true.
   */
  private synchronized boolean checkCCDatabase( String ccnumber,
                                                String ccexp ) {
    return true;
  }

  /**
   * Create secure ServerSocketFactory.
   * Read authentication and trust information from
   * keystore files in local directory
   */
  private ServerSocketFactory getServerSocketFactory()
      throws GeneralSecurityException, IOException {

    // Read authentication keys.  These are used to authenticate
    // ourselves to the client
    KeyStore authKeyStore = KeyStore.getInstance( "JKS" );
    authKeyStore.load( new FileInputStream( "ccserver.auth" ),
                       passphrase.toCharArray() );

    // The KeyManagerFactory manages the authentication keys
    KeyManagerFactory kmf =
      KeyManagerFactory.getInstance( "SunX509" );
    kmf.init( authKeyStore, passphrase.toCharArray() );

    // Read trust keys.  These are used to verify the client's
    // attempt to authenticate itself with us
    KeyStore trustKeyStore = KeyStore.getInstance( "JKS" );
    trustKeyStore.load( new FileInputStream( "ccserver.trust" ),
                        passphrase.toCharArray() );

    // The TrustManagerFactory manages the trust keys
    TrustManagerFactory tmf =
      TrustManagerFactory.getInstance( "SunX509" );
    tmf.init( trustKeyStore );

    // Create an SSLContext using our key and trust managers
    SSLContext sslContext = SSLContext.getInstance( "TLS" );
    sslContext.init( kmf.getKeyManagers(),
                     tmf.getTrustManagers(),
                     secureRandom );

    // Create a ServerSocketFactory
    ServerSocketFactory ssf = sslContext.getServerSocketFactory();
    return ssf;
  }

  /**
   * INNER Class:
   * SocketHandler handles an incoming connection
   * in a separate thread
   */
  class SocketHandler implements Runnable {
    private Socket socket;

    /**
     * Constructor: start a background thread
     */
    public SocketHandler( Socket socket ) {
      this.socket = socket;

      // Start a background thread
      Thread thread = new Thread( this );
      thread.start();
    }

    /**
     * Handle a series of transactions sent over the socket
     */
    public void run() {
      try {

        // Call back to CCServer.handleSocket
        handleSocket( socket );

      } catch( IOException ie ) {
      } finally {
        try {
          socket.close();
          System.out.println( "Closed "+socket.getInetAddress() );
        } catch( IOException ie ) {
          System.err.println( "Problem closing socket: "+socket );
          ie.printStackTrace();
        }
      }
    }
  }

  /**
   * Pre-initialize the SecureRandom so we can print
   * something about it to standard out
   */
  static private void initializeRandom() {
    System.out.print(
      "Please wait, initializing random numbers..." );
    System.out.flush();
    secureRandom = new SecureRandom();
    secureRandom.nextInt();
    System.out.println( "done");
  }

  /**
   * Verify arguments, init the random number generator,
   * and start the server
   */
  static public void main( String args[] ) {
    if (args.length != 1) {
      System.err.println( "Usage: java CCServer <port>" );
      System.exit( 1 );
    }

    int port = Integer.parseInt( args[0] );

    initializeRandom();

    new CCServer( port );
  }
}
