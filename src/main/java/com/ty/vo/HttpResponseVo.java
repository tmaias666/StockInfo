package com.ty.vo;

public class HttpResponseVo{

    Integer statusCode;

    String responseBody;

    public HttpResponseVo(){
    }

    public HttpResponseVo(Integer status, String responseBody){
        super();
        this.statusCode = status;
        this.responseBody = responseBody;
    }

    public Integer getStatus(){
        return statusCode;
    }

    public void setStatus(Integer status){
        this.statusCode = status;
    }

    public String getResponseBody(){
        return responseBody;
    }

    public void setResponseBody(String responseBody){
        this.responseBody = responseBody;
    }

    @Override
    public String toString(){
        return "HttpResponseVo [status=" + statusCode + ", responseBody=" + responseBody + "]";
    }
}
