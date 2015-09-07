/*
 * Copyright 2009 Valtech GmbH
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package com.valtech.source.dependometer.app.typedefprovider.filebased.java;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Dietmar Menges (dietmar.menges@valtech.de)
 */
final class SimplifiedJavaSourceFileReader
{
   private static final int ASTERISK = '*';

   private static final int BACKSLASH = '\\';

   private static final int STRING_MARKER = '"';

   private static final int CHAR_MARKER = '\'';

   private static final int FORWARD_SLASH = '/';

   private static final int CARRIAGE_RETURN = '\r';

   private static final int LINE_FEED = '\n';

   private static final int BLANK = ' ';

   private static final int DOT = '.';

   private static final int SEMICOLON = ';';

   private static final int TAB = '\t';

   private static final int EQUAL_SIGN = '=';

   private static final int UNDERSCORE = '_';

   private static final int BRACKET1_OPEN = '(';

   private static final int BRACKET1_CLOSE = ')';

   private static final int BRACKET2_OPEN = '{';

   private static final int BRACKET2_CLOSE = '}';

   private static final int BRACKET3_OPEN = '[';

   private static final int BRACKET3_CLOSE = ']';

   private static final int ZERO = '0';

   private static final int ONE = '1';

   private static final int TWO = '2';

   private static final int THREE = '3';

   private static final int FOUR = '4';

   private static final int FIVE = '5';

   private static final int SIX = '6';

   private static final int SEVEN = '7';

   private static final int EIGHT = '8';

   private static final int NINE = '9';

   private static final int AT = '@';

   static final String KEYWORD_PACKAGE = "package";

   static final String KEYWORD_IMPORT = "import";

   static final String KEYWORD_CLASS = "class";

   static final String KEYWORD_INTERFACE = "interface";

   static final String KEYWORD_FINAL = "final";

   static final String KEYWORD_PUBLIC = "public";

   static final String KEYWORD_PRIVATE = "private";

   static final String KEYWORD_PROTECTED = "protected";

   static final String KEYWORD_STATIC = "static";

   static final String KEYWORD_EQUAL_SIGN = "=";

   static final String KEYWORD_IMPLEMENTS = "implements";

   static final String KEYWORD_EXTENDS = "extends";

   static final String KEYWORD_BRACKET1_OPEN = "(";

   static final String KEYWORD_BRACKET1_CLOSE = ")";

   static final String KEYWORD_BRACKET2_OPEN = "{";

   static final String KEYWORD_BRACKET2_CLOSE = "}";

   static final String KEYWORD_BRACKET3_OPEN = "[";

   static final String KEYWORD_BRACKET3_CLOSE = "]";

   static final String KEYWORD_SEMICOLON = ";";

   static final String KEYWORD_ABSTRACT = "abstract";

   static final String KEYWORD_DIVISION = "/";

   static final String KEYWORD_AT = "@";

   static final String KEYWORD_ENUM = "enum";

   private static final Set<String> s_Keywords = new TreeSet<String>();

   private int m_Index = -1;

   private byte[] m_Bytes;

   private StringBuffer m_WordBuffer = new StringBuffer();

   private String m_NextToken;

   private String m_Keyword;

   private boolean m_IsKeyword;

   private String m_SourceFileName;

   private int m_LineNumber;

   static
   {
      s_Keywords.add(KEYWORD_PACKAGE);
      s_Keywords.add(KEYWORD_IMPORT);
      s_Keywords.add(KEYWORD_CLASS);
      s_Keywords.add(KEYWORD_INTERFACE);
      s_Keywords.add(KEYWORD_FINAL);
      s_Keywords.add(KEYWORD_PUBLIC);
      s_Keywords.add(KEYWORD_PRIVATE);
      s_Keywords.add(KEYWORD_PROTECTED);
      s_Keywords.add(KEYWORD_STATIC);
      s_Keywords.add(KEYWORD_IMPLEMENTS);
      s_Keywords.add(KEYWORD_EXTENDS);
      s_Keywords.add(KEYWORD_BRACKET2_OPEN);
      s_Keywords.add(KEYWORD_BRACKET2_CLOSE);
      s_Keywords.add(KEYWORD_SEMICOLON);
      s_Keywords.add(KEYWORD_ABSTRACT);
      s_Keywords.add(KEYWORD_AT);
      s_Keywords.add(KEYWORD_ENUM);
   }

