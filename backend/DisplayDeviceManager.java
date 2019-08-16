//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

// This should belong to hlt.language.util

/**
 * @version     Last modified on Sat Nov 03 01:41:33 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.types.Type; // This is not needed !
import java.io.PrintStream;
import java.io.IOException;

/**
 * This interface specifies the method signatures for output (print)
 * methods.
 */
public interface DisplayDeviceManager
{
  public void setOutputStream (PrintStream stream);
  public PrintStream getOutputStream ();
  public void close ();

  public void print (String string);
  public void println (String string);
  public void println ();

  public void flush () throws IOException;
}
