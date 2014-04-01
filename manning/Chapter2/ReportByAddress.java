package Chapter2;
import java.net.*;

public class ReportByAddress
{
  static public void main( String args[] ) throws Exception {
    InetAddress ia = InetAddress.getByName( args[0] );
    NetworkInterface ni = NetworkInterface.getByInetAddress( ia );
    System.out.println( ni );
  }
}
