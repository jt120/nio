package Chapter9;
import java.io.*;
import java.util.regex.*;

public class ParseName
{
  static public void main( String args[] ) throws IOException {
    // BufferedReader lets us read line-by-line
    Reader r = new InputStreamReader( System.in );
    BufferedReader br = new BufferedReader( r );

    String patternString =
      "^\\s*          # Ignore any whitespace at\n"+
      "               # the start of the line\n"+
      "(M(s|r|rs)\\.) # Match Ms., Mrs., and Mr. (titles)\n"+
      "\\s+           # Space between title and first name\n"+
      "(\\S+)         # First name\n"+
      "\\s+           # Space between first name and last name\n"+
      "(\\S+)         # Last name\n"+
      "\\s*$          # Allow whitespace, but nothing else,\n"+
      "               # after name\n";

    int patternFlags = Pattern.CASE_INSENSITIVE|Pattern.COMMENTS;

    Pattern pattern =
      Pattern.compile( patternString, patternFlags );

    while (true) {
      String line = br.readLine();
      if (line==null)
        break;

      Matcher matcher = pattern.matcher( line );

      System.out.println( line );

      if (matcher.matches()) {
        String title = matcher.group( 1 );
        String firstName = matcher.group( 3 );
        String lastName = matcher.group( 4 );

        System.out.println( "  Title: "+title );
        System.out.println( "  First Name: "+firstName );
        System.out.println( "  Last Name: "+lastName );

        String modernLine = modernize( line );

        if (!modernLine.equals( line )) {
          System.out.println( "  Modernized: "+modernLine );
        }
      } else {
        System.out.println( "  (Doesn't match!)" );
      }
    }
  }

  static public String modernize( String name ) {
    String patternString = "(?<=m)rs\\.";
    int patternFlags = Pattern.CASE_INSENSITIVE;

    Pattern pattern =
      Pattern.compile( patternString, patternFlags );

    Matcher matcher = pattern.matcher( name );

    // StringBuffer to accumulate output of find-and-replace
    StringBuffer sb = new StringBuffer();

    // Find first occurrence of target string
    boolean result = matcher.find();
    while( result ) {
        // Replace target string with replacement string
        matcher.appendReplacement( sb, "s." );

        // Find next occurrence of target string
        result = matcher.find();
    }

    // Append unmatched remainder of string
    matcher.appendTail(sb);

    return sb.toString();
  }
}
