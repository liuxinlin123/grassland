package com.datapps.qa.service.test.client;

import javax.ws.rs.core.MediaType;

import com.datapps.qa.service.test.utils.Config;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.WebResource.Builder;
import com.sun.jersey.core.util.MultivaluedMapImpl;
/**
 * 鐢ㄤ簬鍙戣捣post璇锋眰
 * @author Matrix42
 *
 */
public class PostClient implements MyClient{
	
	private ClientResponse response ;
	private WebResource resource;
	private String RequestUrl;
	
	/**
	 * 鍒濆鍖朠ostClient瀵硅薄
	 * @param url api鍦板潃
	 * @param params 鍙傛暟,鍙互鏄疢ultivaluedMapImpl鎴杍son瀛楃涓�
	 */
	public PostClient(String url, Object params) {
		Client client = Client.create();
		RequestUrl = Config.BaseUrl+url;
		resource = client.resource(RequestUrl);
		connect(params);
	}
	
	/**
	 * 鍙戣捣杩炴帴
	 * @param params 鍙傛暟
	 */
	private void connect(Object params){
		Builder builder = null;
		if(params instanceof String){
			builder = resource.type(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON);
		}else if(params instanceof MultivaluedMapImpl){
			builder = resource.type(MediaType.APPLICATION_FORM_URLENCODED).accept(MediaType.APPLICATION_JSON);
		}
		
		//Cookie cookie = new Cookie("AMBARISESSIONID", Config.Cookie);
		
		response = builder
				.header("X-AUTH-TOKEN",Config.AuthToken)
			//	.cookie(cookie)
				.post(ClientResponse.class,params);
	}
	
	/**
	 * 鑾峰彇int鍨嬬姸鎬佺爜
	 */
	@Override
    public int getStatus(){
		return response.getStatus();
	}
	
	/**
	 * 鑾峰彇缃戦〉杩斿洖鐨勫唴瀹�
	 */
	@Override
    public String getContent(){
		return response.getEntity(String.class);
	}
	
	/**
	 * 鑾峰彇TOKEN瀛楃涓�
	 */
	public String getAuthToken(){
		return response.getHeaders()
				.get("X-AUTH-TOKEN")
				.get(0)
				.toString();
	}
	
}
