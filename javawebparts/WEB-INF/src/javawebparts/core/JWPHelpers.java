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


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectOutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.rmi.dgc.VMID;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Random;


/**
 * This class contains miscellaneous methods that are used by classes in
 * multiple packages.
 * <br><br>
 * This class depends on the following extra packages, beyond the JDK,
 * to compile and run: None.
 *
 * @author <a href="mailto:fzammetti@omnytex.com">Frank W. Zammetti</a>.
 */
public final class JWPHelpers {


  /**
   * This is a utility class, so we want a private noarg constructor so
   * instances cannot be created.
   */
  private JWPHelpers() {
  } // End JWPHelpers().


  /**
   * This method is used by the getContextSize() method to determine if a
   * given object is serializable.
   *
   * @param  obj The object to test.
   * @return     A response string detailing the outcome of the test.
   */
  public static String serializiableTest(Object obj) {

    String ans = "ok";
    if (obj == null) {
      return "Object is null";
    } else {
      try {
        ByteArrayOutputStream bastream = new ByteArrayOutputStream();
        ObjectOutputStream p = new ObjectOutputStream(bastream);
        p.writeObject(obj);
        ans = "OK (size=" + bastream.size() + ")";
      } catch (NotSerializableException ex) {
        Field[] fields = obj.getClass().getDeclaredFields();
        ans = "NOT SERIALIZABLE (fields=" + String.valueOf(fields) + ")";
        Object fldObj = null;
        if (fields != null && (fields.length != 0)) {
          StringBuffer sb = new StringBuffer("\n" + ans + "[");
          for (int i = 0; i < fields.length; i++) {
            sb.append(fields[i].getName());
            try {
              if (obj != null) {
                fldObj = getFieldWithPrivilege(fields[i], obj);
              }
              sb.append("::");
              if (fldObj == null) {
                sb.append("<field null>");
              } else {
                sb.append(serializiableTest(fldObj));
              }
            } catch (IllegalArgumentException aex) {
              sb.append("::");
              sb.append("ILLEGAL ARGUMENT EXCEPTION");
            }
            if (i != fields.length - 1) {
              sb.append('\n');
            }
          }
          sb.append("]");
          ans = sb.toString();
        }
      } catch (IOException ex) {
        ans = "IOEXCEPTION: " + ex.getMessage();
      }
    }
    return obj.getClass().getName() + " is " + ans;

  } // End serializiableTest().


  /**
   * This method is used by the serializiableTest() method to get the
   * needed priveleges on a given field of a given object needed to
   * perform the serializable test.
   *
   * @param  fld The field to get priveleges on.
   * @param  obj The object to test.
   * @return     A Priveleged reference to the field essentially.
   */
  public static Object getFieldWithPrivilege(Field fld, Object obj) {

    final Object obj2 = obj;
    final Field  fld2 = fld;
    return AccessController.doPrivileged(
      new PrivilegedAction() {
        public Object run() {
          try {
            return fld2.get(obj2);
          } catch (IllegalAccessException ex) {
            return null;
          }
        }
      }
    );

  } // End getFieldWithPrivilege().


