package com.datapps.qa.service.test.cla;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;

import net.sf.json.JSONObject;

public class Client {
    public static void main(String[] args) throws IOException {
        File file = new File("C:\\Users\\Matrix42\\Desktop\\project\\carpo-test-tool\\case\\aggregate\\aggregate_approxCountDistinct\\case.json");
        JSONObject json = JSONObject.fromObject(getStringFromFile(file));
        CaseEntity entity = new CaseEntity(json);
        Iterator<Map<String, String>> iterator = entity.iterator();
        while(iterator.hasNext()){
            Map<String, String> map = iterator.next();
            
            for(String key:map.keySet()){
                System.out.println(key+":"+map.get(key));
            }
            
            System.out.println();
        }
    }
    
    public static String getStringFromFile(File file) throws IOException {
        StringBuffer result = new StringBuffer();
        // BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file),"UTF-8"));

        InputStreamReader isr = null;
        isr = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader reader = new BufferedReader(isr);
        String string;
        // 去除文件中的注释，单行注释//，单独一行的文件
        while ((string = reader.readLine()) != null) {
            string = string.trim();
            // if(string.startsWith("//"))
            if (string.startsWith("//")) {
                if (!(string.indexOf("//") == -1)) {
                    string = string.substring(0, string.indexOf("//"));
                }
            }
            result.append(string);
        }
        reader.close();
        // System.out.println("result\n"+result);
        return result.toString();
    }
}
