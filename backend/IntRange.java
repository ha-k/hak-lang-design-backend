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
 * This denotes an integer range at runtime.
 */
public class IntRange implements RuntimeObject, IndexableContainer, Lockable
{
  private int _lo;
  private int _hi;

  private IntSet _set;

  public IntRange ()
    {
      _lo = 0;
      _hi = -1;
    }

  public IntRange (int lo, int hi)
    {
      if (lo > hi)
        {
          _lo = 0;
          _hi = -1;
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

  public final boolean contains (int element)
    {
      return _lo <= element && element <= _hi;
    }

  public final boolean contains (double element)
    {
      int n = (int)element;
      return _lo <= n && n <= _hi;
    }

  public final boolean contains (Object element)
    {
      int n = ((RuntimeInt)element).value();
      return _lo <= n && n <= _hi;
    }

  public final int lb ()
    {
      return _lo;
    }

  public final int ub ()
    {
      return _hi;
    }

  public final int size ()
    {
      return _hi - _lo + 1;
    }

  public final int getIndex (int i)
    {
      return i - _lo;
    }

  public final int getIndex (double x)
    {
      return (int)x - _lo;
    }

  public final int getIndex (Object o)
    {
      return ((RuntimeInt)o).value() - _lo;
    }

  public final void lock ()
    {
    }
    
  public final void unlock ()
    {
    }
    
  public final boolean isLocked ()
    {
      return true;
    }

  public final IntSet toIntSet ()
    {
      if (_set != null)
        return _set;

      _set = new IntSet();

      for (int i=_lo; i<=_hi; i++)
        _set.add(i);

      return _set;
    }

  public final IntIterator intIterator (boolean ordered)
    {
      return new IntRangeIterator(_lo,_hi);
    }

  public final IntIterator intIterator ()
    {
      return new IntRangeIterator(_lo,_hi);
    }

  public final DoubleIterator realIterator (boolean ordered)
    {
      return new RealRangeIterator(_lo,_hi);
    }

  public final DoubleIterator realIterator ()
    {
      return new RealRangeIterator(_lo,_hi);
    }

  public final Iterator iterator (boolean ordered)
    {
      return new ObjectRangeIterator(_lo,_hi);
    }

  public final Iterator iterator ()
    {
      return new ObjectRangeIterator(_lo,_hi);
    }

  public final IntIterator backwardIntIterator ()
    {
      return new BackwardIntRangeIterator(_hi,_lo);
    }

  public final DoubleIterator backwardRealIterator ()
    {
      return new BackwardRealRangeIterator(_hi,_lo);
    }

  public final Iterator backwardIterator ()
    {
      return new BackwardObjectRangeIterator(_hi,_lo);
    }

  public final IntIterator indexIterator ()
    {
      return new IntRangeIterator(0,size()-1);
    }

  /**
   * Returns <tt>true</tt> when this range is equal (as a set) to the specified object,
   */
  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (object instanceof IntRange)
        {
          IntRange range = (IntRange)object;
          return range.lb() == _lo && range.ub() == _hi;
        }

      if (object instanceof IntSet)
        {
          IntSet set = (IntSet)object;

          if (size() != set.size())
            return false;

          return equals(set.toIntRange());
        }

      if (!(object instanceof IndexableContainer)) // it may be a set of boxed ints
        return false;

      IndexableContainer other = (IndexableContainer)object;

      if (size() != other.size())
        return false;

      for (IntIterator i = intIterator(false); i.hasNext();)
        if (!other.contains(i.next())) return false;

      return true;
    }

  /**
   * Returns <tt>true</tt> when this range is equal (as a set) to the specified object,
   * with a side-effect on the specified array of ints that will contain the index
   * permutation when the sets are found to be equal.
   */
  public final boolean equals (Object object, int[] permutation)
    {
      if (this == object)
        {
          for (int i=0; i<permutation.length; i++) permutation[i] = i;
          return true;
        }

      if (!(object instanceof Indexable)) // it may be a set of ints
        return false;

      Indexable other = (Indexable)object;

      if (size() != other.size())
        return false;

      for (int i=_lo; i<=_hi; i++)
        {
          int index = other.getIndex(i);
          if (index == -1) return false;
          permutation[index] = i-_lo;
        }

      return true;
    }


  public final String toString ()
    {
      return isEmpty() ? "<empty int range>" : _lo + " .. " + _hi;
    }

  /**
   * This is a class for iterators over int ranges.
   */
  private static class IntRangeIterator implements IntIterator
  {
    private int _current;
    private int _hi;

    public IntRangeIterator (int lo, int hi)
      {
        _current = lo;
        _hi = hi;
      }

    public final boolean hasNext ()
      {
        return _current <= _hi;
      }

    public final int next ()
      {
        return _current++;
      }
  }

  /**
   * This is a class for backward iterators over int ranges.
   */
  private static class BackwardIntRangeIterator implements IntIterator
  {
    private int _current;
    private int _lo;

    public BackwardIntRangeIterator (int hi, int lo)
      {
        _current = hi;
        _lo = lo;
      }

    public final boolean hasNext ()
      {
        return _current >= _lo;
      }

    public final int next ()
      {
        return _current--;
      }
  }

  /**
   * This is a class for iterators over int ranges but returning boxed ints.
   */
  private static class ObjectRangeIterator implements Iterator
  {
    private IntRangeIterator _intIterator;

    public ObjectRangeIterator (int lo, int hi)
      {
        _intIterator = new IntRangeIterator(lo,hi);
      }

    public final boolean hasNext ()
      {
        return _intIterator.hasNext();
      }

    public final Object next ()
      {
        return Runtime.newInt(_intIterator.next());
      }

    public final void remove ()
      {
        throw new UnsupportedOperationException();
      }
  }

  /**
   * This is a class for backward iterators over int ranges but returning boxed ints.
   */
  private static class BackwardObjectRangeIterator implements Iterator
  {
    private BackwardIntRangeIterator _intIterator;

    public BackwardObjectRangeIterator (int hi, int lo)
      {
        _intIterator = new BackwardIntRangeIterator(hi,lo);
      }

    public final boolean hasNext ()
      {
        return _intIterator.hasNext();
      }

    public final Object next ()
      {
        return Runtime.newInt(_intIterator.next());
      }

    public final void remove ()
      {
        throw new UnsupportedOperationException();
      }
  }

  /**
   * This is a class for iterators over int ranges but returning doubles.
   */
  private static class RealRangeIterator implements DoubleIterator
  {
    private IntRangeIterator _intIterator;

    public RealRangeIterator (int lo, int hi)
      {
        _intIterator = new IntRangeIterator(lo,hi);
      }

    public final boolean hasNext ()
      {
        return _intIterator.hasNext();
      }

    public final double next ()
      {
          return (double)_intIterator.next();
      }

  }

  /**
   * This is a class for backward iterators over int ranges but returning doubles.
   */
  private static class BackwardRealRangeIterator implements DoubleIterator
  {
    private BackwardIntRangeIterator _intIterator;

    public BackwardRealRangeIterator (int hi, int lo)
      {
        _intIterator = new BackwardIntRangeIterator(hi,lo);
      }

    public final boolean hasNext ()
      {
        return _intIterator.hasNext();
      }

    public final double next ()
      {
          return (double)_intIterator.next();
      }

  }

}
