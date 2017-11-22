/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author wlent1
 */
public class Community {
    private String Community_Name;
    private String Leader_ID;
    
    public Community(String Community_Name, String Leader_ID){
        this.Community_Name = Community_Name;
        this.Leader_ID = Leader_ID;
    }
    
    public String getCommunityName(){
        return Community_Name;
    }
    
    public String getLeaderID(){
        return Leader_ID;
    }
}
