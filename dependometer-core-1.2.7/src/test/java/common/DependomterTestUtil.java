package common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import com.valtech.source.dependometer.ui.console.Dependometer;

public class DependomterTestUtil
{
   /**
    * This is a workaround for resetting dependometer to a clean state, so that we can run dependometer tests more than
    * once in the same JVM.
    * 
    * This should be refactored by removing the static fields from dependometer core project soon.
    */
   public static void resetDependometer()
   {
      Dependometer.reset();
   }
   
   /**
    * Reads content from (text) file as string.
    * @param in file
    * @return string content
    * @throws IOException
    */
   public static String readFileAsString(File in) throws IOException
   {
      StringBuffer sb = new StringBuffer();
      BufferedReader br = null;
      try
      {
         br = new BufferedReader(new FileReader(in));
         while (br.ready())
         {
            sb.append(br.readLine());
         }
      }
      finally
      {
         if (br != null)
         {
            try
            {
               br.close();
            }
            catch (IOException e)
            {
               e.printStackTrace();
            }
         }

      }
      return sb.toString();
   }
}
