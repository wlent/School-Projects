/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author wlent
 */
class Student {
    private String S_ID;
    private String F_Name;
    private String L_Name;
    private String DOB;
    private String Room_No;
    
    public Student(String S_ID, String F_Name, String L_Name, String DOB, String Room_No){
        this.S_ID = S_ID;
        this.F_Name = F_Name;
        this.L_Name = L_Name;
        this.DOB = DOB;
        this.Room_No = Room_No;
    }
    
    public String getS_ID(){
        return S_ID;
    }
    public String getF_Name(){
        return F_Name;
    }
    public String getL_Name(){
        return L_Name;
    }
    public String getDOB(){
        return DOB;
    }
    public String getRoom_No(){
        return Room_No;
    }
}
