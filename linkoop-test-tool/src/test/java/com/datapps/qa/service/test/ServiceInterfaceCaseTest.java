package com.datapps.qa.service.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.datapps.qa.service.test.cla.CaseEntity;
import com.datapps.qa.service.test.cla.FolderFilter;
import com.datapps.qa.service.test.cla.JsonFilter;
import com.datapps.qa.service.test.cla.Random_Str;
import com.datapps.qa.service.test.cla.SaveNameId;
import com.datapps.qa.service.test.client.GetClient;
import com.datapps.qa.service.test.client.PostClient;
import com.datapps.qa.service.test.constant.ElementType;
import com.datapps.qa.service.test.parser.CaseFolderParser;
import com.datapps.qa.service.test.utils.Config;
import com.datapps.qa.service.test.utils.FileUtil;
import com.datapps.qa.service.test.utils.JsonDiff;
import com.datapps.qa.service.test.utils.Result_Error_out_Main;
import com.datapps.qa.service.test.utils.StringUtil;
import com.sun.jersey.core.util.MultivaluedMapImpl;

@RunWith(Parameterized.class)
public class ServiceInterfaceCaseTest {

	// 当前case的路径
	private String path;
	// 行为,比如创建、删除
	private String action;
	// API地址
	private String api;
	// 两次执行间隔时间
	private String sleepTime;
	// 向服务器发送的json字符串所在文件路径
	private String dataPath;
	// 期待的状态码
	private String status;
	// 期待返回的json字符串所在文件路径
	private String expectPath;
	
	private boolean isAssert;
	
	private CaseEntity entity;
	
	private static List<CaseEntity> _allCases;

