package com.datapps.qa.service.test.cla;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.json.JSONObject;

public class CaseEntity implements Iterable<Map<String,String>>{

    private String caseName;
    private int caseSize;
    private String casePath;
    private String lastCasePath;
    private JSONObject caseJson;
    private ArrayList<Map<String,String>> caseItems;
    
    public CaseEntity() {
    }
    
    public CaseEntity(JSONObject json) {
        this.caseJson = json;
        this.caseSize = json.getJSONArray("case").size();
        this.caseItems = parseCase();
    }
    
    @SuppressWarnings("unchecked")
    private ArrayList<Map<String,String>> parseCase(){
        ArrayList<Map<String,String>> caseList= new ArrayList<Map<String,String>>();
        for(int i=0;i<caseSize;i++){
            Map<String, String> caseItem = new HashMap<String, String>();
            JSONObject itemJson = caseJson.getJSONArray("case").getJSONObject(i);
            for(String key:(Set<String>)itemJson.keySet()){
                caseItem.put(key, itemJson.getString(key));
            }
            caseList.add(caseItem);
        }
        return caseList;
    }
    

    @Override
    public Iterator<Map<String,String>> iterator() {
        return caseItems.iterator();
    }
    
    public String getCaseName() {
        return caseName;
    }

    public void setCaseName(String caseName) {
        this.caseName = caseName;
    }

    public String getCasePath() {
        return casePath;
    }

    public void setCasePath(String casePath) {
        this.casePath = casePath;
    }
    
    public String getLastCasePath() {
        return lastCasePath;
    }

    public void setLastCasePath(String lastCasePath) {
        this.lastCasePath = lastCasePath;
    }

    public int getCaseSize() {
        return caseSize;
    }

    public JSONObject getJson() {
        return caseJson;
    }
    
    public void setJson(JSONObject caseJson){
        this.caseJson = caseJson;
        this.caseSize = caseJson.getJSONArray("case").size();
        this.caseItems = parseCase();
    }

    public ArrayList<Map<String, String>> getCaseItems() {
        return caseItems;
    }

    public void setCaseItems(ArrayList<Map<String, String>> caseItems) {
        this.caseItems = caseItems;
    }
    
}
