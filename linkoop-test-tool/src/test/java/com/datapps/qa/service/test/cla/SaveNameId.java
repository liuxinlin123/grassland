package com.datapps.qa.service.test.cla;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 将id 和 name 保存在文件
 * @author Xenia
 *
 */
 public class SaveNameId {
	
	private Properties propertie;
	private FileInputStream inputFile;
	private FileOutputStream outputFile;
	private String path;
	private static  String fileType = ".properties"; 
	
	/**
	 * 初始化 saveNameId 类
	 * @param fielName   Properties 文件路径
	 * @throws IOException 
	 * 
	 */
	 public SaveNameId(String Casepath,String fileName){
	    this.path = Casepath+"/saveId/";
	    propertie = new Properties();
	    path+=fileName+fileType;
		File file = new File(path);
		if(!file.getParentFile().exists()){
			
			file.getParentFile().mkdirs();		
		}
		if(!file.exists()){
			this.WriteFile(path, "Init properties");
		}
			//读已经存在的  properties 文件
			this.readExistPro(path);
	}
	
	 
	/**
	 * 创建  properties 文件 
	 * @param path
	 * @throws IOException 
	 */
	private  void WriteFile(String path,String discription){
		
			try {
                outputFile = new FileOutputStream(path);
                propertie.store(outputFile, discription);
                outputFile.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
			
	
	
	}
	/**
	 * 读  properties 文件
	 * @param path
	 * @throws IOException 
	 */
	private  void  readExistPro(String path){
	            try {
	                inputFile = new FileInputStream(path);
	                propertie.load(inputFile);
                    inputFile.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }catch (IOException e) {
                    e.printStackTrace();
                }
	}
	/**
	 * 更新或者添加一对 key-value
	 * @param nameKey
	 * @param idValue
	 */

	public void setValue(String nameKey,String idValue){
		propertie.setProperty(nameKey, idValue);
		//System.out.println("key:"+nameKey+" value:"+idValue);
			this.WriteFile(path,"update propertie");
			//System.out.println("保存");
	
		
	}
	/**
	 * 通过 key 值获取 value
	 * @param keyName
	 * @return 成功返回值，不成功返回空串
	 */
	
	public String getValue(String keyName){
		if(propertie.containsKey(keyName)){
		String valueStr = propertie.getProperty(keyName);
		return valueStr;
		}
		return "";
		
	}
	/**
	 * 给定 Key 值进行删除
	 * @param keyName
	 * @return  Boolean 型变量， 成功 true， 失败 false
	 */
	
	public boolean deleteValue(String keyName){
		if(propertie.containsKey(keyName)){
		   propertie.remove(keyName);
		this.WriteFile(path,"delete a record");
		   return true; 
		}else{	
			return false;
			
		}
	}
	/**
	 * 多条数据的删除
	 * @param keyArry
	 * @return 返回 String[]， 保存不存的的 key 值
	 */
	public String[] deleteManyValue(String keyArry[]){
		ArrayList<String> listArray = new ArrayList<String>();
		for(int i = 0; i < keyArry.length; i++ ){
			String keyTemp = keyArry[i];
			boolean flag = true;
            flag = this.deleteValue(keyTemp);
			if(flag == false)
			{	
			 listArray.add(keyArry[i]);	
				
			}
			
		}
		return listArray.toArray(new String[listArray.size()]);
	}
	/**
	 * 删除文件
	 * @return 返回  boolean, 成功 true， 失败 false 
	 */
	
	public boolean deleteFile(){
		File file = new File(path);
		boolean flag = file.delete();
		String[] dirList = file.getParentFile().list();
		if(dirList.length == 0)
		{
			 file.getParentFile().delete();
		}
		return flag;
	}
	
}
