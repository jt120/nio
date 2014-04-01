package Chapter7;
import java.util.*;

abstract public class Task
{
  static private TaskQueue tasks = new TaskQueue();
  static private final int numTaskThreads = 5;
  static private boolean initialized = false;
  private Exception exception;

  // Set this to false to turn off synthesizing, for
  // debugging the Task system
  static private final boolean synthesizing = true;

  /**
   * Accessor method for exception
   */
  Exception exception() {
    return exception;
  }

  /**
   * Accessor method for exception
   */
  void exception( Exception exception ) {
    this.exception = exception;
  }

  /**
   * Start up the background task threads
   */
  synchronized static private void init() {
    if (initialized)
      return;
    for (int i=0; i<numTaskThreads; ++i) {
      TaskThread tt = new TaskThread( "task thread "+i );
      tt.start();
    }
    initialized = true;
  }

  /**
   * Initiate the execution of a task by putting it in
   * the task queue
   */
  public void carryOut() throws Exception {
    try {
      init();
      // synchronize on 'this' so that we don't get our
      // done-signal before we start waiting for it
      synchronized( this ) {
        // Note that "tasks" has class scope
        tasks.put( this );
        waitTilDone();
      }
    } catch( Exception e ) {
      if (synthesizing)
        throw synthesizeException( e );
      else
        throw e;
    }
  }

  /**
   * Synthesize an exception that combines info from a task's
   * main thread and the worker thread in which the Task
   * was running
   */
  private Exception synthesizeException( Exception remote ) {
    Exception local = new Exception();
    StackTraceElement remoteSTEs[] = remote.getStackTrace();
    int remoteLen = remoteSTEs.length;
    StackTraceElement localSTEs[] = local.getStackTrace();
    int localLen = localSTEs.length;
    StackTraceElement synthSTEs[] =
      new StackTraceElement[remoteLen+localLen-2];
    for (int i=0; i<remoteLen-1; ++i)
      synthSTEs[i] = remoteSTEs[i];
    for (int i=1; i<localLen; ++i)
      synthSTEs[i+remoteLen-2] = localSTEs[i];
    Exception synth = new Exception( remote.getMessage() );
    synth.setStackTrace( synthSTEs );
    return synth;
  }

  /**
   * Main thread calls this to wait for the worker
   * thread to finish executing the Task
   */
  private void waitTilDone() throws Exception {
    synchronized( this ) {
      try {
        wait();
      } catch( InterruptedException ie ) {}
      if (exception!=null) {
        Exception exception2 = exception;
        exception = null;
        throw exception2;
      }
    }
  }

  /**
   * Worker thread calls this to signal the main thread
   * that it is done executing the Task
   * synchronize on 'this' so that we don't get our
   * done-signal before we start waiting for it
   */
  void signalDone() {
    synchronized( this ) {
      notify();
    }
  }

  /**
   * Override this with the code that the Task will
   * run in the worker thread
   */
  abstract public void run() throws Exception ;

  /**
   * Worker thread calls this to get the next Task from the
   * queue
   */
  static Task getNextTask() {
    return tasks.get();
  }
}

/**
 * Worker thread
 */
class TaskThread extends Thread {
  public TaskThread( String name ) {
    super( name );
    setDaemon( true );
  }

  public void run() {
    while (true) {
      Task task = Task.getNextTask();
      try {
        task.run();
      } catch( Exception e ) {
        task.exception( e );
      }
      task.signalDone();
    }
  }
}

/**
 * Queue for holding tasks until it's time to
 * execute them
 */
class TaskQueue
{
  private Vector vec = new Vector();

  synchronized public void put( Task task ) {
    vec.addElement( task );
    notifyAll();
  }

  synchronized public Task get() {
    while (true) {
      if (vec.size()>0) {
        Task task = (Task)vec.elementAt( 0 );
        vec.removeElementAt( 0 );
        return task;
      } else {
        try { wait(); } catch( InterruptedException ie ) {}
      }
    }
  }
}
