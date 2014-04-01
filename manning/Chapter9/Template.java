package Chapter9;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Template
{
  private String content;

  public Template( File file ) throws IOException {
    // Read the entire file into memory
    FileInputStream fin = new FileInputStream( file );
    byte raw[] = new byte[(int)file.length()];
    int r = fin.read( raw );
    if (r != file.length()) {
      throw new IOException( "Can't fully read "+file );
    }
    fin.close();

    // Convert the raw data to a string
    content = new String( raw );
  }

  public String instantiate( HashMap mapping ) {
    String instantiation = content;

    for (Iterator it=mapping.keySet().iterator(); it.hasNext();) {
      String var = (String)it.next();
      String value = (String)mapping.get( var );

      Pattern pattern = Pattern.compile( "\\$"+var );
      Matcher matcher = pattern.matcher( instantiation );
      instantiation = matcher.replaceAll( value );
    }

    return instantiation;
  }
}
