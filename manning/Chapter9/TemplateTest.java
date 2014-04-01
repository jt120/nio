package Chapter9;
import java.io.*;
import java.util.*;

public class TemplateTest
{
  static private final String templateFile = "test.thtml";
  static private final String htmlFile = "test.html";

  static public void main( String args[] ) throws IOException {
    Template template = new Template( new File( templateFile ) );

    HashMap mapping = new HashMap();
    mapping.put( "title", "Template Test" );
    mapping.put( "subtitle", "An easier way to generate a page" );
    mapping.put( "text", "Using a templating system is much "+
                 "easier than generating each page completely "+
                 "from code." );

    String instantiation = template.instantiate( mapping );

    FileOutputStream fout = new FileOutputStream( htmlFile );
    PrintWriter out = new PrintWriter( fout );
    out.println( instantiation );
    out.flush();
    fout.close();
  }
}
