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

import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit tests of the CaseInsensitiveMap containing tests that work on an empty
 * map.  
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMapEmptyMapTest extends TestCase {

  private Map theMap;
  
  public void setUp() {
    theMap = new CaseInsensitiveMap();
  }
  
  public void testAccessorMethods() {
    assertTrue(theMap.isEmpty());
    assertEquals(0, theMap.size());
    
    assertTrue(theMap.keySet().isEmpty());
    assertEquals(0, theMap.keySet().size());
    assertFalse(theMap.keySet().iterator().hasNext());
  }
  
  public void testPut() {
    Object oldValue = theMap.put("some key", "some value");

    assertNull(oldValue);
    assertFalse(theMap.isEmpty());
    assertEquals(1, theMap.size());
    assertTrue(theMap.containsKey("some key"));
    assertTrue(theMap.containsValue("some value"));
    assertEquals("some value", theMap.get("some key"));
  }

}
