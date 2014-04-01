package Chapter9;
import java.io.*;
import java.util.regex.*;

public class LongestWord
{
  static public void main( String args[] ) throws IOException {
    // BufferedReader lets us read line-by-line
    Reader r = new InputStreamReader( System.in );
    BufferedReader br = new BufferedReader( r );

    Pattern pattern = Pattern.compile( "\\s*:\\s*" );

    while (true) {
      String line = br.readLine();

      // Null line means input is exhausted
      if (line==null)
        break;

      String words[] = pattern.split( line );

      // -1 means we haven't found a word yet
      int longest=-1;
      int longestLength=0;
      for (int i=0; i<words.length; ++i) {
        if (words[i].length() > longestLength) {
          longest = i;
          longestLength = words[i].length();
        }
      }

      System.out.println( words[longest] );
    }
  }
}
