package javawebparts.core;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Unit tests of the class <code>CaseInsensitiveMap</code>.
 * Please note that there are more test fixtures for this class.
 * To run them all please use the <code>CaseInsensitiveMapAllTests</code> suite. 
 * 
 * @author Tamas Szabo
 */
public class CaseInsensitiveMapTest extends TestCase {
  
  private Map theMap;

  public void setUp() {
    Map valuesMap = new HashMap();
    valuesMap.put("Key1", "value1");
    valuesMap.put("Key2", "value2");
    valuesMap.put("Key3", "value2");
    valuesMap.put("Key of null", null);
    valuesMap.put(new Object(), new Object());
    
    theMap = new CaseInsensitiveMap(valuesMap);
  }
  
  public void testAccessorMethods() {
    assertFalse(theMap.isEmpty());
    assertEquals(4, theMap.size());
    assertEquals("value1", theMap.get("Key1"));
    assertTrue(theMap.containsKey("Key1"));
    assertTrue(theMap.containsValue("value1"));
  }
    
  
  public void testGetAndContainsKeyAreCaseInsensitive() {
    assertEquals("value1", theMap.get("keY1"));
    assertTrue(theMap.containsKey("keY1"));
  }

  public void testGetAndContainsKeyWithNullKey() {
    assertNull(theMap.get(null));
    assertFalse(theMap.containsKey(null));
  }

  public void testGetAndContainsKeyWithUnexistingKey() {
    assertNull(theMap.get("unexisting key"));
    assertFalse(theMap.containsKey("unexisting key"));
  }

  public void testGetAndContainsKeyForNullValue() {
    assertNull(theMap.get("Key of null"));
    assertTrue(theMap.containsKey("Key of null"));
  }

  public void testGetAndContainsThrowCCEForNonStringKey() {
    try {
      theMap.containsKey(new Object());
      fail("Should throw ClassCastException");
    } catch (ClassCastException expected) {
      // expected
    }

    try {
      theMap.get(new Object());
      fail("Should throw ClassCastException");
    } catch (ClassCastException expected) {
      // expected
    }
  }

  public void testContainsValueForMoreMappingsAndNullValue() {
    assertTrue(theMap.containsValue("value2"));
    assertTrue(theMap.containsValue(null));
  }

  public void testPut() {
    Object oldValue = theMap.put("some key", "some value");

    assertNull(oldValue);
    assertFalse(theMap.isEmpty());
    assertEquals(5, theMap.size());
    assertTrue(theMap.containsKey("some key"));
    assertTrue(theMap.containsValue("some value"));
    assertEquals("some value", theMap.get("some key"));
  }

  public void testPutReplaceIsCaseInsensitive() {
    Object oldValue = theMap.put("KeY1", "new value1");
    
    assertEquals(4, theMap.size());
    assertEquals("new value1", theMap.get("KEY1"));
    assertEquals("value1", oldValue);
  }

  public void testPutThrowsNPEIfKeyIsNull() {
    try {
      theMap.put(null, "some value");
      fail("Should throw NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }
  
  public void testPutThrowsCCEIfKeyIsntString() {
    try {
      theMap.put(new Long(1), "some value");
      fail("Should throw ClassCastException");
    } catch (ClassCastException expected) {
      // expected
    }
  }

  
  public void testPutAllWithNullThrowsNPE() {
    try {
      theMap.putAll(null);
      fail("Should throw NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }
  
  public void testMapConstructorWithNullThrowsNPE() {
    try {
      new CaseInsensitiveMap(null);
      fail("Should throw NullPointerException");
    } catch (NullPointerException expected) {
      // expected
    }
  }
  
  
  public void testRemoveIsCaseInsensitive() {
    Object removedValue = theMap.remove("KeY1");
    
    assertEquals(3, theMap.size());
    assertFalse(theMap.containsKey("Key1"));
    assertEquals("value1", removedValue);
  }
  
  public void testRemoveOfNullValue() {
    Object removedValue = theMap.remove("Key of null");
    
    assertEquals(3, theMap.size());
    assertFalse(theMap.containsKey("Key of null"));
    assertNull(removedValue);
  }
  
  public void testRemoveThrowsCCEForNonStringKey() {
    try {
      theMap.remove(new Object());
      fail("Should throw ClassCastException");
    } catch (ClassCastException expected) {
      // expected
    }
  }


  public void testClear() {
    theMap.clear();

    assertTrue(theMap.isEmpty());
    assertEquals(0, theMap.size());
    assertTrue(theMap.keySet().isEmpty());
    assertTrue(theMap.entrySet().isEmpty());
    assertTrue(theMap.values().isEmpty());
  }

}
