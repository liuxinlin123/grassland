package com.datapps.qa.service.test.parser;

import java.io.IOException;

import com.datapps.qa.service.test.utils.Config;

/**
 * 读取测试文件路径
 * @author Matrix42
 *
 */
public class CaseFolderParser {
    
    private String path;
    
    public CaseFolderParser() throws IOException {
        
        path = Config.CasePath;
        //print
        System.out.println("test cases location: "+path);
    }

    public String getPath() {
        return path;
    }
    
}
