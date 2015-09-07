package common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.ChangedCharSetException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Tag;
import javax.swing.text.html.parser.ParserDelegator;

import org.apache.log4j.Logger;

public class HTMLTableParser extends ArrayList
{

   /**
	 * 
	 */
   private static final long serialVersionUID = 1L;

   private static Logger s_Logger = Logger.getLogger(HTMLTableParser.class);

   Stack s = new Stack();

   /**
    * * Process this reader.
    * 
    * @param f
    * 
    * @throws IOException
    * 
    */
   public void parse(URL url) throws IOException
   {

      this.clear();

      Reader reader = null;
      try
      {
         reader = new InputStreamReader(url.openStream());
         new ParserDelegator().parse(reader, parser, false);
      }
      catch (ChangedCharSetException e)
      {
         // Reparse the entire file using the specified charset. A regexp
         // pattern is specified to extract the charset name.

         String csspec = e.getCharSetSpec();
         Pattern p = Pattern.compile("charset=\"?(.+)\"?\\s*;?", Pattern.CASE_INSENSITIVE);
         Matcher m = p.matcher(csspec);
         String charset = m.find() ? m.group(1) : "ISO-8859-1";

         // Read and parse HTML document using appropriate character set.

         try
         {
            // Read HTML document via an input stream reader that uses the
            // specified character set to decode bytes into characters.

            reader = new InputStreamReader(url.openStream(), charset);

            // This time, pass true to ignore the <meta> tag with its charset
            // attribute.

            new ParserDelegator().parse(reader, parser, true);
         }
         catch (UnsupportedEncodingException e1)
         {
            s_Logger.error("Invalid charset", e1);
         }
         catch (IOException e2)
         {
            s_Logger.error("Input/Output problem", e2);
         }
      }
      finally
      {
         if (reader != null)
         {
            try
            {
               reader.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }
      }
   }

   private HTMLEditorKit.ParserCallback parser = new HTMLEditorKit.ParserCallback()
   {

      private boolean inTD;

      private String tdBuffer;

      private String attrName = "";

      private Tag lastTag;

      public void handleError(String arg0, int arg1)
      {
         super.handleError(arg0, arg1);
      }

      public void handleText(char[] arg0, int arg1)
      {
         if (inTD)
         {
            tdBuffer += new String(arg0);
         }
      }

      public void handleStartTag(Tag tag, MutableAttributeSet arg1, int arg2)
      {

         if (tag == HTML.Tag.TABLE)
         {
            if (lastTag == HTML.Tag.A)
            {
               s.add(new HTMLTableParser.HTMLTable(attrName));
            }
            else
            {
               s.add(new HTMLTableParser.HTMLTable(null));
            }

         }
         else if (tag == HTML.Tag.A)
         {

            Object attr = arg1.getAttribute(HTML.Attribute.NAME);
            if (attr != null)
               attrName = (String)attr;
            // else attrName =""; darf nicht gesetzt werden, da ansonsten durch folgende HREF attribute �berschrieben

         }
         else if (tag == HTML.Tag.TR)
         {

            s.add(new HTMLTableParser.HTMLRow());

         }
         else if (tag == HTML.Tag.TD)
         {

            inTD = true;

            tdBuffer = "";

         }
         lastTag = tag;
      }

      public void handleEndTag(Tag tag, int arg1)
      {

         if (tag == HTML.Tag.TABLE)
         {

            HTMLTableParser.HTMLTable T = (HTMLTableParser.HTMLTable)s.pop();

            if (s.size() == 0)
            {

               HTMLTableParser.this.add(T);

            }
            else if (s.peek() instanceof HTMLRow)
            {

               ((HTMLRow)s.peek()).add(T);

            }
            else
            {

               s_Logger.error("Need to be within nothing or a cell/row");

            }

         }
         else if (tag == HTML.Tag.TR)
         {

            HTMLRow r = (HTMLRow)s.pop();

            ((HTMLTableParser.HTMLTable)s.peek()).rows.add(r);

         }
         else if (tag == HTML.Tag.TD)
         {

            if (inTD)
            {

               ((HTMLTableParser.HTMLRow)s.peek()).add(tdBuffer);

               inTD = false;

            }

         }

      }

   };

   public class HTMLTable
   {
      String tableName = null;

      ArrayList<HTMLRow> rows = new ArrayList<HTMLRow>();

      public HTMLTable(String tableName)
      {
         this.tableName = tableName;
      }
   }

   public class HTMLRow extends ArrayList
   {

      /**
		 * 
		 */
      private static final long serialVersionUID = 1L;
   }

}