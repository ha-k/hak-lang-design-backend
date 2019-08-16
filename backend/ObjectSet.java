//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import java.util.HashMap;
import java.util.Iterator;


import hlt.language.util.IntIterator;
import hlt.language.util.DoubleIterator;
import hlt.language.util.ToIntMap;
import hlt.language.util.ObjectToIntMap;

import hlt.language.tools.Misc;

/**
 * This is a runtime representation for sets of objects.
 * @see <a href="RuntimeSet.html"><tt>RuntimeSet</tt></a>
 */
public class ObjectSet extends RuntimeSet implements Sliceable
{
  /**
   * The hash table containing the elements and their indices.
   */
  private ObjectToIntMap _map = new ObjectToIntMap();

  /**
   * This array reflects the inverse relation of <tt>_map</tt>.
   */
  private Object[] _array;

  /**
   * Constructs a new empty set.
   */
  public ObjectSet ()
    {
    }

  /**
   * Constructs a new set with the elements of the specified map.
   */
  public ObjectSet (ObjectToIntMap map)
    {
      _map.include(map);
    }

  /**
   * Returns the underlying index map representing the set.
   */
  final ToIntMap map ()
    {
      return _map;
    }

  /**
   * Returns the inverse relation of the index map as an array.
   */
  public final Object[] array ()
    {
      if (_map.size() > 0 && (_array == null || _hasHoles))
        {
          _array = new Object[_map.size()];

          if (_hasHoles)
            {
              ToIntMap.Entry[] entries = _resetIndices();
              for (int i = _array.length; i-->0;)
                {
                  ObjectToIntMap.Entry entry = (ObjectToIntMap.Entry)entries[i];
                  _array[entry.value()] = entry.key();
                }
            }
          else
            for (Iterator i = _map.iterator(); i.hasNext();)
              {
                ObjectToIntMap.Entry entry = (ObjectToIntMap.Entry)i.next();
                _array[entry.value()] = entry.key();
              }
        }

      return _array;
    }

  /**
   * Returns the first element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int firstInt () throws NoSuchElementException
    {
      return ((RuntimeInt)firstObject()).value();
    }
    
  /**
   * Returns the first element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double firstReal () throws NoSuchElementException
    {
      return ((RuntimeReal)firstObject()).value();
    }
    
  /**
   * Returns the first element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object firstObject () throws NoSuchElementException
    {
      if (size() == 0)
        throw new NoSuchElementException("first element of an empty set");

      return array()[0];
    }
    

  /**
   * Returns the last element of this set as an int. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int lastInt () throws NoSuchElementException
    {
      return ((RuntimeInt)lastObject()).value();
    }
    
  /**
   * Returns the last element of this set as a double. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double lastReal () throws NoSuchElementException
    {
      return ((RuntimeReal)lastObject()).value();
    }
    
  /**
   * Returns the last element of this set as an object. If there is no
   * such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object lastObject () throws NoSuchElementException
    {
      if (size() == 0)
        throw new NoSuchElementException("last element of an empty set");

      return array()[size()-1];
    }
    

  /**
   * Returns the position of given int if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (int element) throws NoSuchElementException
    {
      return ord(new RuntimeInt(element));
    }
    
  /**
   * Returns the position of given double if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (double element) throws NoSuchElementException
    {
      return ord(new RuntimeReal(element));
    }
    
  /**
   * Returns the position of given object if it is an element of this set.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int ord (Object element) throws NoSuchElementException
    {
      if (_hasHoles) _resetIndices();

      int index = getIndex(element);
      if (index == -1)
        throw new NoSuchElementException(element+" doesn't belong to this set");

      return index;
    }
    

  /**
   * Returns the element following the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int next (int element) throws NoSuchElementException
    {
      return ((RuntimeInt)next(new RuntimeInt(element))).value();
    }
    
  /**
   * Returns the element following the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double next (double element) throws NoSuchElementException
    {
      return ((RuntimeReal)next(new RuntimeReal(element))).value();
    }
    
  /**
   * Returns the element following the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object next (Object element) throws NoSuchElementException
    {
      int index = ord(element)+1;

      if (index == size())
        throw new NoSuchElementException(element+" has no successor in this set");

      return array()[index];
    }
    

  /**
   * Returns the element preceding the given one in this set, as an int.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int prev (int element) throws NoSuchElementException
    {
      return ((RuntimeInt)prev(new RuntimeInt(element))).value();
    }
    
  /**
   * Returns the element preceding the given one in this set, as a double.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double prev (double element) throws NoSuchElementException
    {
      return ((RuntimeReal)prev(new RuntimeReal(element))).value();
    }
    
  /**
   * Returns the element preceding the given one in this set, as an object.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object prev (Object element) throws NoSuchElementException
    {
      int index = ord(element)-1;

      if (index == -1)
        throw new NoSuchElementException(element+" has no predecessor in this set");

      return array()[index];
    }
    

  /**
   * Returns the element following the given one in this set, as an int,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int nextc (int element) throws NoSuchElementException
    {
      return ((RuntimeInt)nextc(new RuntimeInt(element))).value();
    }
    
  /**
   * Returns the element following the given one in this set, as a double,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double nextc (double element) throws NoSuchElementException
    {
      return ((RuntimeReal)nextc(new RuntimeReal(element))).value();
    }
    
  /**
   * Returns the element following the given one in this set, as an object,
   * wrapping back to the beginning if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object nextc (Object element) throws NoSuchElementException
    {
      return array()[(ord(element)+1) % size()];
    }
    

  /**
   * Returns the element preceding the given one in this set, as an int,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final int prevc (int element) throws NoSuchElementException
    {
      return ((RuntimeInt)prevc(new RuntimeInt(element))).value();
    }
    
  /**
   * Returns the element preceding the given one in this set, as a double,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final double prevc (double element) throws NoSuchElementException
    {
      return ((RuntimeReal)prevc(new RuntimeReal(element))).value();
    }
    
  /**
   * Returns the element preceding the given one in this set, as an object,
   * wrapping to last element if necessary.
   * If there is no such element, throws a <tt>NoSuchElementException</tt>.
   */
  public final Object prevc (Object element) throws NoSuchElementException
    {
      return array()[(ord(element)+size()-1) % size()];
    }


