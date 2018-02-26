package com.datapps.qa.service.test.cla;

import java.io.File;
import java.io.FileFilter;

/**
 * 过滤文件实现类
 * @author Matrix42
 *
 */
public class JsonFilter implements FileFilter {

    private final String[] okFileExtensions = new String[] {"json"};

    @Override
    public boolean accept(File pathname) {
        for (String extension : okFileExtensions)
        {
            //System.out.println(pathname.getName());
          if (pathname.getName().toLowerCase().endsWith(extension))
          {
            return true;
          }
        }
        return false;
    }

}
