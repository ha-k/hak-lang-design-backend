//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

// This should belong to hlt.language.util

/**
 * @version     Last modified on Sat Nov 03 01:42:32 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.io.PrintStream;
import java.io.IOException;

/**
 * This class defines print methods (on the standard output). This may be
 * used as a default display device manager which may be redefined in a
 * subclass for another output device (<i>e.g.</i>, GUI display).
 */
public class DefaultDisplayDeviceManager implements DisplayDeviceManager
{
  private PrintStream _stream = System.out;

  public final void setOutputStream (PrintStream stream)
    {
      _stream = stream;
    }

  public final PrintStream getOutputStream ()
    {
      return _stream;
    }

  public void println ()
    {
      _stream.println();
    }
    
  public void print (String string)
    {
      _stream.print(string);
    }
    
  public void println (String string)
    {
      _stream.println(string);
    }

  public void close ()
    {
      _stream.close();
    }

  public void flush () throws IOException
    {
    }

}
