//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

/**
 * This is a light-weight wrapper for boxed double used at runtime.
 */
public class RuntimeReal implements RuntimeObject
{
  public static final RuntimeReal ZERO = new RuntimeReal(0.0);

  private double _value;

  public RuntimeReal (double value)
    {
      _value = value;
    }

  public final double value ()
    {
      return _value;
    }

  public final int hashCode ()
    {
      return (int)_value;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof RuntimeReal))
        return false;

      return ((RuntimeReal)object).value() == _value;
    }

  public final boolean equals (double value)
    {
      return _value == value;
    }

  public final String toString ()
    {
      return String.valueOf(_value);
    }
}
