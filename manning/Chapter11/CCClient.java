package Chapter11;
import java.io.*;
import java.net.*;
import java.security.*;
import javax.net.*;
import javax.net.ssl.*;

/**
 * Client for secure credit card verification system
 */
public class CCClient
{
  // Hostname and port of the verification server
  private String hostname;
  private int port;

  // Streams connected to the server
  private DataInputStream din;
  private DataOutputStream dout;

  // Passphrase for the authentication and trust keystores
  static private final String passphrase = "clientpass";

  // Secure random source
  static private SecureRandom secureRandom;

  /**
   * Constructor: set up the connection, and start
   * reading credit card info from standard in
   */
  public CCClient( String hostname, int port ) {
    this.hostname = hostname;
    this.port = port;
  }

  /**
   * Process a series of credit cards read from
   * the command-line
   */
  private void process() {
    setupConnection();
    verifyFromStandardIn();
  }

  /**
   * Make connection to server, and get
   * a stream pair to communicate with it
   */
  private void setupConnection() {
    try {
      SocketFactory sf = getSocketFactory();
      Socket socket = sf.createSocket( hostname, port );

      InputStream in = socket.getInputStream();
      OutputStream out = socket.getOutputStream();

      din = new DataInputStream( in );
      dout = new DataOutputStream( out );
    } catch( GeneralSecurityException gse ) {
      gse.printStackTrace();
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  /**
   * Read credit card information from standard in,
   * and verify it via the server
   */
  private void verifyFromStandardIn() {
    try {
      // Get a BufferedReader for reading standard in one
      // line at a time
      InputStreamReader isr = new InputStreamReader( System.in );
      BufferedReader br = new BufferedReader( isr );

      while (true) {
        // Read credit card number
        System.out.print( "Enter CC number: " );
        System.out.flush();
        String ccnumber = br.readLine();

        // Read expiration date
        System.out.print( "Enter CC expiration date: " );
        System.out.flush();
        String ccexp = br.readLine();

        // Verify on remote server
        boolean verified = verify( ccnumber, ccexp );

        String message = verified ? "Verified." : "Not verified.";
        System.out.println( message );
      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  /**
   * Send credit card info to server, and get
   * verification in response
   */
  private boolean verify( String ccnumber, String ccexp )
      throws IOException {
    dout.writeUTF( ccnumber );
    dout.writeUTF( ccexp );
    dout.flush();
    boolean ok = din.readBoolean();
    return ok;
  }

  /**
   * Create secure SocketFactory.
   * Read authentication and trust information from
   * keystore files in local directory
   */
  private SocketFactory getSocketFactory()
      throws GeneralSecurityException, IOException {

    // Read authentication keys.  These are used to authenticate
    // ourselves to the server
    KeyStore authKeyStore = KeyStore.getInstance( "JKS" );
    authKeyStore.load( new FileInputStream( "ccclient.auth" ),
                       passphrase.toCharArray() );

    // The KeyManagerFactory manages the authentication keys
    KeyManagerFactory kmf =
      KeyManagerFactory.getInstance( "SunX509" );
    kmf.init( authKeyStore, passphrase.toCharArray() );

    // Read trust keys.  These are used to verify the server's
    // attempt to authenticate itself with us
    KeyStore trustKeyStore = KeyStore.getInstance( "JKS" );
    trustKeyStore.load( new FileInputStream( "ccclient.trust" ),
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

    // Create a SocketFactory
    SocketFactory ssf = sslContext.getSocketFactory();
    return ssf;
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
   * and start the client
   */
  static public void main( String args[] ) {
    if (args.length != 2) {
      System.err.println(
        "Usage: java CCServer <hostname> <port>" );
      System.exit( 1 );
    }

    String hostname = args[0];
    int port = Integer.parseInt( args[1] );

    initializeRandom();

    CCClient ccc = new CCClient( hostname, port );
    ccc.process();
  }
}
