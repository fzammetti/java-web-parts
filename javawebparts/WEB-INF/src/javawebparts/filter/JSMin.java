/*
 * JSMin.java 2006-07-23
 *
 * Copyright (c) 2006 Herman van Rosmalen (javawebparts.sourceforge.net)
 *
 * This work is a modification of the work of John Reilly.
 * Permission is hereby granted to use this version under the same
 * conditions as the JSMin.java on which it is based.
 *
 *
 * JSMin.java 2006-02-13
 *
 * Copyright (c) 2006 John Reilly (www.inconspicuous.org)
 *
 * This work is a translation from C to Java of jsmin.c published by
 * Douglas Crockford.  Permission is hereby granted to use the Java
 * version under the same conditions as the jsmin.c on which it is
 * based.
 *
 *
 *
 *
 * jsmin.c 2003-04-21
 *
 * Copyright (c) 2002 Douglas Crockford (www.crockford.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * The Software shall be used for Good, not Evil.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package javawebparts.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PushbackInputStream;

/**
 * This class is used to do compression.
 *
 * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen </a>.
 */
public class JSMin {
    private static final int EOF = -1;

    private PushbackInputStream in;

    private StringBuffer out;

    private int theA;

    private int theB;

    /**
     * Constructor
     */
    public JSMin() {
    }

