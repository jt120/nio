package Chapter5.logging;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.logging.*;

/**
 * Log Handler that sends its output to a window
 */
public class WindowHandler extends StreamHandler
{
  // The default width and height for a logging window;
  // these can be overridden in the logging.properties file
  static private final int defaultWidth = 400;
  static private final int defaultHeight = 500;

  // The logger being displayed in this window
  private Logger logger;

  /**
   * Set up the connection between the stream
   * handler and the stream window: log data
   * written to the handler goes to the window
   */
  public WindowHandler( String loggerName ) {
    logger = Logger.getLogger( loggerName );

    // Get the output stream that feeds the window
    // and install it in the Stream handler
    WindowHandlerWindow whw =
      new WindowHandlerWindow( loggerName );
    OutputStream out = whw.getOutputStream();
    setOutputStream( out );

    setLevel( Level.ALL );
  }

  /**
   * Log a LogRecord.  We flush after every log
   * because we want to see log messages as soon as
   * they arrive
   */
  public void publish( LogRecord lr ) {
    // Check any filter, and possibly other criteria,
    // before publishing
    if (!isLoggable( lr ))
      return;

    super.publish( lr );
    flush();
  }

  /**
   * De-install this Handler from its Logger
   */
  private void removeHandler() {
    logger.removeHandler( this );
  }

  /**
   * Inner class: WindowHandlerWindow is a StreamWindow.
   * We need to override closeWindow() so that we
   * can de-install the handler when the window is
   * closed
   */
  class WindowHandlerWindow extends StreamWindow
  {
    public WindowHandlerWindow( String name ) {
      super( "Logger for "+name );

      // Assume the defaults, initially
      int width = defaultWidth;
      int height = defaultHeight;

      LogManager manager = LogManager.getLogManager();

      // We need the fully-qualified class name to access
      // the properties
      String className = WindowHandler.class.getName();

      String widthString = manager.getProperty( className+".width" );
      if (widthString != null) {
        width = Integer.parseInt( widthString );
      }

      String heightString = manager.getProperty( className+".height" );
      if (heightString != null) {
        height = Integer.parseInt( heightString );
      }

      setSize( width, height );
    }

    protected void closeWindow() {
      removeHandler();
      super.closeWindow();
    }
  }
}