  /**
   * Returns a copy of this set.
   */
  public final RuntimeSet copy ()
    {
      return new ObjectSet(_map)._setHasHoles(_hasHoles)._setMaxIndex(_maxIndex);
    }      

  /**
   * Returns the index of specified object if it belongs to this set,
   * otherwise <tt>-1</tt>.
   */
  public final int getIndex (Object element)
    {
      int index = _map.get(element);
      return index == _map.NOT_FOUND_VALUE && !_map.containsKey(element)
           ? -1
           : index;
    }

  /**
   * Returns the index of a boxed wrapper of the specified int if such an
   * object belongs to this set, otherwise <tt>-1</tt>.
   */
  public final int getIndex (int element)
    {
      return getIndex(new RuntimeInt(element));
    }

  /**
   * Returns the index of a boxed wrapper of the specified double if such an
   * object belongs to this set, otherwise <tt>-1</tt>.
   */
  public final int getIndex (double element)
    {
      return getIndex(new RuntimeReal(element));
    }

  /**
   * Adds the specified Object to this set if it does not already belong to this set.
   */
  public final void addObject (Object element) 
    {
      add(element);
    }

  /**
   * Adds the specified object to this set if it does not already belong to this set,
   * and returns this set.
   */
  protected final RuntimeSet _add (Object element)
    {
      if (!_map.containsKey(element))
        {
          _map.put(element,_maxIndex++);
          _array = null;
        }

      return this;
    }

  /**
   * Adds a boxed wrapper of the specified int to this set if it does not already
   * belong to this set, and returns this set.
   */
  protected final RuntimeSet _add (int element)
    {
      return _add(new RuntimeInt(element));
    }

  /**
   * Adds a boxed wrapper of the specified double to this set if it does not already
   * belong to this set, and returns this set.
   */
  protected final RuntimeSet _add (double element)
    {
      return _add(new RuntimeReal(element));
    }

  /**
   * Removes the specified object from this set and returns this set.
   */
  protected final RuntimeSet _remove (Object element)
    {
      if (_hasHoles |= (_map.remove(element) >= 0))
        _array = null;
      return this;
    }

  /**
   * Removes any boxed wrapper of the specified int from this set and returns this
   * set.
   */
  protected final RuntimeSet _remove (int element)
    {
      return _remove(new RuntimeInt(element));
    }

  /**
   * Removes any boxed wrapper of the specified double from this set and returns
   * this set.
   */
  protected final RuntimeSet _remove (double element)
    {
      return _remove(new RuntimeReal(element));
    }

  /**
   * Returns  <tt>true</tt> iff this set contains the specified object.
   */
  public final boolean contains (Object element)
    {
      return _map.containsKey(element);
    }

