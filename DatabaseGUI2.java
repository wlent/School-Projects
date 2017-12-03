/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.HeadlessException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author wlent
 */
public class DatabaseGUI2 extends javax.swing.JFrame {

    /**
     * Creates new form DatabaseGUI2
     * @throws java.sql.SQLException
     */
    public DatabaseGUI2() throws SQLException {
        initComponents();
        showStudents();
        showTeachers();
        showFocusReports();
        showCommunities();
        showHomerooms();
        showMedication();
        showGuardians();
        
        sort_student_table();       
        sort_teacher_table();      
        sort_focusreport_table();       
        sort_community_table();      
        sort_homeroom_table();
        sort_medication_table();
        printHrMostFr();
        printTeacherMostFr();
        printStudentMostFr();
        printCommunityMostFr();
        
        showByDayOfTheWeek();
        showByMonthAndYear();
        showByTimeOfDay();
        showByQuarter();
    }
    
    
    public Connection getConnection(){ //this currently stays open as long as you have the app running
        try{                           //might want to close it and reopen for each method 
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/wlent1db",
                    "wlent1", "Cosc*8pcy");
            return connection;
        }
        
        catch(ClassNotFoundException | SQLException e){
            System.out.println("Could not connect to database.");
        }
        return null;
    }
    
    private final Connection con = getConnection();
    
    public void printHrMostFr() throws SQLException{
        String query = "SELECT Room_No\n" +
                        "FROM Focus_Report, Student\n" +
                        "WHERE Focus_Report.S_ID = Student.S_ID\n" +
                        "GROUP BY Room_No\n" +
                        "HAVING COUNT(Student.S_ID) >=(SELECT MAX(COUNT_SID) AS MAX_COUNT\n" +
                        "FROM (SELECT Room_No, COUNT(Student.S_ID) AS COUNT_SID\n" +
                        "FROM Focus_Report, Student\n" +
                        "WHERE Focus_Report.S_ID = Student.S_ID\n" +
                        "GROUP BY Room_No) AS A)\n";
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            if(rs.first()){
                homeroomRoomNoLabel.setText(rs.getString("Room_No"));
            }
        }
        catch(Exception e){
            
        }
        finally{
            if(st != null) st.close();
            if(rs != null) rs.close();
        }
    }
    
    public void printTeacherMostFr() throws SQLException{
        String query = "SELECT F_Name, L_Name, fr.T_ID, COUNT(fr.T_ID) AS COUNT_TID\n" +
                        "FROM Focus_Report as fr, Teacher as t\n" +
                        "WHERE fr.T_ID = t.T_ID\n" +
                        "GROUP BY fr.T_ID\n" +
                        "HAVING COUNT_TID >= (SELECT MAX(COUNT_TID) AS MAX_COUNT\n" +
                        "FROM(SELECT T_ID, COUNT(T_ID) AS COUNT_TID\n" +
                        "FROM Focus_Report\n" +
                        "GROUP BY T_ID) AS A);";
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            if(rs.first()){
                teacherNameOverviewLabel.setText(rs.getString("T_ID"));
                teacherIdOverviewLabel.setText(rs.getString("F_Name") + " " + rs.getString("L_Name"));
            }
        }
        catch(Exception e){
            
        }
        finally{
            if(st != null) st.close();
            if(rs != null) rs.close();
        }
    }
    
    public void printStudentMostFr() throws SQLException{
        String query = "SELECT F_Name, L_Name, fr.S_ID, COUNT(fr.S_ID) AS COUNT_SID\n" +
                        "FROM Focus_Report as fr, Student as s\n" +
                        "WHERE fr.S_ID = s.S_ID\n" +
                        "GROUP BY fr.S_ID\n" +
                        "HAVING COUNT_SID >= (SELECT MAX(COUNT_SID) AS MAX_COUNT\n" +
                        "FROM(SELECT S_ID, COUNT(S_ID) AS COUNT_SID\n" +
                        "FROM Focus_Report\n" +
                        "GROUP BY S_ID) AS A);";
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            if(rs.first()){
                studentNameOverviewLabel.setText(rs.getString("F_Name")+ " " + rs.getString("L_Name"));
                studentIdOverviewLabel.setText(rs.getString("S_ID"));
            }
        }
        catch(Exception e){
            
        }
        finally{
            if(st != null) st.close();
            if(rs != null) rs.close();
        }
    }
    
    public void printCommunityMostFr() throws SQLException{
        String query = "SELECT Community_Name, COUNT(Focus_Report.S_ID) AS COUNT_SID\n" +
                        "FROM Focus_Report, Student, Homeroom, Community\n" +
                        "WHERE Focus_Report.S_ID = Student.S_ID AND Homeroom.Community = Community.Community_Name AND Homeroom.Room_No = Student.Room_No\n" +
                        "GROUP BY Community_Name\n" +
                        "HAVING COUNT_SID >= (SELECT MAX(COUNT_SID) AS MAX_SID\n" +
                        "FROM (SELECT Community_Name, COUNT(Focus_Report.S_ID) AS COUNT_SID\n" +
                        "FROM Focus_Report, Student, Homeroom, Community\n" +
                        "WHERE Focus_Report.S_ID = Student.S_ID AND Homeroom.Community = Community.Community_Name AND Homeroom.Room_No = Student.Room_No\n" +
                        "GROUP BY Community_Name) AS A);";
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            if(rs.first()){
                communityNameOverviewLabel.setText(rs.getString("Community_Name"));
                communityNumberOverviewLabel.setText(rs.getString("COUNT_SID"));
            }
        }
        catch(Exception e){
            
        }
        finally{
            if(st != null) st.close();
            if(rs != null) rs.close();
        }
    }
    
    public ArrayList<Student> studentList() throws SQLException{ //creates an ArrayList based on the student table in mysql
        ArrayList<Student> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Student");
        
            while(rs.next()){
                Student student = new Student(rs.getString("S_ID"), rs.getString("F_Name"), 
                        rs.getString("L_Name"), rs.getString("DOB"), rs.getString("Room_No"));
                list.add(student);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        return list;
    }
    
    public ArrayList<Teacher> teacherList() throws SQLException{ //creates an ArrayList based on the teacher table
        ArrayList<Teacher> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Teacher");
        
            while(rs.next()){
                Teacher teacher = new Teacher(rs.getString("T_ID"), rs.getString("F_Name"), 
                        rs.getString("L_Name"), rs.getString("Subject"));
                list.add(teacher);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        return list;
    }
    
    public ArrayList<FocusReport> FocusReportList() throws SQLException{ //creates an ArrayList based on the FocusReport table
        ArrayList<FocusReport> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Focus_Report");
        
            while(rs.next()){
                FocusReport report = new FocusReport(rs.getString("S_ID"), rs.getString("T_ID"), 
                        rs.getString("Time_In"), rs.getString("Time_Out"), rs.getString("Date"),
                        rs.getString("Teacher_Description"), rs.getString("Student_Response"),
                        rs.getString("Type"), rs.getString("Comm_Leader_Debrief"));
                list.add(report);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        return list;
    }
    
    public ArrayList<Community> CommunityList() throws SQLException{ //creates an ArrayList based on the Community table
        ArrayList<Community> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Community");
        
            while(rs.next()){
                Community community = new Community(rs.getString("Community_Name"), rs.getString("Leader_ID"));
                list.add(community);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        return list;
    }
    
    public ArrayList<Homeroom> HomeroomList() throws SQLException{ //creates an ArrayList based on the Homeroom table
        ArrayList<Homeroom> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Homeroom");
        
            while(rs.next()){
                Homeroom hr = new Homeroom(rs.getString("Room_No"), rs.getString("T_ID"), rs.getString("Community"));
                list.add(hr);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        
        return list;
    }
    
    public ArrayList<Medication> MedicationList() throws SQLException{ //creates an ArrayList based on the Medication table
        ArrayList<Medication> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Medication");
        
            while(rs.next()){
                Medication med = new Medication(rs.getString("S_ID"), rs.getString("Clinical_Name"), 
                rs.getString("Brand_Name"), rs.getString("Dosage"), rs.getString("Side_Effects"),
                rs.getString("ADM_HS"), rs.getString("M_ID"));
                list.add(med);
            }
        }
        
        catch(Exception e){
            System.out.println("Error with meds");
        }
        
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        
        return list;
    }
    
    public ArrayList<Guardian> GuardianList() throws SQLException{
        ArrayList<Guardian> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Guardian");
            while(rs.next()){
                Guardian guardian = new Guardian(rs.getString("F_name"), rs.getString("L_name"),
                rs.getString("S_ID"), rs.getString("Phone"), rs.getString("Address"),
                rs.getString("Email"), rs.getString("Relationship"));
                list.add(guardian);
            }
        }
        catch(Exception e){
            System.out.println("Error with Guardian table construction");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
        return list;
    }
    
    public void showGuardians() throws SQLException{
        ArrayList<Guardian> list = GuardianList();
        DefaultTableModel model = (DefaultTableModel) guardianTable.getModel();
        Object[] row = new Object[7];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getFName();
            row[1] = list.get(i).getLName();
            row[2] = list.get(i).getSID();
            row[3] = list.get(i).getPhone();
            row[4] = list.get(i).getAddress();
            row[5] = list.get(i).getEmail();
            row[6] = list.get(i).getRelationship();
            model.addRow(row);
        }
    }
    
    public void showByDayOfTheWeek() throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        String query = "Select dayname(Date), Count(dayname(Date))\n" +
                        "from Focus_Report\n" +
                        "group by dayname(Date);";
        DefaultTableModel model = (DefaultTableModel) byDayOfTheWeekTable.getModel();
        Object[] row = new Object[2];
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                row[0] = rs.getString("dayname(Date)");
                row[1] = rs.getString("Count(dayname(Date))");
                model.addRow(row);
            }
        }
        catch(Exception e){
            System.out.println("Error with showByDayOfTheWeek");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
    }
    
    public void showByMonthAndYear() throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        String query = "Select monthname(Date) AS Month, year(Date) AS Year, count(month(Date)) AS Number\n" +
                        "from Focus_Report\n" +
                        "group by month(Date), year(Date);";
        DefaultTableModel model = (DefaultTableModel) byMonthAndYearTable.getModel();
        Object[] row = new Object[3];
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                row[0] = rs.getString("Month");
                row[1] = rs.getString("Year");
                row[2] = rs.getString("Number");
                model.addRow(row);
            }
        }
        catch(Exception e){
            System.out.println("Error with showByDayOfTheWeek");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
    }
    
    public void showByTimeOfDay() throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        String query = "SELECT hour(Time_In), count(hour(Time_In))\n" +
                        "FROM Focus_Report\n" +
                        "GROUP BY hour(Time_In)\n" +
                        "ORDER BY count(hour(Time_In)) DESC;";
        DefaultTableModel model = (DefaultTableModel) byTimeOfDayTable.getModel();
        Object[] row = new Object[2];
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                row[0] = rs.getString("hour(Time_In)");
                row[1] = rs.getString("count(hour(Time_In))");
                model.addRow(row);
            }
        }
        catch(Exception e){
            System.out.println("Error with showByDayOfTheWeek");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
    }
    
    public void showByQuarter() throws SQLException{
        Statement st = null;
        ResultSet rs = null;
        String query = "Select Quarter, count(Quarter)\n" +
                        "from (Select S_ID, T_ID, Time_In, Date, \n" +
                        "    CASE quarter(Date)\n" +
                        "        WHEN 1 THEN \"3rd Quarter\"\n" +
                        "        WHEN 2 THEN \"4th Quarter\"\n" +
                        "        WHEN 3 THEN \"1st Quarter\"\n" +
                        "        WHEN 4 THEN \"2nd Quarter\"\n" +
                        "    END as Quarter\n" +
                        "    from Focus_Report) as Quarter_Table\n" +
                        "group by Quarter;";
        DefaultTableModel model = (DefaultTableModel) byQuarterTable.getModel();
        Object[] row = new Object[2];
        try{
            st = con.createStatement();
            rs = st.executeQuery(query);
            while(rs.next()){
                row[0] = rs.getString("Quarter");
                row[1] = rs.getString("count(Quarter)");
                model.addRow(row);
            }
        }
        catch(Exception e){
            System.out.println("Error with showByQuarter");
        }
        finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
        }
    }
    
    public final void showStudents() throws SQLException{ //displays the full student table
        ArrayList<Student> list = studentList();
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        PreparedStatement preparedStatement = null;
        String query = "SELECT COUNT(S_ID) AS COUNT_SID " +
                                                "FROM Focus_Report " +
                                                "WHERE S_ID = ?";
        String query2 = "SELECT Community\n" +
                        "FROM Student s, Homeroom h\n" +
                        "WHERE s.Room_No = h.Room_No\n" +
                        "AND S_ID = ";
        
        ResultSet count = null;
        ResultSet rs = null;
        Statement st = null;
        Object row[] = new Object[8];
        
        for(int i = 0; i < list.size(); i++){
            
            try{
                String studentToCount = list.get(i).getS_ID();
                preparedStatement = con.prepareStatement(query);
                preparedStatement.setString(1, studentToCount);
                count = preparedStatement.executeQuery();
                if(count.first()){
                    row[5] = count.getString("COUNT_SID");
                }
                
                row[0] = list.get(i).getS_ID();
                row[1] = list.get(i).getF_Name();
                row[2] = list.get(i).getL_Name();
                row[3] = list.get(i).getDOB();
                row[4] = list.get(i).getRoom_No();
            }
        
            catch(Exception e){
                System.out.println("Error with count");
                } 
            finally{
                try{if(count != null)count.close();} catch(Exception e){};
                try{if(preparedStatement != null)preparedStatement.close();} catch(Exception e){};
                
            }
            try{
                String studentToCount = list.get(i).getS_ID();
                st = con.createStatement();
                rs = st.executeQuery(query2 + " " + studentToCount);
                if(rs.first()){
                    row[6] = rs.getString("Community");
                }
            }
            catch(Exception e){
                System.out.println("error with community");
            }
            finally{
                if(rs != null) rs.close();
                if(st != null) st.close();
            }
            try{
                String sid = list.get(i).getS_ID();
                String query3 = "Select MaxTypeGroups.S_ID, Type \n" +
                        "From\n" +
                        "(SELECT S_ID, Type, COUNT(Type) as COUNT_T\n" +
                        "FROM Focus_Report\n" +
                        "WHERE S_ID = " + sid +
                        " GROUP BY S_ID, Type) AS MaxTypeGroups, (SELECT S_ID, MAX(COUNT_T) as MAX_COUNT\n" +
                        "	FROM (SELECT S_ID, Type, count(Type) as COUNT_T\n" +
                        "		FROM Focus_Report\n" +
                        "		GROUP BY S_ID, Type) AS A\n" +
                        "	GROUP BY S_ID) AS StudentsMaxCount\n" +
                        "WHERE MaxTypeGroups.S_ID = StudentsMaxCount.S_ID AND COUNT_T = MAX_COUNT;";
                st = con.createStatement();
                rs = st.executeQuery(query3);
                if(!rs.first()){
                    row[7] = " ";
                }
                else if(rs.first()){
                    row[7] = rs.getString("Type");
                }
            }
            catch(Exception e){
                
            }
            finally{
                if(rs != null) rs.close();
                if(st != null) st.close();
            }
            model.addRow(row);
        }
    }
    
    public final void showTeachers() throws SQLException{ //displays the full teacher table
        ArrayList<Teacher> list = teacherList();
        DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
        Object row[] = new Object[6];
        Statement st = null;
        ResultSet count = null;
        Statement st2 = null;
        ResultSet rs = null;
        for(int i = 0; i < list.size(); i++){
            String teacherToCount = list.get(i).getT_ID();
            try{ //try-catch to display the count of focus reports
                
                st = con.createStatement();
                st2 = con.createStatement();
                count = st.executeQuery("SELECT COUNT(T_ID) AS COUNT_TID " +
                                                "FROM Focus_Report " +
                                                "WHERE T_ID = " + teacherToCount);
                if(count.first()){
                    row[4] = count.getString("COUNT_TID");
                }
            }
            catch(Exception e){
                System.out.println("Error with count");
                }
            finally{
                count.close();
                st.close();
            }
            try{
                st2 = con.createStatement();
                rs = st2.executeQuery("Select MaxTypeGroups.T_ID, Type From\n" +
                                        "(SELECT T_ID, Type, COUNT(Type) as COUNT_T\n" +
                                        "FROM Focus_Report\n" +
                                        "WHERE T_ID = " + teacherToCount +
                                        " GROUP BY T_ID, Type) AS MaxTypeGroups, (SELECT T_ID, MAX(COUNT_T) as MAX_COUNT\n" +
                                        "FROM (SELECT T_ID, Type, count(Type) as COUNT_T\n" +
                                        "            FROM Focus_Report                                        GROUP BY T_ID, Type) AS A\n" +
                                        "        GROUP BY T_ID) AS TeachersMaxCount\n" +
                                        "WHERE MaxTypeGroups.T_ID = TeachersMaxCount.T_ID AND COUNT_T = MAX_COUNT;");
                if(!rs.first()){
                    row[5] = " ";
                }
                else if(rs.first()){
                    row[5] = rs.getString("Type");
                }
            }
            catch(Exception e){
                System.out.println("teacher type not working");
            }
            finally{
                rs.close();
                if(st2 != null) st.close();
            }
            row[0] = list.get(i).getT_ID();
            row[1] = list.get(i).getF_Name();
            row[2] = list.get(i).getL_Name();
            row[3] = list.get(i).getSubject();
            model.addRow(row);
        }
    }
    
    public final void showFocusReports()throws SQLException{ //displays the full focus report table
        ArrayList<FocusReport> list = FocusReportList();
        DefaultTableModel model = (DefaultTableModel) focusReportsTable.getModel();
        Object row[] = new Object[10];
        Statement st = null;
        ResultSet rs = null;
        String query = "Select  \n" +
                        "CASE quarter(Date)\n" +
                        "    WHEN 1 THEN \"3rd Quarter\"\n" +
                        "    WHEN 2 THEN \"4th Quarter\"\n" +
                        "    WHEN 3 THEN \"1st Quarter\"\n" +
                        "    WHEN 4 THEN \"2nd Quarter\"\n" +
                        "END as Quarter\n" +
                        "FROM Focus_Report\n" +
                        "WHERE S_ID = ";
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getS_ID();
            row[1] = list.get(i).getT_ID();
            row[2] = list.get(i).getTime_In();
            row[3] = list.get(i).getTime_Out();
            row[4] = list.get(i).getDate();
            row[5] = list.get(i).getTeacher_Description();
            row[6] = list.get(i).getStudent_Response();
            row[7] = list.get(i).getType();
            row[8] = list.get(i).getComm_Leader_Debrief();
            try{
                st = con.createStatement();
                rs = st.executeQuery(query + list.get(i).getS_ID());
                if(rs.first()){
                    row[9] = rs.getString("Quarter");
                }
            }
            catch(Exception e){
                
            }
            finally{
                if(rs != null) rs.close();
                if(st != null) st.close();
            }
            model.addRow(row);
        }
    }
    
    public final void showCommunities() throws SQLException{ //displays the full community table
        ArrayList<Community> list = CommunityList();
        DefaultTableModel model = (DefaultTableModel) communityTable.getModel();
        Object row[] = new Object[3];
        Statement st = null;
        ResultSet rs = null;
        for(int i = 0; i < list.size(); i++){
            try{
                String communityToCount = list.get(i).getCommunityName();
                st = con.createStatement();
                rs = st.executeQuery("SELECT COUNT(Student.S_ID) AS count " +
                                       " FROM Focus_Report, Student, Homeroom, Community " +
                                       " WHERE Focus_Report.S_ID = Student.S_ID AND " + 
                                       " Homeroom.Community = Community.Community_Name" +  
                                       " AND Student.Room_No = Homeroom.Room_No" + 
                                       " AND Community_Name = " + "'" + communityToCount + "'");
                if(rs.first()){
                    row[2] = rs.getString("count");
                }
            }
            catch(Exception e){
                System.out.println("Error with count");
                }
            finally{
                if(rs != null)rs.close();
                if(st != null)st.close();
            }
            row[0] = list.get(i).getCommunityName();
            row[1] = list.get(i).getLeaderID();
           
            
            model.addRow(row);
        }
    }
    
    public final void showHomerooms() throws SQLException{ //displays the full homeroom table
        ArrayList<Homeroom> list = HomeroomList();
        DefaultTableModel model = (DefaultTableModel) homeroomTable.getModel();
        ResultSet rs = null;
        Statement st = null;
        Object row[] = new Object[4];
        for(int i = 0; i < list.size(); i++){
            try{
                st = con.createStatement();
                rs = st.executeQuery("SELECT COUNT(Room_No) AS COUNT_R " +
                                            "FROM Focus_Report f, Student s " +
                                            "WHERE f.S_ID = s.S_ID " + 
                                            "AND Room_No = " + list.get(i).getRoomNo());
                if(rs.first()){
                    row[3] = rs.getString("COUNT_R");
                }   
            }   
            catch(Exception e){
            
            }
            
            finally{
            if(rs != null) rs.close();
            if(st != null) st.close();
            }
            
            row[0] = list.get(i).getRoomNo();
            row[1] = list.get(i).getTID();
            row[2] = list.get(i).getCommunity();
            model.addRow(row);
        }
    }
    
    public final void showMedication() throws SQLException{ //displays the full medication table
        ArrayList<Medication> list = MedicationList();
        DefaultTableModel model = (DefaultTableModel) medicationTable.getModel();
        Object row[] = new Object[7];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getSID();
            row[1] = list.get(i).getClinicalName();
            row[2] = list.get(i).getBrandName();
            row[3] = list.get(i).getDosage();
            row[4] = list.get(i).getSideEffects();
            row[5] = list.get(i).getADMHS();
            row[6] = list.get(i).getMID();
            model.addRow(row);
        }
    }
    
    public void sort_student_table(){
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        studentTable.setRowSorter(sorter);
    }
    
    public void sort_teacher_table(){
        DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        teacherTable.setRowSorter(sorter);
    }
        
    public void sort_focusreport_table(){
        DefaultTableModel model = (DefaultTableModel) focusReportsTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        focusReportsTable.setRowSorter(sorter);
    }
            
    public void sort_community_table(){
        DefaultTableModel model = (DefaultTableModel) communityTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        communityTable.setRowSorter(sorter);
    }
                
    public void sort_homeroom_table(){
        DefaultTableModel model = (DefaultTableModel) homeroomTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        homeroomTable.setRowSorter(sorter);
    }
                    
    public void sort_medication_table(){
        DefaultTableModel model = (DefaultTableModel) medicationTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        medicationTable.setRowSorter(sorter);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        guardianPanel = new javax.swing.JTabbedPane();
        studentsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        studentIDTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        studentIDSearchButton = new javax.swing.JButton();
        studentResetButton = new javax.swing.JButton();
        addStudentButton = new javax.swing.JButton();
        teachersPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        teacherIDTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        teacherTable = new javax.swing.JTable();
        teacherIDSearchButton = new javax.swing.JButton();
        teacherResetButton = new javax.swing.JButton();
        addTeacherButton = new javax.swing.JButton();
        focusReportsPanel = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        studentIdFrSearchField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        timeInFrSearchField = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        dateFrSearchField = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        focusReportsTable = new javax.swing.JTable();
        focusReportSearchButton = new javax.swing.JButton();
        createFocusReportButton = new javax.swing.JButton();
        focusReportRefreshButton = new javax.swing.JButton();
        jLabel8 = new javax.swing.JLabel();
        teacherFrSearchField = new javax.swing.JTextField();
        communityPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        communityTable = new javax.swing.JTable();
        homeroomPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        homeroomTeacherIDTextField = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        homeroomTable = new javax.swing.JTable();
        medicationPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        studentIDMedicationTextField = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        medicationTable = new javax.swing.JTable();
        studentSearchButtonMedTable = new javax.swing.JButton();
        addMedicationButton = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        mostFrOverviewPanel = new javax.swing.JPanel();
        teacherOverviewPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        teacherNameOverviewLabel = new javax.swing.JLabel();
        teacherIdOverviewLabel = new javax.swing.JLabel();
        studentOverviewPanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        studentNameOverviewLabel = new javax.swing.JLabel();
        studentIdOverviewLabel = new javax.swing.JLabel();
        communityOverviewPanel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        communityNameOverviewLabel = new javax.swing.JLabel();
        communityNumberOverviewLabel = new javax.swing.JLabel();
        homeroomOverviewPanel = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        homeroomRoomNoLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        byDayOfTheWeekPanel = new javax.swing.JPanel();
        jScrollPane8 = new javax.swing.JScrollPane();
        byDayOfTheWeekTable = new javax.swing.JTable();
        byMonthAndYearPanel = new javax.swing.JPanel();
        jScrollPane9 = new javax.swing.JScrollPane();
        byMonthAndYearTable = new javax.swing.JTable();
        byTimeOfDayPanel = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        byTimeOfDayTable = new javax.swing.JTable();
        byQuarterPanel = new javax.swing.JPanel();
        jScrollPane11 = new javax.swing.JScrollPane();
        byQuarterTable = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        studentIdGuardianSearchTextField = new javax.swing.JTextField();
        studentIdFilterButtonGuardians = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        guardianTable = new javax.swing.JTable();
        globalRefreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Student ID");

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "First Name", "Last Name", "D.O.B.", "Homeroom", "# of Focus Reports", "Community", "Common FR Type"
            }
        ));
        jScrollPane1.setViewportView(studentTable);

        studentIDSearchButton.setText("Filter");
        studentIDSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIDSearchButtonActionPerformed(evt);
            }
        });

        studentResetButton.setText("Reset");
        studentResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentResetButtonActionPerformed(evt);
            }
        });

        addStudentButton.setText("Add");
        addStudentButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addStudentButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout studentsPanelLayout = new javax.swing.GroupLayout(studentsPanel);
        studentsPanel.setLayout(studentsPanelLayout);
        studentsPanelLayout.setHorizontalGroup(
            studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentIDSearchButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentResetButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(addStudentButton)
                .addContainerGap(1160, Short.MAX_VALUE))
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1)
                .addContainerGap())
        );
        studentsPanelLayout.setVerticalGroup(
            studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentIDSearchButton)
                    .addComponent(studentResetButton)
                    .addComponent(addStudentButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Students", studentsPanel);

        jLabel2.setText("Teacher ID");

        teacherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Teacher ID", "First Name", "Last Name", "Subject", "# of Focus Reports", "Common FR Type"
            }
        ));
        jScrollPane2.setViewportView(teacherTable);

        teacherIDSearchButton.setText("Filter");
        teacherIDSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherIDSearchButtonActionPerformed(evt);
            }
        });

        teacherResetButton.setText("Reset");
        teacherResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherResetButtonActionPerformed(evt);
            }
        });

        addTeacherButton.setText("Add");
        addTeacherButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addTeacherButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout teachersPanelLayout = new javax.swing.GroupLayout(teachersPanel);
        teachersPanel.setLayout(teachersPanelLayout);
        teachersPanelLayout.setHorizontalGroup(
            teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teachersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(teachersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherResetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addTeacherButton)
                        .addContainerGap(1157, Short.MAX_VALUE))))
        );
        teachersPanelLayout.setVerticalGroup(
            teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teachersPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(teacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teacherIDSearchButton)
                    .addComponent(teacherResetButton)
                    .addComponent(addTeacherButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Teachers", teachersPanel);

        jLabel3.setText("Student ID");

        jLabel4.setText("Time In");

        jLabel5.setText("Date");

        focusReportsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Teacher ID", "Time In", "Time Out", "Date", "Teacher Description", "Student Response", "Type", "Debrief", "Quarter"
            }
        ));
        jScrollPane3.setViewportView(focusReportsTable);

        focusReportSearchButton.setText("Filter");
        focusReportSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusReportSearchButtonActionPerformed(evt);
            }
        });

        createFocusReportButton.setText("Create");
        createFocusReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createFocusReportButtonActionPerformed(evt);
            }
        });

        focusReportRefreshButton.setText("Refresh");
        focusReportRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusReportRefreshButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("Teacher ID");

        javax.swing.GroupLayout focusReportsPanelLayout = new javax.swing.GroupLayout(focusReportsPanel);
        focusReportsPanel.setLayout(focusReportsPanelLayout);
        focusReportsPanelLayout.setHorizontalGroup(
            focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 1498, Short.MAX_VALUE)
                    .addGroup(focusReportsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(studentIdFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel8)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(timeInFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(dateFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(focusReportSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(createFocusReportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(focusReportRefreshButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        focusReportsPanelLayout.setVerticalGroup(
            focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(studentIdFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(timeInFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(dateFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(focusReportSearchButton)
                    .addComponent(createFocusReportButton)
                    .addComponent(focusReportRefreshButton)
                    .addComponent(jLabel8)
                    .addComponent(teacherFrSearchField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Focus Reports", focusReportsPanel);

        communityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Community Name", "Leader ID", "# of Focus Reports"
            }
        ));
        jScrollPane4.setViewportView(communityTable);

        javax.swing.GroupLayout communityPanelLayout = new javax.swing.GroupLayout(communityPanel);
        communityPanel.setLayout(communityPanelLayout);
        communityPanelLayout.setHorizontalGroup(
            communityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(1057, Short.MAX_VALUE))
        );
        communityPanelLayout.setVerticalGroup(
            communityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(267, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Community", communityPanel);

        jLabel6.setText("Teacher ID");

        homeroomTeacherIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeroomTeacherIDTextFieldActionPerformed(evt);
            }
        });

        homeroomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Room Number", "Teacher", "Community", "# of Focus Reports"
            }
        ));
        jScrollPane5.setViewportView(homeroomTable);

        javax.swing.GroupLayout homeroomPanelLayout = new javax.swing.GroupLayout(homeroomPanel);
        homeroomPanel.setLayout(homeroomPanelLayout);
        homeroomPanelLayout.setHorizontalGroup(
            homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeroomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(homeroomPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(homeroomTeacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(1057, Short.MAX_VALUE))
        );
        homeroomPanelLayout.setVerticalGroup(
            homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeroomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(homeroomTeacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(242, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Homeroom", homeroomPanel);

        jLabel7.setText("Student ID");

        medicationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Clinical Name", "Brand Name", "Dosage", "Side Effects", "Adminstered", "Medication ID"
            }
        ));
        jScrollPane6.setViewportView(medicationTable);

        studentSearchButtonMedTable.setText("Filter");
        studentSearchButtonMedTable.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentSearchButtonMedTableActionPerformed(evt);
            }
        });

        addMedicationButton.setText("Add");
        addMedicationButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMedicationButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout medicationPanelLayout = new javax.swing.GroupLayout(medicationPanel);
        medicationPanel.setLayout(medicationPanelLayout);
        medicationPanelLayout.setHorizontalGroup(
            medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1498, Short.MAX_VALUE)
                    .addGroup(medicationPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(studentIDMedicationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(studentSearchButtonMedTable)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addMedicationButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        medicationPanelLayout.setVerticalGroup(
            medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(studentIDMedicationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentSearchButtonMedTable)
                    .addComponent(addMedicationButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Medication", medicationPanel);

        mostFrOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Most Focus Reports"));

        teacherOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Teacher"));

        jLabel14.setText("Name:");

        jLabel15.setText("ID:");

        javax.swing.GroupLayout teacherOverviewPanelLayout = new javax.swing.GroupLayout(teacherOverviewPanel);
        teacherOverviewPanel.setLayout(teacherOverviewPanelLayout);
        teacherOverviewPanelLayout.setHorizontalGroup(
            teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(teacherNameOverviewLabel)
                    .addComponent(teacherIdOverviewLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        teacherOverviewPanelLayout.setVerticalGroup(
            teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(teacherNameOverviewLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(teacherIdOverviewLabel))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        studentOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Student"));

        jLabel16.setText("Name:");

        jLabel17.setText("ID:");

        javax.swing.GroupLayout studentOverviewPanelLayout = new javax.swing.GroupLayout(studentOverviewPanel);
        studentOverviewPanel.setLayout(studentOverviewPanelLayout);
        studentOverviewPanelLayout.setHorizontalGroup(
            studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel17)
                    .addComponent(jLabel16))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(studentNameOverviewLabel)
                    .addComponent(studentIdOverviewLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        studentOverviewPanelLayout.setVerticalGroup(
            studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(studentNameOverviewLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(studentIdOverviewLabel))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        communityOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Community"));

        jLabel18.setText("Name:");

        jLabel19.setText("Number:");

        javax.swing.GroupLayout communityOverviewPanelLayout = new javax.swing.GroupLayout(communityOverviewPanel);
        communityOverviewPanel.setLayout(communityOverviewPanelLayout);
        communityOverviewPanelLayout.setHorizontalGroup(
            communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel18)
                    .addComponent(jLabel19))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(communityNameOverviewLabel)
                    .addComponent(communityNumberOverviewLabel))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        communityOverviewPanelLayout.setVerticalGroup(
            communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(communityNameOverviewLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(communityNumberOverviewLabel))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        homeroomOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Homeroom"));

        jLabel11.setText("Room # :");

        javax.swing.GroupLayout homeroomOverviewPanelLayout = new javax.swing.GroupLayout(homeroomOverviewPanel);
        homeroomOverviewPanel.setLayout(homeroomOverviewPanelLayout);
        homeroomOverviewPanelLayout.setHorizontalGroup(
            homeroomOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeroomOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(homeroomRoomNoLabel)
                .addContainerGap(105, Short.MAX_VALUE))
        );
        homeroomOverviewPanelLayout.setVerticalGroup(
            homeroomOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeroomOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeroomOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(homeroomRoomNoLabel))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout mostFrOverviewPanelLayout = new javax.swing.GroupLayout(mostFrOverviewPanel);
        mostFrOverviewPanel.setLayout(mostFrOverviewPanelLayout);
        mostFrOverviewPanelLayout.setHorizontalGroup(
            mostFrOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mostFrOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mostFrOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(homeroomOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(communityOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(studentOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(teacherOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
        );
        mostFrOverviewPanelLayout.setVerticalGroup(
            mostFrOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mostFrOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(teacherOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(communityOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(homeroomOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(229, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Focus Reports by Time and Date"));

        byDayOfTheWeekPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("By Day of the Week"));

        byDayOfTheWeekTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Day", "# "
            }
        ));
        jScrollPane8.setViewportView(byDayOfTheWeekTable);

        javax.swing.GroupLayout byDayOfTheWeekPanelLayout = new javax.swing.GroupLayout(byDayOfTheWeekPanel);
        byDayOfTheWeekPanel.setLayout(byDayOfTheWeekPanelLayout);
        byDayOfTheWeekPanelLayout.setHorizontalGroup(
            byDayOfTheWeekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byDayOfTheWeekPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8, javax.swing.GroupLayout.DEFAULT_SIZE, 149, Short.MAX_VALUE)
                .addContainerGap())
        );
        byDayOfTheWeekPanelLayout.setVerticalGroup(
            byDayOfTheWeekPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byDayOfTheWeekPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane8)
                .addContainerGap())
        );

        byMonthAndYearPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("By Month and Year"));

        byMonthAndYearTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Month", "Year", "#"
            }
        ));
        jScrollPane9.setViewportView(byMonthAndYearTable);

        javax.swing.GroupLayout byMonthAndYearPanelLayout = new javax.swing.GroupLayout(byMonthAndYearPanel);
        byMonthAndYearPanel.setLayout(byMonthAndYearPanelLayout);
        byMonthAndYearPanelLayout.setHorizontalGroup(
            byMonthAndYearPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byMonthAndYearPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(28, Short.MAX_VALUE))
        );
        byMonthAndYearPanelLayout.setVerticalGroup(
            byMonthAndYearPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byMonthAndYearPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane9)
                .addContainerGap())
        );

        byTimeOfDayPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("By Time of Day"));

        byTimeOfDayTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Hour", "#"
            }
        ));
        jScrollPane10.setViewportView(byTimeOfDayTable);

        javax.swing.GroupLayout byTimeOfDayPanelLayout = new javax.swing.GroupLayout(byTimeOfDayPanel);
        byTimeOfDayPanel.setLayout(byTimeOfDayPanelLayout);
        byTimeOfDayPanelLayout.setHorizontalGroup(
            byTimeOfDayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byTimeOfDayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10, javax.swing.GroupLayout.PREFERRED_SIZE, 197, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(42, Short.MAX_VALUE))
        );
        byTimeOfDayPanelLayout.setVerticalGroup(
            byTimeOfDayPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byTimeOfDayPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane10)
                .addContainerGap())
        );

        byQuarterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("By Quarter"));

        byQuarterTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Quarter", "#"
            }
        ));
        jScrollPane11.setViewportView(byQuarterTable);

        javax.swing.GroupLayout byQuarterPanelLayout = new javax.swing.GroupLayout(byQuarterPanel);
        byQuarterPanel.setLayout(byQuarterPanelLayout);
        byQuarterPanelLayout.setHorizontalGroup(
            byQuarterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byQuarterPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane11, javax.swing.GroupLayout.PREFERRED_SIZE, 223, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        byQuarterPanelLayout.setVerticalGroup(
            byQuarterPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(byQuarterPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane11)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(byDayOfTheWeekPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(byMonthAndYearPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(byTimeOfDayPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(byQuarterPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(168, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(byQuarterPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(byTimeOfDayPanel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(byMonthAndYearPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(byDayOfTheWeekPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mostFrOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(mostFrOverviewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Overview", jPanel1);

        jLabel9.setText("Student ID");

        studentIdFilterButtonGuardians.setText("Filter");
        studentIdFilterButtonGuardians.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIdFilterButtonGuardiansActionPerformed(evt);
            }
        });

        guardianTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "First Name", "Last Name", "Student ID", "Phone", "Address", "Email", "Relationship"
            }
        ));
        jScrollPane7.setViewportView(guardianTable);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentIdGuardianSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentIdFilterButtonGuardians)
                .addContainerGap(1286, Short.MAX_VALUE))
            .addComponent(jScrollPane7)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(studentIdGuardianSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentIdFilterButtonGuardians))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Guardians", jPanel2);

        globalRefreshButton.setText("Refresh");
        globalRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalRefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(guardianPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 1527, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(globalRefreshButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(globalRefreshButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(guardianPanel)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void studentIDSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentIDSearchButtonActionPerformed
        ArrayList<Student> list = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Student WHERE S_ID LIKE '" 
                    + studentIDTextField.getText() + "%'");
        
            while(rs.next()){
                Student student = new Student(rs.getString("S_ID"), rs.getString("F_Name"), 
                        rs.getString("L_Name"), rs.getString("DOB"), rs.getString("Room_No"));
                list.add(student);
            }
        }
        
        catch(SQLException e){
            System.out.println("Error");
        }
        
        finally{
            if(rs != null) try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(st != null) try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        model.setRowCount(0);
        Object row[] = new Object[6];
        for(int i = 0; i < list.size(); i++){
            try{
                String studentToCount = list.get(i).getS_ID();
                st = con.createStatement();
                ResultSet count = st.executeQuery("SELECT COUNT(S_ID) AS COUNT_SID " +
                                                "FROM Focus_Report " +
                                                "WHERE S_ID = " + studentToCount);
                if(count.first()){
                    row[5] = count.getString("COUNT_SID");
                }
            }
        
            catch(Exception e){
                System.out.println("Error with count");
                } 
            row[0] = list.get(i).getS_ID();
            row[1] = list.get(i).getF_Name();
            row[2] = list.get(i).getL_Name();
            row[3] = list.get(i).getDOB();
            row[4] = list.get(i).getRoom_No();
            model.addRow(row);
        }
    }//GEN-LAST:event_studentIDSearchButtonActionPerformed

    private void teacherIDSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teacherIDSearchButtonActionPerformed
        ArrayList<Teacher> list = new ArrayList<Teacher>();
        DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Teacher WHERE T_ID LIKE '" 
                    + teacherIDTextField.getText() + "%'");
        
            while(rs.next()){
                Teacher teacher = new Teacher(rs.getString("T_ID"), rs.getString("F_Name"),
                        rs.getString("L_Name"), rs.getString("Subject"));
                list.add(teacher);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        model.setRowCount(0);
        Object row[] = new Object[5];
        for(int i = 0; i < list.size(); i++){
            try{
                String teacherToCount = list.get(i).getT_ID();
                Statement st = con.createStatement();
                ResultSet count = st.executeQuery("SELECT COUNT(T_ID) AS COUNT_TID " +
                                                "FROM Focus_Report " +
                                                "WHERE T_ID = " + teacherToCount);
                if(count.first()){
                    row[4] = count.getString("COUNT_TID");
                }
            }
            catch(Exception e){
                System.out.println("Error with count");
                } 
            row[0] = list.get(i).getT_ID();
            row[1] = list.get(i).getF_Name();
            row[2] = list.get(i).getL_Name();
            row[3] = list.get(i).getSubject();
            model.addRow(row);
        }
    }//GEN-LAST:event_teacherIDSearchButtonActionPerformed

    private void studentResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentResetButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        model.setRowCount(0);
        try {
            showStudents();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_studentResetButtonActionPerformed

    private void teacherResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teacherResetButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
        model.setRowCount(0);
        try {
            showTeachers();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_teacherResetButtonActionPerformed

    private void focusReportSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_focusReportSearchButtonActionPerformed
        ArrayList<FocusReport> list = new ArrayList<FocusReport>();
        DefaultTableModel model = (DefaultTableModel) focusReportsTable.getModel();
        try{
            Statement st = con.createStatement();
            String query = "WHERE 1 = 1";
            if(!studentIdFrSearchField.getText().equals("")){
                query += " AND S_ID LIKE '" + studentIdFrSearchField.getText() + "%'";
            }
            if(!teacherFrSearchField.getText().equals("")){
                query += " AND T_ID LIKE '" + teacherFrSearchField.getText() + "%'";
            }
            if(!timeInFrSearchField.getText().equals("")){
                query += " AND Time_In LIKE '" + timeInFrSearchField.getText() + "%'";
            }
            if(!dateFrSearchField.getText().equals("")){
                query += " AND Date LIKE '" + dateFrSearchField.getText() + "%'";
            }
            ResultSet rs = st.executeQuery("SELECT * FROM Focus_Report " + query);
        
            while(rs.next()){
                FocusReport focusreport = new FocusReport(rs.getString("S_ID"), 
                        rs.getString("T_ID"), rs.getString("Time_In"), 
                        rs.getString("Time_Out"), rs.getString("Date"), 
                        rs.getString("Teacher_Description"), rs.getString("Student_Response"), 
                        rs.getString("Type"), rs.getString("Comm_Leader_Debrief"));
                list.add(focusreport);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        model.setRowCount(0);
        Object row[] = new Object[9];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getS_ID();
            row[1] = list.get(i).getT_ID();
            row[2] = list.get(i).getTime_In();
            row[3] = list.get(i).getTime_Out();
            row[4] = list.get(i).getDate();
            row[5] = list.get(i).getTeacher_Description();
            row[6] = list.get(i).getStudent_Response();
            row[7] = list.get(i).getType();
            row[8] = list.get(i).getComm_Leader_Debrief();
            model.addRow(row);
        }
    }//GEN-LAST:event_focusReportSearchButtonActionPerformed

    private void homeroomTeacherIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeroomTeacherIDTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_homeroomTeacherIDTextFieldActionPerformed

    private void addStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStudentButtonActionPerformed
      JTextField studentIDField = new JTextField(9);
      JTextField fNameField = new JTextField(15);
      JTextField lNameField = new JTextField(15);
      JTextField DOBField = new JTextField(8);
      JTextField homeroomField = new JTextField(3);

      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("Student ID:"));
      myPanel.add(studentIDField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("First Name:"));
      myPanel.add(fNameField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Last Name:"));
      myPanel.add(lNameField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("D.O.B:"));
      myPanel.add(DOBField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Homeroom:"));
      myPanel.add(homeroomField);
      

      int result = JOptionPane.showConfirmDialog(null, myPanel, 
               "New Student", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        PreparedStatement ps = null;
        try{
             String query = "INSERT INTO Student(S_ID, F_Name, L_Name, DOB, Room_No)" + 
                     "VALUES(?, ?, ?, ?, ?)";
             ps = con.prepareStatement(query);
             ps.setString(1, studentIDField.getText());
             ps.setString(2, fNameField.getText());
             ps.setString(3, lNameField.getText());
             ps.setString(4, DOBField.getText());
             ps.setString(5, homeroomField.getText());
             ps.executeUpdate();
             JOptionPane.showMessageDialog(null, "Success");
             
         }
        catch(Exception e){
             JOptionPane.showMessageDialog(null, "Please check all fields and try again.");
        }
        finally{
            try {
                ps.close();
            } 
            catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }
    }//GEN-LAST:event_addStudentButtonActionPerformed

    private void addTeacherButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addTeacherButtonActionPerformed
      JTextField teacherIDField = new JTextField(9);
      JTextField fNameField = new JTextField(15);
      JTextField lNameField = new JTextField(15);
      JTextField subjectField = new JTextField(8);

      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("Teacher ID:"));
      myPanel.add(teacherIDField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("First Name:"));
      myPanel.add(fNameField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Last Name:"));
      myPanel.add(lNameField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Subject:"));
      myPanel.add(subjectField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      

      int result = JOptionPane.showConfirmDialog(null, myPanel, 
               "New Teacher", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        PreparedStatement ps = null;
        try{
             String query = "INSERT INTO Teacher(T_ID, F_Name, L_Name, Subject)" + 
                     "VALUES(?, ?, ?, ?)";
             ps = con.prepareStatement(query);
             ps.setString(1, teacherIDField.getText());
             ps.setString(2, fNameField.getText());
             ps.setString(3, lNameField.getText());
             ps.setString(4, subjectField.getText());
             ps.executeUpdate();
             JOptionPane.showMessageDialog(null, "Success");
         }
        catch(Exception e){
             JOptionPane.showMessageDialog(null, "Please check all fields and try again.");
        }
        finally{
            try {
                ps.close();
            } 
            catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }
    }//GEN-LAST:event_addTeacherButtonActionPerformed

    private void createFocusReportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_createFocusReportButtonActionPerformed
      JTextField studentIDField = new JTextField(9);
      JTextField teacherIDField = new JTextField(15);
      JTextField timeInField = new JTextField(15);
      JTextField timeOutField = new JTextField(15);
      JTextField dateField = new JTextField(15);
      JTextArea teacherDescriptionArea = new JTextArea(5, 20);
      teacherDescriptionArea.setLineWrap(true);
      JTextArea studentResponseArea = new JTextArea(5, 20);
      studentResponseArea.setLineWrap(true);
      JTextField typeField = new JTextField(9);
      JTextArea commLeaderDebriefArea = new JTextArea(5, 20);
      commLeaderDebriefArea.setLineWrap(true);
      
      JPanel myPanel = new JPanel();
      myPanel.add(new JLabel("Student ID:"));
      myPanel.add(studentIDField);
      myPanel.add(Box.createVerticalStrut(15)); // a spacer
      myPanel.add(new JLabel("Teacher ID:"));
      myPanel.add(teacherIDField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Time In:"));
      myPanel.add(timeInField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Time Out:"));
      myPanel.add(timeOutField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Date:"));
      myPanel.add(dateField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Teacher Description:"));
      myPanel.add(teacherDescriptionArea);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Student Response:"));
      myPanel.add(studentResponseArea);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Type:"));
      myPanel.add(typeField);
      myPanel.add(Box.createHorizontalStrut(15)); // a spacer
      myPanel.add(new JLabel("Community Leader Debrief:"));
      myPanel.add(commLeaderDebriefArea);
      Object[] inputFields = {"Student ID:", studentIDField, "Teacher ID:", teacherIDField,
                                "Time In:", timeInField, "Time Out:", timeOutField,
                                "Date:", dateField, "Teacher_Description", teacherDescriptionArea,
                                "Student Response:", studentResponseArea, "Type:", typeField,
                                "Community Leader Debrief:", commLeaderDebriefArea};
      

      int result = JOptionPane.showConfirmDialog(null, inputFields, 
               "New Focus Report", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        PreparedStatement ps = null;
        try{
             String query = "INSERT INTO Focus_Report(S_ID, T_ID, Time_In, Time_Out, " +
                     "Date, Teacher_Description, Student_Response, Type, Comm_Leader_Debrief) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?)";
             ps = con.prepareStatement(query);
             ps.setString(1, studentIDField.getText());
             ps.setString(2, teacherIDField.getText());
             ps.setString(3, timeInField.getText());
             ps.setString(4, timeOutField.getText());
             ps.setString(5, dateField.getText());
             ps.setString(6, teacherDescriptionArea.getText());
             ps.setString(7, studentResponseArea.getText());
             ps.setString(8, typeField.getText());
             ps.setString(9, commLeaderDebriefArea.getText());
             ps.executeUpdate();
             JOptionPane.showMessageDialog(null, "Success");
         }
        catch(HeadlessException | SQLException e){
             JOptionPane.showMessageDialog(null, "Please check all fields and try again.");
        }
        finally{
            try {
                ps.close();
            } 
            catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }
    }//GEN-LAST:event_createFocusReportButtonActionPerformed

    private void focusReportRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_focusReportRefreshButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) focusReportsTable.getModel();
        model.setRowCount(0);
        try {
            showFocusReports();
        } 
        catch(SQLException ex) {
            Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_focusReportRefreshButtonActionPerformed

    private void studentSearchButtonMedTableActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentSearchButtonMedTableActionPerformed
        ArrayList<Medication> list = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) medicationTable.getModel();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Medication WHERE S_ID LIKE '" 
                    + studentIDMedicationTextField.getText() + "%'");
        
            while(rs.next()){
                Medication med = new Medication(rs.getString("S_ID"), rs.getString("Clinical_Name"), 
                        rs.getString("Brand_Name"), rs.getString("Dosage"), rs.getString("Side_Effects"), 
                        rs.getString("ADM_HS"), rs.getString("M_ID"));
                list.add(med);
            }
        }
        
        catch(SQLException e){
            System.out.println("Error");
        }
        
        finally{
            if(rs != null) try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(st != null) try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        model.setRowCount(0);
        Object row[] = new Object[7];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getSID();
            row[1] = list.get(i).getClinicalName();
            row[2] = list.get(i).getBrandName();
            row[3] = list.get(i).getDosage();
            row[4] = list.get(i).getSideEffects();
            row[5] = list.get(i).getADMHS();
            row[6] = list.get(i).getMID();
            model.addRow(row);
        }
        
    }//GEN-LAST:event_studentSearchButtonMedTableActionPerformed

    private void studentIdFilterButtonGuardiansActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentIdFilterButtonGuardiansActionPerformed
        ArrayList<Guardian> list = new ArrayList<>();
        DefaultTableModel model = (DefaultTableModel) guardianTable.getModel();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Guardian WHERE S_ID LIKE '" 
                    + studentIdGuardianSearchTextField.getText() + "%'");
        
            while(rs.next()){
                Guardian guardian = new Guardian(rs.getString("F_name"), rs.getString("L_name"),
                rs.getString("S_ID"), rs.getString("Phone"), rs.getString("Address"),
                rs.getString("Email"), rs.getString("Relationship"));
                list.add(guardian);
            }
        }
        
        catch(SQLException e){
            System.out.println("Error");
        }
        
        finally{
            if(rs != null) try {
                rs.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
            if(st != null) try {
                st.close();
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        model.setRowCount(0);
        Object[] row = new Object[7];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getFName();
            row[1] = list.get(i).getLName();
            row[2] = list.get(i).getSID();
            row[3] = list.get(i).getPhone();
            row[4] = list.get(i).getAddress();
            row[5] = list.get(i).getEmail();
            row[6] = list.get(i).getRelationship();
            model.addRow(row);
        }
        
    }//GEN-LAST:event_studentIdFilterButtonGuardiansActionPerformed

    private void globalRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalRefreshButtonActionPerformed
        DefaultTableModel studentModel = (DefaultTableModel) studentTable.getModel();
        studentModel.setRowCount(0);
        DefaultTableModel teacherModel = (DefaultTableModel) teacherTable.getModel();
        teacherModel.setRowCount(0);
        DefaultTableModel focusReportModel = (DefaultTableModel) focusReportsTable.getModel();
        focusReportModel.setRowCount(0);
        DefaultTableModel homeroomModel = (DefaultTableModel) homeroomTable.getModel();
        homeroomModel.setRowCount(0);
        DefaultTableModel communityModel = (DefaultTableModel) communityTable.getModel();
        communityModel.setRowCount(0);
        DefaultTableModel medicationModel = (DefaultTableModel) medicationTable.getModel();
        medicationModel.setRowCount(0);
        DefaultTableModel guardianModel = (DefaultTableModel) guardianTable.getModel();
        guardianModel.setRowCount(0);
        DefaultTableModel showByQuarterModel = (DefaultTableModel) byQuarterTable.getModel();
        showByQuarterModel.setRowCount(0);
        DefaultTableModel showByMonthAndYearModel = (DefaultTableModel) byMonthAndYearTable.getModel();
        showByMonthAndYearModel.setRowCount(0);
        DefaultTableModel showByDayOfTheWeekModel = (DefaultTableModel) byDayOfTheWeekTable.getModel();
        showByDayOfTheWeekModel.setRowCount(0);
        DefaultTableModel showByTimeOfDayModel = (DefaultTableModel) byTimeOfDayTable.getModel();
        showByTimeOfDayModel.setRowCount(0);
        try {
            showStudents();
            showTeachers();
            showFocusReports();
            showHomerooms();
            showCommunities();
            showMedication();
            showGuardians();
            showByQuarter();
            showByMonthAndYear();
            showByDayOfTheWeek();
            showByTimeOfDay();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_globalRefreshButtonActionPerformed

    private void addMedicationButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMedicationButtonActionPerformed
      JTextField studentIDField = new JTextField(10);
      JTextField clinicalNameField = new JTextField(15);
      JTextField brandNameField = new JTextField(15);
      JTextField dosageField = new JTextField(8);
      JTextField sideEffectsField = new JTextField(15);
      JTextField administeredField = new JTextField(15);
      JTextField medicationIDField = new JTextField(15);
      Object[] inputFields = {"Student ID:", studentIDField, "Clinical Name:", clinicalNameField,
                                "Brand Name:", brandNameField, "Dosage:", dosageField, 
                                "Side Effects: ", sideEffectsField, "Administered:", administeredField,
                                "Medication ID:", medicationIDField};
      

      int result = JOptionPane.showConfirmDialog(null, inputFields, 
               "New Medication", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        PreparedStatement ps = null;
        try{
             String query = "INSERT INTO Medication (S_ID, Clinical_Name, Brand_Name, " +
                     "Dosage, Side_Effects, ADM_HS, M_ID) " + 
                     " VALUES(?, ?, ?, ?, ?, ?, ?);";
             ps = con.prepareStatement(query);
             ps.setString(1, studentIDField.getText());
             ps.setString(2, clinicalNameField.getText());
             ps.setString(3, brandNameField.getText());
             ps.setString(4, dosageField.getText());
             ps.setString(5, sideEffectsField.getText());
             ps.setString(6, administeredField.getText());
             ps.setString(7, medicationIDField.getText());
             ps.executeUpdate();
             JOptionPane.showMessageDialog(null, "Success");
             
         }
        catch(HeadlessException | SQLException e){
             JOptionPane.showMessageDialog(null, "Please check all fields and try again.");
        }
        finally{
            try {
                ps.close();
            } 
            catch (SQLException ex) {
                Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
      }
    }//GEN-LAST:event_addMedicationButtonActionPerformed
    
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DatabaseGUI2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DatabaseGUI2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DatabaseGUI2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DatabaseGUI2.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new DatabaseGUI2().setVisible(true);
                } catch (SQLException ex) {
                    Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addMedicationButton;
    private javax.swing.JButton addStudentButton;
    private javax.swing.JButton addTeacherButton;
    private javax.swing.JPanel byDayOfTheWeekPanel;
    private javax.swing.JTable byDayOfTheWeekTable;
    private javax.swing.JPanel byMonthAndYearPanel;
    private javax.swing.JTable byMonthAndYearTable;
    private javax.swing.JPanel byQuarterPanel;
    private javax.swing.JTable byQuarterTable;
    private javax.swing.JPanel byTimeOfDayPanel;
    private javax.swing.JTable byTimeOfDayTable;
    private javax.swing.JLabel communityNameOverviewLabel;
    private javax.swing.JLabel communityNumberOverviewLabel;
    private javax.swing.JPanel communityOverviewPanel;
    private javax.swing.JPanel communityPanel;
    private javax.swing.JTable communityTable;
    private javax.swing.JButton createFocusReportButton;
    private javax.swing.JTextField dateFrSearchField;
    private javax.swing.JButton focusReportRefreshButton;
    private javax.swing.JButton focusReportSearchButton;
    private javax.swing.JPanel focusReportsPanel;
    private javax.swing.JTable focusReportsTable;
    private javax.swing.JButton globalRefreshButton;
    private javax.swing.JTabbedPane guardianPanel;
    private javax.swing.JTable guardianTable;
    private javax.swing.JPanel homeroomOverviewPanel;
    private javax.swing.JPanel homeroomPanel;
    private javax.swing.JLabel homeroomRoomNoLabel;
    private javax.swing.JTable homeroomTable;
    private javax.swing.JTextField homeroomTeacherIDTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JScrollPane jScrollPane11;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JPanel medicationPanel;
    private javax.swing.JTable medicationTable;
    private javax.swing.JPanel mostFrOverviewPanel;
    private javax.swing.JTextField studentIDMedicationTextField;
    private javax.swing.JButton studentIDSearchButton;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JButton studentIdFilterButtonGuardians;
    private javax.swing.JTextField studentIdFrSearchField;
    private javax.swing.JTextField studentIdGuardianSearchTextField;
    private javax.swing.JLabel studentIdOverviewLabel;
    private javax.swing.JLabel studentNameOverviewLabel;
    private javax.swing.JPanel studentOverviewPanel;
    private javax.swing.JButton studentResetButton;
    private javax.swing.JButton studentSearchButtonMedTable;
    private javax.swing.JTable studentTable;
    private javax.swing.JPanel studentsPanel;
    private javax.swing.JTextField teacherFrSearchField;
    private javax.swing.JButton teacherIDSearchButton;
    private javax.swing.JTextField teacherIDTextField;
    private javax.swing.JLabel teacherIdOverviewLabel;
    private javax.swing.JLabel teacherNameOverviewLabel;
    private javax.swing.JPanel teacherOverviewPanel;
    private javax.swing.JButton teacherResetButton;
    private javax.swing.JTable teacherTable;
    private javax.swing.JPanel teachersPanel;
    private javax.swing.JTextField timeInFrSearchField;
    // End of variables declaration//GEN-END:variables
}
