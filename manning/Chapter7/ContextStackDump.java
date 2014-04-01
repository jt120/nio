package Chapter7;
import java.io.*;

public class ContextStackDump
{
  static private final int numContextLines = 2;
  private Throwable throwable;

  public ContextStackDump( Throwable throwable ) {
    this.throwable = throwable;
  }

  public void printStackTrace() {
    System.err.print( getDump() );
  }

  public String getDump() {
    return generateDump( throwable );
  }

  private String generateDump( Throwable e ) {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamWriter osw = new OutputStreamWriter( baos );
    PrintWriter out = new PrintWriter( osw );

    out.println( e );

    StackTraceElement stes[] = e.getStackTrace();
    for (int i=0; i<stes.length; ++i) {
      StackTraceElement ste = stes[i];
      String filename = ste.getFileName();
      int lineNumber = ste.getLineNumber();
      out.println( "\t"+ste );
      try {
        out.println( getContext( filename, lineNumber ) );
      } catch( IOException ie ) {
        out.println( "\t  (No source information available)" );
      }
    }
    out.flush();

    String dump = new String( baos.toByteArray() );
    Throwable cause = e.getCause();
    if (cause != null) {
      dump += "Caused by: ";
      dump += generateDump( cause );
    }
    return dump;
  }

  private String getContext( String filename, int line )
    throws IOException {
    int start = line-numContextLines;
    int end = line+numContextLines+1;
    String context = "";

    FileInputStream fin = new FileInputStream( filename );
    InputStreamReader isr = new InputStreamReader( fin );
    LineNumberReader lnr = new LineNumberReader( isr );
    for (int i=1; i<start; ++i) {
      lnr.readLine();
    }
    for (int i=start; i<end; ++i) {
      String lineText = lnr.readLine();
      lineText = "\t  "+i+":"+
        (i==line?"->":"  ")+lineText;
      context += lineText;
      if (i!=end-1)
        context += "\n";
    }
    fin.close();
    return context;
  }
}
