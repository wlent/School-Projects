/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject2;

/**
 *
 * @author wlent
 */
public class Teacher {
    private String T_ID, F_Name, L_Name, Subject;
    
    public Teacher(String T_ID, String F_Name, String L_Name, String Subject){
        this.T_ID = T_ID;
        this.F_Name = F_Name;
        this.L_Name = L_Name;
        this.Subject = Subject;
    }
    
    public String getT_ID(){
        return T_ID;
    }
    public String getF_Name(){
        return F_Name;
    }
    public String getL_Name(){
        return L_Name;
    }
    public String getSubject(){
        return Subject;
    }
}