	/**
	 * 静态代码块 用来读取配置文件以及登录
	 */
	static {
		Properties properties = null;
		try {
			properties = FileUtil.getProperties();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 初始化Config配置文件
		Config.BaseUrl = properties.getProperty("BaseUrl");

		Config.LoginApi = properties.getProperty("LoginApi");

		Config.UserName = properties.getProperty("UserName");

		Config.Password = properties.getProperty("Password");

		Config.AuthToken = properties.getProperty("AuthToken");

		Config.Version = properties.getProperty("Version");

		if (properties.getProperty("Cases") != null && !properties.getProperty("Cases").isEmpty()) {
			Config.Cases = properties.getProperty("Cases");
		}

		if ((properties.getProperty("CasePath") != null) && !properties.getProperty("CasePath").isEmpty()) {
			Config.CasePath = properties.getProperty("CasePath");
		}

		System.out.println("Test configuration: ");
		System.out.println("  Version : " + Config.Version);
		System.out.println("  BaseUrl : " + Config.BaseUrl);
		System.out.println("  LoginApi: " + Config.LoginApi);
		System.out.println("  UserName: " + Config.UserName);
		System.out.println("  Password: " + Config.Password);
		System.out.println("  CasePath: " + Config.CasePath);
		System.out.println("  Cases   : " + Config.Cases);

		login();
	}

	public static void login() {
		// 构造登录表单
		MultivaluedMapImpl params = new MultivaluedMapImpl();
		params.add("name", Config.UserName);
		params.add("password", Config.Password);
		params.add("version", Config.Version);
		// 发起请求
		PostClient client = new PostClient(Config.LoginApi, params);

		if (client.getStatus() != 200) {
			throw new RuntimeException("login to test system failed:" + client.getContent());
		}

		// 存储AuthToken(认证字符串)
		Config.AuthToken = client.getAuthToken();
	}

	/**
	 * 构造方法初始化TestCase
	 * 
	 * @param cases
	 */
	public ServiceInterfaceCaseTest(String caseName,CaseEntity entity) {
	    this.entity = entity;
	}

	/**
	 * 测试前的初始化,读取测试内容的文件
	 * 
	 * @return
	 * @throws Exception
	 */
	@Parameters(name = "{index}][{0}")
	public static Collection<Object[]> loadTestData() throws Exception {
		CaseFolderParser parser = new CaseFolderParser();

		// 创建case所在文件夹的File对象
		File folder = new File(parser.getPath());

		// 过滤对象,用于过滤文件夹
		FolderFilter folderFilter = new FolderFilter();

		// 列出所有case文件夹
		File[] caseFolderFiles = null;
		// 如果有需要单独执行的case,则单独执行他们
		if ("".equals(Config.Cases)) {
			// case文件夹
			caseFolderFiles = folder.listFiles(folderFilter);
		} else {
			// 解析配置文件中需要单独执行的case
			String[] relativeCasePath = Config.Cases.split(",");
			caseFolderFiles = new File[relativeCasePath.length];
			// count为case的总数
			int count = 0;
			for (int i = 0; i < relativeCasePath.length; i++) {
				File file = new File(Config.CasePath, relativeCasePath[i]);
				if (file.exists()) {
					caseFolderFiles[count++] = file;
				}
			}
		}

		ArrayList<File> allCase = new ArrayList<File>();
		// 遍历二级目录
		for (File file : caseFolderFiles) {
			File[] allFolder = file.listFiles();
			// 把所有case加入到Array
			for (File file2 : allFolder) {
			    File[] jFiles = file2.listFiles(new JsonFilter());
			    for(File file3:jFiles){
			        allCase.add(file3);
			    }
			}
		}
		
		_allCases = new ArrayList<CaseEntity>();
		String lastCasePath = null;
		int count = 0;
		for (File file : allCase) {
		    JSONObject json = JSONObject.fromObject(FileUtil.getStringFromFile(file));
            CaseEntity entity = new CaseEntity(json);
            
            String absolutePath = file.getAbsolutePath();
            String path = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
            entity.setCasePath(path);
            entity.setCaseName(path.substring(path.lastIndexOf(File.separator)+1));
            if(lastCasePath == null){
                lastCasePath = path;
            }else {
                entity.setLastCasePath(lastCasePath);
                lastCasePath = path;
            }
            _allCases.add(entity);
            count++;
        }
		
		//手动增加收尾清理case
		CaseEntity tempEntity = new CaseEntity();
		tempEntity.setCaseName("clean");
		tempEntity.setCasePath(lastCasePath);
		tempEntity.setLastCasePath(lastCasePath);
		ArrayList<Map<String, String>> caseItems = new ArrayList<Map<String, String>>();
		Map<String, String> item = new HashMap<String, String>();
		item.put("action", "delete,all-server");
		item.put("api", "");
		item.put("status", "200");
		item.put("sleepTime", "0");
		item.put("data", "");
		item.put("expect", "");
		caseItems.add(item);
		tempEntity.setCaseItems(caseItems);
		_allCases.add(tempEntity);
		count++;

		if (_allCases.isEmpty()) {
			throw new RuntimeException("测试用例文件不存在!");
		}

		Object[][] objects = new Object[count][2];
		
		for(int i=0;i<count;i++){
		    CaseEntity entity = _allCases.get(i);
		    objects[i][0] = entity.getCaseName();
		    objects[i][1] = entity;
		}

		return Arrays.asList(objects);
	}

	@Before
	public void setup() {
		login();
	}

	@Test(timeout = 600000)
	public void doTest() throws IOException{
	    String LastCasePath = entity.getLastCasePath();
	    if(LastCasePath!=null){
	        delete(new String[]{"delete","all-server"},LastCasePath,null,null,null);
	    }
	    
	    Iterator<Map<String, String>> iterator = entity.iterator();
	    System.out.println("execute case: " + entity.getCaseName());
        while(iterator.hasNext()){
            Map<String, String> map = iterator.next();
            this.path = entity.getCasePath();
            this.action = map.get("action");
            this.api = map.get("api");
            this.sleepTime = map.get("sleepTime");
            String casePath = entity.getCasePath()+File.separator;
            this.dataPath = map.get("data").equals("")?"":casePath+map.get("data");
            this.status = map.get("status");
            this.expectPath = map.get("expect").equals("")?"":casePath+map.get("expect");
            this.isAssert = "true".equals(map.get("isAssert"))?true:false;
            testCase();
        }
	}
	
	
	/**
	 * 不用关心其他地方是怎么实现的,在这里进行请求就可以了 这里的数据格式是: create_cdc ---> 动作(action) /api/schemas ---> 请求的api post ---> 请求方法
	 * data/cdc_1.json ---> 发过去的信息 201 ---> 响应码 ---> 期待结果(此处为空字符串)
	 */
	public void testCase() {
		String[] args = action.split(",");
		SaveNameId nameId = null;
		SaveNameId nameMonitorId = null;
		String queryid = "";
		PostClient postClient = null;
		GetClient getClient = null;
		JSONObject jsonStr = null;
		switch (args[0].toLowerCase()) {
			case ElementType.CREATE:
				/*
				 * 1.获得cdc的name 2.获得cdc的id 3.保存到文件
				 */
				try {
					if ("".equals(dataPath)) {
						throw new RuntimeException("没有请求的json串!");
					} else {
						// content为待发送的json字符串,需要对里面的内容进行更改
						String content = FileUtil.getStringFromFile(new File(dataPath));
						// 将json字符串构造为json对象
						JSONObject originalJson = JSONObject.fromObject(content);
						// originalName为json中name被保存
						String originalName = originalJson.getString("name");
						// 调用StringUtil的Replace方法获得id
						String json = StringUtil.Replace(path, args, content);
						// System.out.println(json);
						if (json.indexOf("$error") != -1)
							json = json.replace("$error", StringUtil.RandomName());
						if (json.indexOf("$path") != -1) {
							Random_Str ran_str = new Random_Str();
							json = json.replace("$path", ran_str.getRandomString(5));
						}
						// 创建post请求对象
						postClient = new PostClient(api, json);
						// System.out.println(postClient.getContent());

						// 创建SaveNameId对象保存 名字相应的id
						nameId = new SaveNameId(path, args[1].toLowerCase());

						nameId.setValue(originalName, StringUtil.getIdFromJson(postClient.getContent()));
					}
					if(isAssert){
					    assertEquals(status, String.valueOf(postClient.getStatus()));
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 休眠
				try {
					Thread.sleep(Long.parseLong(sleepTime));
				} catch (NumberFormatException e3) {
					e3.printStackTrace();
				} catch (InterruptedException e3) {
					e3.printStackTrace();
				}
				break;
			case ElementType.DELETE:
				delete(args,path,queryid,nameId,postClient);
				break;

			case ElementType.QUERY:

				if (!expectPath.equals("") && "result_cdo".equals(args[1])) {
					// 获取sheduler的id
					nameId = new SaveNameId(path, "scheduler");
					String schedulname = args[2];
					// schedulname = nameId.getValue(schedulname);
					String schedulId = nameId.getValue(schedulname);

					// 拼接成execution的id
					String executionId = "exec_" + schedulId + "_NO0";

					getClient = new GetClient(api + "/" + executionId, null);

					String content = getClient.getContent();
					jsonStr = JSONObject.fromObject(content);
					String status = jsonStr.getString("status");
					String array = status;
					String expect_json = null;
					try {
						expect_json = FileUtil.getStringFromFile(new File(expectPath));
					} catch (IOException e) {
						e.printStackTrace();
					}
					JsonDiff diff = new JsonDiff();

					try {
						String differ = diff.Diff(expect_json, array);
						Result_Error_out_Main.result_Error_Output_Main(differ);
					} catch (IOException e) {
						e.printStackTrace();
					}

				} else {

					if (!expectPath.equals("")) {
						nameId = new SaveNameId(path, args[1].toLowerCase());
						queryid = nameId.getValue(args[2]);
						getClient = new GetClient(api + "/" + queryid, null);
						// 打印
					} else {
						throw new RuntimeException("预期的json串不存在!");
					}
					if(isAssert){
					    assertEquals(Integer.parseInt(status), getClient.getStatus());
					}
					// 匹配两个json是否相同
					JsonDiff diff = new JsonDiff();
					String expect_json = null;
					try {
						expect_json = FileUtil.getStringFromFile(new File(expectPath));
					} catch (IOException e2) {
						e2.printStackTrace();
					}
					try {
					    if(isAssert){
    						String getString = getClient.getContent();
    						String differ = diff.Diff(expect_json, getString);
    						// 如果与预期不同,报错并指出哪里不同
    						Result_Error_out_Main.result_Error_Output_Main(differ);
					    }
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				break;
			case ElementType.UPDATE:
				try {
					if (dataPath.equals("")) {
						throw new RuntimeException("没有请求的json串!");
					} else {
						String content = FileUtil.getStringFromFile(new File(dataPath));
						StringUtil.Update(path, api, args, content, status);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				break;
			case ElementType.RUN:
				String content = null;
				try {
					content = FileUtil.getStringFromFile(new File(dataPath));
				} catch (IOException e) {
					e.printStackTrace();
				}
				// 获得dataflow的json文件
				JSONObject jsonObject = JSONObject.fromObject(content);
				SaveNameId runParamter = new SaveNameId(path, args[1]);

				// 获得flow的id
				String getFlowId = runParamter.getValue(args[2]);
				// 更换flow的id
				jsonObject.put("flowId", getFlowId);

				// 获得scheduler的name
				String name = jsonObject.getString("name");
				// 延时10s执行
				long getCurrentTime = System.currentTimeMillis() + 10000;
				jsonObject.put("startTime", getCurrentTime);

				// 向服务器发送请求
				PostClient postClientRun = new PostClient(api, jsonObject.toString());

				// 对比相应码
				if(isAssert){
				    assertEquals(Integer.parseInt(status), postClientRun.getStatus());
				}

				// 存储run的id
				SaveNameId runSaveNameId = new SaveNameId(path, "scheduler");
				runSaveNameId.setValue(name, StringUtil.getIdFromJson(postClientRun.getContent()));
				break;
			case ElementType.PREVIEW:
				// 创建预期数据存储位置
				nameId = new SaveNameId(path, "");

				// 获得需求行数：rows
				int rows = 100;
				try {
					rows = Integer.parseInt(args[2]);
				} catch (NumberFormatException e) {
					e.printStackTrace();
				}

				// 获得cdo的id
				String cdoId = args[1];
				if (!cdoId.equals("")) {
				    //获取json,判断名称完全相等取id
					GetClient client = new GetClient("/api/datasets?filter=&limit=10&offset=0&query=" + args[1] + "&sorts=-createTime", null);
					JSONObject returnedObject = JSONObject.fromObject(client.getContent());
					JSONArray arr = returnedObject.getJSONArray("content");
					for(int i=0;i<arr.size();i++){
					    JSONObject item = arr.getJSONObject(i);
					    if(arr.getJSONObject(i).getString("name").equals(args[1])){
					        cdoId = item.getString("id");
					        break;
					    }
					}
					
					SaveNameId cdoNameId = new SaveNameId(path, "cdo");
					cdoNameId.setValue(args[1], cdoId);
					//System.out.println(args[1]+","+cdoId);
				}

				// get获得数据
				getClient = new GetClient(api + "/" + cdoId + "/preview?rows=" + rows, null);
				content = getClient.getContent();
				content = "{\"\":" + content + "}";

				// 对比期望值与获得值
				JsonDiff different = new JsonDiff();
				String expectJson = null;
				try {
					expectJson = FileUtil.getStringFromFile(new File(expectPath));
					expectJson = "{\"\":" + expectJson + "}";
				} catch (IOException e2) {
					e2.printStackTrace();
				}
				if(isAssert){
    				try {
    					String differStr = different.Diff(expectJson, content);
    					// 如果与预期不同,报错并指出哪里不同
    					Result_Error_out_Main.result_Error_Output_Main(differStr);
    				} catch (IOException e1) {
    					e1.printStackTrace();
    				}
				}
				break;

			case ElementType.MONITOR:

				// 获取sheduler的id
				nameId = new SaveNameId(path, "scheduler");
				String schedulId = args[1];
				schedulId = nameId.getValue(schedulId);
				// 拼接成execution的id
				GetClient client = new GetClient(
						"/api/executions?filter=&limit=10&offset=0&query=fshId%3D"+schedulId+"&sorts=-createTime", null);
				JSONObject exejson = JSONObject.fromObject(client.getContent());
				exejson = exejson.getJSONArray("content").getJSONObject(0);
				String executionId = exejson.getString("id");
				
				File file = new File(path + "/saveId");
				SaveNameId execId = new SaveNameId(path, "executions");
				execId.setValue(schedulId, executionId);
				// 每隔一秒查看监控是否成功
				while (true) {
				    
					getClient = new GetClient(api + "/" + executionId, null);
					content = getClient.getContent();
					jsonStr = JSONObject.fromObject(content);
					String status = jsonStr.getString("status");
					JSONObject json1 = JSONObject.fromObject(status);
					String type = json1.getString("type");
					// 判断六种状态做出不同动作
					// SUCCEEDED 成功，跳出
					// FAILED KILLED UNKNOW 失败，终止
					// RUNNING READ 等待，继续监控
					if ("SUCCEEDED".equals(type)) {
						// System.out.println("Run Succeed!");
						break;
					} else if ("FAILED".equals(type))
						throw new RuntimeException("result: FAILED " + executionId);
					else if ("KILLED".equals(type))
						throw new RuntimeException("result: KILLED " + executionId);
					else if ("UNKNOW".equals(type))
						throw new RuntimeException("result: UNKNOW " + executionId);
					try {
						// 睡眠3秒
						Thread.sleep(3000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				if(isAssert){
				    assertEquals(Integer.parseInt(status), getClient.getStatus());
				}
				break;
		}
	}
	
	void delete(String args[],String path,String queryid,SaveNameId nameId,PostClient postClient){
	    if (args[1].equals("all-local")) {
            File file = new File(path + "/saveId");
            if (file.exists()) {
                for (File file2 : file.listFiles()) {
                    file2.delete();
                }
            }
        } else if (args[1].equals("all-server")) {

            File file = new File(path + "/saveId");
            if (file.exists()) {
                for (File file2 : file.listFiles()) {
                    int dot = file2.getName().lastIndexOf('.');
                    String fileName = null;
                    switch (file2.getName().substring(0, dot)) {
                    
                       case "cdo":
                        fileName = "datasets";
                        break;

                        case "cdc":
                            fileName = "schemas";
                            break;

                        case "processconfigs":
                            fileName = "processconfigs";
                            break;

                        case "tag":
                            fileName = "tags";
                            break;

                        case "scheduler":
                            fileName = "schedulers";
                            break;

                        case "executions":
                            fileName = "executions";
                            break;
                        case "dataflow":
                            fileName = "flows";
                            break;
                        case "workflow":
                            fileName = "flows";
                            break;
                        default:
                            break;
                    }
                    api = '/' + "api" + '/' + fileName + "/removeList";
                    InputStream in;
                    Properties properties = new Properties();
                    try {
                        in = new FileInputStream(file2);
                        properties.load(in);
                        in.close();
                    } catch (Exception e) {
                    }

                    Iterator<Entry<Object, Object>> itr = properties.entrySet().iterator();
                    while (itr.hasNext()) {
                        Entry<Object, Object> entry =  itr.next();
                        queryid = entry.getValue().toString();
                        if (queryid != null && !"".equals(queryid)) {
                            String param = StringUtil.Formate(queryid);
                            postClient = new PostClient(api, param);
                            if(isAssert){
                                assertEquals(Integer.parseInt("204"), postClient.getStatus());
                            }
                        }

                    }

                    file2.delete();
                }
            }

        } else {
            nameId = new SaveNameId(path, args[1].toLowerCase());
            String customName = nameId.getValue(args[2]);
            String param = StringUtil.Formate(customName);
            postClient = new PostClient(api, param);
            if(isAssert){
                assertEquals(Integer.parseInt(status), postClient.getStatus());
            }
            // 在id文件中删除相应的名字和id
            nameId.deleteValue(customName);
            nameId.deleteValue(args[2]);
        }
	}
}
