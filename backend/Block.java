//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\
// PLEASE DO NOT EDIT WITHOUT THE EXPLICIT CONSENT OF THE AUTHOR! \\
//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\//\\

package hlt.language.design.backend;

/**
 * @version     Last modified on Sun Nov 25 17:12:40 2018 by hak
 * @author      <a href="mailto:hak@acm.org">Hassan A&iuml;t-Kaci</a>
 * @copyright   &copy; <a href="http://www.hassan-ait-kaci.net/">by the author</a>
 */

import hlt.language.design.types.CodeEntry;
import hlt.language.design.instructions.Instruction;

import hlt.language.tools.Misc;

import java.util.HashMap;

/**
 * A <tt>Block</tt> object is to a <a href="Closure.html"><tt>Closure</tt></a> object
 * what a <a href="../kernel/Scope.html"><tt>Scope</tt></a> expression is to
 * an <a href="../kernel/Abstraction.html"><tt>Abstraction</tt></a> expression. In other words,
 * it is a <tt>Closure</tt> that does not need any frame machinery.
 */
public class Block implements RuntimeObject
{
  protected Instruction[] _code;
  protected int _address;
  protected int _voidArity;
  protected int _intArity;
  protected int _realArity;
  protected int _objectArity;
  protected int _arity;

  public Block (Instruction[] code, int address,
                int voidArity, int intArity, int realArity, int objectArity)
    {
      _code = code;
      _address = address;
      _voidArity = voidArity;
      _intArity = intArity;
      _realArity = realArity;
      _objectArity = objectArity;
      _arity = _intArity + _realArity + _objectArity;
    }

  public final Instruction[] code ()
    {
      return _code;
    }

  public final int address ()
    {
      return _address;
    }

  public final int arity ()
    {
      return _arity;
    }

  public final int trueArity ()
    {
      return _arity + _voidArity;
    }

  public final int voidArity ()
    {
      return _voidArity;
    }

  public final int intArity ()
    {
      return _intArity;
    }

  public final int realArity ()
    {
      return _realArity;
    }

  public final int objectArity ()
    {
      return _objectArity;
    }

  public boolean equals (Object object)
    {
      if (this == object)
        return true;

      if (!(object instanceof Block) || object instanceof Closure)
        return false;

      Block block = (Block)object;

      return _address == block.address()
          && _intArity == block.intArity()
          && _voidArity == block.voidArity()
          && _realArity == block.realArity()
          && _objectArity == block.objectArity()
          && (_code == null ? block.code() == null : _code == block.code());
    }

  public int hashCode ()
    {
      return 2*_address + (_code == null ? 0 : 3*_code.length)
           + 5*_voidArity + 7*_intArity + 11*_realArity + 13*_objectArity;
    }

  /**
   *  This flag is used to catch cycles in <tt>toString()</tt>; a cycle may occur
   * in a block that contains an object instance whose field is that same block.
   * For example, an object instance of the class:
   * <pre>
   *      class Foo { value : int; next : int -> int = function(n) this.value + 1; }
   * </pre>
   * will have a <tt>next</tt> member that is a block that contains that very
   * object instance in its object frame.
   */
  protected boolean _dejaVu = false;    

  public String toString ()
    {
      String s = null;

      if (_dejaVu) // indicate that this block is that at the outset with same code and address ...
        s = "BLOCK(" + CodeEntry.getId(_code) + "," + _address + ", ...)";
      else
        {
          _dejaVu = true;

          s = "BLOCK(" + CodeEntry.getId(_code) + "," + _address + ","
            + "<" + _voidArity + "," + _intArity + "," + _realArity + "," + _objectArity + ">)";

          _dejaVu = false;
        }

      return s;
    }

}