   public boolean isKeyword()
   {
      return m_IsKeyword;
   }

   private String getKeyword()
   {
      assert m_Keyword != null;
      assert m_Keyword.length() > 0;
      String keyword = m_Keyword;
      m_Keyword = null;
      m_IsKeyword = true;
      return keyword;
   }

   String nextToken()
   {
      if (m_Keyword != null)
      {
         String keyword = getKeyword();
         return keyword;
      }
      else
      {
         m_IsKeyword = false;
      }

      while (m_NextToken == null)
      {
         int next = next();
         if (next == -1)
         {
            return null;
         }

         switch (next)
         {
            case BACKSLASH:
               next();
               break;
            case STRING_MARKER:
               consumeString();
               break;
            case CHAR_MARKER:
               consumeChar();
               break;
            case FORWARD_SLASH:
               next = next();
               switch (next)
               {
                  case FORWARD_SLASH:
                     consumeSlashSlashComment();
                     break;
                  case ASTERISK:
                     consumeSlashStarComment();
                     break;
                  default:
                     pushBack();
                     m_Keyword = KEYWORD_DIVISION;
                     checkWord();
                     break;
               }
               break;
            case CARRIAGE_RETURN:
               if (nextIgnoringWhitespace() != LINE_FEED)
               {
                  pushBack();
               }
               int testNext = nextIgnoringWhitespace();
               if (!wordBuffersLastCharIsDot() && testNext != DOT)
               {
                  pushBack();
                  checkWord();
               }
               else
               {
                  pushBack();
               }
               m_LineNumber++;
               break;
            case LINE_FEED:
            case BLANK:
            case TAB:
               testNext = nextIgnoringWhitespace();
               if (!wordBuffersLastCharIsDot() && testNext != DOT)
               {
                  pushBack();
                  checkWord();
               }
               else
               {
                  pushBack();
               }
               m_LineNumber++;
               break;
            case SEMICOLON:
               m_Keyword = KEYWORD_SEMICOLON;
               checkWord();
               break;
            case BRACKET2_OPEN:
               m_Keyword = KEYWORD_BRACKET2_OPEN;
               checkWord();
               break;
            case BRACKET2_CLOSE:
               m_Keyword = KEYWORD_BRACKET2_CLOSE;
               checkWord();
               break;
            case BRACKET1_OPEN:
               m_Keyword = KEYWORD_BRACKET1_OPEN;
               checkWord();
               break;
            case BRACKET1_CLOSE:
               m_Keyword = KEYWORD_BRACKET1_CLOSE;
               checkWord();
               break;
            case BRACKET3_OPEN:
               m_Keyword = KEYWORD_BRACKET3_OPEN;
               checkWord();
               break;
            case BRACKET3_CLOSE:
               m_Keyword = KEYWORD_BRACKET3_CLOSE;
               checkWord();
               break;
            case EQUAL_SIGN:
               m_Keyword = KEYWORD_EQUAL_SIGN;
               checkWord();
               break;
            case AT:
               m_Keyword = KEYWORD_AT;
               checkWord();
               break;
            case DOT:
            case UNDERSCORE:
            case ZERO:
            case ONE:
            case TWO:
            case THREE:
            case FOUR:
            case FIVE:
            case SIX:
            case SEVEN:
            case EIGHT:
            case NINE:
            case 65:
            case 66:
            case 67:
            case 68:
            case 69:
            case 70:
            case 71:
            case 72:
            case 73:
            case 74:
            case 75:
            case 76:
            case 77:
            case 78:
            case 79:
            case 80:
            case 81:
            case 82:
            case 83:
            case 84:
            case 85:
            case 86:
            case 87:
            case 88:
            case 89:
            case 90:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
               processIdentifierCharacter(next);
               break;
            default:
               break;
         }
      }

      String nextToken = m_NextToken;
      m_NextToken = null;
      return nextToken;
   }

