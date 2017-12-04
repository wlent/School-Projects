/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wlent
 */
public class Guardian {
    private String F_Name, L_Name, S_ID, Phone, Address, Email, Relationship;
    
    public Guardian(String F_Name, String L_Name, String S_ID, String Phone,
            String Address, String Email, String Relationship){
        this.F_Name = F_Name;
        this.L_Name = L_Name;
        this.S_ID = S_ID;
        this.Phone = Phone;
        this.Address = Address;
        this.Email = Email;
        this.Relationship = Relationship;
    }
    
    public String getFName(){
        return F_Name;
    }
    public String getLName(){
        return L_Name;
    }
    public String getSID(){
        return S_ID;
    }
    public String getPhone(){
        return Phone;
    }
    public String getAddress(){
        return Address;
    }
    public String getEmail(){
        return Email;
    }
    public String getRelationship(){
        return Relationship;
    }
}
