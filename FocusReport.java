/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


/**
 *
 * @author wlent
 */
public class FocusReport {
    private String S_ID, T_ID, Time_In, Time_Out, Date, Teacher_Description, 
            Student_Response, Type, Comm_Leader_Debrief;
    
    public FocusReport(String S_ID, String T_ID, String Time_In, String Time_Out, 
            String Date, String Teacher_Description, String Student_Response, 
            String Type, String Comm_Leader_Debrief){
        this.S_ID = S_ID;
        this.T_ID = T_ID;
        this.Time_In = Time_In;
        this.Time_Out = Time_Out;
        this.Date = Date;
        this.Teacher_Description = Teacher_Description;
        this.Student_Response = Student_Response;
        this.Type = Type;
        this.Comm_Leader_Debrief = Comm_Leader_Debrief;
    }
    
    public String getS_ID(){
        return S_ID;
    }
    public String getT_ID(){
        return T_ID;
    }
    public String getTime_In(){
        return Time_In;
    }
    public String getTime_Out(){
        return Time_Out;
    }
    public String getDate(){
        return Date;
    }
    public String getTeacher_Description(){
        return Teacher_Description;
    }
    public String getStudent_Response(){
        return Student_Response;
    }
    public String getType(){
        return Type;
    }
    public String getComm_Leader_Debrief(){
        return Comm_Leader_Debrief;
    }
}