   void read(File sourceFile) throws IOException
   {
      assert sourceFile != null;
      assert sourceFile.isFile();
      m_SourceFileName = sourceFile.getAbsolutePath();
      FileInputStream fileInputStream = new FileInputStream(sourceFile);
      m_Bytes = new byte[fileInputStream.available() + 1];
      int read = fileInputStream.read(m_Bytes);
      fileInputStream.close();
      assert read == m_Bytes.length - 1;
      m_Bytes[m_Bytes.length - 1] = -1;
      m_Index = 0;
      m_LineNumber = 1;
   }

   int getCurrentLineNumber()
   {
      return m_LineNumber;
   }

   String getCurrentSourceFileName()
   {
      return m_SourceFileName;
   }

   private void consumeString()
   {
      int next = -1;
      do
      {
         next = next();
         if (next == BACKSLASH)
         {
            next();
         }
      }
      while (next != STRING_MARKER);
   }

   private void consumeChar()
   {
      int next = -1;
      do
      {
         next = next();
         if (next == BACKSLASH)
         {
            next();
         }
      }
      while (next != CHAR_MARKER);
   }

   private boolean wordBuffersLastCharIsDot()
   {
      int length = m_WordBuffer.length();

      if (length > 0)
      {
         return m_WordBuffer.charAt(length - 1) == '.';
      }
      else
      {
         return false;
      }
   }

   private void checkWord()
   {
      assert m_WordBuffer != null;

      if (m_WordBuffer.length() > 0)
      {
         m_NextToken = m_WordBuffer.toString();
         if (m_Keyword != null)
         {
            m_WordBuffer.setLength(0);
         }
         else if (s_Keywords.contains(m_NextToken))
         {
            m_IsKeyword = true;
            m_WordBuffer.setLength(0);
         }
         else
         {
            m_WordBuffer.setLength(0);
         }
      }
      else if (m_Keyword != null)
      {
         m_NextToken = getKeyword();
      }
   }

   private void processIdentifierCharacter(int next)
   {
      assert m_WordBuffer != null;
      char nextChar = (char)next;
      m_WordBuffer.append(nextChar);
   }

   private int next()
   {
      assert m_Index != -1;
      assert m_Bytes != null;
      assert m_Index < m_Bytes.length;
      int result = m_Bytes[m_Index];
      m_Index++;
      return result;
   }

   private int nextIgnoringWhitespace()
   {
      int next = -1;

      do
      {
         next = next();
      }
      while (next == BLANK || next == TAB || next == CARRIAGE_RETURN || next == LINE_FEED);

      return next;
   }

   void pushBackToken(String token)
   {
      assert token != null;
      assert token.length() > 0;
      pushBack(token.length());
   }

   private void pushBack()
   {
      assert m_Index > 0;
      --m_Index;
   }

   private void pushBack(int back)
   {
      assert m_Index > 0;
      assert back > 0;
      m_Index -= (back + 1);
      assert m_Index >= 0;
   }

   private void consumeSlashSlashComment()
   {
      boolean condition = true;
      int next = -1;
      do
      {
         next = next();
         if (next == -1)
         {
            condition = false;
            pushBack();
         }
         else if (next == CARRIAGE_RETURN)
         {
            next = next();
            if (next != LINE_FEED)
            {
               pushBack();
            }
            condition = false;
            m_LineNumber++;
         }
         else if (next == LINE_FEED)
         {
            condition = false;
            m_LineNumber++;
         }
      }
      while (condition);
   }

   private void consumeSlashStarComment()
   {
      int next = -1;
      boolean condition = true;
      do
      {
         next = next();
         if (next == ASTERISK)
         {
            next = next();
            if (next == FORWARD_SLASH)
            {
               condition = false;
            }
            else
            {
               pushBack();
            }
         }
      }
      while (condition);
   }

}