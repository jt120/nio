package Chapter9;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Lexer
{
  static private final String lxRuleString =
    "^\\s*(\\S+)\\s+(\\S+)\\s*$";

  // Set of rules for this lexer
  private LexerRule rules[] = new LexerRule[0];

  // The current input
  private FileInputStream currentInputStream;
  private LineNumberReader currentReader;
  private String currentFilename;
  private String currentLine;
  private int currentColumn;

  public Lexer( String specfile ) throws IOException {
    loadSpecification( specfile );
  }

  private void loadSpecification( String specfile )
      throws IOException {
    // Read the specification file line-by-line
    FileInputStream fin = new FileInputStream( specfile );
    InputStreamReader isr = new InputStreamReader( fin );
    LineNumberReader lnr = new LineNumberReader( isr );

    // Pattern for "parsing" each line of the spec file
    Pattern lxRule = Pattern.compile( lxRuleString );

    // Temporarily stores the lists we find
    ArrayList rulesAL = new ArrayList();

    while (true) {
      String line = lnr.readLine();

      // Read until file is exhausted
      if (line==null)
        break;

      Matcher matcher = lxRule.matcher( line );
      if (matcher.matches()) {
        // Add rule to the list of rules
        String name = matcher.group( 1 );
        String regex = matcher.group( 2 );
        LexerRule lr = new LexerRule( name, regex );
        rulesAL.add( lr );
      } else {
        // Syntax error in the specification file
        System.err.println( "Syntax error in "+specfile+" line "+
                            lnr.getLineNumber()+": " );
        System.err.println( "  "+line );
        System.exit( 1 );
      }
    }

    // Don't forget to close the file
    fin.close();

    // Convert the list of rules to an array, and save it
    rules = (LexerRule[])rulesAL.toArray( rules );

    System.out.println( "Parsed "+specfile );
  }

  public void setSource( String filename ) throws IOException {
    currentInputStream = new FileInputStream( filename );
    InputStreamReader isr =
      new InputStreamReader( currentInputStream );
    currentReader = new LineNumberReader( isr );

    currentFilename = filename;

    // Position within the file
    currentLine = null;
    currentColumn = 1;
  }

  public LexerToken getNextToken() throws IOException {
    if (currentLine==null || currentLine.length()==0) {
      // If the current line is exhausted, read the next one
      currentLine = currentReader.readLine();

      if (currentLine==null) {
        // If there are no more lines, we're done
        currentInputStream.close();
        currentReader = null;
        currentFilename = null;
        return null;
      }

      currentColumn = 1;
    }

    // Match the next token
    LexerToken token = null;

    // The end of the next token, within the line
    int tokenEnd = -1;

    // The length of the rule that matches the most characters
    // from the input
    int longestMatchLength=-1;

    for (int i=0; i<rules.length; ++i) {
      LexerRule rule = rules[i];

      // The pattern for this rule
      Pattern pattern = rule.pattern();
      Matcher matcher = pattern.matcher( currentLine );

      if (matcher.lookingAt()) {
        int matchLength = matcher.end();
        if (matchLength > longestMatchLength) {
          // This match is the longest so far; save info about it
          longestMatchLength = matchLength;
          String text = matcher.group( 0 );
          int lineNumber = currentReader.getLineNumber();
          token = new LexerToken( rule, text,
                                  lineNumber, currentColumn );
          tokenEnd = matchLength;
        }
      }
    }

    // If we didn't match anything, it's an error
    if (token == null) {
      System.err.println( "Syntax error in "+currentFilename+
                          " line "+currentReader.getLineNumber()+
                          ", column "+currentColumn+": " );
      System.out.println( "  "+currentLine );
      System.exit( 1 );

      // Never reached
      assert false;

      // For the compiler
      return null;
    } else {
      // We matched something, so we'll return a token.

      // But first, skip past the current token so we're
      // ready to scan for the next one
      currentColumn += tokenEnd;
      currentLine = currentLine.substring( tokenEnd );

      // Now, return the token
      return token;
    }
  }

  static public void main( String args[] ) throws IOException {
    if (args.length != 2) {
      System.err.println( "Usage: Lexer <lxfile> <input>" );
      System.exit( 1 );
    }

    String specfile = args[0];
    String inputfile = args[1];

    Lexer lexer = new Lexer( specfile );
    lexer.setSource( inputfile );

    while (true) {
      LexerToken token = lexer.getNextToken();
      if (token == null)
        break;

      System.out.println( token );
    }
  }
}
