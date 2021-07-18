package com.ty.vo;

public class SingleStockInfo{

    private String name;//公司全名

    private String code;//股票代號

    private String date;//20210623

    private String yPrice;//昨日收盤價

    private String oPrice;//開盤價

    private String fPrice;//收盤價

    private String lPrice;//最低價

    private String hPrice;//最高價

    private String diff;//與昨日價差

    private String totalVolumn;//當天累積成交量

    private String minPrice;//跌停價

    private String maxPrice;//漲停價

    private String z;//當天最末盤成交價

    private String tv;//當天末盤成交量

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getCode(){
        return code;
    }

    public void setCode(String code){
        this.code = code;
    }

    public String getDate(){
        return date;
    }

    public void setDate(String date){
        this.date = date;
    }

    public String getyPrice(){
        return yPrice;
    }

    public void setyPrice(String yPrice){
        this.yPrice = yPrice;
    }

    public String getoPrice(){
        return oPrice;
    }

    public void setoPrice(String oPrice){
        this.oPrice = oPrice;
    }

    public String getfPrice(){
        return fPrice;
    }

    public void setfPrice(String fPrice){
        this.fPrice = fPrice;
    }

    public String getlPrice(){
        return lPrice;
    }

    public void setlPrice(String lPrice){
        this.lPrice = lPrice;
    }

    public String gethPrice(){
        return hPrice;
    }

    public void sethPrice(String hPrice){
        this.hPrice = hPrice;
    }

    public String getDiff(){
        return diff;
    }

    public void setDiff(String diff){
        this.diff = diff;
    }

    public String getTotalVolumn(){
        return totalVolumn;
    }

    public void setTotalVolumn(String totalVolumn){
        this.totalVolumn = totalVolumn;
    }

    public String getMinPrice(){
        return minPrice;
    }

    public void setMinPrice(String minPrice){
        this.minPrice = minPrice;
    }

    public String getMaxPrice(){
        return maxPrice;
    }

    public void setMaxPrice(String maxPrice){
        this.maxPrice = maxPrice;
    }

    public String getZ(){
        return z;
    }

    public void setZ(String z){
        this.z = z;
    }

    public String getTv(){
        return tv;
    }

    public void setTv(String tv){
        this.tv = tv;
    }
}