    /**
     * isAlphanum -- return true if the character is a letter, digit,
     * underscore, dollar sign, or non-ASCII character.
     *
     * @param c
     *            the character from the input as an int.
     * @return boolean true if it is alphanumeric else false.
     */
    static boolean isAlphanum(int c) {
        return (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')
                || (c >= 'A' && c <= 'Z') || c == '_' || c == '$' || c == '\\'
                || c > 126;
    }

    /**
     * get -- return the next character from stdin. Watch out for lookahead. If
     * the character is a control character, translate it to a space or
     * linefeed.
     *
     * @return int the next character as int.
     * @throws IOException
     *             IOException.
     */
    int get() throws IOException {
        int c = in.read();

        if (c >= ' ' || c == '\n' || c == EOF) {
            return c;
        }

        if (c == '\r') {
            return '\n';
        }

        return ' ';
    }

    /**
     * Get the next character without getting it.
     *
     * @return int the next character as int.
     * @throws IOException
     *             IOException.
     */
    int peek() throws IOException {
        int lookaheadChar = in.read();
        in.unread(lookaheadChar);
        return lookaheadChar;
    }

    /**
     * This method gets the next character, excluding comments.
     * peek() is used to see if a '/' is followed by a '/' or '*'.
     *
     * @return int the next character as int.
     *
     * @throws UnterminatedCommentException
     *             UnterminatedCommentException.
     * @throws IOException
     *             IOException.
     */
    int next() throws UnterminatedCommentException, IOException {
        int c = get();
        if (c == '/') {
          int d = peek();

          if (d == '/') {
            for (;;) {
              c = get();
              if (c <= '\n') {
                return c;
            }
          }
        } else if (d == '*') {
          get();
          for (;;) {
            switch (get()) {
            case '*':
              if (peek() == '/') {
                  get();
                  return ' ';
              }
              break;
            case EOF:
              throw new UnterminatedCommentException();
            default:
            }
          }
        } else {
            return c;
        }
        }
        return c;
    }

    /**
     * action -- do something. What you do is determined by the argument: 1
     * Output A. Copy B to A. Get the next B. 2 Copy B to A. Get the next B.
     * (Delete A). 3 Get the next B. (Delete B). action treats a string as a
     * single character. Wow! action recognizes a regular expression if it is
     * preceded by ( or , or =.
     *
     * @param d
     *            Type of action (see above).
     *
     * @throws IOException
     *             IOException.
     * @throws UnterminatedRegExpLiteralException
     *             UnterminatedRegExpLiteralException.
     * @throws UnterminatedCommentException
     *             UnterminatedCommentException.
     * @throws UnterminatedStringLiteralException
     *             UnterminatedStringLiteralException.
     */
     void action(int d) throws IOException, UnterminatedRegExpLiteralException,
      UnterminatedCommentException, UnterminatedStringLiteralException {
    switch (d) {
    case 1:
      out.append((char) theA);
    case 2:
      theA = theB;

      if (theA == '\'' || theA == '"') {
        for (;;) {
          out.append((char) theA);
          theA = get();
          if (theA == theB) {
            break;
          }
          if (theA <= '\n') {
            throw new UnterminatedStringLiteralException();
          }
          if (theA == '\\') {
            out.append((char) theA);
            theA = get();
          }
        }
      }

    case 3:
      theB = next();
      if (theB == '/' && (theA == '(' || theA == ',' || theA == '=')) {
        out.append((char) theA);
        out.append((char) theB);
        for (;;) {
          theA = get();
          if (theA == '/') {
            break;
          } else if (theA == '\\') {
            out.append((char) theA);
            theA = get();
          } else if (theA <= '\n') {
            throw new UnterminatedRegExpLiteralException();
          }
          out.append((char) theA);
        }
        theB = next();
      }
    }
  }

    /**
     * compress -- Copy the input to the output, deleting the characters which
     * are insignificant to JavaScript. Comments will be removed. Tabs will be
     * replaced with spaces. Carriage returns will be replaced with linefeeds.
     * Most spaces and linefeeds will be removed.
     *
     * @param text
     *            The text to compress.
     * @return String The compressed text.
     *
     * @throws IOException
     *             IOException.
     * @throws UnterminatedRegExpLiteralException
     *             UnterminatedRegExpLiteralException.
     * @throws UnterminatedCommentException
     *             UnterminatedCommentException.
     * @throws UnterminatedStringLiteralException
     *             UnterminatedStringLiteralException.
     */
    public String compress(String text) throws IOException,
            UnterminatedRegExpLiteralException, UnterminatedCommentException,
            UnterminatedStringLiteralException {
        this.in = new PushbackInputStream(new ByteArrayInputStream(text
                .getBytes()));
        this.out = new StringBuffer();
        theA = '\n';
        action(3);
        while (theA != EOF) {
            switch (theA) {
            case ' ':
                if (isAlphanum(theB)) {
                    action(1);
                } else {
                    action(2);
                }
                break;
            case '\n':
                switch (theB) {
                case '{':
                case '[':
                case '(':
                case '+':
                case '-':
                    action(1);
                    break;
                case ' ':
                    action(3);
                    break;
                default:
                    if (isAlphanum(theB)) {
                        action(1);
                    } else {
                        action(2);
                    }
                }
                break;
            default:
                switch (theB) {
                case ' ':
                    if (isAlphanum(theA)) {
                        action(1);
                        break;
                    }
                    action(3);
                    break;
                case '\n':
                    switch (theA) {
                    case '}':
                    case ']':
                    case ')':
                    case '+':
                    case '-':
                    case '"':
                    case '\'':
                        action(1);
                        break;
                    default:
                        if (isAlphanum(theA)) {
                            action(1);
                        } else {
                            action(3);
                        }
                    }
                    break;
                default:
                    action(1);
                    break;
                }
            }
        }
        return out.toString();
    }

    /**
     * This class is thrown when a Comment is not terminated correctly.
     *
     * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen </a>.
     */
    class UnterminatedCommentException extends Exception {
    }

    /**
     * This class is thrown when a String is not terminated correctly.
     *
     * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen </a>.
     */
    class UnterminatedStringLiteralException extends Exception {
    }

    /**
     * This class is thrown when a RegExp is not terminated correctly.
     *
     * @author <a href="mailto:herros@gmail.com">Herman van Rosmalen </a>.
     */
    class UnterminatedRegExpLiteralException extends Exception {
    }
}