//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 22:32:32 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

/**
 * This is a light-weight wrapper for boxed int used at runtime.
 */
public class RuntimeInt implements RuntimeObject
{
  public static final RuntimeInt ZERO = new RuntimeInt(0);
  public static final RuntimeInt ONE  = new RuntimeInt(1);

  private int _value;

  public RuntimeInt (int value)
    {
      _value = value;
    }

  public final int value ()
    {
      return _value;
    }

  public final int hashCode ()
    {
      return _value;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof RuntimeInt))
        return false;

      return ((RuntimeInt)object).value() == _value;
    }

  public final boolean equals (int value)
    {
      return _value == value;
    }

  public final String toString ()
    {
      return String.valueOf(_value);
    }
}
