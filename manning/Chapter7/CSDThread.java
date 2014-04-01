package Chapter7;
import java.io.*;

public class CSDThread extends Thread
{
  public CSDThread( Runnable runnable ) {
    super( runnable );
  }

  public void run() {
    try {
      super.run();
    } catch( Throwable e ) {
      ContextStackDump csd = new ContextStackDump( e );
      csd.printStackTrace();
      System.exit( 1 );
    }
  }

  static class CSDTest implements Runnable
  {
    public CSDTest() {
      Thread thread = new CSDThread( this );
      thread.start();
    }

    public void run() {
      run0();
    }

    public void run0() {
      run1();
    }

    public void run1() {
      throw new RuntimeException( "Bang!" );
    }
  }

  static public void main( String args[] ) {
    new CSDTest();
  }
}
