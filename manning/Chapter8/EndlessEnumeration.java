package Chapter8;
import java.util.*;

public class EndlessEnumeration implements Enumeration
{
  // The last prime we returned
  // initialize it to be before the first prime
  private int lastPrime = 1;

  // There are always more primes
  public boolean hasMoreElements() {
    return true;
  }

  public Object nextElement() {
    // Start searching from after the last one we found
    int n = lastPrime+1;
    while (true) {
      if (isPrime( n )) {
        lastPrime = n;
        return new Integer( n );
      } else {
        n++;
      }
    }
  }

  private boolean isPrime( int n ) {
    // 2 is the lowest possible factor
    // n/2 is the highest possible factor
    for (int i=2; i<=n/2; ++i) {
      // If n is divisible by i, then it's not prime
      if ((n%i)==0) {
        return false;
      }
    }
    return true;
  }

  static public void main( String args[] ) throws Exception {
    Enumeration e = new EndlessEnumeration();
    for (int i=0; i<20; ++i)
      System.out.println( e.nextElement() );
  }
}
