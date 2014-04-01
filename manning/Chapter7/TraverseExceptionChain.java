package Chapter7;
public class TraverseExceptionChain
{
  /**
   * Traverse a chain of exceptions and print each one out
   */
  static public void traverseExceptionChain( Throwable t ) {
    while (t != null) {
      System.out.println( t );
      t = t.getCause();
    }
  }

  /**
   * Test routine: synthesize a nice chain of exceptions
   * and traverse it
   */
  static public void main( String args[] ) {
    int array[] = new int[10];
    try {
      // out-of-bounds access
      array[500] = 1;
    } catch( Exception e ) {
      Exception e2 = new Exception( "Two", e );
      Exception e3 = new Exception( "Three", e2 );
      traverseExceptionChain( e3 );
    }
  }
}
