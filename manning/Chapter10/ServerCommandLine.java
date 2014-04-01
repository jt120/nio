package Chapter10;

import java.io.*;
import java.util.prefs.*;

public class ServerCommandLine extends CommandLine
{
  private Preferences prefs =
    Preferences.userNodeForPackage( getClass() );

  public ServerCommandLine( InputStream in, OutputStream out ) {
    super( in, out );
  }

  public boolean processCommand( String command[] ) {
    if (command[0].equalsIgnoreCase( "port" )) {
      int port = Integer.parseInt( command[1] );
      prefs.putInt( "port", port );
      System.out.println( "Set port to "+port );
      return true;
    } else {
      return false;
    }
  }

  static public void main( String args[] ) throws IOException {
    new ServerCommandLine( System.in, System.out );
  }
}
