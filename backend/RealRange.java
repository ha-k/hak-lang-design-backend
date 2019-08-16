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
 * This denotes a real range at runtime.
 */
public class RealRange implements RuntimeObject, Container
{
  private double _lo;
  private double _hi;

  public RealRange ()
    {
      _lo = 0.0;
      _hi = -1.0;
    }

  public RealRange (double lo, double hi)
    {
      if (_lo > _hi)
        {
          _lo = 0.0;
          _hi = -1.0;
        }
      else
        {
          _lo = lo;
          _hi = hi;
        }
    }

  public final boolean isEmpty ()
    {
      return _lo > _hi;
    }

  public final boolean contains (double element)
    {
      return _lo <= element && element <= _hi;
    }

  public final boolean contains (int element)
    {
      double n = (double)element;
      return _lo <= n && n <= _hi;
    }

  public final boolean contains (Object element)
    {
      double n = ((RuntimeReal)element).value();
      return _lo <= n && n <= _hi;
    }

  public final double lb ()
    {
      return _lo;
    }

  public final double ub ()
    {
      return _hi;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof RealRange))
        return false;

      RealRange range = (RealRange)object;

      return range.lb() == _lo && range.ub() == _hi; 
    }

  public final String toString ()
    {
      return isEmpty() ? "<empty real range>" : _lo + " .. " + _hi;
    }
}
