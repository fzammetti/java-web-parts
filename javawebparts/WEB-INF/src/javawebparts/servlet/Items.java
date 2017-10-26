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


package javawebparts.servlet;


import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;


/**
 * This class holds the items from a configuration file.  It is a simple list
 * of String items.  This class is used by the TextReturnerServlet and the
 * ImageReturnerServlet.
 */
public class Items implements Serializable {


  /**
   * Constant used when the order is "random".
   */
  private static final String ORDER_RANDOM = "random";


  /**
   * Constant used when the order is "forward".
   */
  private static final String ORDER_FORWARD = "forward";


  /**
   * Constant used when the order is "reverse".
   */
  private static final String ORDER_REVERSE = "reverse";


  /**
   * The array that will store the order to return the items in.
   */
  private int[] orderArray;


  /**
   * Current index into the orderArray array.
   */
  private int orderArrayIndex;


  /**
   * Our actual list of items as read in from the file.
   */
  private ArrayList items = new ArrayList();


  /**
   * What order items are retrieved in.
   */
  private String order;


  /**
   * This method is called AFTER the items have been populated by
   * Digester.  It's job is to populate the orderArray with the list of index
   * values in the order the items will be retrieved.  So, later on when the
   * servlet does its thing, all it has to do is call getItem() on this
   * object and it will get the next item in the correct order.
   *
   * @param inOrder What the order will be ("random", "forward" or "reverse").
   */
  public void setOrder(String inOrder) {

    orderArray      = new int[items.size()];
    orderArrayIndex = 0;
    // Random order.
    if (inOrder.equalsIgnoreCase(ORDER_RANDOM)) {
      for (int i = 0; i < items.size(); i++) {
        orderArray[i] = i;
      }
      Random r = new Random(new GregorianCalendar().getTimeInMillis());
      for (int i = 0; i < orderArray.length - 1; i++) {
        int k = r.nextInt(orderArray.length - i);
        int temp = orderArray[i + k];
        orderArray[i + k] = orderArray[i];
        orderArray[i] = temp;
      }
    }
    // Forward order.
    if (inOrder.equalsIgnoreCase(ORDER_FORWARD)) {
      for (int i = 0; i < items.size(); i++) {
        orderArray[i] = i;
      }
    }
    // Reverse order.
    if (inOrder.equalsIgnoreCase(ORDER_REVERSE)) {
      int j = items.size() - 1;
      for (int i = 0; i < items.size(); i++) {
        orderArray[i] = j;
        j--;
      }
    }

    order = inOrder;

  } // End setOrder().


  /**
   * Adds an item to the collection.
   *
   * @param item The item to add.
   */
  public void setItem(String item) {

    items.add(item);

  } // End setItem().


  /**
   * Returns the next item in the list.  When the end of orderArray is
   * reached, we start from the beginning again.  So, it is true that random
   * is really on psuedo-random, but we'll live with that!
   *
   * @return The next item.
   */
  public String getItem() {

    String retVal = (String)items.get(orderArray[orderArrayIndex]);
    orderArrayIndex++;
    // If we reached the end, restart at the beginning.
    if (orderArrayIndex > orderArray.length - 1) {
      // Set the order to what it currently is.  This is technically
      // redundant for forward and reverse, but does no harm.  For random
      // though, it re-randomizes so we don't repeat in the same order.
      setOrder(order);
      orderArrayIndex = 0;
    }
    return retVal;

  } // End getItems().


  /**
   * Overriden toString method.
   *
   * @return A reflexively-built string representation of this bean.
   */
  public String toString() {

    String str = null;
    StringBuffer sb = new StringBuffer(1000);
    sb.append("[" + super.toString() + "]={");
    boolean firstPropertyDisplayed = false;
    try {
      Field[] fields = this.getClass().getDeclaredFields();
      for (int i = 0; i < fields.length; i++) {
        if (firstPropertyDisplayed) {
          sb.append(", ");
        } else {
          firstPropertyDisplayed = true;
        }
        sb.append(fields[i].getName() + "=" + fields[i].get(this));
      }
      sb.append("}");
      str = sb.toString().trim();
    } catch (IllegalAccessException iae) {
      iae.printStackTrace();
    }
    return str;

  } // End toString().


} // End class.
