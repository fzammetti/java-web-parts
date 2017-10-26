package javawebparts.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javawebparts.core.org.apache.commons.lang.ObjectUtils;

/**
 * A Map implementation that handles its keys in a case insensitive manner.
 * This is just a wrapper class, it uses another Map implementation of your
 * choice (HashMap by default) to actually store the entries.
 * 
 * The Map accepts only String objects as keys, it doesn't accept null keys, 
 * but it accepts null values (even if the wrapped Map doesn't support null 
 * values). 
 * 
 * The Map implements all the methods defined by the Map interface, and 
 * although it handles keys in a case insensitive manner, it maintains the
 * original headers as they were added. This means that if you use the 
 * <code>keySet()</code> or the <code>entrSet()</code> methods to get a 
 * Collection view of the keys(or Map.Entry objects respectively) you'll find 
 * the keys with the same casing you added them (ie. they will not be converted
 * to all lower-case or upper-case characters).  
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMap implements Map {
  
  
  /**
   * Internal Map used to store the Entries.
   */
  private Map map;
  
  
  /**
   * Creates a new CaseInsensitiveMap that will use a HashMap internally to 
   * store the mappings. The HashMap is created with the default no-arg 
   * constructor. If a default HashMap doesn't meet your needs please create a 
   * HashMap (or any other Map implementation) for and use the 
   * <code>wrapExistingMap(Map)</code> method to add the case-insensitive 
   * functionality to the Map you created. 
   *  
   * @see #wrapExistingMap(Map)
   */
  public CaseInsensitiveMap() {
  
    map = new HashMap();
    
  }

  
  /**
   * Creates a new CaseInsensitiveMap and adds the mappings defined in the
   * passed in map. The functionality is the same as you would create the 
   * CaseInsensitiveMap with the no-arg constructor, and then you would invoke
   * the <code>putAll(Map)</code> method on it, passing in the Map you pass
   * in to this constructor.
   * 
   * @see   #CaseInsensitiveMap()
   * @see   #putAll(Map)
   * @param someOtherMap a map containing the mappings to be added
   */
  public CaseInsensitiveMap(Map someOtherMap) {
  
    this();
    this.putAll(someOtherMap);
    
  }

  
  /**
   * Wrap an existing map and make it case-insensitive.
   * The passed in map is used to store the elements internally. 
   * Use this method if the HashMap used by default, doesn't meet your needs.
   * For example you might want to specify another initial size or load factor,
   * or another Map implementation. If the map contains any elements they will 
   * be clearedit and re-added again using the put method. This way 
   * <code>null</code> and non-string keys are ignored, and entries are 
   * overwritten if their keys are equal (case-insensitive check).
   * So you could end up with less entries than in the passed in map.
   * 
   * @param  someMap the map to wrap
   * @return         a CaseInsensitiveMap wraping the passed in map
   */
  public static CaseInsensitiveMap wrapExistingMap(Map someMap) {
    CaseInsensitiveMap cim = new CaseInsensitiveMap();
    cim.map = someMap;
    
    if (!cim.isEmpty()) {
      Map.Entry[] entriesCopy = (Entry[]) cim.map.entrySet()
          .toArray(new Map.Entry[0]);
      cim.clear();
      for (int i = 0; i < entriesCopy.length; i++) {
        try {
          cim.put(entriesCopy[i].getKey(), entriesCopy[i].getValue());
        } catch (NullPointerException npe) {
          // invalid element, ignore it
          continue;
        } catch (ClassCastException cce) {
          // invalid element, ignore it
          continue;
        }
      }
    }
    
    return cim;
  }

  
  /**
   * Returns the number of entries in this map.
   * 
   * @return the number of entries in this map.
   */
  public int size() {
    
    return this.map.size();
    
  }

  
  /**
   * Return <code>true</code> if this map contains no entries.
   * 
   * @return <code>true</code> if this map contains no entries.
   */
  public boolean isEmpty() {
    
    return this.map.isEmpty();
    
  }

  
  /**
   * Returns <code>true</code> if this map contains a mapping for the specified
   * key. The passed in key and the keys in the map are compared in a 
   * case-insensitive way.
   * 
   * @param  key                the key to check if it exists in this Map.
   * @return                    <code>true</code> if this map contains a 
   *                            mapping for the specified key.
   * @throws ClassCastException if the passed in key isn't a 
   *                            <code>String</code>
   */
  public boolean containsKey(Object key) {
    
    if (key == null) {
      return false;
    } else {
      return this.map.containsKey(((String)key).toLowerCase());
    }
    
  }

  
  /**
   * Returns <code>true</code> if this map maps at least one key to the 
   * specified value.
   *
   * @param  value the value to check if it exists in this Map
   * @return       <code>true</code> if this map maps at least one key to the 
   *               specified value.
   */
  public boolean containsValue(Object value) {
    
    for (Iterator iter = this.map.values().iterator(); iter.hasNext();) {
      KeyAndValue kv = (KeyAndValue) iter.next();
      if (ObjectUtils.equals(kv.getValue(),value)) {
        return true;
      }
    }
    return false;
    
  }

  
  /**
   * Returns the value mapped to the specified key or <code>null</code> if the
   * specified key isn't mapped to any value. A return value of 
   * <code>null</code> does not <em>necessarily</em> indicate that the map 
   * contains no mapping for the key; it is also possible that the map 
   * explicitly maps the key to <code>null</code>. The <code>containsKey</code> 
   * method may be used to distinguish these two cases.
   * 
   * @param  key                the key whose associated value is to be returned
   * @return                    the value mapped to the specified key or 
   *                            <code>null</code> if the specified key isn't 
   *                            mapped to any value.
   * @throws ClassCastException if the passed in key isn't a 
   *                            <code>String</code>
   */
  public Object get(Object key) {
  
    if (key == null) {
      return null;
    }
    KeyAndValue kv = (KeyAndValue)this.map.get(((String)key).toLowerCase());
    
    if (kv == null) {
      return null;
    } else {
      return kv.getValue();
    }
    
  }

  
  /**
   * Associates the specified value with the specified key. If the map 
   * previously contained a mapping with a key that is equal (case-insensitive
   * comparition) to the passed in key, the old mapping is removed and the old
   * value is returned from the method.
   * 
   * @param  key                  the key to be associated with the value
   * @param  value                the value to be associated with the key
   * @return                      the value that was previously associated with 
   *                              the specified key or <code>null</code> if no 
   *                              previous association existed. 
   *                              <code>Null</code> can also be returned if the
   *                              specified key was previously associated with 
   *                              the <code>null</code> value.
   * @throws NullPointerException if the passed in key is null
   * @throws ClassCastException   if the passed in key isn't a 
   *                              <code>String</code>
   */
  public Object put(Object key, Object value) {
  
    KeyAndValue kv = (KeyAndValue)this.map.put(
        ((String)key).toLowerCase(), new KeyAndValue(key,value));
    if (kv == null) {
      return null;
    } else {
      return kv.getValue();
    }
    
  }

  
  /** 
   * Removes the mapping for this key from this map if present. The specified
   * key and the keys from the mapping are compared in a case-insensitive 
   * manner.
   * 
   * @param  key                the key whose mapping is to be removed from the
   *                            map
   * @return                    the value that was associated with the key or 
   *                            <code>null</code> if there was no mapping for 
   *                            the specified key. A value of <code>null</code> 
   *                            can also be returned if the specified key was
   *                            associated with the <code>null</code> value. 
   * @throws ClassCastException if the passed in key isn't a 
   *                            <code>String</code>
   */
  public Object remove(Object key) {
  
    if (key == null) {
      return null;
    }
    KeyAndValue kv = (KeyAndValue)this.map.remove(((String)key).toLowerCase());
    if (kv == null) {
      return null;
    } else {
      return kv.getValue();
    }
    
  }

  
  /**
   * Iterates over all the mappings of the specified map and adds them with
   * <code>put(key, value)</code> to this map. The method ignores (doesn't add)
   * null and non-string keys.
   * 
   * @param  otherMap             Map containing the mappings to be added to 
   *                              this map
   */
  public void putAll(Map otherMap) {
    
    for (Iterator iter = otherMap.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      try {
        this.put(entry.getKey(),entry.getValue());
      } catch (NullPointerException npe) {
        // invalid element ... ignore
        continue;
      } catch (ClassCastException npe) {
        // invalid element ... ignore
        continue;
      }
    }
    
  }

  
  /**
   * Removes all the mappings from this map.
   */
  public void clear() {
  
    this.map.clear();
    
  }

  
  /**
   * Returns a set view of the keys contained in this map. The set is backed by 
   * the map, so changes to the map are reflected in the set, and vice-versa. 
   * The set supports element removal, which removes the corresponding mapping 
   * from this map, via the <code>Iterator.remove, Set.remove, removeAll, 
   * retainAll,</code> and <code>clear</code> operations. 
   * It does not support the <code>add</code> or <code>addAll</code> 
   * operations.
   * 
   * @return a set view of the keys contained in this map
   */
  public Set keySet() {
    
    return new KeySet();
    
  }
    
  
  /**
   * Returns a collection view of the values contained in this map. 
   * The collection is backed by the map, so changes to the map are reflected 
   * in the collection, and vice-versa. The collection supports element 
   * removal, which removes the corresponding mapping from the map, via the 
   * <code>Iterator.remove, Collection.remove, removeAll, retainAll</code> and 
   * <code>clear</code> operations. It does not support the <code>add</code> or
   * <code>addAll</code> operations.
   * 
   * @return a collection view of the values contained in this map.
   */
  public Collection values() {
    
    return new ValuesCollection();
    
  }

  
  /**
   * Returns a set view of the mappings contained in this map. 
   * Each element in the returned set is a <code>Map.Entry</code>. The set is 
   * backed by the map, so changes to the map are reflected in the set, and 
   * vice-versa. The set supports element removal, which removes the 
   * corresponding mapping from the map, via the <code>Iterator.remove, 
   * Set.remove, removeAll, retainAll</code> and <code>clear</code> operations.
   * It does not support the <code>add</code> or <code>addAll</code> operations.
   * 
   * @return a set view of the mappings contained in this map.
   */
  public Set entrySet() {
    
    return new EntrySet();
    
  }
  

  /**
   * Compares this map with the passed in object for equality.
   * Returns <code>true</true> if the passed in object is also a 
   * CaseInsensitiveMap and the entrySets of the two maps are equal.
   *
   * @param  obj the object to compare with
   * @return     <code>true</code> if the passed in object and this map are 
   *             equal
   */
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || !obj.getClass().equals(CaseInsensitiveMap.class)) {
      return false;
    }
    CaseInsensitiveMap that = (CaseInsensitiveMap)obj;
    
    return this.entrySet().equals(that.entrySet());
  }

  
  /**
   * Returns the hash code of this map.
   * 
   * @return the hash code of this map.
   */
  public int hashCode() {
    return this.entrySet().hashCode();
  }


  /**
   * Returns the string representation of this map.
   *  
   * @see java.lang.Object#toString()
   * @return the string representation of this map.
   */
  public String toString() {
  
    StringBuffer buffer = new StringBuffer("{");
    
    boolean first = true;
    for (Iterator iter = this.entrySet().iterator(); iter.hasNext();) {
      Map.Entry entry = (Map.Entry) iter.next();
      if (first) {
        first = false;
      } else {
        buffer.append(",");
      }
      buffer.append(entry.getKey()).append("=").append(entry.getValue());
    }
    buffer.append("}");

    return buffer.toString();
  
  }
  
  
  /**
   * Inner class used to store a key-value pair.
   * When a key and value are passed to this Map, we store the lowercased key
   * in an internal Map, to have the case-insensitive behaviour.
   * However, we will need the original key (original casing) as well, so in
   * the value of the internal Map we store a KeyAndValue object, that has 
   * the original key and the value.
   */
  private static class KeyAndValue {
    
    private Object key;
    private Object value;
  
    
    /**
     * Creates a new KeyAndValue objects using the passed in key and value.
     * 
     * @param inKey   the key to be used.
     * @param inValue the value to be used.
     */
    public KeyAndValue(Object inKey, Object inValue) {
    
      this.key = inKey;
      this.value = inValue;

    }

    
    /**
     * Returns the key.
     * 
     * @return the key.
     */
    public Object getKey() {
    
      return key;
    
    }

    
    /**
     * Returns the value.
     * 
     * @return the value.
     */
    public Object getValue() {
    
      return value;
      
    }

    
    /**
     * Set the value.
     * 
     * @param newValue the value to be set.
     */
    public void setValue(Object newValue) {
    
      this.value = newValue;
      
    }
    
  } // end of KeyAndValue

  
  /**
   * Our <code>Map.Entry</code> implementation used in the Set returned by the 
   * <code>entrySet()</code> method.
   * This implementation wraps the internal Maps <code>Map.Entry</code> so
   * it can return the original key from the value component of the internal
   * Map.
   */
  private static class FakeEntry implements Map.Entry {

    private Map.Entry entry;

    
    /**
     * Constructs a new Entry by wrapping the passed in Map.Entry
     * 
     * @param sourceEntry the Map.Entry to be wrapped
     */
    FakeEntry(Map.Entry sourceEntry) {
    
      this.entry = sourceEntry;
    
    }

    
    /**
     * Returns the key of the entry.
     * 
     * @return the key of the entry.
     */
    public Object getKey() {
    
      return ((KeyAndValue)entry.getValue()).getKey();
    
    }

    
    /**
     * Returns the value of the entry.
     * 
     * @return the value of the entry.
     */
    public Object getValue() {
    
      return ((KeyAndValue)entry.getValue()).getValue();
    
    }

    
    /**
     * Sets the value of the entry.
     * 
     * @param  newValue the new value to be set.
     * @return          the old value of the entry
     */
    public Object setValue(Object newValue) {
    
      Object oldValue = this.getValue();
      ((KeyAndValue)entry.getValue()).setValue(newValue);
      return oldValue;
      
    }

    
    /**
     * Returns <code>true</code> if this Map.Entry is equal to the Object
     * passed in.
     * 
     * @param  o the Object to compare against
     * @return   <code>true</code> if this Map.Entry is equal to the Map.Entry
     *           passed in.
     */
    public boolean equals(Object o) {
    
      if (!(o instanceof Map.Entry)) {
        return false;
      }
      
      Map.Entry that = (Map.Entry)o;
      
      return ObjectUtils.equals(this.getKey(), that.getKey())
             && ObjectUtils.equals(this.getValue(), that.getValue());
    }


    /**
     * Returns the hash code of the object.
     * 
     * @return the hash code of the object
     */
    public int hashCode() {
      int hashCode = 17;
      
      hashCode = 31 * hashCode + ObjectUtils.hashCode(this.getKey());
      hashCode = 31 * hashCode + ObjectUtils.hashCode(this.getValue());

      return hashCode;
    }
    
  } // end of FakeEntry

  
  /**
   * Abstract implementation having all the common functionality used by
   * the collection view classes returned by 
   * <code>keySet(), entrySet()</code> and <code>values()</code>.
   */
  private abstract class AbstractCollectionView implements Collection {

    
    /**
     * Returns the number of elements in the Collection.
     * 
     * @return the number of elements in the Collection.
     */
    public int size() {
    
      return CaseInsensitiveMap.this.size();
    
    }

    
    /**
     * Returns <code>true</cose> if the Collection doesn't contain any elements.
     * 
     * @return <code>true</cose> if the Collection doesn't contain any elements.
     */
    public boolean isEmpty() {
    
      return CaseInsensitiveMap.this.isEmpty();
    
    }

    
    /**
     * Returns <code>true</code> if the Collection contains the passed in 
     * object.
     * 
     * @param  o the Object to be checked for existence in this Collection
     * @return   <code>true</code> if the Colection contains the passed in 
     *           object.
     */
    public boolean contains(Object o) {
    
      for (Iterator iter = this.iterator(); iter.hasNext();) {
        Object curElement = iter.next();
        if (ObjectUtils.equals(curElement, o)) {
          return true;
        }
      }
      return false;
      
    }
    
    
    /**
     * Returns an array containing a copy of all the elements found in this 
     * Collection. The array isn't backed by the Collection, so it can be 
     * modified without affecting this Collection.
     * 
     * @see    Collection#toArray()
     * @return an array containing a copy of all the elements found in this 
     *         Collection.
     */
    public Object[] toArray() {
    
      List arrayList = new ArrayList(this.size());
      for (Iterator iter = this.iterator(); iter.hasNext();) {
        arrayList.add(iter.next());
      }
      return arrayList.toArray();
      
    }
    
    
    /**
     * Returns an array of the passed in type, containing a copy of all the 
     * elements found in this Collection. If the elements fit in the array 
     * passed in they will added to it, and the array will be returned. 
     * Otherwise a new array of the same type will be created, populated and 
     * returned. The array isn't backed by the Collection, so it can be 
     * modified without affecting this Collection.
     * 
     * @see   Collection#toArray(Object[])
     * @param  os an array that dictates the runtime-type of the array to be 
     *            used. 
     * @return    an array containing a copy of all the elements found in this 
     *            Collection.
     */
    public Object[] toArray(Object[] os) {
    
      List arrayList = new ArrayList(this.size());
      for (Iterator iter = this.iterator(); iter.hasNext();) {
        arrayList.add(iter.next());
      }
      return arrayList.toArray(os);
      
    }

    
    /**
     * Adds the passed in element to this collection. 
     * This method is unsupported for this collection.
     * 
     * @param  o the Object to be added
     * @return   <code>true</code> if the set changed as the result of the call
     * @throws UnsupportedOperationException will always be thrown
     */
    public boolean add(Object o) {
    
      throw new UnsupportedOperationException();
      
    }

    
    /**
     * Add all the elements found in the passed in collection to this 
     * collection. This operation is unsupported for this collection.
     * 
     * @param  c the collection of which elements should be added to this 
     *           collection
     * @return   <code>true</code> if this Set 
     *                                       changed as the result of this call
     * @throws UnsupportedOperationException will always be thrown
     */
    public boolean addAll(Collection c) {
    
      throw new UnsupportedOperationException();
    
    }

    
    /**
     * Returns <code>true</code> if this Set contains all the elements of the
     * passed in Collection.
     * 
     * @param c a Collection of which elements should be checked
     * @return  <code>true</code> if this Set contains all the elements of the
     *          passed in Collection.
     */
    public boolean containsAll(Collection c) {
    
      for (Iterator iter = c.iterator(); iter.hasNext();) {
        if (!this.contains(iter.next())) {
          return false;
        }
      }
      return true;
    
    }
    

    /**
     * Remove all elements from this Set.
     */
    public void clear() {
    
      CaseInsensitiveMap.this.clear();
    
    }

    
    /**
     * Removes the passed in element from the Collection.
     * 
     * @param  o the element to be removed
     * @return   <code>true</code> if the Collection changed as the result of 
     *           the call
     */
    public boolean remove(Object o) {
    
      boolean removed = false;
      for (Iterator iter = this.iterator(); iter.hasNext(); ) {
        Object element = iter.next();
        if (ObjectUtils.equals(element, o)) {
          iter.remove();
          removed = true;
        }
      }
      return removed;
      
    }

    
    /**
     * Removes all the elements from this Collection that are in the passed in 
     * Collection.
     * 
     * @param c the Collection of which elements should be removed
     * @return  <code>true</code> if this Collection changed as the result of 
     *          this call
     */
    public boolean removeAll(Collection c) {
    
      boolean changed = false;
      for (Iterator iter = c.iterator(); iter.hasNext();) {
        Object o = iter.next();
        if (this.remove(o)) {
          changed = true;
        }
      }
      return changed;
    
    }

    
    /**
     * Retains in this Collection only the elements found in the passed in
     * Collection. In other words it deletes all the elements from this 
     * Collection, that are not in the passed in Collection.
     * 
     * @param  c the Collection of which elements should be retained
     * @return   <code>true</code> if this Collection changed as the result of 
     *           this call
     */
    public boolean retainAll(Collection c) {
    
      boolean changed = false;

      for (Iterator iter = this.iterator(); iter.hasNext(); ) {
        Object key = iter.next();
        if (!c.contains(key)) {
          changed = true;
          iter.remove();
        }
      }
      return changed;
    
    }
    
  }
  
  
  /**
   * Private <code>Set</code> implementation used to return a set of the keys
   * in the map.  
   * 
   * @see CaseInsensitiveMap#keySet()
   */
  private class KeySet extends AbstractCollectionView implements Set {

    
    /**
     * Returns an <code>Iterator</code> that can be used to iterate on the Set.
     * 
     * @return an <code>Iterator</code> that can be used to iterate on the Set.
     */
    public Iterator iterator() {
    
      return new Iterator() {
        private Iterator valuesIterator = map.values().iterator();
      
        public boolean hasNext() {
          return valuesIterator.hasNext();
        }

        public Object next() {
          return ((KeyAndValue)valuesIterator.next()).getKey();
        }

        public void remove() {
          valuesIterator.remove();
        }
    
      };
    }

  } // end of KeySet


  /**
   * Private <code>Set</code> implementation used to return a Set of entries
   * in the map. Every element of the Set is a <code>Map.Entry</code>  
   * 
   * @see CaseInsensitiveMap#entrySet()
   */
  private class EntrySet extends AbstractCollectionView implements Set {
  
    
    /**
     * Returns an <code>Iterator</code> that can be used to iterate on the Set.
     * 
     * @return an <code>Iterator</code> that can be used to iterate on the Set.
     */
    public Iterator iterator() {
    
      return new Iterator() {
        private Iterator entriesIterator = map.entrySet().iterator();
        
        public boolean hasNext() {
          return entriesIterator.hasNext();
        }

        public Object next() {
          Map.Entry realEntry = (Map.Entry)entriesIterator.next();
          return new FakeEntry(realEntry);
        }
        
        public void remove() {
          entriesIterator.remove();
        }

      };
    }

    
    /**
     * Returns <code>true</code> if this set is equal with another set.
     * 
     * @param  obj the object to compare with
     * @return     <code>true</code> if this set is equal with another set.
     */
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof Set)) {
        return false;
      }
      Set that = (Set)obj;

      if (this.containsAll(that) && that.containsAll(this)) {
        return true;
      } 

      return false;
    }

    
    /**
     * Returns the hash code of the object.
     * 
     * @return the hash code of the object
     */
    public int hashCode() {
      int hashCode = 17;
      for (Iterator iter = this.iterator(); iter.hasNext();) {
        Map.Entry entry = (Map.Entry) iter.next();
        hashCode += 31 * hashCode + ObjectUtils.hashCode(entry);
      }
      return hashCode;
    }
      
  } // end of EntrySet

  
  /**
   * Private Collection implementation used to return a Collection of the 
   * values in the map.  
   * 
   * @see CaseInsensitiveMap#values()
   */
  private class ValuesCollection extends AbstractCollectionView {
  
    
    /**
     * Returns an <code>Iterator</code> that can be used to iterate on the 
     * values.
     * 
     * @return an <code>Iterator</code> that can be used to iterate on the 
     * values.
     */
    public Iterator iterator() {
    
      return new Iterator() {
        private Iterator valuesIterator = map.values().iterator();
        
        public boolean hasNext() {
          return valuesIterator.hasNext();
        }

        public Object next() {
          return ((KeyAndValue)valuesIterator.next()).getValue();
        }

        public void remove() {
          valuesIterator.remove();
          
        }
      
      };
    }

  } // end of ValuesCollection


}
