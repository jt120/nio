package Chapter2;
import java.net.*;
import java.util.*;

public class Accept
{
  static private InetAddress getAddress( NetworkInterface ni ) {
    Enumeration e = ni.getInetAddresses();
    if (!e.hasMoreElements())
      return null;
    InetAddress ia = (InetAddress)e.nextElement();
    return ia;
  }

  static public void main( String args[] ) throws Exception {
    int port = Integer.parseInt( args[0] );
    String interf = args.length > 1 ? args[1] : null;

    if (interf != null) {
      NetworkInterface ni = NetworkInterface.getByName( interf );
      InetAddress ia = getAddress( ni );
      ServerSocket ss = new ServerSocket( port, 20, ia );
      System.out.println( "Listening" );
      Socket s = ss.accept();
      System.out.println( s );
    } else {
      ServerSocket ss = new ServerSocket( port );
      System.out.println( "Listening" );
      Socket s = ss.accept();
      System.out.println( s );
    }
  }
}
