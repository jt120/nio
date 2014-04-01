package Chapter9;
public class LexerToken {
  private LexerRule rule;
  private String text;
  private int line;
  private int column;

  /**
   * Create a LexerToken
   */
  public LexerToken( LexerRule rule, String text,
                     int line, int column ) {
    this.rule = rule;
    this.text = text;
    this.line = line;
    this.column = column;
  }

  /**
   * Return the rule that matched this token
   */
  public LexerRule rule() {
    return rule;
  }

  /**
   * Return the text matched by this token
   */
  public String text() {
    return text;
  }

  /**
   * Return a string representation of the token
   */
  public String toString() {
    return "["+"\""+text+"\""+" at line "+line+", column "+column+", rule:"+rule+"]";
  }
}
