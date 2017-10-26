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


package javawebparts.request;

import java.util.Arrays;
import java.util.Map;

import junit.framework.TestCase;

import org.springframework.mock.web.MockHttpServletRequest;


/**
 * Tests of the class RequestHelpers.
 *
 * @author Tamas Szabo
 */
public class RequestHelpersTest extends TestCase {

  private MockHttpServletRequest request;
  private final static String[] HEADER2_VALUES = new String[] {
      "value2a", "value2b"};
  
  public void setUp() {
    request = new MockHttpServletRequest();
    request.addHeader("Header1", "value1");
    
    request.addHeader("Header2", HEADER2_VALUES);
  }
  
  /**
   * Test of the method <code>getRequestHeaders()</code>
   */
  public void testGetRequestHeaders() {
    Map headers = RequestHelpers.getRequestHeaders(request);
        
    assertEquals("There should be 2 Headers", 2, headers.size());
    assertEquals("Header1 should have one value: 'value1'", 
        Arrays.asList(new String[] {"value1"}), headers.get("Header1"));
 
    assertEquals("Header2 should have two values: 'value2a' and 'value2b'",
        Arrays.asList(HEADER2_VALUES), headers.get("Header2"));

    assertEquals("Header names should be case insensitive", 
        Arrays.asList(new String[] {"value1"}), headers.get("heaDer1"));
    
  }

  /**
   * Just to make sure that the Spring mocks don't misbehave again :-)
   */
  public void testSpringMock() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader("Header1", "value1");
    assertEquals("value1", request.getHeader("Header1"));
    assertEquals("value1", request.getHeader("HeaDer1"));
  }
  
}

