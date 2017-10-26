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
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javawebparts.core.org.apache.commons.lang.ArrayUtils;
import junit.framework.TestCase;

/**
 * Unit tests of the CaseInsensitiveMap targeting the functionality of the
 * collection returned by the values() method.  
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMapValuesTest extends TestCase {
  
  private Map theMap;
  private Collection theValues;
  
  public void setUp() {
    Map valuesMap = new HashMap();
    valuesMap.put("Key1", "value1");
    valuesMap.put("Key2", "value2");
    valuesMap.put("Key3", "value2");
    valuesMap.put("Key of null", null);
    
    theMap = new CaseInsensitiveMap(valuesMap);
    theValues = theMap.values();
  }
  
  public void testContainsSameKeys() {
    assertEquals(4, theValues.size());
    assertFalse(theValues.isEmpty());
    assertTrue(theValues.contains("value1"));
    assertTrue(theValues.contains("value2"));
    assertTrue(theValues.contains(null));
  }

  public void testContainsAll() {
    List expectedValues = Arrays.asList(
        new String[] {"value1", "value2", null} );
    List unexistingValues = Arrays.asList(
        new String[] {"value1", "unexisting value"} );
    
    assertTrue(theValues.containsAll(expectedValues));
    assertFalse(theValues.containsAll(unexistingValues));
  }

  public void testRemoveAllReflectedInMap() {
    List valuesToBeRemoved = Arrays.asList( new Object[] {"vALUe1", "value2", 
        null, "unexisting value", new Object()} );
    
    assertTrue(theValues.removeAll(valuesToBeRemoved));
    
    assertEquals(1, theValues.size());
    assertTrue(theValues.contains("value1"));
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsValue("value1"));
  }

  public void testRetainAllReflectedInMap() {
    List valuesToBeRetained = Arrays.asList( new Object[] {"value1", "vaLUE2",
        "unexisting value", new Object()} );
    
    assertTrue(theValues.retainAll(valuesToBeRetained));
    
    assertEquals(1, theValues.size());
    assertTrue(theValues.contains("value1"));
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsValue("value1"));
  }

  public void testRemoveAllReturnFalse() {
    List unexistingValue = Arrays.asList( new Object[] {"unexisting value"});
    
    assertFalse(theValues.removeAll(unexistingValue));
  }

  public void testRetainAllReturnFalse() {
    List existingValues = Arrays.asList(
        new String[] {"value1", "value2", null} );
    
    assertFalse(theValues.retainAll(existingValues));
  }

  public void testMapChangesReflectedInKeySet() {
    theMap.put("new key", "new value");
    theMap.remove("key1");

    assertTrue(theValues.contains("new value"));
    assertFalse(theValues.contains("value1"));
    
    // assert using the iterator to
    boolean newValueFound = false;
    boolean value1Found = false;
    for (Iterator iter = theValues.iterator(); iter.hasNext() ; ) {
      Object curValue = iter.next();
      if ("new value".equals(curValue)) {
        newValueFound = true;
      }
      if ("value1".equals(curValue)) {
        value1Found = true;
      }
    }
    assertTrue(newValueFound);
    assertFalse(value1Found);
  }

  public void testRemoveReflectedInMap() {
    assertTrue(theValues.remove("value1"));
    assertFalse(theMap.containsValue("value1"));
  }

  public void testClearReflectedInMap() {
    theValues.clear();
    assertTrue(theValues.isEmpty());
    assertEquals(0, theValues.size());
    assertTrue(theMap.isEmpty());
  }

  public void testAddAndAddAllNotSupported() {
    try {
      theValues.add("Some Value");
      fail("Should throw UnsupportedException");
    } catch (UnsupportedOperationException expected) {
      // expected
    }

    try {
      theValues.addAll(new ArrayList());
      fail("Should throw UnsupportedException");
    } catch (UnsupportedOperationException expected) {
      // expected
    }
  }
  
  public void testIteratorRemoveReflectedInMap() {
    Iterator valuesIterator = theValues.iterator();
    
    Object nextValue = valuesIterator.next();
    valuesIterator.remove();

    assertFalse(theMap.containsValue(nextValue));
  }
  
  public void testToArrayMethod() {
    Object[] keys = theValues.toArray();
    
    assertEquals(keys.length, theValues.size());
    for (int i = 0; i < keys.length; i++) {
      assertTrue(theValues.contains(keys[i]));
    }
    for (Iterator iter = theValues.iterator(); iter.hasNext();) {
      assertTrue(ArrayUtils.contains(keys, iter.next()));
    }
  }
  
}
