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
          case '­': sb.append("&shy;");    break;
          case '"': sb.append("&quot;");   break;
          case '&': sb.append("&amp;");    break;
          case '¡': sb.append("&iexcl;");  break;
          case '¦': sb.append("&brvbar;"); break;
          case '¨': sb.append("&uml;");    break;
          case '¯': sb.append("&macr;");   break;
          case '´': sb.append("&acute;");  break;
          case '¸': sb.append("&cedil;");  break;
          case '¿': sb.append("&iquest;"); break;
          case '<': sb.append("&lt;");     break;
          case '>': sb.append("&gt;");     break;
          case '±': sb.append("&plusmn;"); break;
          case '«': sb.append("&laquo;");  break;
          case '»': sb.append("&raquo;");  break;
          case '×': sb.append("&times;");  break;
          case '÷': sb.append("&divide;"); break;
          case '¢': sb.append("&cent;");   break;
          case '£': sb.append("&pound;");  break;
          case '¤': sb.append("&curren;"); break;
          case '¥': sb.append("&yen;");    break;
          case '§': sb.append("&sect;");   break;
          case '©': sb.append("&copy;");   break;
          case '¬': sb.append("&not;");    break;
          case '®': sb.append("&reg;");    break;
          case '°': sb.append("&deg;");    break;
          case 'µ': sb.append("&micro;");  break;
          case '¶': sb.append("&para;");   break;
          case '·': sb.append("&middot;"); break;
          case '€': sb.append("&euro;");   break;
          case '¼': sb.append("&frac14;"); break;
          case '½': sb.append("&frac12;"); break;
          case '¾': sb.append("&frac34;"); break;
          case '¹': sb.append("&sup1;");   break;
          case '²': sb.append("&sup2;");   break;
          case '³': sb.append("&sup3;");   break;
          case 'á': sb.append("&aacute;"); break;
          case 'Á': sb.append("&Aacute;"); break;
          case 'â': sb.append("&acirc;");  break;
          case 'Â': sb.append("&Acirc;");  break;
          case 'à': sb.append("&agrave;"); break;
          case 'À': sb.append("&Agrave;"); break;
          case 'å': sb.append("&aring;");  break;
          case 'Å': sb.append("&Aring;");  break;
          case 'ã': sb.append("&atilde;"); break;
          case 'Ã': sb.append("&Atilde;"); break;
          case 'ä': sb.append("&auml;");   break;
          case 'Ä': sb.append("&Auml;");   break;
          case 'ª': sb.append("&ordf;");   break;
          case 'æ': sb.append("&aelig;");  break;
          case 'Æ': sb.append("&AElig;");  break;
          case 'ç': sb.append("&ccedil;"); break;
          case 'Ç': sb.append("&Ccedil;"); break;
          case 'Ð': sb.append("&ETH;");    break;
          case 'ð': sb.append("&eth;");    break;
          case 'é': sb.append("&eacute;"); break;
          case 'É': sb.append("&Eacute;"); break;
          case 'ê': sb.append("&ecirc;");  break;
          case 'Ê': sb.append("&Ecirc;");  break;
          case 'è': sb.append("&egrave;"); break;
          case 'È': sb.append("&Egrave;"); break;
          case 'ë': sb.append("&euml;");   break;
          case 'Ë': sb.append("&Euml;");   break;
          case 'í': sb.append("&iacute;"); break;
          case 'Í': sb.append("&Iacute;"); break;
          case 'î': sb.append("&icirc;");  break;
          case 'Î': sb.append("&Icirc;");  break;
          case 'ì': sb.append("&igrave;"); break;
          case 'Ì': sb.append("&Igrave;"); break;
          case 'ï': sb.append("&iuml;");   break;
          case 'Ï': sb.append("&Iuml;");   break;
          case 'ñ': sb.append("&ntilde;"); break;
          case 'Ñ': sb.append("&Ntilde;"); break;
          case 'ó': sb.append("&oacute;"); break;
          case 'Ó': sb.append("&Oacute;"); break;
          case 'ô': sb.append("&ocirc;");  break;
          case 'Ô': sb.append("&Ocirc;");  break;
          case 'ò': sb.append("&ograve;"); break;
          case 'Ò': sb.append("&Ograve;"); break;
          case 'º': sb.append("&ordm;");   break;
          case 'ø': sb.append("&oslash;"); break;
          case 'Ø': sb.append("&Oslash;"); break;
          case 'õ': sb.append("&otilde;"); break;
          case 'Õ': sb.append("&Otilde;"); break;
          case 'ö': sb.append("&ouml;");   break;
          case 'Ö': sb.append("&Ouml;");   break;
          case 'ß': sb.append("&szlig;");  break;
          case 'þ': sb.append("&thorn;");  break;
          case 'Þ': sb.append("&THORN;");  break;
          case 'ú': sb.append("&uacute;"); break;
          case 'Ú': sb.append("&Uacute;"); break;
          case 'û': sb.append("&ucirc;");  break;
          case 'Û': sb.append("&Ucirc;");  break;
          case 'ù': sb.append("&ugrave;"); break;
          case 'Ù': sb.append("&Ugrave;"); break;
          case 'ü': sb.append("&uuml;");   break;
          case 'Ü': sb.append("&Uuml;");   break;
          case 'ý': sb.append("&yacute;"); break;
          case 'Ý': sb.append("&Yacute;"); break;
          case 'ÿ': sb.append("&yuml;");   break;
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
