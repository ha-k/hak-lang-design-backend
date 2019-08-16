//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.Iterator;
import hlt.language.util.IntIterator;
import hlt.language.util.DoubleIterator;

/**
 * This is a representation of arrays of reals that may indexed by any
 * type value from an <a href="Indexable.html"><tt>Indexable</tt></a>
 * collection.
 */


public class RealMap extends RuntimeMap
{
  private double[] _array;

  public RealMap (double[] array, Indexable indexable)
    {
      _array = array;
      (_indexable = indexable).lock();
    }

  public final double[] array ()
    {
      return _array;
    }

  public final RuntimeMap copy ()
    {
      return new RealMap(Runtime.copy(_array),_indexable);
    }

  public final Object extractArray ()
    {
      return _array;
    }

  public final double[] toArray ()
    {
      trimToSize();
      return (double[])_array;
    }

  public final RuntimeMap setArray (Object array)
    {
      _array = (double[])array;
      return this;
    }

  public final double get (double index)
    {
      return _array[_indexable.getIndex(index)];
    }

  public final double get (int index)
    {
      return _array[_indexable.getIndex(index)];
    }

  public final double get (Object index)
    {
      return _array[_indexable.getIndex(index)];
    }

  public final double set (double index, double value)
    {
      return _array[_indexable.getIndex(index)] = value;
    }

  public final double set (int index, double value)
    {
      return _array[_indexable.getIndex(index)] = value;
    }

  public final double set (Object index, double value)
    {
      return _array[_indexable.getIndex(index)] = value;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof RealMap))
        return false;

      return _array == ((RealMap)object).array()
          && _indexable.equals(((RealMap)object).indexable());
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder("#[");

      if (_indexable instanceof IntRange || _indexable instanceof IntSet)
        {
          for (IntIterator i=(IntIterator)_indexable.intIterator(); i.hasNext();)
            {
              int element = i.next();
              buf.append(element).append(":").append(get(element));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      if (_indexable instanceof RealSet)
        {
          for (DoubleIterator i=_indexable.realIterator(); i.hasNext();)
            {
              double element = i.next();
              buf.append(element).append(":").append(get(element));
              if (i.hasNext()) buf.append(",");
            }

          return buf.append("]#").toString();
        }

      for (Iterator i=_indexable.iterator(); i.hasNext();)
        {
          Object element = i.next();
          buf.append(element).append(":").append(get(element));
          if (i.hasNext()) buf.append(",");
        }

      return buf.append("]#").toString();
    }
}
