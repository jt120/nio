package Chapter2;
import java.net.*;

public class ReportByName
{
  static public void main( String args[] ) throws Exception {
    NetworkInterface ni = NetworkInterface.getByName( args[0] );
    System.out.println( ni );
  }
}
