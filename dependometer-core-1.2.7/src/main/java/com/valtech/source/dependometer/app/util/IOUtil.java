package com.valtech.source.dependometer.app.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

/**
 * Utility class for IO operations.
 * 
 * @version $Revision: 1.1 $
 */
public class IOUtil
{
   private static Logger logger = Logger.getLogger(IOUtil.class.getName());

   /**
    * Deletes a file or a directory or deletes a directory and deletes recursively all subdirectories and files below.
    * 
    * @param fileToDelete file or directory
    * @return true if everthing could be deleted
    */
   public static boolean deleteFileOrDirectory(File fileToDelete)
   {
      logger.debug("Deleting directory '" + fileToDelete + "'");
      if (fileToDelete.exists())
      {
         File[] files = fileToDelete.listFiles();
         for (File f : files)
         {
            if (f.isDirectory() && !deleteFileOrDirectory(f))
            {
               return false;
            }
            boolean deleted = f.delete();
            if (!deleted)
            {
               logger.warn("Could not delete '" + f.getAbsolutePath() + "'");
               return false;
            }
         }
      }
      return fileToDelete.delete();
   }

   /**
    * Copies a file from one location to antother
    * 
    * @param in source
    * @param out target
    * @throws IOException
    */
   public static void copyFile(File in, File out) throws IOException
   {
      FileInputStream fis = null;
      try
      {
         fis = new FileInputStream(in);
         copyStreamToFile(fis, out);
      }
      finally
      {
         if (fis != null)
         {
            fis.close();
         }
      }

   }

   public static void copyStreamToFile(InputStream in, File out) throws IOException {
      FileOutputStream fos = null;
      try
      {
         fos = new FileOutputStream(out);
         byte[] buf = new byte[1024];
         int i = 0;
         while ((i = in.read(buf)) != -1)
         {
            fos.write(buf, 0, i);
         }
      }
      finally
      {
         if (in != null)
         {
             try {
                 in.close();
             } catch( IOException e ) {
             }
         }
         if (fos != null)
         {
             try {
                 fos.close();
             } catch( IOException e ) {
             }
         }
      }
   }

   /**
    * @param file file
    * @param dir directory
    * @throws IOException
    */
   public static void copyFileToDir(File file, File dir) throws IOException
   {
      File out = new File(dir, file.getName());
      copyFile(file, out);
   }

   /**
    * Copies content of the directory in first param to target directory in second param. If target directory does not
    * exist, it is created.
    * 
    * @param from source directory
    * @param to target directory
    * @throws IOException
    */
   public static void copyDirectoryContent(File from, File to) throws IOException
   {

      if (from.isDirectory())
      {
         if (!to.exists())
         {
            to.mkdir();
         }

         String files[] = from.list();

         for (int i = 0; i < files.length; i++)
         {
            copyDirectoryContent(new File(from, files[i]), new File(to, files[i]));
         }
      }

      else
      {
         if (!from.exists())
         {
            logger.error("File or directory does not exist: '" + from.getAbsolutePath() + "'");
         }

         else
         {
            copyFile(from, to);
         }
      }

      logger.info("Directory copied: '" + from + "' to '" + to + "'");
   }

   /**
    * @param absFilePath file path
    * @return true if file exists
    */
   public static boolean isExistingFile(String absFilePath)
   {
      if (absFilePath == null)
      {
         return false;
      }
      return new File(absFilePath).exists();
   }

   /**
    * Deletes all children files in a directory, not the directory itself.
    * 
    * @param dir direcory
    * @return true if all files could be deleted
    */
   public static boolean deleteAllFilesInDirectory(File dir)
   {
      File[] files = dir.listFiles();
      for (File file : files)
      {
         if (file.isFile())
         {
            logger.debug("Deleting file: " + file.getAbsolutePath());
            boolean ok = file.delete();
            if (!ok)
            {
               logger.warn("File '" + file.getAbsolutePath() + "' could not be deleted!");
               return false;
            }
         }
      }
      return true;
   }

   /**
    * @param filePath file path
    * @return true if file could be deleted
    */
   public static boolean deleteFile(String filePath)
   {
      logger.debug("Deleting file '" + filePath + "'");
      File file = new File(filePath);
      boolean deleted = file.delete();
      if (!deleted)
      {
         logger.warn("Could not delete '" + file.getAbsolutePath() + "'");
      }
      return deleted;
   }

   public static void writeResourceToFile(String resLoc, File targetFile) throws IOException
   {
      InputStream resStream = getInputStream(resLoc);
      IOUtil.copyStreamToFile(resStream, targetFile);
   }

   public static InputStream getInputStream(String resLoc) throws IOException
   {
      InputStream in = IOUtil.class.getResourceAsStream(resLoc);
      if (in == null)
      {
         throw new IOException("Could not find resource with locator '" + resLoc + "'");
      }

      return in;
   }
}
