package Chapter11;
import java.io.*;
import java.net.*;
import java.security.*;
import java.util.*;
import java.util.regex.*;
import javax.net.*;
import javax.net.ssl.*;

public class THTTPSD
{
  // The port we will listen on
  private int port;

  // The document root of the server.  All requested filenames
  // are relative to this directory
  private File docroot;

  // Are we running as a secure server or as a regular server?
  private boolean secure = false;

  // All configuration comes from this file
  static private final String configurationFile = "thttpsd.cfg";

  // Configuration variables read from 'configurationFile'
  private Properties properties;

  // Regex for parsing "GET" requests
  static private final String getParser =
    "^GET\\s+(.+)\\s+HTTP/[\\d\\.]+$";
  static private final Pattern getPattern =
    Pattern.compile( getParser );

  // Regex for parsing server config variable names
  static private final String serverVarParser =
    "^server\\.([^\\.]+)\\.([^\\.]+)$";
  static private final Pattern serverVarPattern =
    Pattern.compile( serverVarParser );

  // Size of buffer used to send file contents to a browser
  static private final int bufferLength = 1024;

  static private SecureRandom secureRandom;

  /**
   * Constructor:
   * Configure and start the server
   */
  public THTTPSD() throws IOException {
    readConfiguration();
    startServer();
  }

  /**
   * Read configuration values
   */
  private void readConfiguration() throws IOException {
    FileInputStream fin = new FileInputStream( configurationFile );
    properties = new Properties();
    properties.load( fin );
    fin.close();

    // Show the configuration values on the console
    System.out.println( "Configuration: " );
    properties.list( System.out );

    // Get 'port', 'docroot', and 'secure' values from properties
    port = Integer.parseInt( (String)properties.get( "port" ) );
    docroot = new File( (String)properties.get( "docroot" ) );
    if (properties.get( "secure" ) != null &&
        properties.get( "secure" ).equals( "true" )) {
      secure = true;
    }
  }

  /**
   * Start the server: start a listener
   */
  private void startServer() {
    new Listener();
  }

  /**
   * Deal with a new Socket: create a SocketHandler
   * to process the new connection
   */
  private void processSocket( Socket socket ) {
    new SocketHandler( socket );
  }

  /**
   * Deal with a new connection: parse transaction
   * and respond to it
   */
  private void processTransaction( Socket socket,
                                   InputStream in,
                                   OutputStream out )
      throws IOException {
    while (true) {
      String doc = getDocString( in );

      // getDocString() returns null when the connection is closed
      if (doc==null)
        break;

      // Show a log message
      System.out.println( "Request for "+doc+" from "+
                          socket.getInetAddress() );

      // Send the requested document to the browser
      sendDoc( doc, out );
    }
  }

  /**
   * Send the requested document to the stream, based on
   * what kind of document it is
   */
  private void sendDoc( String docString, OutputStream out )
      throws IOException  {
    // Derive the absolute pathname from the relative
    // pathname and the document root
    File doc = new File( docroot+docString );
    if (!doc.exists()) {

      // File doesn't exist
      sendFileNotFound( docString, out );
    } else if (doc.isDirectory()) {

      // File is really a directory
      sendDirectoryListing( doc, docString, out );
    } else if (doc.getName().toLowerCase().endsWith( "html" )) {

      // File is an HTML file
      sendWebPage( doc, out );
    } else {

      // Default: send as a text page
      sendTextFile( doc, out );
    }
  }

  /**
   * Send a file-not-found message
   */
  private void sendFileNotFound( String url, OutputStream out )
      throws IOException {

    // Build the response message
    StringBuffer message = new StringBuffer();
    message.append( "<!DOCTYPE HTML PUBLIC \"-//IETF//"+
                    "DTD HTML 2.0//EN\">\r\n" );
    message.append( "<HTML><HEAD>\r\n" );
    message.append( "<TITLE>404 Not Found</TITLE>\r\n" );
    message.append( "</HEAD><BODY>\r\n" );
    message.append( "<H1>Not Found</H1>\r\n" );
    message.append( "The requested URL "+url+
                    " was not found on this server.<P>\r\n" );
    message.append( "<HR>\r\n" );
    message.append( "<ADDRESS>THTTPSD/1.0</ADDRESS>\r\n" );
    message.append( "</BODY></HTML>\r\n" );
    message.append( "\r\n" );

    // Send the header and the message
    // Mime type is 'text/html'
    PrintWriter pw = new PrintWriter( out );
    sendHeader( pw, "text/html", message.length() );
    pw.print( message );

    // Make sure all the data gets there, especially because of
    // any buffering that might be used by the encryption
    pw.flush();
  }

