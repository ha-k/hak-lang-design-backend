//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Wed Jun 20 14:29:51 2012 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */
import java.lang.reflect.Array;

/**
 * This is the mother of <a href="IntMap.html"><tt>IntMap</tt></a>,
 * <a href="RealMap.html"><tt>realMap</tt></a>, and
 * <a href="ObjectMap.html"><tt>ObjectMap</tt></a>.
 */
public abstract class RuntimeMap implements RuntimeObject
{
  protected Indexable _indexable;

  public final Indexable indexable ()
    {
      return _indexable;
    }

//   public final Indexer getIndexer ()
//     {
//       return _indexable;
//     }

  public final int size ()
    {
      return _indexable.size();
    }

  public final void setIndexable(Indexable indexable)
    {
      _indexable = indexable;
    }

  abstract public Object extractArray ();
  abstract public RuntimeMap setArray (Object array);
  abstract public RuntimeMap copy ();

  /**
   * Trims the underlying native array to the size of its indexable if necessary and
   * returns it (this is needed because the size of the array of a map indexed by an
   * int range  may grow to accommodate additional elements). Calling this method
   * guarantees that the underlying array of this map has the same size as its indexable.
   */
  public final void trimToSize ()
    {
      Object nativeArray = extractArray();
      Indexable indexable = indexable();

      if (nativeArray != null && indexable instanceof IntRange)
        {
          int indexableSize = indexable.size();

          if (Array.getLength(nativeArray) > indexableSize)
            {
              Object oldNativeArray = nativeArray;
              Class componentType = oldNativeArray.getClass().getComponentType();
              nativeArray = Array.newInstance(componentType,indexableSize);

              if (indexableSize > 0)
                System.arraycopy(oldNativeArray,0,nativeArray,0,indexableSize);
              setArray(nativeArray);
            }
        }
    }
}

