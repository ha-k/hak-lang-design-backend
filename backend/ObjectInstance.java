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
import hlt.language.design.types.*;

/**
 * This is the class of object structures.
 */
public class ObjectInstance implements RuntimeObject
{
  private ClassType _type;

  private int[] _intFields;
  private double[] _realFields;
  private Object[] _objectFields;

  public ObjectInstance (ClassType type) throws ObjectInitializationException
    {
      if (!type.isDeclared())
        throw new ObjectInitializationException("undeclared class type: "+type);

      _type = type;

      int intCount = type.intFieldsCount();
      int realCount = type.realFieldsCount();
      int objectCount = type.objectFieldsCount();

      if (intCount != 0) _intFields = new int[intCount];
      if (realCount != 0) _realFields = new double[realCount];
      if (objectCount != 0) _objectFields = new Object[objectCount];
    }
    
  public final ClassType type ()
    {
      return _type;
    }

  public final String getClassName()
    {
      return _type.name();
    }

  public final int getIntField (int offset)
    {
      return _intFields[offset];
    }

  public final int setIntField (int offset, int value)
    {
      return _intFields[offset] = value;
    }

  public final double getRealField (int offset)
    {
      return _realFields[offset];
    }

  public final double setRealField (int offset, double value)
    {
      return _realFields[offset] = value;
    }

  public final Object getObjectField (int offset)
    {
      return _objectFields[offset];
    }

  public final Object setObjectField (int offset, Object value)
    {
      return _objectFields[offset] = value;
    }

  final public String toString ()
    {
      _clearTags();
      return _toTaggedString();
    }

  private final String _toTaggedString ()
    {
      String tag = _getTag();
      boolean dejaVu = (tag != null);

      if (!dejaVu) tag = _putTag();

      StringBuilder buf = new StringBuilder(_type.name()+tag);

      if (!dejaVu)
        {
          buf.append("{");

          DefinedEntry[] fields = _type.fields();

          if (fields.length == 0) return buf+"}";
          for (int i=0; i<fields.length; i++)
            buf.append(fields[i].symbol()+" = "+_fieldStringValueOf(fields[i])
                       +(i == fields.length-1?"}":", "));
        }

      return buf.toString();
    }

  /**
   * The following table is used to keep a record of tags labelling objects that have
   * already been displayed and thus need not be redisplayed beyond their tag.
   * This allows printing circular objects, and saves on the display form of objects
   * that are shared. A tag is of the form <tt>Classname#n</tt>, where <tt>Classname</tt>
   * is the name of the class of the tagged object and <tt>n</tt> is a number. Therefore,
   * the table <tt>_tags</tt> is a table of tables: it maps a classname to a table that
   * maps a number to the object it designates.
   */
  private final static HashMap _tags = new HashMap();

  private static void _clearTags ()
    {
      _tags.clear();
    }

  private String _getTag ()
    {
      HashMap typeTags = (HashMap)_tags.get(_type);

      if (typeTags == null)
        {
          _tags.put(_type,typeTags = new HashMap());
          return null;
        }

      return (String)typeTags.get(this);
    }

  private String _putTag ()
    {
      HashMap typeTags = (HashMap)_tags.get(_type);

      if (typeTags == null)
        _tags.put(_type,typeTags = new HashMap());

      String tag = "#"+typeTags.size();
      typeTags.put(this,tag);

      return tag;
    }

  private final String _fieldStringValueOf (DefinedEntry entry)
    {
      switch (entry.fieldSort())
        {
        case Type.INT_SORT:
          int value = getIntField(entry.fieldOffset());
          Type type = entry.fieldType();
          if (type.isVoid()) return "()";
          if (type.isBoolean()) return value == 0 ? "false" : "true";
          if (type.isChar()) return String.valueOf((char)value);
          return String.valueOf(value);
        case Type.REAL_SORT:
          return String.valueOf(getRealField(entry.fieldOffset()));
        }

      Object object = getObjectField(entry.fieldOffset());

      if (object == null) return "null";

      if (object instanceof ObjectInstance)
        return ((ObjectInstance)object)._toTaggedString();

      String fieldString = object.toString();

      // We need to recognize boxed integer types like void, boolean, and char. However,
      // the following does not work always correctly because the type instance is not available
      // in objects built by polymorphic code! Must change all this... (to be done later -hlt).

//        _type.bindParameters();
//        Type type = entry.fieldType();

//        if (type.isVoid())
//          fieldString = "()";
//        else
//          {
//            int value = ((RuntimeInt)object).value();
//            if (type.isBoolean())
//              fieldString = value == 0 ? "false" : "true";
//            else
//              if (type.isChar())
//                fieldString = String.valueOf((char)value);
//          }
//        _type.unbindParameters();

      return fieldString;
    }

}
