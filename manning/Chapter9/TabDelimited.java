package Chapter9;
import java.io.*;
import java.util.regex.*;
import java.util.*;

public class TabDelimited
{
  static public void main( String args[] ) throws IOException {
    // BufferedReader lets us read line-by-line
    Reader r = new InputStreamReader( System.in );
    BufferedReader br = new BufferedReader( r );

    // We'll store the data in a vector of arrays
    Vector vec = new Vector();
    //use \t to split words and not include \t
    Pattern pattern = Pattern.compile( "(?<!\\\\)\\t" );

    while (true) {
      String line = br.readLine();
      if (line==null||"".equals(line))
        break;

      String words[] = pattern.split( line );

      vec.addElement( words.clone() );
    }

    int ri=0;
    for (Enumeration e=vec.elements(); e.hasMoreElements();) {
      String words[] = (String[])e.nextElement();
      System.out.println( "Record "+ri );
      ri++;
      for (int i=0; i<words.length; ++i) {
        System.out.println( "  "+words[i] );
      }
    }
  }
}
