package Chapter9;
import java.util.regex.*;

class LexerRule {
  // The name of the lexical category that this rule matches
  private String name;

  // The regex used for matchin
  private String regex;

  // A pre-compiled Pattern object, kept to save time
  private Pattern pattern;

  /**
   * Create a LexerRule
   */
  public LexerRule( String name, String regex ) {
    this.name = name;
    this.regex = regex;
  }

  /**
   * Return the category name
   */
  public String name() {
    return name;
  }

  /**
   * Return the regex defining the rule
   */
  public String regex() {
    return regex;
  }

  /**
   * Return the Pattern object.  Create one if
   * it hasn't been created already
   */   
  public Pattern pattern() {
    if (pattern == null) {
      pattern = Pattern.compile( regex );
    }
    return pattern;
  }

  /**
   * Return a string representation of the rul
   */
  public String toString() {
    return "["+name+" "+regex+"]";
  }
}