  /**
   * Write an array of lines out to file a file, overwriting the file if it
   * already exists.  Note that this method is synchronized!  If performance
   * rather than safety is a concern, don't use this!
   *
   * @param  pathToFile  Path to file to write.
   * @param  lines       Array of lines to be saved to the file.
   * @param  append      True to append to an existing file, if it exists, or
   *                     false to overwrite an existing file, if it exists.
   * @throws IOException If any problems are encountered.
   */
  public static synchronized void writeToFile(String pathToFile, String[] lines,
                                             boolean append)
                                             throws IOException {

    File           f   = new File(pathToFile);
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(f, append));
      for (int i = 0; i < lines.length; i++) {
        out.write(lines[i]);
        out.newLine();
      }
    } finally {
      if (out != null) {
        out.close();
      }
    }

  } // End writeToFile().


  /**
   * Read a file and return its contents as an array of Strings.  Note that
   * this method is synchronized!  If performance rather than safety is a
   * concern, don't use this!
   * @param  pathToFile            Path to file to write.
   * @throws IOException           If any other problems are encountered.
   * @return                       An array of lines read from the file.
   */
  public static synchronized String[] readFromFile(String pathToFile)
                                                  throws IOException {

    File           f        = new File(pathToFile);
    BufferedReader input    = null;
    ArrayList      a        = new ArrayList();
    try {
      input = new BufferedReader(new FileReader(f));
      String line = null;
      while ((line = input.readLine()) != null) {
        a.add(line);
      }
    } finally {
      if (input!= null) {
        input.close();
      }
    }
    Object[] reso = a.toArray();
    String[] ress = new String[reso.length];
    for (int i = 0; i < reso.length; i++) {
      ress[i] = (String)reso[i];
    }
    return ress;

  } // End readFromFile().


  /**
   * This method is used to generate a random number in a given range and the
   * number should be more random than the standard Java random number
   * generator and also should guarantee that a random number generated on two
   * different machines at the exact same time will yield a different number.
   *
   * @param  low  Lowest numeber allowed.
   * @param  high Highest number allowed.
   * @return      A number between low and high, inclusive.
   */
  public static int randomNumber(int low, int high) {

    int  vmidHash    = new VMID().hashCode();
    long currentTime = new GregorianCalendar().getTimeInMillis();
    int  ipHash      = 0;
    try {
      ipHash = InetAddress.getLocalHost().hashCode();
    } catch (UnknownHostException uhe) {
      // If this occurs, just use a "magic number".  It SHOULDN'T ever happen!
      ipHash = 1234567890;
    }
    long seed = vmidHash + currentTime + ipHash;
    Random generator = new Random(seed);
    // Get the range, casting to long to avoid overflow problems.
    long range = (long)high - (long)low + 1;
    // Compute a fraction of the range, 0 <= frac < range.
    long fraction = (long)(range * generator.nextDouble());
    return (int)(fraction + low);


  } // End randomNumber().


  /**
   * This method replaces any character in a given string that has a
   * corresponding HTML entity with that entity.  You can also pass it a
   * string containing characters you DO NOT want entities inserted for.
   *
   * @param str        The string to insert entities into.
   * @param exclusions Characters you DO NOT wanted entities inserted for.
   *                   Pass null for no exclusions.
   * @return           A new string with HTML entities replacing all
   *                   charactercs, except those excluded, that have
   *                   corresponding entities.
   */
  public static String encodeEntities(String str, String exclusions) {

    StringBuffer sb = new StringBuffer(str.length() + 100);

    char c;
    for (int i = 0; i < str.length(); i++) {
      c = str.charAt(i);
      if (exclusions == null || exclusions.indexOf(c) == -1) {
        switch (c) {
          case ' ': sb.append("&nbsp");    break;
          case '�': sb.append("&shy;");    break;
          case '"': sb.append("&quot;");   break;
          case '&': sb.append("&amp;");    break;
          case '�': sb.append("&iexcl;");  break;
          case '�': sb.append("&brvbar;"); break;
          case '�': sb.append("&uml;");    break;
          case '�': sb.append("&macr;");   break;
          case '�': sb.append("&acute;");  break;
          case '�': sb.append("&cedil;");  break;
          case '�': sb.append("&iquest;"); break;
          case '<': sb.append("&lt;");     break;
          case '>': sb.append("&gt;");     break;
          case '�': sb.append("&plusmn;"); break;
          case '�': sb.append("&laquo;");  break;
          case '�': sb.append("&raquo;");  break;
          case '�': sb.append("&times;");  break;
          case '�': sb.append("&divide;"); break;
          case '�': sb.append("&cent;");   break;
          case '�': sb.append("&pound;");  break;
          case '�': sb.append("&curren;"); break;
          case '�': sb.append("&yen;");    break;
          case '�': sb.append("&sect;");   break;
          case '�': sb.append("&copy;");   break;
          case '�': sb.append("&not;");    break;
          case '�': sb.append("&reg;");    break;
          case '�': sb.append("&deg;");    break;
          case '�': sb.append("&micro;");  break;
          case '�': sb.append("&para;");   break;
          case '�': sb.append("&middot;"); break;
          case '�': sb.append("&euro;");   break;
          case '�': sb.append("&frac14;"); break;
          case '�': sb.append("&frac12;"); break;
          case '�': sb.append("&frac34;"); break;
          case '�': sb.append("&sup1;");   break;
          case '�': sb.append("&sup2;");   break;
          case '�': sb.append("&sup3;");   break;
          case '�': sb.append("&aacute;"); break;
          case '�': sb.append("&Aacute;"); break;
          case '�': sb.append("&acirc;");  break;
          case '�': sb.append("&Acirc;");  break;
          case '�': sb.append("&agrave;"); break;
          case '�': sb.append("&Agrave;"); break;
          case '�': sb.append("&aring;");  break;
          case '�': sb.append("&Aring;");  break;
          case '�': sb.append("&atilde;"); break;
          case '�': sb.append("&Atilde;"); break;
          case '�': sb.append("&auml;");   break;
          case '�': sb.append("&Auml;");   break;
          case '�': sb.append("&ordf;");   break;
          case '�': sb.append("&aelig;");  break;
          case '�': sb.append("&AElig;");  break;
          case '�': sb.append("&ccedil;"); break;
          case '�': sb.append("&Ccedil;"); break;
          case '�': sb.append("&ETH;");    break;
          case '�': sb.append("&eth;");    break;
          case '�': sb.append("&eacute;"); break;
          case '�': sb.append("&Eacute;"); break;
          case '�': sb.append("&ecirc;");  break;
          case '�': sb.append("&Ecirc;");  break;
          case '�': sb.append("&egrave;"); break;
          case '�': sb.append("&Egrave;"); break;
          case '�': sb.append("&euml;");   break;
          case '�': sb.append("&Euml;");   break;
          case '�': sb.append("&iacute;"); break;
          case '�': sb.append("&Iacute;"); break;
          case '�': sb.append("&icirc;");  break;
          case '�': sb.append("&Icirc;");  break;
          case '�': sb.append("&igrave;"); break;
          case '�': sb.append("&Igrave;"); break;
          case '�': sb.append("&iuml;");   break;
          case '�': sb.append("&Iuml;");   break;
          case '�': sb.append("&ntilde;"); break;
          case '�': sb.append("&Ntilde;"); break;
          case '�': sb.append("&oacute;"); break;
          case '�': sb.append("&Oacute;"); break;
          case '�': sb.append("&ocirc;");  break;
          case '�': sb.append("&Ocirc;");  break;
          case '�': sb.append("&ograve;"); break;
          case '�': sb.append("&Ograve;"); break;
          case '�': sb.append("&ordm;");   break;
          case '�': sb.append("&oslash;"); break;
          case '�': sb.append("&Oslash;"); break;
          case '�': sb.append("&otilde;"); break;
          case '�': sb.append("&Otilde;"); break;
          case '�': sb.append("&ouml;");   break;
          case '�': sb.append("&Ouml;");   break;
          case '�': sb.append("&szlig;");  break;
          case '�': sb.append("&thorn;");  break;
          case '�': sb.append("&THORN;");  break;
          case '�': sb.append("&uacute;"); break;
          case '�': sb.append("&Uacute;"); break;
          case '�': sb.append("&ucirc;");  break;
          case '�': sb.append("&Ucirc;");  break;
          case '�': sb.append("&ugrave;"); break;
          case '�': sb.append("&Ugrave;"); break;
          case '�': sb.append("&uuml;");   break;
          case '�': sb.append("&Uuml;");   break;
          case '�': sb.append("&yacute;"); break;
          case '�': sb.append("&Yacute;"); break;
          case '�': sb.append("&yuml;");   break;
          default:  sb.append(c); break;
        } // End switch.
      } else {
        // Winds up here if the character is in the exclusion list.
        sb.append(c);
      }
    } // End for.

    return sb.toString();

  } // End encodeEntities().


} // End class.
