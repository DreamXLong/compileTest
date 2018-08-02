package com.eduhdsdk.entity;

import org.json.JSONObject;

/**
 * Created by Administrator on 2017/11/18.
 */

public class Service {
    private String serviceName;
    private String chinesedesc;
    private String englishdesc;
    private boolean isDefault;

    public Service() {
    }
    public Service(JSONObject js) {
        this.serviceName = js.optString("serverareaname");
        this.chinesedesc = js.optString("chinesedesc");
        this.englishdesc = js.optString("englishdesc");
    }


    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getChinesedesc() {
        return chinesedesc;
    }

    public void setChinesedesc(String chinesedesc) {
        this.chinesedesc = chinesedesc;
    }

    public String getEnglishdesc() {
        return englishdesc;
    }

    public void setEnglishdesc(String englishdesc) {
        this.englishdesc = englishdesc;
    }
}