  /**
   * Returns <tt>true</tt> iff any boxed wrapper of the specified int belongs
   * to this set.
   */
  public final boolean contains (int element)
    {
      return _map.containsKey(new RuntimeInt(element));
    }

  /**
   * Returns <tt>true</tt> iff any boxed wrapper of the specified double belongs
   * to this set.
   */
  public final boolean contains (double element)
    {
      return _map.containsKey(new RuntimeReal(element));
    }

  /**
   * Returns <tt>true</tt> iff this set contains all the elements of the specified set.
   */
  public final boolean contains (RuntimeSet set)
    {
      for (Iterator i = set.iterator(false); i.hasNext();)
        if (!contains(i.next())) return false;

      return true;
    }

  /**
   * Returns this set modified to contain the union of this and the specified set.
   */
  protected final RuntimeSet _union (RuntimeSet set)
    {
      for (Iterator i = set.iterator(true); i.hasNext();)
        _add(i.next());

      return this;
    }

  /**
   * Returns this set modified to contain the intersection of this and the specified set.
   */
  public final RuntimeSet _intersection (RuntimeSet set)
    {
      for (Iterator i = iterator(false); i.hasNext();)
        {
          Object element = i.next();
          if (!set.contains(element))
            _remove(element);
        }

      return this;
    }

  /**
   * Returns this set modified to contain the set difference of this and the specified set.
   */
  public final RuntimeSet _minus (RuntimeSet set) 
    {
      for (Iterator i = set.iterator(false); i.hasNext();)
        _remove(i.next());

      return this;
    }

  /**
   * Returns this set modified to contain the symmetric difference of this and the specified set.
   */
  public final RuntimeSet _exclusion (RuntimeSet set)
    {
      for (Iterator i = set.iterator(true); i.hasNext();)
        {
          Object element = i.next();
          if (contains(element))
            _remove(element);
          else
            _add(element);
        }

      return this;
    }

  /**
   * Returns an iterator for this set such that whenever the specified boolean is
   * <tt>true</tt>, iteration will be respecting the index order of the elements;
   * otherwise, order of iteration is unpredictable.  The former is less efficient
   * than the latter and should be used only when it is mandatory to do ordered
   * iteration.
   */
  public final Iterator iterator (boolean ordered)
    {
      return ordered ? orderedIterator() : _map.keys();
    }

  /**
   * Returns an iterator for this set. This iterator is not guaranteed to be
   * ordered, unless the set is locked.
   */
  public final Iterator iterator ()
    {
      return iterator(_isLocked);
    }

  /**
   * Returns an iterator of the int values over the boxed int elements of this set
   * such that whenever the specified boolean is <tt>true</tt>, iteration will be
   * respecting the index order of the elements; otherwise, order of iteration is
   * unpredictable.  The former is less efficient than the latter and should be
   * used only when it is mandatory to do ordered iteration.
   */
  public final IntIterator intIterator (boolean ordered)
    {
      return new UnboxedIntIterator(iterator(ordered));
    }

  /**
   * Returns an iterator of the int values over the boxed int elements of this set.
   * This iterator is not guaranteed to be ordered, unless the set is locked.
   */
  public final IntIterator intIterator ()
    {
      return new UnboxedIntIterator(iterator());
    }

  /**
   * Returns an iterator of the double values over the boxed double elements of
   * this set.  such that whenever the specified boolean is <tt>true</tt>,
   * iteration will be respecting the index order of the elements; otherwise, order
   * of iteration is unpredictable.  The former is less efficient than the latter
   * and should be used only when it is mandatory to do ordered iteration.
   */
  public final DoubleIterator realIterator (boolean ordered)
    {
      return new UnboxedRealIterator(iterator(ordered));
    }

  /**
   * Returns an iterator of the double values over the boxed double elements of
   * this set.  This iterator is not guaranteed to be ordered, unless the set is
   * locked.
   */
  public final DoubleIterator realIterator ()
    {
      return new UnboxedRealIterator(iterator());
    }

  /**
   * Returns an iterator of ints over the elements of this set in the order of
   * its indices.
   */
  public final Iterator orderedIterator ()
    {
      return new OrderedIterator(array());
    }

  /**
   * Returns an iterator of objects over the elements of this set in the reverse order
   * of its indices.
   */
  public final Iterator backwardIterator ()
    {
      return new BackwardIterator(array());
    }

  /**
   * Returns an iterator of unboxed ints over the boxed int elements of this set
   * in the reverse order of its indices.
   */
  public final IntIterator backwardIntIterator ()
    {
      return new UnboxedIntIterator(backwardIterator());
    }

