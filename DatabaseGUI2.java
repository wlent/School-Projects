/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.HeadlessException;
import java.awt.event.KeyEvent; //for prevent copy and paste (press crtl, alt, shift key)
import java.awt.event.KeyAdapter; //add function input validation
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
        sort_guardian_table();
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
                        "group by dayname(Date) ORDER BY Count(dayname(Date)) DESC;";
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
                        "group by month(Date), year(Date) ORDER BY Number DESC;";
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
                        "group by Quarter ORDER BY count(Quarter) DESC;";
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
                    row[5] = count.getInt("COUNT_SID");
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
                    row[4] = count.getInt("COUNT_TID");
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
    public Class<?> getColumnClass(int columnIndex){
        return Integer.class;
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
    
    public void sort_guardian_table(){
        DefaultTableModel model = (DefaultTableModel) guardianTable.getModel();
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<DefaultTableModel>(model);
        guardianTable.setRowSorter(sorter);
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
        studentsPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        studentIDTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        studentTable = new javax.swing.JTable();
        studentIDSearchButton = new javax.swing.JButton();
        studentResetButton = new javax.swing.JButton();
        addStudentButton = new javax.swing.JButton();
        studentIdDisplayTextField = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        firstNameDisplayTextField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        lastNameDisplayTextField = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        DOBDisplayTextField = new javax.swing.JTextField();
        jLabel22 = new javax.swing.JLabel();
        studentTableUpdateButton = new javax.swing.JButton();
        homeroomDisplayTextField = new javax.swing.JTextField();
        jLabel23 = new javax.swing.JLabel();
        teachersPanel = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        teacherIDTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        teacherTable = new javax.swing.JTable();
        teacherIDSearchButton = new javax.swing.JButton();
        teacherResetButton = new javax.swing.JButton();
        addTeacherButton = new javax.swing.JButton();
        teacherIdDisplayTextField = new javax.swing.JTextField();
        teacherFirstNameDisplayTextField = new javax.swing.JTextField();
        teacherLastNameDisplayTextField = new javax.swing.JTextField();
        teacherSubjectDisplayTextField = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        teacherUpdateButton = new javax.swing.JButton();
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
        sidFrDisplayTextField = new javax.swing.JTextField();
        tidFrDisplayTextField = new javax.swing.JTextField();
        timeInFrDisplayTextField = new javax.swing.JTextField();
        timeOutFrDisplayTextField = new javax.swing.JTextField();
        dateFrDisplayTextField = new javax.swing.JTextField();
        jScrollPane12 = new javax.swing.JScrollPane();
        teacherDescriptionFrDisplayTextArea = new javax.swing.JTextArea();
        jScrollPane13 = new javax.swing.JScrollPane();
        studentResponseFrDisplayTextArea = new javax.swing.JTextArea();
        typeFrDisplayTextField = new javax.swing.JTextField();
        debriefFrDisplayTextField = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel32 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        focusReportsUpdateButton = new javax.swing.JButton();
        communityPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        communityTable = new javax.swing.JTable();
        homeroomPanel = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        homeroomNoTextField = new javax.swing.JTextField();
        jScrollPane5 = new javax.swing.JScrollPane();
        homeroomTable = new javax.swing.JTable();
        HomeroomSearchButton = new javax.swing.JButton();
        HomeroomRefreshButton = new javax.swing.JButton();
        medicationPanel = new javax.swing.JPanel();
        jLabel7 = new javax.swing.JLabel();
        studentIDMedicationTextField = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        medicationTable = new javax.swing.JTable();
        studentSearchButtonMedTable = new javax.swing.JButton();
        addMedicationButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        medicationIDMedicationTextField = new javax.swing.JTextField();
        medicationRefreshButton = new javax.swing.JButton();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        sidMDisplayTextField = new javax.swing.JTextField();
        cNameMDisplayTextField = new javax.swing.JTextField();
        bNameMDisplayTextField = new javax.swing.JTextField();
        doseMDisplayTextField = new javax.swing.JTextField();
        sideEffectsMDisplayTextField = new javax.swing.JTextField();
        medicationUpdateButton = new javax.swing.JButton();
        jLabel42 = new javax.swing.JLabel();
        adminsteredMDisplayTextField = new javax.swing.JTextField();
        jLabel43 = new javax.swing.JLabel();
        midMDisplayTextField = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        studentIdGuardianSearchTextField = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        firstNameGuardianSearchTextField = new javax.swing.JTextField();
        studentIdFilterButtonGuardians = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        guardianTable = new javax.swing.JTable();
        addGuardianButton = new javax.swing.JButton();
        guardianRefreshButton = new javax.swing.JButton();
        jLabel44 = new javax.swing.JLabel();
        jLabel45 = new javax.swing.JLabel();
        jLabel46 = new javax.swing.JLabel();
        jLabel47 = new javax.swing.JLabel();
        jLabel48 = new javax.swing.JLabel();
        jLabel49 = new javax.swing.JLabel();
        jLabel50 = new javax.swing.JLabel();
        fNameGDisplayTextField = new javax.swing.JTextField();
        lNameGDisplayTextField = new javax.swing.JTextField();
        sidGDisplayTextField = new javax.swing.JTextField();
        phoneGDisplayTextField = new javax.swing.JTextField();
        addressGDisplayTextField = new javax.swing.JTextField();
        emailGDisplayTextField = new javax.swing.JTextField();
        relationshipGDisplayTextField = new javax.swing.JTextField();
        guardianUpdateButton = new javax.swing.JButton();
        globalRefreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
                .addContainerGap(110, Short.MAX_VALUE))
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
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(72, Short.MAX_VALUE))
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

        jLabel1.setText("Student ID");

        studentIDTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentIDTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentIDTextFieldKeyTyped(evt);
            }
        });

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "First Name", "Last Name", "D.O.B.", "Homeroom", "# of Focus Reports", "Community", "Common FR Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        studentTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                studentTableMouseClicked(evt);
            }
        });
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

        studentIdDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIdDisplayTextFieldActionPerformed(evt);
            }
        });
        studentIdDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentIdDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentIdDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel13.setText("Student ID");

        firstNameDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                firstNameDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                firstNameDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel20.setText("First Name");

        lastNameDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lastNameDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lastNameDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel21.setText("Last Name");

        DOBDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DOBDisplayTextFieldActionPerformed(evt);
            }
        });
        DOBDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                DOBDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                DOBDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel22.setText("D.O.B.");

        studentTableUpdateButton.setText("Update");
        studentTableUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentTableUpdateButtonActionPerformed(evt);
            }
        });

        homeroomDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                homeroomDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                homeroomDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel23.setText("Homeroom");

        javax.swing.GroupLayout studentsPanelLayout = new javax.swing.GroupLayout(studentsPanel);
        studentsPanel.setLayout(studentsPanelLayout);
        studentsPanelLayout.setHorizontalGroup(
            studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1510, Short.MAX_VALUE)
                    .addGroup(studentsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(studentsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(studentIDSearchButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(studentResetButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addStudentButton))
                            .addGroup(studentsPanelLayout.createSequentialGroup()
                                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(studentIdDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel13))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(firstNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel20))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lastNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel21))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(DOBDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel22))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel23)
                                    .addComponent(homeroomDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(studentTableUpdateButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jLabel20)
                    .addComponent(jLabel21)
                    .addComponent(jLabel22)
                    .addComponent(jLabel23))
                .addGap(8, 8, 8)
                .addGroup(studentsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(studentIdDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(firstNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lastNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(DOBDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(homeroomDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(studentTableUpdateButton)
                .addContainerGap(178, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Students", studentsPanel);

        jLabel2.setText("Teacher ID");

        teacherIDTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherIDTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherIDTextFieldKeyTyped(evt);
            }
        });

        teacherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Teacher ID", "First Name", "Last Name", "Subject", "# of Focus Reports", "Common FR Type"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        teacherTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                teacherTableMouseClicked(evt);
            }
        });
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

        teacherIdDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherIdDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherIdDisplayTextFieldKeyTyped(evt);
            }
        });

        teacherFirstNameDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherFirstNameDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherFirstNameDisplayTextFieldKeyTyped(evt);
            }
        });

        teacherLastNameDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherLastNameDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherLastNameDisplayTextFieldKeyTyped(evt);
            }
        });

        teacherSubjectDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherSubjectDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherSubjectDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel24.setText("TeacherID");

        jLabel25.setText("First Name");

        jLabel26.setText("Last Name");

        jLabel27.setText("Subject");

        teacherUpdateButton.setText("Update");
        teacherUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherUpdateButtonActionPerformed(evt);
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
                        .addContainerGap(1157, Short.MAX_VALUE))
                    .addGroup(teachersPanelLayout.createSequentialGroup()
                        .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(teachersPanelLayout.createSequentialGroup()
                                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(teacherIdDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel24))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(teacherFirstNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel25))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(teacherLastNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel26))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel27)
                                    .addComponent(teacherSubjectDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(teacherUpdateButton))
                        .addGap(0, 0, Short.MAX_VALUE))))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(jLabel25)
                    .addComponent(jLabel26)
                    .addComponent(jLabel27))
                .addGap(2, 2, 2)
                .addGroup(teachersPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(teacherIdDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teacherFirstNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teacherLastNameDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teacherSubjectDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(teacherUpdateButton)
                .addContainerGap(184, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Teachers", teachersPanel);

        jLabel3.setText("Student ID");

        studentIdFrSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIdFrSearchFieldActionPerformed(evt);
            }
        });
        studentIdFrSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentIdFrSearchFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentIdFrSearchFieldKeyTyped(evt);
            }
        });

        jLabel4.setText("Time In");

        timeInFrSearchField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeInFrSearchFieldActionPerformed(evt);
            }
        });
        timeInFrSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                timeInFrSearchFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                timeInFrSearchFieldKeyTyped(evt);
            }
        });

        jLabel5.setText("Date");

        dateFrSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dateFrSearchFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dateFrSearchFieldKeyTyped(evt);
            }
        });

        focusReportsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Teacher ID", "Time In", "Time Out", "Date", "Teacher Description", "Student Response", "Type", "Debrief", "Quarter"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        focusReportsTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                focusReportsTableMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(focusReportsTable);

        focusReportSearchButton.setText("Filter");
        focusReportSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusReportSearchButtonActionPerformed(evt);
            }
        });

        createFocusReportButton.setText("Add");
        createFocusReportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                createFocusReportButtonActionPerformed(evt);
            }
        });

        focusReportRefreshButton.setText("Reset");
        focusReportRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusReportRefreshButtonActionPerformed(evt);
            }
        });

        jLabel8.setText("Teacher ID");

        teacherFrSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherFrSearchFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherFrSearchFieldKeyTyped(evt);
            }
        });

        sidFrDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sidFrDisplayTextFieldActionPerformed(evt);
            }
        });
        sidFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sidFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sidFrDisplayTextFieldKeyTyped(evt);
            }
        });

        tidFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tidFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                tidFrDisplayTextFieldKeyTyped(evt);
            }
        });

        timeInFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                timeInFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                timeInFrDisplayTextFieldKeyTyped(evt);
            }
        });

        timeOutFrDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                timeOutFrDisplayTextFieldActionPerformed(evt);
            }
        });
        timeOutFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                timeOutFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                timeOutFrDisplayTextFieldKeyTyped(evt);
            }
        });

        dateFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                dateFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                dateFrDisplayTextFieldKeyTyped(evt);
            }
        });

        teacherDescriptionFrDisplayTextArea.setColumns(20);
        teacherDescriptionFrDisplayTextArea.setRows(5);
        teacherDescriptionFrDisplayTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherDescriptionFrDisplayTextAreaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherDescriptionFrDisplayTextAreaKeyTyped(evt);
            }
        });
        jScrollPane12.setViewportView(teacherDescriptionFrDisplayTextArea);

        studentResponseFrDisplayTextArea.setColumns(20);
        studentResponseFrDisplayTextArea.setRows(5);
        studentResponseFrDisplayTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentResponseFrDisplayTextAreaKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentResponseFrDisplayTextAreaKeyTyped(evt);
            }
        });
        jScrollPane13.setViewportView(studentResponseFrDisplayTextArea);

        typeFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                typeFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                typeFrDisplayTextFieldKeyTyped(evt);
            }
        });

        debriefFrDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                debriefFrDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                debriefFrDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel28.setText("Student ID");

        jLabel29.setText("Teacher ID");

        jLabel30.setText("Time In");

        jLabel31.setText("Time Out");

        jLabel32.setText("Date");

        jLabel33.setText("Teacher Description");

        jLabel34.setText("Student Response");

        jLabel35.setText("Type");

        jLabel36.setText("Debrief");

        focusReportsUpdateButton.setText("Update");
        focusReportsUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                focusReportsUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout focusReportsPanelLayout = new javax.swing.GroupLayout(focusReportsPanel);
        focusReportsPanel.setLayout(focusReportsPanelLayout);
        focusReportsPanelLayout.setHorizontalGroup(
            focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(focusReportsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane3))
                    .addGroup(focusReportsPanelLayout.createSequentialGroup()
                        .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(sidFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel28))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel29)
                                    .addComponent(tidFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel30)
                                    .addComponent(timeInFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel31)
                                    .addComponent(timeOutFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, Short.MAX_VALUE)
                                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel32)
                                    .addComponent(dateFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(focusReportsUpdateButton))
                        .addGap(18, 18, 18)
                        .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, focusReportsPanelLayout.createSequentialGroup()
                                .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18))
                            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel33)
                                .addGap(104, 104, 104)))
                        .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel34)
                            .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                                .addComponent(jLabel35)
                                .addGap(148, 148, 148)
                                .addComponent(jLabel36))
                            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                                .addComponent(typeFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(debriefFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(11, 11, 11))
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
                        .addGap(6, 6, 6)
                        .addComponent(focusReportRefreshButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(createFocusReportButton)
                        .addGap(288, 288, 288)))
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
                .addGap(8, 8, 8)
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30)
                    .addComponent(jLabel31)
                    .addComponent(jLabel32)
                    .addComponent(jLabel33)
                    .addComponent(jLabel34)
                    .addComponent(jLabel35)
                    .addComponent(jLabel36))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane13, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(typeFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(debriefFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(focusReportsPanelLayout.createSequentialGroup()
                        .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sidFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tidFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timeInFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(timeOutFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dateFrDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(36, 36, 36)
                        .addComponent(focusReportsUpdateButton))
                    .addComponent(jScrollPane12, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Focus Reports", focusReportsPanel);

        communityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Community Name", "Leader ID", "# of Focus Reports"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(communityTable);

        javax.swing.GroupLayout communityPanelLayout = new javax.swing.GroupLayout(communityPanel);
        communityPanel.setLayout(communityPanelLayout);
        communityPanelLayout.setHorizontalGroup(
            communityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 557, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(982, Short.MAX_VALUE))
        );
        communityPanelLayout.setVerticalGroup(
            communityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityPanelLayout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(262, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Community", communityPanel);

        jLabel6.setText("Room Number:");

        homeroomNoTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeroomNoTextFieldActionPerformed(evt);
            }
        });

        homeroomTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Room Number", "Teacher", "Community", "# of Focus Reports"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane5.setViewportView(homeroomTable);

        HomeroomSearchButton.setText("Filter");
        HomeroomSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeroomSearchButtonActionPerformed(evt);
            }
        });

        HomeroomRefreshButton.setText("Reset");
        HomeroomRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                HomeroomRefreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout homeroomPanelLayout = new javax.swing.GroupLayout(homeroomPanel);
        homeroomPanel.setLayout(homeroomPanelLayout);
        homeroomPanelLayout.setHorizontalGroup(
            homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeroomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 558, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(homeroomPanelLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(homeroomNoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HomeroomSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(HomeroomRefreshButton)))
                .addContainerGap(981, Short.MAX_VALUE))
        );
        homeroomPanelLayout.setVerticalGroup(
            homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeroomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeroomPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(homeroomNoTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(HomeroomSearchButton)
                    .addComponent(HomeroomRefreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(261, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Homeroom", homeroomPanel);

        jLabel7.setText("Student ID");

        studentIDMedicationTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIDMedicationTextFieldActionPerformed(evt);
            }
        });
        studentIDMedicationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentIDMedicationTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentIDMedicationTextFieldKeyTyped(evt);
            }
        });

        medicationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Clinical Name", "Brand Name", "Dosage", "Side Effects", "Adminstered", "Medication ID"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        medicationTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                medicationTableMouseClicked(evt);
            }
        });
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

        jLabel12.setText("Medication ID");

        medicationIDMedicationTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                medicationIDMedicationTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                medicationIDMedicationTextFieldKeyTyped(evt);
            }
        });

        medicationRefreshButton.setText("Reset");
        medicationRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medicationRefreshButtonActionPerformed(evt);
            }
        });

        jLabel37.setText("Student ID");

        jLabel38.setText("Clinical Name");

        jLabel39.setText("Brand Name");

        jLabel40.setText("Dosage");

        jLabel41.setText("Side Effects");

        sidMDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sidMDisplayTextFieldActionPerformed(evt);
            }
        });
        sidMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sidMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sidMDisplayTextFieldKeyTyped(evt);
            }
        });

        cNameMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cNameMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                cNameMDisplayTextFieldKeyTyped(evt);
            }
        });

        bNameMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                bNameMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                bNameMDisplayTextFieldKeyTyped(evt);
            }
        });

        doseMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                doseMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                doseMDisplayTextFieldKeyTyped(evt);
            }
        });

        sideEffectsMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sideEffectsMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sideEffectsMDisplayTextFieldKeyTyped(evt);
            }
        });

        medicationUpdateButton.setText("Update");
        medicationUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                medicationUpdateButtonActionPerformed(evt);
            }
        });

        jLabel42.setText("Administered");

        adminsteredMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                adminsteredMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                adminsteredMDisplayTextFieldKeyTyped(evt);
            }
        });

        jLabel43.setText("Medication ID");

        midMDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                midMDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                midMDisplayTextFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout medicationPanelLayout = new javax.swing.GroupLayout(medicationPanel);
        medicationPanel.setLayout(medicationPanelLayout);
        medicationPanelLayout.setHorizontalGroup(
            medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(medicationPanelLayout.createSequentialGroup()
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 1502, Short.MAX_VALUE)
                            .addGroup(medicationPanelLayout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(studentIDMedicationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(medicationIDMedicationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(studentSearchButtonMedTable)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(addMedicationButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(medicationRefreshButton)
                                .addGap(0, 0, Short.MAX_VALUE)))
                        .addContainerGap())
                    .addGroup(medicationPanelLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel37)
                            .addComponent(medicationUpdateButton)
                            .addComponent(sidMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel38)
                            .addComponent(cNameMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel39)
                            .addComponent(bNameMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(doseMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel40))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel41)
                            .addComponent(sideEffectsMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(medicationPanelLayout.createSequentialGroup()
                                .addComponent(jLabel42)
                                .addGap(62, 62, 62)
                                .addComponent(jLabel43))
                            .addGroup(medicationPanelLayout.createSequentialGroup()
                                .addComponent(adminsteredMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(midMDisplayTextField)))
                        .addContainerGap(766, Short.MAX_VALUE))))
        );
        medicationPanelLayout.setVerticalGroup(
            medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(studentIDMedicationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentSearchButtonMedTable)
                    .addComponent(addMedicationButton)
                    .addComponent(jLabel12)
                    .addComponent(medicationIDMedicationTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(medicationRefreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel37)
                    .addComponent(jLabel38)
                    .addComponent(jLabel39)
                    .addComponent(jLabel40)
                    .addComponent(jLabel41)
                    .addComponent(jLabel42)
                    .addComponent(jLabel43))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(sidMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cNameMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(bNameMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(doseMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sideEffectsMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(adminsteredMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(midMDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(medicationUpdateButton)
                .addContainerGap(164, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Medication", medicationPanel);

        jLabel9.setText("Student ID");

        studentIdGuardianSearchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentIdGuardianSearchTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentIdGuardianSearchTextFieldKeyTyped(evt);
            }
        });

        jLabel10.setText("First Name");

        firstNameGuardianSearchTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                firstNameGuardianSearchTextFieldActionPerformed(evt);
            }
        });
        firstNameGuardianSearchTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                firstNameGuardianSearchTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                firstNameGuardianSearchTextFieldKeyTyped(evt);
            }
        });

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
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, true, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        guardianTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                guardianTableMouseClicked(evt);
            }
        });
        jScrollPane7.setViewportView(guardianTable);

        addGuardianButton.setText("Add");
        addGuardianButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addGuardianButtonActionPerformed(evt);
            }
        });

        guardianRefreshButton.setText("Reset");
        guardianRefreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardianRefreshButtonActionPerformed(evt);
            }
        });

        jLabel44.setText("First Name");

        jLabel45.setText("Last Name");

        jLabel46.setText("Student ID");

        jLabel47.setText("Phone");

        jLabel48.setText("Address");

        jLabel49.setText("Email");

        jLabel50.setText("Relationship");

        fNameGDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fNameGDisplayTextFieldActionPerformed(evt);
            }
        });
        fNameGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                fNameGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                fNameGDisplayTextFieldKeyTyped(evt);
            }
        });

        lNameGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                lNameGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lNameGDisplayTextFieldKeyTyped(evt);
            }
        });

        sidGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                sidGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                sidGDisplayTextFieldKeyTyped(evt);
            }
        });

        phoneGDisplayTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                phoneGDisplayTextFieldActionPerformed(evt);
            }
        });
        phoneGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                phoneGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                phoneGDisplayTextFieldKeyTyped(evt);
            }
        });

        addressGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                addressGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                addressGDisplayTextFieldKeyTyped(evt);
            }
        });

        emailGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                emailGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                emailGDisplayTextFieldKeyTyped(evt);
            }
        });

        relationshipGDisplayTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                relationshipGDisplayTextFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                relationshipGDisplayTextFieldKeyTyped(evt);
            }
        });

        guardianUpdateButton.setText("Update");
        guardianUpdateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                guardianUpdateButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 1522, Short.MAX_VALUE)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(firstNameGuardianSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel9)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(studentIdGuardianSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(studentIdFilterButtonGuardians)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addGuardianButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(guardianRefreshButton))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(fNameGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel44))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel45)
                            .addComponent(lNameGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(jLabel46)
                                .addGap(80, 80, 80)
                                .addComponent(jLabel47))
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addComponent(sidGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(phoneGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(6, 6, 6)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(addressGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel48))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(emailGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel49))
                        .addGap(11, 11, 11)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel50)
                            .addComponent(relationshipGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(guardianUpdateButton))
                .addContainerGap(348, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(firstNameGuardianSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(studentIdGuardianSearchTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentIdFilterButtonGuardians)
                    .addComponent(addGuardianButton)
                    .addComponent(guardianRefreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel44)
                    .addComponent(jLabel45)
                    .addComponent(jLabel46)
                    .addComponent(jLabel47)
                    .addComponent(jLabel48)
                    .addComponent(jLabel49)
                    .addComponent(jLabel50))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(fNameGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lNameGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sidGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(phoneGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addressGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(emailGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(relationshipGDisplayTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(guardianUpdateButton)
                .addContainerGap(159, Short.MAX_VALUE))
        );

        guardianPanel.addTab("Guardians", jPanel2);

        globalRefreshButton.setText("Refresh All");
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
                    .addComponent(guardianPanel)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(globalRefreshButton)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(55, 55, 55))
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
        String query2 = "SELECT Community\n" +
                        "FROM Student s, Homeroom h\n" +
                        "WHERE s.Room_No = h.Room_No\n" +
                        "AND S_ID = ";
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
        Object row[] = new Object[8];
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
        Object row[] = new Object[6];
        for(int i = 0; i < list.size(); i++){
            String teacherToCount = list.get(i).getT_ID();
            try{
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
            try{
                Statement st2 = con.createStatement();
                ResultSet rs = st2.executeQuery("Select MaxTypeGroups.T_ID, Type From\n" +
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
        Object row[] = new Object[10];
        for(int i = 0; i < list.size(); i++){
            Statement st = null;
            ResultSet rs = null;
            row[0] = list.get(i).getS_ID();
            row[1] = list.get(i).getT_ID();
            row[2] = list.get(i).getTime_In();
            row[3] = list.get(i).getTime_Out();
            row[4] = list.get(i).getDate();
            row[5] = list.get(i).getTeacher_Description();
            row[6] = list.get(i).getStudent_Response();
            row[7] = list.get(i).getType();
            row[8] = list.get(i).getComm_Leader_Debrief();
            String query = "Select  \n" +
                        "CASE quarter(Date)\n" +
                        "    WHEN 1 THEN \"3rd Quarter\"\n" +
                        "    WHEN 2 THEN \"4th Quarter\"\n" +
                        "    WHEN 3 THEN \"1st Quarter\"\n" +
                        "    WHEN 4 THEN \"2nd Quarter\"\n" +
                        "END as Quarter\n" +
                        "FROM Focus_Report\n" +
                        "WHERE S_ID = ";
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
            model.addRow(row);
        }
    }//GEN-LAST:event_focusReportSearchButtonActionPerformed

    private void homeroomNoTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeroomNoTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_homeroomNoTextFieldActionPerformed

    private void addStudentButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addStudentButtonActionPerformed
      JTextField studentIDField = new JTextField(9);
      JTextField fNameField = new JTextField(15);
      JTextField lNameField = new JTextField(15);
      JTextField DOBField = new JTextField(8);
      JTextField homeroomField = new JTextField(3);
      
      //Jack's input validation code begin
      studentIDField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(studentIDField.getText().length() >= 9){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      fNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(fNameField.getText().length() >= 45){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      lNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(lNameField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      DOBField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == '-') || (enter >='0') && (enter <= '9'))){
                    evt.consume();
                }
                if(DOBField.getText().length() >= 10)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      homeroomField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(homeroomField.getText().length() >= 3)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      //Jack's input validation code end

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

      //Jack's input validation code begin
      teacherIDField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(teacherIDField.getText().length() >= 9){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      fNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(fNameField.getText().length() >= 45){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      lNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(lNameField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      subjectField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(subjectField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
             public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      //Jack's input validation code end
      
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
      
      //Jack's input validation code begin
      studentIDField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(studentIDField.getText().length() >= 9){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      teacherIDField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(teacherIDField.getText().length() >= 8){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      timeInField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ':') || Character.isDigit(enter))){
                    evt.consume();
                }
                if(timeInField.getText().length() >= 8)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      timeOutField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ':') || Character.isDigit(enter))){
                    evt.consume();
                }
                if(timeOutField.getText().length() >= 8)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      dateField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == '-') || Character.isDigit(enter))){
                    evt.consume();
                }
                if(dateField.getText().length() >= 10)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      teacherDescriptionArea.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
                    Character.isAlphabetic(enter) || Character.isDigit(enter)))
                {    
                    evt.consume();
                }
                if(teacherDescriptionArea.getText().length() >= 140)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      studentResponseArea.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
                    Character.isAlphabetic(enter) || Character.isDigit(enter)))
                {    
                    evt.consume();
                }
                if(studentResponseArea.getText().length() >= 140)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
     typeField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(typeField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
     
      commLeaderDebriefArea.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
                    Character.isAlphabetic(enter) || Character.isDigit(enter)))
                {    
                    evt.consume();
                }
                if(commLeaderDebriefArea.getText().length() >= 140)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      //Jack's input validation code end
      
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
            rs = st.executeQuery("SELECT * FROM Medication WHERE S_ID LIKE '%" + studentIDMedicationTextField.getText() + 
                    "%' AND M_ID LIKE '%"+ medicationIDMedicationTextField.getText()+"%'");
        
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
            rs = st.executeQuery("SELECT * FROM Guardian WHERE F_name LIKE '%" 
                    + firstNameGuardianSearchTextField.getText() + "%' AND S_ID LIKE '%" + studentIdGuardianSearchTextField.getText() + "%'");
        
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
      
      //Jack's input validation code begin
      studentIDField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(studentIDField.getText().length() >= 9){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      clinicalNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(clinicalNameField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
            
      brandNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(brandNameField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
                  
      dosageField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter) || Character.isDigit(enter))){
                    evt.consume();
                }
                if(dosageField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
                        
      sideEffectsField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(sideEffectsField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
                              
      administeredField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!((enter == ' ') || Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(administeredField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
                                    
      medicationIDField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(medicationIDField.getText().length() >= 4){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      //Jack's input validation code end
      
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

    private void firstNameGuardianSearchTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_firstNameGuardianSearchTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_firstNameGuardianSearchTextFieldActionPerformed

    private void studentIDTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIDTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_studentIDTextFieldKeyPressed

    private void studentIDTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIDTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(studentIDTextField.getText().length() >= 9){
            evt.consume();
        }
    }//GEN-LAST:event_studentIDTextFieldKeyTyped

    private void teacherIDTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherIDTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherIDTextFieldKeyPressed

    private void teacherIDTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherIDTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(teacherIDTextField.getText().length() >= 8){
            evt.consume();
        }
    }//GEN-LAST:event_teacherIDTextFieldKeyTyped

    private void studentIdFrSearchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdFrSearchFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_studentIdFrSearchFieldKeyPressed

    private void studentIdFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdFrSearchFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(studentIdFrSearchField.getText().length() >= 9){
            evt.consume();
        }
    }//GEN-LAST:event_studentIdFrSearchFieldKeyTyped

    private void teacherFrSearchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherFrSearchFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherFrSearchFieldKeyPressed

    private void teacherFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherFrSearchFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(teacherFrSearchField.getText().length() >= 8){
            evt.consume();
        }
    }//GEN-LAST:event_teacherFrSearchFieldKeyTyped

    private void timeInFrSearchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeInFrSearchFieldKeyPressed
        
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //shift is needed for press ':' colon key
        {           
            
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_timeInFrSearchFieldKeyPressed

    private void timeInFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeInFrSearchFieldKeyTyped
        // only accept integer and ':' colon for TimeIn serach
        char enter = evt.getKeyChar();

        if(!((enter == ':') || (enter >='0') && (enter <= '9'))){
            evt.consume();
        }
        
        if(timeInFrSearchField.getText().length() >= 8){
            evt.consume();
        }
    }//GEN-LAST:event_timeInFrSearchFieldKeyTyped

    private void dateFrSearchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFrSearchFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_dateFrSearchFieldKeyPressed

    private void dateFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFrSearchFieldKeyTyped
        // only accept integer and '-'hyphen
        char enter = evt.getKeyChar();
        if(!((enter == '-') || (enter >='0') && (enter <= '9'))){
            evt.consume();
        }
        
        if(dateFrSearchField.getText().length() >= 10){
            evt.consume();
        }
    }//GEN-LAST:event_dateFrSearchFieldKeyTyped

    private void firstNameGuardianSearchTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameGuardianSearchTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_firstNameGuardianSearchTextFieldKeyPressed

    private void firstNameGuardianSearchTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameGuardianSearchTextFieldKeyTyped
        // only accept Alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        
        if(firstNameGuardianSearchTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_firstNameGuardianSearchTextFieldKeyTyped

    private void studentIdGuardianSearchTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdGuardianSearchTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_studentIdGuardianSearchTextFieldKeyPressed

    private void studentIdGuardianSearchTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdGuardianSearchTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(studentIdGuardianSearchTextField.getText().length() >= 9){
            evt.consume();
        }
    }//GEN-LAST:event_studentIdGuardianSearchTextFieldKeyTyped

    private void studentIDMedicationTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIDMedicationTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_studentIDMedicationTextFieldKeyPressed

    private void studentIDMedicationTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIDMedicationTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(studentIDMedicationTextField.getText().length() >= 9){
            evt.consume();
        }
    }//GEN-LAST:event_studentIDMedicationTextFieldKeyTyped

    private void medicationIDMedicationTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_medicationIDMedicationTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_medicationIDMedicationTextFieldKeyPressed

    private void medicationIDMedicationTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_medicationIDMedicationTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(medicationIDMedicationTextField.getText().length() >= 4){
            evt.consume();
        }
    }//GEN-LAST:event_medicationIDMedicationTextFieldKeyTyped

    private void studentTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_studentTableMouseClicked
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        int selectedRowIndex = studentTable.getSelectedRow();
        
        studentIdDisplayTextField.setText(model.getValueAt(selectedRowIndex, 0).toString());
        firstNameDisplayTextField.setText(model.getValueAt(selectedRowIndex, 1).toString());
        lastNameDisplayTextField.setText(model.getValueAt(selectedRowIndex, 2).toString());
        DOBDisplayTextField.setText(model.getValueAt(selectedRowIndex, 3).toString());
        homeroomDisplayTextField.setText(model.getValueAt(selectedRowIndex, 4).toString());
    }//GEN-LAST:event_studentTableMouseClicked

    private void studentTableUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentTableUpdateButtonActionPerformed
        PreparedStatement ps = null;
        int row = studentTable.getSelectedRow();
        String Sid = studentTable.getModel().getValueAt(row, 0).toString();
        String query = "UPDATE Student SET S_ID = ?, F_Name = ?, L_Name = ?, DOB = ?, Room_No = ? WHERE S_ID = " + Sid;
        try{
            ps = con.prepareStatement(query);
            ps.setString(1, studentIdDisplayTextField.getText());
            ps.setString(2, firstNameDisplayTextField.getText());
            ps.setString(3, lastNameDisplayTextField.getText());
            ps.setString(4, DOBDisplayTextField.getText());
            ps.setString(5, homeroomDisplayTextField.getText());
            ps.executeUpdate();
            DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
            model.setRowCount(0);
            showStudents();
        }
        catch(Exception e){
            System.out.println("Error with Update");
        }
        finally{
            if(ps != null){
                try {
                    ps.close();
                }
            
                catch (SQLException ex) {
                    Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_studentTableUpdateButtonActionPerformed

    private void teacherTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_teacherTableMouseClicked
        DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
        int selectedRowIndex = teacherTable.getSelectedRow();
        
        teacherIdDisplayTextField.setText(model.getValueAt(selectedRowIndex, 0).toString());
        teacherFirstNameDisplayTextField.setText(model.getValueAt(selectedRowIndex, 1).toString());
        teacherLastNameDisplayTextField.setText(model.getValueAt(selectedRowIndex, 2).toString());
        teacherSubjectDisplayTextField.setText(model.getValueAt(selectedRowIndex, 3).toString());
    }//GEN-LAST:event_teacherTableMouseClicked

    private void teacherUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teacherUpdateButtonActionPerformed
        PreparedStatement ps = null;
        int row = teacherTable.getSelectedRow();
        String Tid = teacherTable.getModel().getValueAt(row, 0).toString();
        String query = "UPDATE Teacher SET T_ID = ?, F_Name = ?, L_Name = ?, Subject = ? WHERE T_ID = " + Tid;
        try{
            ps = con.prepareStatement(query);
            ps.setString(1, teacherIdDisplayTextField.getText());
            ps.setString(2, teacherFirstNameDisplayTextField.getText());
            ps.setString(3, teacherLastNameDisplayTextField.getText());
            ps.setString(4, teacherSubjectDisplayTextField.getText());
            ps.executeUpdate();
            DefaultTableModel model = (DefaultTableModel) teacherTable.getModel();
            model.setRowCount(0);
            showTeachers();
        }
        catch(Exception e){
            System.out.println("Error with Update");
        }
        finally{
            if(ps != null){
                try {
                    ps.close();
                }
            
                catch (SQLException ex) {
                    Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_teacherUpdateButtonActionPerformed

    private void timeOutFrDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeOutFrDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timeOutFrDisplayTextFieldActionPerformed

    private void focusReportsTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_focusReportsTableMouseClicked
        DefaultTableModel model = (DefaultTableModel) focusReportsTable.getModel();
        int selectedRowIndex = focusReportsTable.getSelectedRow();
        teacherDescriptionFrDisplayTextArea.setLineWrap(true);
        studentResponseFrDisplayTextArea.setLineWrap(true);
        
        sidFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 0).toString());
        tidFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 1).toString());
        timeInFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 2).toString());
        timeOutFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 3).toString());
        dateFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 4).toString());
        teacherDescriptionFrDisplayTextArea.setText(model.getValueAt(selectedRowIndex, 5).toString());
        studentResponseFrDisplayTextArea.setText(model.getValueAt(selectedRowIndex, 6).toString());
        typeFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 7).toString());
        debriefFrDisplayTextField.setText(model.getValueAt(selectedRowIndex, 8).toString());
    }//GEN-LAST:event_focusReportsTableMouseClicked

    private void addGuardianButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addGuardianButtonActionPerformed
      JTextField firstNameField = new JTextField(10);
      JTextField lastNameField = new JTextField(15);
      JTextField sidField = new JTextField(15);
      JTextField phoneField = new JTextField(8);
      JTextField addressField = new JTextField(15);
      JTextField emailField = new JTextField(15);
      JTextField relationshipField = new JTextField(15);
      
      //Jack's input validation code begin
      firstNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(firstNameField.getText().length() >= 45){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste(Ctrl+V)
                {
                    evt.consume();
                }
            }
      });
      
      lastNameField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter))){
                    evt.consume();
                }
                if(lastNameField.getText().length() >= 45){
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      sidField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(sidField.getText().length() >= 9)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      phoneField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(phoneField.getText().length() >= 10)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      addressField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter) || Character.isAlphabetic(enter) || (enter == '.') || (enter == ',') || (enter == ' '))){
                    evt.consume();
                }
                if(addressField.getText().length() >= 140)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      
      emailField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter) || Character.isAlphabetic(enter) || (enter == '.') || (enter == '@') || (enter == '-') || (enter == '_'))){
                    evt.consume();
                }
                if(emailField.getText().length() >= 140)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
            
      relationshipField.addKeyListener(new KeyAdapter(){
            public void keyTyped(KeyEvent evt) {  
                char enter = evt.getKeyChar();
                if(!(Character.isAlphabetic(enter) || (enter == ' '))){
                    evt.consume();
                }
                if(relationshipField.getText().length() >= 45)
                {
                    evt.consume();
                }
            }
            
            public void keyPressed(KeyEvent evt){
                if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V) //prevent paste
                {
                    evt.consume();
                }
            }
      });
      //Jack's input validation code end
      
      
      Object[] inputFields = {"First Name:", firstNameField, "Last Name:", lastNameField,
                                "Student ID:", sidField, "Phone:", phoneField, 
                                "Address: ", addressField, "Email:", emailField,
                                "Realtionship:", relationshipField};
      

      int result = JOptionPane.showConfirmDialog(null, inputFields, 
               "New Guardian", JOptionPane.OK_CANCEL_OPTION);
      if (result == JOptionPane.OK_OPTION) {
        PreparedStatement ps = null;
        try{
             String query = "INSERT INTO Guardian (F_Name, L_Name, S_ID, " +
                     "Phone, Address, Email, Relationship) " + 
                     " VALUES(?, ?, ?, ?, ?, ?, ?);";
             ps = con.prepareStatement(query);
             ps.setString(1, firstNameField.getText());
             ps.setString(2, lastNameField.getText());
             ps.setString(3, sidField.getText());
             ps.setString(4, phoneField.getText());
             ps.setString(5, addressField.getText());
             ps.setString(6, emailField.getText());
             ps.setString(7, relationshipField.getText());
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
    }//GEN-LAST:event_addGuardianButtonActionPerformed

    private void focusReportsUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_focusReportsUpdateButtonActionPerformed
        PreparedStatement ps = null;
        int row = focusReportsTable.getSelectedRow();
        String sid = focusReportsTable.getModel().getValueAt(row, 0).toString();
        String tid = focusReportsTable.getModel().getValueAt(row, 1).toString();
        String timeIn = focusReportsTable.getModel().getValueAt(row, 2).toString();
        String date = focusReportsTable.getModel().getValueAt(row, 4).toString();
        String query = "UPDATE Focus_Report SET S_ID = ?, T_ID = ?, Time_In = ?, "
        + " Time_Out = ?, Date = ?, Teacher_Description = ?, Student_Response = ?, "
        + " Type = ?, Comm_Leader_Debrief = ? WHERE S_ID = " + sid + " AND T_ID = " + tid +
        " AND Time_In = '" + timeIn + "' AND Date = '" + date + "'";
        try{
            ps = con.prepareStatement(query);
            ps.setString(1, sidFrDisplayTextField.getText());
            ps.setString(2, tidFrDisplayTextField.getText());
            ps.setString(3, timeInFrDisplayTextField.getText());
            ps.setString(4, timeOutFrDisplayTextField.getText());
            ps.setString(5, dateFrDisplayTextField.getText());
            ps.setString(6, teacherDescriptionFrDisplayTextArea.getText());
            ps.setString(7, studentResponseFrDisplayTextArea.getText());
            ps.setString(8, typeFrDisplayTextField.getText());
            ps.setString(9, debriefFrDisplayTextField.getText());
            ps.executeUpdate();
            DefaultTableModel model = (DefaultTableModel) focusReportsTable.getModel();
            model.setRowCount(0);
            showFocusReports();
        }
        catch(Exception e){
            System.out.println("Error with Update");
        }
        finally{
            if(ps != null){
                try {
                    ps.close();
                }

                catch (SQLException ex) {
                    Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_focusReportsUpdateButtonActionPerformed

    private void sidFrDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sidFrDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sidFrDisplayTextFieldActionPerformed

    private void guardianRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardianRefreshButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) guardianTable.getModel();
        model.setRowCount(0);
        try {
            showGuardians();
        } 
        catch(SQLException ex) {
            Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_guardianRefreshButtonActionPerformed

    private void medicationRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medicationRefreshButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) medicationTable.getModel();
        model.setRowCount(0);
        try {
            showMedication();
        } 
        catch(SQLException ex) {
            Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_medicationRefreshButtonActionPerformed

    private void studentIDMedicationTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentIDMedicationTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_studentIDMedicationTextFieldActionPerformed

    private void sidMDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sidMDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_sidMDisplayTextFieldActionPerformed

    private void medicationTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_medicationTableMouseClicked
        DefaultTableModel model = (DefaultTableModel) medicationTable.getModel();
        int selectedRowIndex = medicationTable.getSelectedRow();

        sidMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 0).toString());
        cNameMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 1).toString());
        bNameMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 2).toString());
        doseMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 3).toString());
        sideEffectsMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 4).toString());
        adminsteredMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 5).toString());
        midMDisplayTextField.setText(model.getValueAt(selectedRowIndex, 6).toString());
       
    }//GEN-LAST:event_medicationTableMouseClicked

    private void medicationUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_medicationUpdateButtonActionPerformed
        PreparedStatement ps = null;
        int row = medicationTable.getSelectedRow();
        String sid = medicationTable.getModel().getValueAt(row, 0).toString();
        String mid = medicationTable.getModel().getValueAt(row, 6).toString();
        String query = "UPDATE Medication SET S_ID = ?, Clinical_Name = ?, Brand_Name = ?, "
        + " Dosage = ?, Side_Effects = ?, ADM_HS = ?, M_ID = ? "
        + " WHERE S_ID = " + sid + " AND M_ID = " + mid;
        try{
            ps = con.prepareStatement(query);
            ps.setString(1, sidMDisplayTextField.getText());
            ps.setString(2, cNameMDisplayTextField.getText());
            ps.setString(3, bNameMDisplayTextField.getText());
            ps.setString(4, doseMDisplayTextField.getText());
            ps.setString(5, sideEffectsMDisplayTextField.getText());
            ps.setString(6, adminsteredMDisplayTextField.getText());
            ps.setString(7, midMDisplayTextField.getText());
            ps.executeUpdate();
            DefaultTableModel model = (DefaultTableModel) medicationTable.getModel();
            model.setRowCount(0);
            showMedication();
        }
        catch(Exception e){
            System.out.println("Error with Update");
        }
        finally{
            if(ps != null){
                try {
                    ps.close();
                }

                catch (SQLException ex) {
                    Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_medicationUpdateButtonActionPerformed

    private void studentIdFrSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentIdFrSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_studentIdFrSearchFieldActionPerformed

    private void timeInFrSearchFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_timeInFrSearchFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_timeInFrSearchFieldActionPerformed

    private void sidFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sidFrDisplayTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        if(sidFrDisplayTextField.getText().length() >= 9){
            evt.consume();
        }
    }//GEN-LAST:event_sidFrDisplayTextFieldKeyTyped

    private void studentIdDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdDisplayTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        if(studentIdDisplayTextField.getText().length() >= 9){
            evt.consume();
        }
        
    }//GEN-LAST:event_studentIdDisplayTextFieldKeyTyped

    private void firstNameDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameDisplayTextFieldKeyTyped
        // only accept alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(firstNameDisplayTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_firstNameDisplayTextFieldKeyTyped

    private void studentIdDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_studentIdDisplayTextFieldKeyPressed

    private void HomeroomSearchButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeroomSearchButtonActionPerformed
        ArrayList<Homeroom> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        DefaultTableModel model = (DefaultTableModel) homeroomTable.getModel();
        model.setRowCount(0);
        
        
        try{
            st = con.createStatement();
            rs = st.executeQuery("select * from Homeroom where Room_No like'%"+homeroomNoTextField.getText()+"%'");
 
            while(rs.next()){
                String Room_No = rs.getString(1);
                String T_ID = rs.getString(2);
                String Community = rs.getString(3);
                Homeroom homeroom = new Homeroom(Room_No, T_ID, Community);
                list.add(homeroom);
                                  
            }
                
        } catch(Exception e){ 
            e.printStackTrace();
        }  
        Object[] row = new Object[4];
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
            
            row[0] = list.get(i).getRoomNo();
            row[1] = list.get(i).getTID();
            row[2] = list.get(i).getCommunity();
            model.addRow(row);
        }
    }//GEN-LAST:event_HomeroomSearchButtonActionPerformed

    private void firstNameDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_firstNameDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_firstNameDisplayTextFieldKeyPressed

    private void lastNameDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameDisplayTextFieldKeyTyped
        // only accept alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(lastNameDisplayTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_lastNameDisplayTextFieldKeyTyped

    private void lastNameDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lastNameDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_lastNameDisplayTextFieldKeyPressed

    private void DOBDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DOBDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == '-') || (enter >='0') && (enter <= '9'))){
            evt.consume();
        }
        if(DOBDisplayTextField.getText().length() >= 10)
        {
            evt.consume();
        }
    }//GEN-LAST:event_DOBDisplayTextFieldKeyTyped

    private void DOBDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_DOBDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_DOBDisplayTextFieldKeyPressed

    private void homeroomDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_homeroomDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        if(homeroomDisplayTextField.getText().length() >= 3)
        {
            evt.consume();
        }
    }//GEN-LAST:event_homeroomDisplayTextFieldKeyTyped

    private void homeroomDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_homeroomDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_homeroomDisplayTextFieldKeyPressed

    private void teacherIdDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherIdDisplayTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        if(teacherIdDisplayTextField.getText().length() >= 8){
            evt.consume();
        }
    }//GEN-LAST:event_teacherIdDisplayTextFieldKeyTyped

    private void teacherIdDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherIdDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherIdDisplayTextFieldKeyPressed

    private void teacherFirstNameDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherFirstNameDisplayTextFieldKeyTyped
        // only accept alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(teacherFirstNameDisplayTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_teacherFirstNameDisplayTextFieldKeyTyped

    private void teacherFirstNameDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherFirstNameDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherFirstNameDisplayTextFieldKeyPressed

    private void teacherLastNameDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherLastNameDisplayTextFieldKeyTyped
        // only accept alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(teacherLastNameDisplayTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_teacherLastNameDisplayTextFieldKeyTyped

    private void teacherLastNameDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherLastNameDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherLastNameDisplayTextFieldKeyPressed

    private void teacherSubjectDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherSubjectDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(teacherSubjectDisplayTextField.getText().length() >= 45)
        {
            evt.consume();
        }
    }//GEN-LAST:event_teacherSubjectDisplayTextFieldKeyTyped

    private void teacherSubjectDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherSubjectDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherSubjectDisplayTextFieldKeyPressed

    private void sidFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sidFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_sidFrDisplayTextFieldKeyPressed

    private void tidFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tidFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_tidFrDisplayTextFieldKeyPressed

    private void tidFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tidFrDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        if(tidFrDisplayTextField.getText().length() >= 8)
        {
            evt.consume();
        }
    }//GEN-LAST:event_tidFrDisplayTextFieldKeyTyped

    private void timeInFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeInFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_timeInFrDisplayTextFieldKeyPressed

    private void timeInFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeInFrDisplayTextFieldKeyTyped
                char enter = evt.getKeyChar();
                if(!((enter == ':') || Character.isDigit(enter))){
                    evt.consume();
                }
                if(timeInFrDisplayTextField.getText().length() >= 8)
                {
                    evt.consume();
                }
    }//GEN-LAST:event_timeInFrDisplayTextFieldKeyTyped

    private void timeOutFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeOutFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_timeOutFrDisplayTextFieldKeyPressed

    private void timeOutFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeOutFrDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ':') || Character.isDigit(enter))){
            evt.consume();
        }
        if(timeOutFrDisplayTextField.getText().length() >= 8)
        {
            evt.consume();
        }
    }//GEN-LAST:event_timeOutFrDisplayTextFieldKeyTyped

    private void dateFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_dateFrDisplayTextFieldKeyPressed

    private void dateFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFrDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == '-') || Character.isDigit(enter))){
            evt.consume();
        }
        if(dateFrDisplayTextField.getText().length() >= 10)
        {
            evt.consume();
        }
    }//GEN-LAST:event_dateFrDisplayTextFieldKeyTyped

    private void teacherDescriptionFrDisplayTextAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherDescriptionFrDisplayTextAreaKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_teacherDescriptionFrDisplayTextAreaKeyPressed

    private void teacherDescriptionFrDisplayTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherDescriptionFrDisplayTextAreaKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
        Character.isAlphabetic(enter) || Character.isDigit(enter)))
        {    
            evt.consume();
        }
        if(teacherDescriptionFrDisplayTextArea.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_teacherDescriptionFrDisplayTextAreaKeyTyped

    private void studentResponseFrDisplayTextAreaKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentResponseFrDisplayTextAreaKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_studentResponseFrDisplayTextAreaKeyPressed

    private void studentResponseFrDisplayTextAreaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentResponseFrDisplayTextAreaKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
        Character.isAlphabetic(enter) || Character.isDigit(enter)))
        {    
            evt.consume();
        }
        if(studentResponseFrDisplayTextArea.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_studentResponseFrDisplayTextAreaKeyTyped

    private void typeFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_typeFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_typeFrDisplayTextFieldKeyPressed

    private void typeFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_typeFrDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
        Character.isAlphabetic(enter) || Character.isDigit(enter)))
        {    
            evt.consume();
        }
        if(typeFrDisplayTextField.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_typeFrDisplayTextFieldKeyTyped

    private void debriefFrDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_debriefFrDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_debriefFrDisplayTextFieldKeyPressed

    private void debriefFrDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_debriefFrDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || (enter == ':') || (enter == '/') || (enter == '?') || (enter == '.') || (enter == ',') || (enter == '"') ||
        Character.isAlphabetic(enter) || Character.isDigit(enter)))
        {    
            evt.consume();
        }
        if(debriefFrDisplayTextField.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_debriefFrDisplayTextFieldKeyTyped

    private void sidMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sidMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_sidMDisplayTextFieldKeyPressed

    private void sidMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sidMDisplayTextFieldKeyTyped
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(sidMDisplayTextField.getText().length() >= 9){
                    evt.consume();
                }
    }//GEN-LAST:event_sidMDisplayTextFieldKeyTyped

    private void cNameMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cNameMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_cNameMDisplayTextFieldKeyPressed

    private void cNameMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cNameMDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(cNameMDisplayTextField.getText().length() >= 45)
        {
            evt.consume();
        }
    }//GEN-LAST:event_cNameMDisplayTextFieldKeyTyped

    private void bNameMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bNameMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_bNameMDisplayTextFieldKeyPressed

    private void bNameMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_bNameMDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(bNameMDisplayTextField.getText().length() >= 45)
        {
            evt.consume();
        }
    }//GEN-LAST:event_bNameMDisplayTextFieldKeyTyped

    private void doseMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doseMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_doseMDisplayTextFieldKeyPressed

    private void doseMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_doseMDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter) || Character.isDigit(enter))){
            evt.consume();
        }
        if(doseMDisplayTextField.getText().length() >= 45)
        {
            evt.consume();
        }
    }//GEN-LAST:event_doseMDisplayTextFieldKeyTyped

    private void sideEffectsMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sideEffectsMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_sideEffectsMDisplayTextFieldKeyPressed

    private void sideEffectsMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sideEffectsMDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(sideEffectsMDisplayTextField.getText().length() >= 45)
        {
            evt.consume();
        }
    }//GEN-LAST:event_sideEffectsMDisplayTextFieldKeyTyped

    private void adminsteredMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adminsteredMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_adminsteredMDisplayTextFieldKeyPressed

    private void adminsteredMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_adminsteredMDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter))){
            evt.consume();
        }
        if(sideEffectsMDisplayTextField.getText().length() >= 45)
        {
            evt.consume();
        }
    }//GEN-LAST:event_adminsteredMDisplayTextFieldKeyTyped

    private void midMDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_midMDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_midMDisplayTextFieldKeyPressed

    private void midMDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_midMDisplayTextFieldKeyTyped
                char enter = evt.getKeyChar();
                if(!(Character.isDigit(enter))){
                    evt.consume();
                }
                if(midMDisplayTextField.getText().length() >= 4){
                    evt.consume();
                }
    }//GEN-LAST:event_midMDisplayTextFieldKeyTyped

    private void studentIdDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentIdDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_studentIdDisplayTextFieldActionPerformed

    private void HomeroomRefreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_HomeroomRefreshButtonActionPerformed
        DefaultTableModel model = (DefaultTableModel) homeroomTable.getModel();
        model.setRowCount(0);
        try {
            showHomerooms();
        } catch (SQLException ex) {

        }
    }//GEN-LAST:event_HomeroomRefreshButtonActionPerformed

    private void fNameGDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fNameGDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fNameGDisplayTextFieldActionPerformed

    private void guardianTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_guardianTableMouseClicked
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) guardianTable.getModel();
        int selectedRowIndex = guardianTable.getSelectedRow();
        
        
        fNameGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 0).toString());
        lNameGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 1).toString());
        sidGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 2).toString());
        phoneGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 3).toString());
        addressGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 4).toString());
        emailGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 5).toString());
        relationshipGDisplayTextField.setText(model.getValueAt(selectedRowIndex, 6).toString());
    }//GEN-LAST:event_guardianTableMouseClicked

    private void guardianUpdateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_guardianUpdateButtonActionPerformed
        PreparedStatement ps = null;
        int row = guardianTable.getSelectedRow();
        String fName = guardianTable.getModel().getValueAt(row, 0).toString();
        String sid = guardianTable.getModel().getValueAt(row, 2).toString();
        String query = "UPDATE Guardian SET F_Name = ?, L_Name = ?, S_ID = ?, "
        + " Phone = ?, Address = ?, Email = ?, Relationship = ? "
        + " WHERE S_ID = " + sid + " AND F_Name = '" + fName + "'";
        try{
            ps = con.prepareStatement(query);
            ps.setString(1, fNameGDisplayTextField.getText());
            ps.setString(2, lNameGDisplayTextField.getText());
            ps.setString(3, sidGDisplayTextField.getText());
            ps.setString(4, phoneGDisplayTextField.getText());
            ps.setString(5, addressGDisplayTextField.getText());
            ps.setString(6, emailGDisplayTextField.getText());
            ps.setString(7, relationshipGDisplayTextField.getText());
            ps.executeUpdate();
            DefaultTableModel model = (DefaultTableModel) guardianTable.getModel();
            model.setRowCount(0);
            showGuardians();
        }
        catch(Exception e){
            System.out.println("Error with Update");
        }
        finally{
            if(ps != null){
                try {
                    ps.close();
                }

                catch (SQLException ex) {
                    Logger.getLogger(DatabaseGUI2.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }//GEN-LAST:event_guardianUpdateButtonActionPerformed

    private void fNameGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fNameGDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_fNameGDisplayTextFieldKeyPressed

    private void fNameGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_fNameGDisplayTextFieldKeyTyped
        // only accept Alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        
        if(fNameGDisplayTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_fNameGDisplayTextFieldKeyTyped

    private void lNameGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lNameGDisplayTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_lNameGDisplayTextFieldKeyPressed

    private void lNameGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lNameGDisplayTextFieldKeyTyped
        // only accept Alphabetic
        char enter = evt.getKeyChar();
        if(!(Character.isAlphabetic(enter))){
            evt.consume();
        }
        
        if(lNameGDisplayTextField.getText().length() >= 45){
            evt.consume();
        }
    }//GEN-LAST:event_lNameGDisplayTextFieldKeyTyped

    private void sidGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sidGDisplayTextFieldKeyPressed
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_sidGDisplayTextFieldKeyPressed

    private void sidGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_sidGDisplayTextFieldKeyTyped
        // TODO add your handling code here:
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        
        if(sidGDisplayTextField.getText().length() >= 9){
            evt.consume();
        }
    }//GEN-LAST:event_sidGDisplayTextFieldKeyTyped

    private void phoneGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneGDisplayTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_phoneGDisplayTextFieldKeyPressed

    private void phoneGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_phoneGDisplayTextFieldKeyTyped
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
        if(phoneGDisplayTextField.getText().length() >= 10)
        {
            evt.consume();
        }
    }//GEN-LAST:event_phoneGDisplayTextFieldKeyTyped

    private void addressGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addressGDisplayTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_addressGDisplayTextFieldKeyPressed

    private void addressGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_addressGDisplayTextFieldKeyTyped
        // TODO add your handling code here:
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || (enter == '.') || (enter == ',') ||
        Character.isAlphabetic(enter) || Character.isDigit(enter)))
        {    
            evt.consume();
        }
        if(addressGDisplayTextField.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_addressGDisplayTextFieldKeyTyped

    private void emailGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailGDisplayTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_emailGDisplayTextFieldKeyPressed

    private void emailGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_emailGDisplayTextFieldKeyTyped
        // TODO add your handling code here:
        char enter = evt.getKeyChar();
        if(!((enter == '_') || (enter == '.') || (enter == '@') || (enter == '-') ||
        Character.isAlphabetic(enter) || Character.isDigit(enter)))
        {    
            evt.consume();
        }
        if(emailGDisplayTextField.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_emailGDisplayTextFieldKeyTyped

    private void relationshipGDisplayTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_relationshipGDisplayTextFieldKeyPressed
        // TODO add your handling code here:
        if(evt.isControlDown()) //prevent further action when ctrl pressed
        {
            evt.consume();
        }
        
        if(evt.isShiftDown()) //prevent further action when shift pressed
        {
            evt.consume();
        }
        
        if(evt.isAltDown()) //prevent further action when alt pressed
        {
            evt.consume();  
        }
    }//GEN-LAST:event_relationshipGDisplayTextFieldKeyPressed

    private void relationshipGDisplayTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_relationshipGDisplayTextFieldKeyTyped
        // TODO add your handling code here:
        char enter = evt.getKeyChar();
        if(!((enter == ' ') || Character.isAlphabetic(enter) ))
        {    
            evt.consume();
        }
        if(relationshipGDisplayTextField.getText().length() >= 140)
        {
            evt.consume();
        }
    }//GEN-LAST:event_relationshipGDisplayTextFieldKeyTyped

    private void phoneGDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_phoneGDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_phoneGDisplayTextFieldActionPerformed

    private void DOBDisplayTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DOBDisplayTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_DOBDisplayTextFieldActionPerformed
    
    
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
    private javax.swing.JTextField DOBDisplayTextField;
    private javax.swing.JButton HomeroomRefreshButton;
    private javax.swing.JButton HomeroomSearchButton;
    private javax.swing.JButton addGuardianButton;
    private javax.swing.JButton addMedicationButton;
    private javax.swing.JButton addStudentButton;
    private javax.swing.JButton addTeacherButton;
    private javax.swing.JTextField addressGDisplayTextField;
    private javax.swing.JTextField adminsteredMDisplayTextField;
    private javax.swing.JTextField bNameMDisplayTextField;
    private javax.swing.JPanel byDayOfTheWeekPanel;
    private javax.swing.JTable byDayOfTheWeekTable;
    private javax.swing.JPanel byMonthAndYearPanel;
    private javax.swing.JTable byMonthAndYearTable;
    private javax.swing.JPanel byQuarterPanel;
    private javax.swing.JTable byQuarterTable;
    private javax.swing.JPanel byTimeOfDayPanel;
    private javax.swing.JTable byTimeOfDayTable;
    private javax.swing.JTextField cNameMDisplayTextField;
    private javax.swing.JLabel communityNameOverviewLabel;
    private javax.swing.JLabel communityNumberOverviewLabel;
    private javax.swing.JPanel communityOverviewPanel;
    private javax.swing.JPanel communityPanel;
    private javax.swing.JTable communityTable;
    private javax.swing.JButton createFocusReportButton;
    private javax.swing.JTextField dateFrDisplayTextField;
    private javax.swing.JTextField dateFrSearchField;
    private javax.swing.JTextField debriefFrDisplayTextField;
    private javax.swing.JTextField doseMDisplayTextField;
    private javax.swing.JTextField emailGDisplayTextField;
    private javax.swing.JTextField fNameGDisplayTextField;
    private javax.swing.JTextField firstNameDisplayTextField;
    private javax.swing.JTextField firstNameGuardianSearchTextField;
    private javax.swing.JButton focusReportRefreshButton;
    private javax.swing.JButton focusReportSearchButton;
    private javax.swing.JPanel focusReportsPanel;
    private javax.swing.JTable focusReportsTable;
    private javax.swing.JButton focusReportsUpdateButton;
    private javax.swing.JButton globalRefreshButton;
    private javax.swing.JTabbedPane guardianPanel;
    private javax.swing.JButton guardianRefreshButton;
    private javax.swing.JTable guardianTable;
    private javax.swing.JButton guardianUpdateButton;
    private javax.swing.JTextField homeroomDisplayTextField;
    private javax.swing.JTextField homeroomNoTextField;
    private javax.swing.JPanel homeroomOverviewPanel;
    private javax.swing.JPanel homeroomPanel;
    private javax.swing.JLabel homeroomRoomNoLabel;
    private javax.swing.JTable homeroomTable;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel44;
    private javax.swing.JLabel jLabel45;
    private javax.swing.JLabel jLabel46;
    private javax.swing.JLabel jLabel47;
    private javax.swing.JLabel jLabel48;
    private javax.swing.JLabel jLabel49;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel50;
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
    private javax.swing.JScrollPane jScrollPane12;
    private javax.swing.JScrollPane jScrollPane13;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JTextField lNameGDisplayTextField;
    private javax.swing.JTextField lastNameDisplayTextField;
    private javax.swing.JTextField medicationIDMedicationTextField;
    private javax.swing.JPanel medicationPanel;
    private javax.swing.JButton medicationRefreshButton;
    private javax.swing.JTable medicationTable;
    private javax.swing.JButton medicationUpdateButton;
    private javax.swing.JTextField midMDisplayTextField;
    private javax.swing.JPanel mostFrOverviewPanel;
    private javax.swing.JTextField phoneGDisplayTextField;
    private javax.swing.JTextField relationshipGDisplayTextField;
    private javax.swing.JTextField sidFrDisplayTextField;
    private javax.swing.JTextField sidGDisplayTextField;
    private javax.swing.JTextField sidMDisplayTextField;
    private javax.swing.JTextField sideEffectsMDisplayTextField;
    private javax.swing.JTextField studentIDMedicationTextField;
    private javax.swing.JButton studentIDSearchButton;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JTextField studentIdDisplayTextField;
    private javax.swing.JButton studentIdFilterButtonGuardians;
    private javax.swing.JTextField studentIdFrSearchField;
    private javax.swing.JTextField studentIdGuardianSearchTextField;
    private javax.swing.JLabel studentIdOverviewLabel;
    private javax.swing.JLabel studentNameOverviewLabel;
    private javax.swing.JPanel studentOverviewPanel;
    private javax.swing.JButton studentResetButton;
    private javax.swing.JTextArea studentResponseFrDisplayTextArea;
    private javax.swing.JButton studentSearchButtonMedTable;
    private javax.swing.JTable studentTable;
    private javax.swing.JButton studentTableUpdateButton;
    private javax.swing.JPanel studentsPanel;
    private javax.swing.JTextArea teacherDescriptionFrDisplayTextArea;
    private javax.swing.JTextField teacherFirstNameDisplayTextField;
    private javax.swing.JTextField teacherFrSearchField;
    private javax.swing.JButton teacherIDSearchButton;
    private javax.swing.JTextField teacherIDTextField;
    private javax.swing.JTextField teacherIdDisplayTextField;
    private javax.swing.JLabel teacherIdOverviewLabel;
    private javax.swing.JTextField teacherLastNameDisplayTextField;
    private javax.swing.JLabel teacherNameOverviewLabel;
    private javax.swing.JPanel teacherOverviewPanel;
    private javax.swing.JButton teacherResetButton;
    private javax.swing.JTextField teacherSubjectDisplayTextField;
    private javax.swing.JTable teacherTable;
    private javax.swing.JButton teacherUpdateButton;
    private javax.swing.JPanel teachersPanel;
    private javax.swing.JTextField tidFrDisplayTextField;
    private javax.swing.JTextField timeInFrDisplayTextField;
    private javax.swing.JTextField timeInFrSearchField;
    private javax.swing.JTextField timeOutFrDisplayTextField;
    private javax.swing.JTextField typeFrDisplayTextField;
    // End of variables declaration//GEN-END:variables
}
