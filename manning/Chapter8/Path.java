package Chapter8;
import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Path
{
  private LinkedHashMap directories = new LinkedHashMap();

  public Path() {
  }

  public void add( String name, String directory ) {
    add( name, new File( directory ) );
  }

  public void add( String name, File directory ) {
    Entry entry = new Entry( directory );
    directories.put( name, entry );
  }

  public void remove( String name ) {
    directories.remove( name );
  }

  public void setActive( String name, boolean active ) {
    Entry entry = (Entry)directories.get( name );
    if (entry == null)
      throw new NoSuchElementException(
        "No element "+name+" in "+this );
    entry.active( active );
  }

  public File findFile( String target ) {
    final File targetFile = new File( target );
    FileFilter filter = new FileFilter() {
      public boolean accept( File pathname ) {
        // This filter accepts files matching the target file
        return targetFile.getName().equals( pathname.getName() );
      }
    }; // end of anonymous inner class

    // Check each directory of the path in turn
    for (Iterator it=directories.keySet().iterator();
         it.hasNext();) {
      String name =  (String)it.next();
      Entry entry = (Entry)directories.get( name );

      // If this directory has been de-activated,
      // don't look in it
      if (!entry.active())
        continue;

      // Search the directory with the filter
      File dir = entry.directory();
      File files[] = dir.listFiles( filter );

      if (files != null && files.length>0) {
        // listFiles() should only return one file
        return files[0];
      }
    }
    return null;
  }

  public String formatAsPath() {
    String s = "";
    for (Iterator it=directories.keySet().iterator();
         it.hasNext();) {
      String name =  (String)it.next();
      Entry entry = (Entry)directories.get( name );
      File dir = entry.directory();
      s += dir;
      if (it.hasNext()) {
        s += File.pathSeparator;
      }
    }
    return s;
  }
}

class Entry
{
  private File directory;
  private boolean active;

  public Entry( String name ) {
    this( new File( name ) );
  }

  public Entry( File directory ) {
    this.directory = directory;
    active = true;
  }

  public boolean active() {
    return active;
  }

  public void active( boolean active ) {
    this.active = active;
  }

  public File directory() {
    return directory;
  }
}
