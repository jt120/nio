package Chapter10;

import java.io.*;
import java.util.regex.*;

public abstract class CommandLine implements Runnable
{
  protected BufferedReader in;
  protected PrintWriter out;

  public CommandLine( InputStream in, OutputStream out ) {
    this( new InputStreamReader( in ),
          new OutputStreamWriter( out ) );
  }

  public CommandLine( Reader reader, Writer writer ) {
    this( new BufferedReader( reader ),
          new PrintWriter( writer ) );
  }

  public CommandLine( BufferedReader in, PrintWriter out ) {
    this.in = in;
    this.out = out;
    new Thread( this ).start();
  }

  public void run() {
    try {
      Pattern pattern = Pattern.compile( "\\s+" );
      while (true) {
        // Read each line and do simple parsing:
        // split the line on whitespace
        String line = in.readLine();
        if (line==null) {
          break;
        }

        String command[] = pattern.split( line );

        // Process each command
        boolean ok = processCommand( command );
        if (!ok) {
          System.out.println( "Unknown command: "+line );
        }
      }
    } catch( IOException ie ) {
      ie.printStackTrace();
    }
  }

  // Override this to implement commands
  abstract public boolean processCommand( String command[] );
}