  /**
   * Send directory listing
   */
  private void sendDirectoryListing( File directory, String url,
                                     OutputStream out )
      throws IOException {

    // Build the response message
    StringBuffer message = new StringBuffer();
    message.append( "<!DOCTYPE HTML PUBLIC \"-//IETF//"+
                    "DTD HTML 2.0//EN\">\r\n" );
    message.append( "<HTML><HEAD>\r\n" );
    message.append( "<TITLE>"+url+"</TITLE>\r\n" );
    message.append( "</HEAD><BODY>\r\n" );
    message.append( "<H1>Directory: "+url+"</H1>\r\n" );

    // Add a link for each file in the directory
    File files[] = directory.listFiles();
    for (int i=0; i<files.length; ++i) {
      File file = files[i];
      String name = file.getName();
      String newURL = url+name;
      if (file.isDirectory())
        newURL += "/";
      message.append( "<a href=\""+newURL+"\">"+name+
                      "</a><br>\r\n" );
    }

    message.append( "</BODY></HTML>\r\n" );
    message.append( "\r\n" );

    // Send the header and the message
    // Mime type is 'text/html'
    PrintWriter pw = new PrintWriter( out );
    sendHeader( pw, "text/html", message.length() );
    pw.print( message );

    // Make sure all the data gets there, especially because of
    // any buffering that might be used by the encryption
    pw.flush();
  }

  /**
   * Send HTML page
   */
  private void sendWebPage( File doc, OutputStream out )
      throws IOException {
    // Send the header and the contents of the file
    // Mime type is 'text/html'
    PrintWriter pw = new PrintWriter( out );
    sendHeader( pw, "text/html", (int)doc.length() );
    sendFile( doc, out );
  }

  /**
   * Send text file
   */
  private void sendTextFile( File doc, OutputStream out )
      throws IOException {
    // Send the header and the contents of the file
    // Mime type is 'text/plain'
    PrintWriter pw = new PrintWriter( out );
    sendHeader( pw, "text/plain", (int)doc.length() );
    sendFile( doc, out );
  }

  /**
   * Send HTTP header, including the MIME type and the
   * content-length
   */
  private void sendHeader( PrintWriter pw, String mimeType,
                           int length )
      throws IOException {
    pw.print( "HTTP/1.1 200 OK\r\n" );
    pw.print( "Content-Length: "+length+"\r\n" );
    pw.print( "Content-Type: "+mimeType+"\r\n" );
    pw.print( "\r\n" );
    pw.flush();
  }

  /**
   * Send the contents of a file
   */
  private void sendFile( File doc, OutputStream out )
      throws IOException {
    byte buffer[] = new byte[bufferLength];
    FileInputStream fin = new FileInputStream( doc );
    while (true) {
      int r = fin.read( buffer );
      if (r==-1)
        break;
      out.write( buffer, 0, r );
    }

    out.flush();
  }

  /**
   * Parse the HTTP header of the incoming request.
   * Return the document that has been requested.
   * Only the GET method is handled
   */
  private String getDocString( InputStream in )
      throws IOException {
    // Create a BufferedReader to read the incoming
    // data one line at a time
    InputStreamReader isr = new InputStreamReader( in );
    BufferedReader br = new BufferedReader( isr );

    // Contains the first line of the request
    String firstLine = null;

    while (true) {
      String line = br.readLine();

      // The request is over if we get a null string,
      // or an empty line
      if (line==null) {
        return null;
      } else if (line.equals( "" )) {
        break;
      }

      // Save the first line of the request
      if (firstLine==null)
        firstLine = line;
    }

    if (firstLine != null) {
      // Use a regex to find the requested document
      // inside the first line

      Matcher matcher = getPattern.matcher( firstLine );
      if (matcher.matches()) {

        // Yes, we got it
        String doc = matcher.group( 1 );
        return doc;
      } else {

        // No: error, or wrong kind of request, or something
        throw new IOException( "Badly formed request" );
      }
    } else {

      // Return null if no more documents are being
      // requested by this client
      return null;
    }
  }

