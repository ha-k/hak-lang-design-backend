//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.types.Type;
import java.io.IOException;

/**
 * This interface specifies the method signatures for reading runtime
 * values according to their types, as well as input (read) methods.
 */
public interface ReadManager
{
//   public void read (String string);
//   public void readln (String string);
//   public void readln ();

//   public String readForm (int n, Type type);
//   public String readForm (double x);
//   public String readForm (Object o, Type type);

//   public String readUnquotedForm (int n, Type type);
//   public String readUnquotedForm (Object o, Type type);

//   public ReadManager clearTags ();

  public Object readObject (Type type)  throws IOException;
//   public double readReal (double x);
//   public int readInt (Object o, Type type);

//   public String readUnquotedForm (int n, Type type);
//   public String readUnquotedForm (Object o, Type type);

  public ReadManager clearTags ();
}
