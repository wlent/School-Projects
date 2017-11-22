/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author wlent
 */
public class Medication {
    private String S_ID, Clinical_Name, Brand_Name, Dosage, Side_Effects, ADM_HS, M_ID;
    
    public Medication(String S_ID, String Clinical_Name, String Brand_Name, String Dosage,
            String Side_Effects, String ADM_HS, String M_ID){
        this.S_ID = S_ID;
        this.ADM_HS = ADM_HS;
        this.Brand_Name = Brand_Name;
        this.Clinical_Name = Clinical_Name;
        this.Dosage = Dosage;
        this.Side_Effects = Side_Effects;
        this.M_ID = M_ID;
    }
    
    public String getSID(){
        return S_ID;
    }
    public String getClinicalName(){
        return Clinical_Name;
    }
    public String getBrandName(){
        return Brand_Name;
    }
    public String getDosage(){
        return Dosage;
    }
    public String getSideEffects(){
        return Side_Effects;
    }
    public String getADMHS(){
        return ADM_HS;
    }
    public String getMID(){
        return M_ID;
    }
    
}