  /**
   * Returns an iterator of unboxed doubles over the boxed double elements of this set
   * in the reverse order of its indices.
   */
  public final DoubleIterator backwardRealIterator ()
    {
      return new UnboxedRealIterator(backwardIterator());
    }

  /**
   * Returns an iterator for the indices of the elements of this set.
   */
  public final IntIterator indexIterator ()
    {
      return _map.values();
    }

  private int[] _slice;
  private HashMap _sliceMap;

  public final void slice (int[] slice)
    {
      if (_slice == null || !Misc.equal(_slice,slice))
        _slice = slice;
      else
        return;

      if (_sliceMap == null)
        _sliceMap = new HashMap();
      else
        _sliceMap.clear();

      for (Iterator i = orderedIterator(); i.hasNext();)
        {
          RuntimeTuple tuple = (RuntimeTuple)i.next();
          Object slicer = tuple.getSlicer(slice);
          ObjectSet set = (ObjectSet)_sliceMap.get(slicer);
          if (set == null)
            _sliceMap.put(slicer,set = new ObjectSet());
          set.add(tuple);
        }
    }

  public final Iterator sliceIterator (int[][] slices, Object[] slicers)
    {
      ObjectSet set = this;
      for (int i=0; i<slicers.length; i++)
        {
          set.slice(slices[i]);
          set = (ObjectSet)set.getSlice(slicers[i]);
          if (set == null) return Misc.EMPTY_ITERATOR;
        }

      return set.orderedIterator();      
    }

  public final ObjectSet getSlice (Object slicer)
    {
      return (ObjectSet)_sliceMap.get(slicer);
    }

  /**
   * Returns <tt>true</tt> when this set is equal (as a set) to the specified object.
   * Note that order on indices is not important.
   */
  public final boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof IndexableContainer)) // may be an IntRange
        return false;

      IndexableContainer other = (IndexableContainer)object;

      if (size() != other.size())
        return false;

      for (Iterator i = iterator(false); i.hasNext();)
        if (!other.contains(i.next())) return false;

      return true;
    }

  /**
   * Returns <tt>true</tt> when this set is equal (as a set) to the specified object,
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

      if (!(object instanceof Indexable)) // may be an IntRange
        return false;

      Indexable other = (Indexable)object;

      if (size() != other.size())
        return false;

     for (Iterator i = _map.iterator(); i.hasNext();)
       {
         ObjectToIntMap.Entry entry = (ObjectToIntMap.Entry)i.next();
         int index = other.getIndex(entry.key());
         if (index == -1) return false;
         permutation[index] = entry.value();
       }

      return true;
    }

  public final String toString ()
    {
      StringBuilder buf = new StringBuilder("{");

      int l = size();
      
      for (Iterator i = iterator(true); i.hasNext();)
        buf.append(i.next()).append(--l == 0 ? "" : ", ");

      return buf.append("}").toString();
    }

  private static class UnboxedIntIterator implements IntIterator
    {
      private Iterator _iterator;

      UnboxedIntIterator (Iterator iterator)
        {
          _iterator = iterator;
        }

      public final boolean hasNext ()
        {
          return _iterator.hasNext();
        }

      public final int next ()
        {
          return ((RuntimeInt)_iterator.next()).value();
        }

    }

  private static class UnboxedRealIterator implements DoubleIterator
    {
      private Iterator _iterator;

      UnboxedRealIterator (Iterator iterator)
        {
          _iterator = iterator;
        }

      public final boolean hasNext ()
        {
          return _iterator.hasNext();
        }

      public final double next ()
        {
          return ((RuntimeReal)_iterator.next()).value();
        }

    }

  private static class OrderedIterator implements Iterator
    {
      private Object[] _array;
      private int _index = 0;

      OrderedIterator (Object[] array)
        {
          _array = array;
        }

      public final boolean hasNext ()
        {
          return _array == null ? false : _index < _array.length;
        }

      public final Object next ()
        {
          return _array[_index++];
        }

      public final void remove ()
        {
          throw new UnsupportedOperationException();
        }
    }

  private static class BackwardIterator implements Iterator
    {
      private Object[] _array;
      private int _index;

      BackwardIterator (Object[] array)
        {
          _array = array;
          _index = _array.length;
        }

      public final boolean hasNext ()
        {
          return _array == null ? false : _index > 0;
        }

      public final Object next ()
        {
          return _array[--_index];
        }

      public final void remove ()
        {
          throw new UnsupportedOperationException();
        }
    }
}

