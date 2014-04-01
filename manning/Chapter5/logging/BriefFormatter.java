package Chapter5.logging;

import java.text.*;
import java.util.Date;
import java.util.logging.*;

public class BriefFormatter extends Formatter
{
  // Buffer for formatting a LogRecord
  private StringBuffer stringBuffer = new StringBuffer();

  // Buffer for formatting the time
  private StringBuffer dateBuffer = new StringBuffer();

  // Pre-allocate these to save time
  private Date date = new Date();
  Object args[] = { date };

  // MessageFormat for formatting the time like this:
  // 4:51:13 PM
  static private final String FORMATSTRING = "{0,time,medium}";
  private MessageFormat format = new MessageFormat( FORMATSTRING );

  // The character that is used to separate lines.
  // It's best to use this value instead of assuming that \n is
  // the line separator
  private String lineSeparator =
    (String)java.security.AccessController.doPrivileged(
    new sun.security.action.GetPropertyAction( "line.separator" ) );

  // Synchronized because the StringBuffers are shared
  synchronized public String format( LogRecord record ) {
    // Initialize the buffers
    stringBuffer.setLength( 0 );
    dateBuffer.setLength( 0 );

    // Format the time into dateBuffer
    date.setTime( record.getMillis() );
    format.format( args, dateBuffer, null );

    // Append the date
    stringBuffer.append( dateBuffer );

    // Append a space
    stringBuffer.append( " " );

    // Append the log message -- call formatMessage()
    // to format the message itself
    String message = formatMessage( record );
    stringBuffer.append( message );

    // Add a newline
    stringBuffer.append( lineSeparator );

    return stringBuffer.toString();
  }
}
