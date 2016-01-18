package com.github.funnygopher.crowddj.client.util;

import java.io.File;

public class XFile extends File
{
    public XFile(String pathname) {
        super(pathname);
    }
   /*
   ==============================================================
   ============================================================== *//**
 This method is a cover for File.renameTo(). The motivation for this
 cover method is that with this new version of Java (1.5.0), rename (and
 other file methods) sometimes don't work on the first try. This seems to
 be because file objects that have been closed are hung onto, pending
 garbage collection.

 @return
 true if and only if the renaming succeeded; false otherwise

 @param
 pNewFile is a File object containing the new name for the file.
 *//*
   -------------------------------------------------------------- */
public boolean renameTo(File pNewFile)
{
      /*
      ===============================================
      HACK - I should just be able to call renameTo() here and return its
      result. In fact, I used to do just that and this method always worked
      fine. Now with this new version of Java (1.5.0), rename (and other
      file methods) sometimes don't work on the first try. This is because
      file objects that have been closed are hung onto, pending garbage
      collection. By suggesting garbage collection, the next time, the
      renameTo() usually (but not always) works.
      ----------------------------------------------- */
    for (int i=0; i<20; i++)
    {
        if (super.renameTo(pNewFile))
        {
            return true;
        }
        System.gc();
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    return false;
}
}