  /**
   * Return a ServerSocketFactory.  Return an
   * SSLServerSocketFactory if we're in secure mode,
   * otherwise use the default ServerSocketFactory
   */
  private ServerSocketFactory getServerSocketFactory() {
    if (secure) {
      try {
        System.out.println( "Running secure" );

        String keyFile =
          (String)properties.get( "keyfile" );
        String passphrase =
          (String)properties.get( "passphrase" );

        // Read authentication keys.  These are used to authenticate
        // ourselves to the client
        KeyStore ks = KeyStore.getInstance( "JKS" );
        ks.load( new FileInputStream( keyFile ),
                 passphrase.toCharArray() );

        // The KeyManagerFactory manages the authentication keys
        KeyManagerFactory kmf =
          KeyManagerFactory.getInstance( "SunX509" );
        kmf.init( ks, passphrase.toCharArray() );

        // Create an SSLContext using our key manager
        SSLContext sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( kmf.getKeyManagers(), null,
                         secureRandom );

        // Create a ServerSocketFactory
        ServerSocketFactory ssf =
          sslContext.getServerSocketFactory();

        return ssf;
      } catch( GeneralSecurityException gse ) {

        System.err.println( gse );
        gse.printStackTrace();
        System.exit( 1 );

        // Should never reach this
        return null;
      } catch( IOException ie ) {

        System.err.println( ie );
        ie.printStackTrace();
        System.exit( 1 );

        // Should never reach this
        return null;
      }
    } else {

      System.out.println( "Running insecure" );
      return ServerSocketFactory.getDefault();
    }
  }

  /**
   * INNER CLASS:
   * Listener listens for incoming connections
   */
  class Listener implements Runnable {

    /**
     * Constructor: start a background thread
     */
    public Listener() {
      Thread thread = new Thread( this );
      thread.start();
    }

    /**
     * Background thread: listen on a port and accept
     * new connections; The new connections are passed
     * to THTTPSD.processSocket().
     */
    public void run() {
      try {
        // Create a ServerSocket from the ServerSocketFactory
        ServerSocketFactory ssf = getServerSocketFactory();
        ServerSocket ss = ssf.createServerSocket( port );

        System.out.println( "Listening on port "+port );

        // Accept connections and process them
        while (true) {
          try {
            Socket socket = ss.accept();
            System.out.println( "Connection from "+
                                socket.getInetAddress() );
            processSocket( socket );
          } catch( IOException ie ) {
            System.err.println( "Listener exception: "+ie );
            ie.printStackTrace();
          }
        }
      } catch( IOException ie ) {
        System.err.println( "Listener exception: "+ie );
        ie.printStackTrace();
      }
    }
  }

  /**
   * INNER CLASS:
   * SocketHandler responds to an incoming socket
   */
  class SocketHandler implements Runnable {
    private Socket socket;

    /**
     * Constructor: start a background thread
     */
    public SocketHandler( Socket socket ) {
      this.socket = socket;
      Thread thread = new Thread( this );
      thread.start();
    }

    /**
     * Background thread:
     * Call THTTPSD.processTransaction() to process
     * this socket in this background thread
     */
    public void run() {
      try {
        InputStream in = socket.getInputStream();
        OutputStream out = socket.getOutputStream();

        processTransaction( socket, in, out );
      } catch( IOException ie ) {
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

  static public void main( String args[] ) {
    try {
      initializeRandom();
      new THTTPSD();
    } catch( IOException ie ) {
      System.err.println( ie );
      ie.printStackTrace();
    }
  }
}
