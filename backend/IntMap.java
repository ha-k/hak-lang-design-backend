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
 * This is a representation of arrays of ints that may indexed by any
 * type value from an <a href="Indexable.html"><tt>Indexable</tt></a>
 * collection.
 */

public class IntMap extends RuntimeMap
{
  private int[] _array;

  public IntMap (int[] array, Indexable indexable)
    {
      _array = array;
      (_indexable = indexable).lock();
    }

  public final int[] array ()
    {
      return _array;
    }

  public final RuntimeMap copy ()
    {
      return new IntMap(Runtime.copy(_array),_indexable);
    }

  public final Object extractArray ()
    {
      return _array;
    }

  public final int[] toArray ()
    {
      trimToSize();
      return (int[])_array;
    }

  public final RuntimeMap setArray (Object array)
    {
      _array = (int[])array;
      return this;
    }

  public final int get (int index)
    {
      return _array[_indexable.getIndex(index)];
    }

  public final int get (double index)
    {
      return _array[_indexable.getIndex(index)];
    }

  public final int get (Object index)
    {
      return _array[_indexable.getIndex(index)];
    }

  public final int set (int index, int value)
    {
      return _array[_indexable.getIndex(index)] = value;
    }

  public final int set (double index, int value)
    {
      return _array[_indexable.getIndex(index)] = value;
    }

  public final int set (Object index, int value)
    {
      return _array[_indexable.getIndex(index)] = value;
    }

  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof IntMap))
        return false;

      return _array == ((IntMap)object).array()
          && _indexable.equals(((IntMap)object).indexable());
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
