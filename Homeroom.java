/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wlent
 */
public class Homeroom {
    private String Room_No, T_ID, Community;
    
    public Homeroom(String Room_No, String T_ID, String Community){
        this.Room_No = Room_No;
        this.T_ID = T_ID;
        this.Community = Community;
    }
    
    public String getRoomNo(){
        return Room_No;
    }
    
    public String getTID(){
        return T_ID;
    }
    
    public String getCommunity(){
        return Community;
    }
}
