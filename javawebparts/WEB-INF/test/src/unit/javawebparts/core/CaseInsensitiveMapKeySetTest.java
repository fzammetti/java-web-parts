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

import javawebparts.core.org.apache.commons.lang.ArrayUtils;

import junit.framework.TestCase;

/**
 * Unit tests of the CaseInsensitiveMap targeting the functionality of the
 * set returned by the keySet() method.  
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMapKeySetTest extends TestCase {
  
  private Map theMap;
  private Set keySet;
  
  public void setUp() {
    Map valuesMap = new HashMap();
    valuesMap.put("Key1", "value1");
    valuesMap.put("Key2", "value2");
    valuesMap.put("Key3", "value2");
    valuesMap.put("Key of null", null);
    
    theMap = new CaseInsensitiveMap(valuesMap);
    keySet = theMap.keySet();
  }
  
  public void testContainsSameKeys() {
    assertEquals(4, keySet.size());
    assertFalse(keySet.isEmpty());
    assertTrue(keySet.contains("Key1"));
    assertFalse(keySet.contains("KeY1"));
    assertTrue(keySet.contains("Key2"));
    assertTrue(keySet.contains("Key3"));
    assertTrue(keySet.contains("Key of null"));
  }

  public void testContainsAll() {
    List expectedKeys = Arrays.asList(
        new String[] {"Key1", "Key2", "Key3", "Key of null"} );
    List unexistingKeys = Arrays.asList(
        new String[] {"Key1", "unexisting key"} );
    
    assertTrue(keySet.containsAll(expectedKeys));
    assertFalse(keySet.containsAll(unexistingKeys));
  }

  public void testRemoveAllReflectedInMap() {
    List keysToBeRemoved = Arrays.asList( new Object[] {"key1", "Key2", "Key3",
        "Key of null", "unexisting key", new Object()} );
    
    assertTrue(keySet.removeAll(keysToBeRemoved));
    assertEquals(1, keySet.size());
    assertTrue(keySet.contains("Key1"));
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsKey("Key1"));
  }

  public void testRetainAllReflectedInMap() {
    List keysToBeRetained = Arrays.asList( new Object[] {"Key1", "KeY2",
        "unexisting key", new Object()} );
    
    assertTrue(keySet.retainAll(keysToBeRetained));
    assertEquals(1, keySet.size());
    assertTrue(keySet.contains("Key1"));
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsKey("Key1"));
  }

  public void testRemoveAllReturnFalse() {
    List unexistingKey = Arrays.asList( new Object[] {"unexisting key"});
    
    assertFalse(keySet.removeAll(unexistingKey));
  }

  public void testRetainAllReturnFalse() {
    List existingKeys = Arrays.asList(
        new String[] {"Key1", "Key2", "Key3", "Key of null"} );
    
    assertFalse(keySet.retainAll(existingKeys));
  }

  public void testMapChangesReflectedInKeySet() {
    theMap.put("new key", "new value");
    theMap.remove("key1");

    assertTrue(keySet.contains("new key"));
    assertFalse(keySet.contains("Key1"));
    
    // assert using the iterator to
    boolean newKeyFound = false;
    boolean key1Found = false;
    for (Iterator iter = keySet.iterator(); iter.hasNext() ; ) {
      Object curKey = iter.next();
      if (curKey.equals("new key")) {
        newKeyFound = true;
      }
      if (curKey.equals("Key1")) {
        key1Found = true;
      }
    }
    assertTrue(newKeyFound);
    assertFalse(key1Found);
  }

  public void testRemoveReflectedInMap() {
    assertTrue(keySet.remove("Key1"));
    assertFalse(theMap.containsKey("Key1"));
  }

  public void testRemoveNotCaseInsensitive() {
    assertFalse(keySet.remove("KeY1"));
    assertTrue(theMap.containsKey("Key1"));
  }

  public void testClearReflectedInMap() {
    keySet.clear();
    assertTrue(keySet.isEmpty());
    assertEquals(0, keySet.size());
    assertTrue(theMap.isEmpty());
  }

  public void testAddAndAddAllNotSupported() {
    try {
      keySet.add("Some Key");
      fail("Should throw UnsupportedException");
    } catch (UnsupportedOperationException expected) {
      // expected
    }

    try {
      keySet.addAll(new ArrayList());
      fail("Should throw UnsupportedException");
    } catch (UnsupportedOperationException expected) {
      // expected
    }
  }
  
  public void testIteratorRemoveReflectedInMap() {
    Iterator keySetIterator = keySet.iterator();
    
    Object nextKey = keySetIterator.next();
    keySetIterator.remove();

    assertFalse(theMap.containsKey(nextKey));
  }
  
  public void testToArray() {
    Object[] keys = keySet.toArray();
    
    assertEquals(keys.length, keySet.size());
    for (int i = 0; i < keys.length; i++) {
      assertTrue(keySet.contains(keys[i]));
    }
    for (Iterator iter = keySet.iterator(); iter.hasNext();) {
      assertTrue(ArrayUtils.contains(keys, iter.next()));
    }
  }
  
}
