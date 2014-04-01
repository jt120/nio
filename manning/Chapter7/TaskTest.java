package Chapter7;
import java.util.*;

public class TaskTest implements Runnable
{
  static private Random rand = new Random();

  /**
   * Start background thread
   */
  public TaskTest() {
    Thread t = new Thread( this );
    t.start();
  }

  /**
   * Holds the work to be done by the Task.
   * TaskTestTask just sleeps for a little while
   */
  public void run() {
    while (true) {
      try {
        final int delay = rand.nextInt( 5 );

        TaskTestTask ttt = new TaskTestTask( delay );

        method0( ttt );
      } catch( Exception e ) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Dummy method to make the stack trace longer
   */
  private void method0( Task task ) throws Exception {
    method1( task );
  }

  /**
   * Dummy method to make the stack trace longer
   */
  private void method1( Task task ) throws Exception {
    task.carryOut();
  }

  /**
   * Start up a bunch of testers
   */
  static public void main( String args[] ) {
    for (int i=0; i<20; ++i) {
      new TaskTest();
    }
  }
}

  /**
   * A Task to test the Task system with.
   * TaskTestTask just sleeps for a little while
   */
class TaskTestTask extends Task
{
  private int delay;
  static private Random rand = new Random();

  public TaskTestTask( int delay ) {
    this.delay = delay;
  }

  public void run() throws Exception {
    method2();
  }

  private void method2() {
    method3();
  }

  private void method3() {
    Thread workerThread = Thread.currentThread();
    try {
      Thread.sleep( delay * 1000 );
      System.out.println( "Slept for "+delay );
    } catch( InterruptedException ie ) {}

    if (rand.nextInt( 100 )<50) {
      throw new RuntimeException(
        "Exception while sleeping for "+delay );
    }
  }
}
