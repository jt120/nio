package Chapter2;
import java.net.*;
import java.util.*;

public class ListNetworkInterfaces
{
  static public void main( String args[] ) throws Exception {
    Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
    while (interfaces.hasMoreElements()) {
      NetworkInterface ni = (NetworkInterface)interfaces.nextElement();
      System.out.println( ni );
    }
  }
}
