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

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Tests class of the <code>CaseInsensitiveMap</code> that needs a second
 * Map to be set up for testing.
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMapWithAnotherMapTest extends TestCase {

  private Map theMap;
  private Map someOtherMap;
  
  public void setUp() {
    theMap = new CaseInsensitiveMap();
    theMap.put("KeY1", "value1");
    theMap.put("Key1b", "value1b");
    
    someOtherMap = new HashMap();
    someOtherMap.put("Key1", "new value1");
    someOtherMap.put("Key2", null);
    someOtherMap.put("key2", Boolean.FALSE);
    someOtherMap.put("key3", "value of key3");
    someOtherMap.put(null, new Object());
    someOtherMap.put(new Object(), new Object());
  }

  
  public void testPutAll() {
    theMap.putAll(someOtherMap);
    
    assertEquals(4, theMap.size());
    assertTrue(theMap.containsKey("KEY1"));
    assertTrue(theMap.containsValue("new value1"));
    assertTrue(theMap.containsKey("KEY2"));
    assertTrue(theMap.containsValue(Boolean.FALSE));
    assertTrue(theMap.containsKey("key1b"));
    assertTrue(theMap.containsKey("key3"));
  }

  
  public void testMapConstructor() {
    CaseInsensitiveMap newMap = new CaseInsensitiveMap(someOtherMap);
    
    assertEquals(3, newMap.size());
    assertTrue(newMap.containsKey("KEY1"));
    assertTrue(newMap.containsValue("new value1"));
    assertTrue(newMap.containsKey("KEY2"));
    assertTrue(newMap.containsValue(Boolean.FALSE));
    assertTrue(newMap.containsKey("key3"));
  }

  
  public void testWrapExistingMap() {
    CaseInsensitiveMap newMap = CaseInsensitiveMap.wrapExistingMap(
        someOtherMap);
    
    assertEquals(3, newMap.size());
    assertTrue(newMap.containsKey("KEY1"));
    assertTrue(newMap.containsValue("new value1"));
    assertTrue(newMap.containsKey("KEY2"));
    assertTrue(newMap.containsValue(Boolean.FALSE));
    assertTrue(newMap.containsKey("key3"));
  }
  
}
