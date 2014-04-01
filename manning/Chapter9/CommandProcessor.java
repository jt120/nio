package Chapter9;
import java.io.*;
import java.util.regex.*;
import java.util.*;

public class CommandProcessor
{
  public CommandProcessor() {
  }

  public void processCommands( InputStream in ) throws IOException {
    // BufferedReader lets us read line-by-line
    Reader r = new InputStreamReader( System.in );
    BufferedReader br = new BufferedReader( r );

    Pattern pattern = Pattern.compile( "\\s+" );

    while (true) {
      String line = br.readLine();
      if (line==null)
        break;
      //use space to split words
      String words[] = pattern.split( line );

      if (words[0].equals( "moveto" )) {
        int x = Integer.parseInt( words[1] );
        int y = Integer.parseInt( words[2] );
        moveTo( x, y );
      } else if (words[0].equals( "setname" )) {
        String name = words[1];
        setName( name );
      } else if (words[0].equals( "setbounds" )) {
        int x = Integer.parseInt( words[1] );
        int y = Integer.parseInt( words[2] );
        int w = Integer.parseInt( words[3] );
        int h = Integer.parseInt( words[4] );
        setBounds( x, y, w, h );
      } else {
        System.out.println( "Error: "+line );
      }
    }
  }

  public void moveTo( int x, int y ) {
    System.out.println( "- moveTo "+x+" "+y );
  }

  public void setName( String name ) {
    System.out.println( "- setName "+name );
  }

  public void setBounds( int x, int y, int w, int h ) {
    System.out.println( "- setBounds "+x+" "+y+" "+w+" "+h );
  }

  static public void main( String args[] ) throws IOException {
    CommandProcessor cp = new CommandProcessor();
    cp.processCommands( System.in );
  }
}
