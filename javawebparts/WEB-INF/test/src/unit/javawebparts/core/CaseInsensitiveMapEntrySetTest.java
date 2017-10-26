/*
 * Copyright 2005 Frank W. Zammetti
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package javawebparts.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javawebparts.core.org.apache.commons.lang.ArrayUtils;

import junit.framework.TestCase;

/**
 * Unit tests of the CaseInsensitiveMap targeting the functionality of the
 * map of entries returned by the entrySet() method.  
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMapEntrySetTest extends TestCase {
  
  private Map theMap;
  private Set entrySet;
  
  public void setUp() {
    Map valuesMap = new HashMap();
    valuesMap.put("Key1", "value1");
    valuesMap.put("Key2", "value2");
    valuesMap.put("Key3", "value2");
    valuesMap.put("Key of null", null);
    
    theMap = new CaseInsensitiveMap(valuesMap);
    entrySet = theMap.entrySet();
  }
  
  public void testContainsSameKeys() {
    assertEquals(4, entrySet.size());
    assertFalse(entrySet.isEmpty());
    assertTrue(entrySet.contains(new TestMapEntry("Key1", "value1")));
    assertTrue(entrySet.contains(new TestMapEntry("Key2", "value2")));
    assertTrue(entrySet.contains(new TestMapEntry("Key3", "value2")));
    assertTrue(entrySet.contains(new TestMapEntry("Key of null", null)));
  }

  public void testContainsAll() {
    List expectedEntries = Arrays.asList(
        new Object[] {
            new TestMapEntry("Key1", "value1"), 
            new TestMapEntry("Key2", "value2"), 
            new TestMapEntry("Key3", "value2"),
            new TestMapEntry("Key of null", null) } );
    List unexistingEntries = Arrays.asList(
        new Object[] {new TestMapEntry("Key1", "value1"), 
            new TestMapEntry("Unexisting key", "value2") } ); 
    
    assertTrue(entrySet.containsAll(expectedEntries));
    assertFalse(entrySet.containsAll(unexistingEntries));
  }

  public void testRemoveAllReflectedInMap() {
    List entriesToBeRemoved = Arrays.asList( new Object[] {
        new TestMapEntry("key1", "value1"), 
        new TestMapEntry("Key2", "value2"), 
        new TestMapEntry("Key3", "value2"),
        new TestMapEntry("Key of null", null),
        new TestMapEntry("Unexisting key", "value2"), 
        new Object() } );
    
    assertTrue(entrySet.removeAll(entriesToBeRemoved));
    
    assertEquals(1, entrySet.size());
    assertTrue(entrySet.contains(new TestMapEntry("Key1", "value1")));
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsKey("Key1"));
    assertTrue(theMap.containsValue("value1"));
  }

  public void testRetainAllReflectedInMap() {
    List entriesToBeRetained = Arrays.asList( new Object[] {
        new TestMapEntry("Key1", "value1"), 
        new TestMapEntry("KeY2", "value2"), 
        new TestMapEntry("Unexisting key", "value2"), 
        new Object() } );
    
    assertTrue(entrySet.retainAll(entriesToBeRetained));
    
    assertEquals(1, entrySet.size());
    assertTrue(entrySet.contains(new TestMapEntry("Key1", "value1")));
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsKey("Key1"));
    assertTrue(theMap.containsValue("value1"));
  }

  public void testRemoveAllReturnFalse() {
    List unexistingEntry = Arrays.asList( new Object[] {
        new TestMapEntry("Unexisting key", "value2")});
    
    assertFalse(entrySet.removeAll(unexistingEntry));
  }

  public void testRetainAllReturnFalse() {
    List existingEntries = Arrays.asList(
        new Object[] {
            new TestMapEntry("Key1", "value1"), 
            new TestMapEntry("Key2", "value2"), 
            new TestMapEntry("Key3", "value2"),
            new TestMapEntry("Key of null", null) } );
    
    assertFalse(entrySet.retainAll(existingEntries));
  }

  public void testMapChangesReflectedInEntrySet() {
    theMap.put("new key", "new value");
    theMap.remove("key1");

    assertTrue(entrySet.contains(new TestMapEntry("new key", "new value")));
    assertFalse(entrySet.contains(new TestMapEntry("Key1", "value1")));
    
    // assert using the iterator to
    boolean newEntryFound = false;
    boolean entry1Found = false;
    for (Iterator iter = entrySet.iterator(); iter.hasNext() ; ) {
      Map.Entry curEntry = (Entry) iter.next();
      if (curEntry.getKey().equals("new key") 
          && curEntry.getValue().equals("new value")) {
        newEntryFound = true;
      }
      if (curEntry.getKey().equals("Key1")) {
        entry1Found = true;
      }
    }
    assertTrue(newEntryFound);
    assertFalse(entry1Found);
  }

  public void testRemoveReflectedInMap() {
    assertTrue(entrySet.remove(new TestMapEntry("Key1", "value1")));
    assertFalse(theMap.containsKey("Key1"));
  }

  public void testRemoveNotCaseInsensitive() {
    assertFalse(entrySet.remove(new TestMapEntry("KeY1", "value1")));
    assertTrue(theMap.containsKey("Key1"));
  }

  public void testClearReflectedInMap() {
    entrySet.clear();
    assertTrue(entrySet.isEmpty());
    assertEquals(0, entrySet.size());
    assertTrue(theMap.isEmpty());
  }

  public void testAddAndAddAllNotSupported() {
    try {
      entrySet.add("Some Key");
      fail("Should throw UnsupportedException");
    } catch (UnsupportedOperationException expected) {
      // expected
    }

    try {
      entrySet.addAll(new ArrayList());
      fail("Should throw UnsupportedException");
    } catch (UnsupportedOperationException expected) {
      // expected
    }
  }
  
  public void testIteratorRemoveReflectedInMap() {
    Iterator entrySetIterator = entrySet.iterator();
    
    Map.Entry nextEntry = (Entry) entrySetIterator.next();
    entrySetIterator.remove();

    assertFalse(theMap.containsKey(nextEntry.getKey()));
  }
  
  public void testToArrayMethod() {
    Object[] entries = entrySet.toArray();
    
    assertEquals(entries.length, entrySet.size());
    for (int i = 0; i < entries.length; i++) {
      assertTrue(entrySet.contains(entries[i]));
    }
    for (Iterator iter = entrySet.iterator(); iter.hasNext();) {
      assertTrue(ArrayUtils.contains(entries, iter.next()));
    }
  }

}

class TestMapEntry implements Map.Entry {

  private Object key;
  private Object value;
  
  public TestMapEntry(Object key, Object value) {
    this.key = key;
    this.value = value;
  }

  public Object getKey() {
    return this.key;
  }

  public Object getValue() {
    return this.value;
  }

  public Object setValue(Object value) {
    throw new UnsupportedOperationException();
  }
  
}
