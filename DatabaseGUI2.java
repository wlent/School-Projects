/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.awt.HeadlessException;
import java.awt.event.KeyEvent; //for prevent copy and paste (ctrl+c, ctrl+x, ctrl+v)
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
                homeroomMaxFrTextField.setText(rs.getString("Room_No"));
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
                teacherMostFrTextField.setText(rs.getString("T_ID"));
                teacherNameMostFrTextField.setText(rs.getString("F_Name") + " " + rs.getString("L_Name"));
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
                studentNameMostFrTextField.setText(rs.getString("F_Name")+ " " + rs.getString("L_Name"));
                studentIDMostFrTextField.setText(rs.getString("S_ID"));
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
                communityNameMostFrTextField.setText(rs.getString("Community_Name"));
                communityAmountMostFrTextField.setText(rs.getString("COUNT_SID"));
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
                rs.getString("Brand Name"), rs.getString("Dosage"), rs.getString("Side_Effects"),
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
    
    public final void showStudents() throws SQLException{ //displays the full student table
        ArrayList<Student> list = studentList();
        DefaultTableModel model = (DefaultTableModel) studentTable.getModel();
        PreparedStatement preparedStatement = null;
        String query = "SELECT COUNT(S_ID) AS COUNT_SID " +
                                                "FROM Focus_Report " +
                                                "WHERE S_ID = ?";
        ResultSet count = null;
        ResultSet rs = null;
        Statement st = null;
        Object row[] = new Object[6];
        
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
                model.addRow(row);
            }
        
            catch(Exception e){
                System.out.println("Error with count");
                } 
            finally{
                try{if(count != null)count.close();} catch(Exception e){};
                try{if(preparedStatement != null)preparedStatement.close();} catch(Exception e){};
                
            }
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
            try{ //try-catch to display the count of focus reports
                String teacherToCount = list.get(i).getT_ID();
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
                                        "WHERE T_ID = " + list.get(i).getT_ID() +
                                        " GROUP BY T_ID, Type) AS MaxTypeGroups, (SELECT T_ID, MAX(COUNT_T) as MAX_COUNT\n" +
                                        "FROM (SELECT T_ID, Type, count(Type) as COUNT_T\n" +
                                        "            FROM Focus_Report                                        GROUP BY T_ID, Type) AS A\n" +
                                        "        GROUP BY T_ID) AS TeachersMaxCount\n" +
                                        "WHERE MaxTypeGroups.T_ID = TeachersMaxCount.T_ID AND COUNT_T = MAX_COUNT;");
                
                if(rs.first()){
                    row[5] = rs.getString("Type");
                }
            }
            catch(Exception e){
                System.out.println("DA FUQ");
            }
            finally{
                if(rs != null) rs.close();
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

        jTabbedPane1 = new javax.swing.JTabbedPane();
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
        studentIDMedicationtextField = new javax.swing.JTextField();
        jScrollPane6 = new javax.swing.JScrollPane();
        medicationTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        mostFrOverviewPanel = new javax.swing.JPanel();
        teacherOverviewPanel = new javax.swing.JPanel();
        jLabel14 = new javax.swing.JLabel();
        teacherNameMostFrTextField = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        teacherMostFrTextField = new javax.swing.JTextField();
        studentOverviewPanel = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        studentNameMostFrTextField = new javax.swing.JTextField();
        jLabel17 = new javax.swing.JLabel();
        studentIDMostFrTextField = new javax.swing.JTextField();
        communityOverviewPanel = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        communityNameMostFrTextField = new javax.swing.JTextField();
        communityAmountMostFrTextField = new javax.swing.JTextField();
        homeroomOverviewPanel = new javax.swing.JPanel();
        homeroomMaxFrTextField = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Student ID");

        studentIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIDTextFieldActionPerformed(evt);
            }
        });
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
                "Student ID", "First Name", "Last Name", "D.O.B.", "Homeroom", "# of Focus Reports"
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
                .addContainerGap(647, Short.MAX_VALUE))
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 637, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
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
                .addContainerGap(201, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Students", studentsPanel);

        jLabel2.setText("Teacher ID");

        teacherIDTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherIDTextFieldActionPerformed(evt);
            }
        });
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
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 532, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(teachersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherResetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addTeacherButton)))
                .addContainerGap(465, Short.MAX_VALUE))
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
                .addContainerGap(201, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Teachers", teachersPanel);

        jLabel3.setText("Student ID");

        studentIdFrSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                studentIdFrSearchFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                studentIdFrSearchFieldKeyTyped(evt);
            }
        });

        jLabel4.setText("Time In");

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

        teacherFrSearchField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                teacherFrSearchFieldKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teacherFrSearchFieldKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout focusReportsPanelLayout = new javax.swing.GroupLayout(focusReportsPanel);
        focusReportsPanel.setLayout(focusReportsPanelLayout);
        focusReportsPanelLayout.setHorizontalGroup(
            focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE)
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
                .addContainerGap(201, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Focus Reports", focusReportsPanel);

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
                .addContainerGap(544, Short.MAX_VALUE))
        );
        communityPanelLayout.setVerticalGroup(
            communityPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Community", communityPanel);

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
                "Room Number", "Teacher", "Community", "Number of Focus Rooms"
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
                .addContainerGap(544, Short.MAX_VALUE))
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
                .addContainerGap(205, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Homeroom", homeroomPanel);

        jLabel7.setText("Student ID");

        medicationTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Clinical Name", "Brand Name", "Dosage", "Side Effects", "Adminstered", "Medication ID"
            }
        ));
        jScrollPane6.setViewportView(medicationTable);

        javax.swing.GroupLayout medicationPanelLayout = new javax.swing.GroupLayout(medicationPanel);
        medicationPanel.setLayout(medicationPanelLayout);
        medicationPanelLayout.setHorizontalGroup(
            medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 985, Short.MAX_VALUE)
                    .addGroup(medicationPanelLayout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(studentIDMedicationtextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        medicationPanelLayout.setVerticalGroup(
            medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(medicationPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(medicationPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(studentIDMedicationtextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(205, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Medication", medicationPanel);

        mostFrOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Most Focus Reports"));

        teacherOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Teacher"));

        jLabel14.setText("Name");

        jLabel15.setText("ID");

        javax.swing.GroupLayout teacherOverviewPanelLayout = new javax.swing.GroupLayout(teacherOverviewPanel);
        teacherOverviewPanel.setLayout(teacherOverviewPanelLayout);
        teacherOverviewPanelLayout.setHorizontalGroup(
            teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel15)
                    .addComponent(jLabel14))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(teacherNameMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teacherMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(56, Short.MAX_VALUE))
        );
        teacherOverviewPanelLayout.setVerticalGroup(
            teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(teacherOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(teacherNameMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(teacherOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(teacherMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        studentOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Student"));

        jLabel16.setText("Name");

        jLabel17.setText("ID");

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
                    .addComponent(studentNameMostFrTextField)
                    .addComponent(studentIDMostFrTextField))
                .addGap(57, 57, 57))
        );
        studentOverviewPanelLayout.setVerticalGroup(
            studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(studentOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(studentNameMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(studentOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(studentIDMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        communityOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Community"));

        jLabel18.setText("Name");

        jLabel19.setText("Number");

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
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(communityNameMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(communityAmountMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        communityOverviewPanelLayout.setVerticalGroup(
            communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(communityOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(communityNameMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(communityOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(communityAmountMostFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        homeroomOverviewPanel.setBorder(javax.swing.BorderFactory.createTitledBorder("Homeroom"));

        homeroomMaxFrTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeroomMaxFrTextFieldActionPerformed(evt);
            }
        });

        jLabel11.setText("Room #");

        javax.swing.GroupLayout homeroomOverviewPanelLayout = new javax.swing.GroupLayout(homeroomOverviewPanel);
        homeroomOverviewPanel.setLayout(homeroomOverviewPanelLayout);
        homeroomOverviewPanelLayout.setHorizontalGroup(
            homeroomOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, homeroomOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(homeroomMaxFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        homeroomOverviewPanelLayout.setVerticalGroup(
            homeroomOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(homeroomOverviewPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(homeroomOverviewPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(homeroomMaxFrTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(51, Short.MAX_VALUE))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mostFrOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(698, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mostFrOverviewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(172, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Overview", jPanel1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1)
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

    private void homeroomMaxFrTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeroomMaxFrTextFieldActionPerformed
        homeroomMaxFrTextField.setText("hi");
    }//GEN-LAST:event_homeroomMaxFrTextFieldActionPerformed

    private void studentIDTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIDTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
    }//GEN-LAST:event_studentIDTextFieldKeyTyped

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

    private void teacherIDTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherIDTextFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
    }//GEN-LAST:event_teacherIDTextFieldKeyTyped

    private void teacherIDTextFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherIDTextFieldKeyPressed
        /* prevent when ctrl pressed (ctrl+x,c,v)
        if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_C)
        {
            System.out.println("pressed ctrl+c");
            evt.consume(); //prevent ctrl+c
        }
        
        if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_V)
        {
            System.out.println("pressed ctrl+v");
            evt.consume(); //prevent ctrl+v
        }
        
        if(evt.isControlDown() && evt.getKeyCode() == KeyEvent.VK_X)
        {
            System.out.println("pressed ctrl+x");
            evt.consume(); //prevent ctrl+x
        }
        */
        
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

    private void teacherIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_teacherIDTextFieldActionPerformed
        
    }//GEN-LAST:event_teacherIDTextFieldActionPerformed

    private void studentIDTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_studentIDTextFieldActionPerformed
        
    }//GEN-LAST:event_studentIDTextFieldActionPerformed

    private void studentIdFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_studentIdFrSearchFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
    }//GEN-LAST:event_studentIdFrSearchFieldKeyTyped

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

    private void teacherFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teacherFrSearchFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!(Character.isDigit(enter))){
            evt.consume();
        }
    }//GEN-LAST:event_teacherFrSearchFieldKeyTyped

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

    private void timeInFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeInFrSearchFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();

        if(!((enter == ':') || (enter >='0') && (enter <= '9'))){
            evt.consume();
        }

    }//GEN-LAST:event_timeInFrSearchFieldKeyTyped

    private void timeInFrSearchFieldKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_timeInFrSearchFieldKeyPressed
       
        char enter = evt.getKeyChar();
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

    private void dateFrSearchFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dateFrSearchFieldKeyTyped
        // only accept integer
        char enter = evt.getKeyChar();
        if(!((enter == '-') || (enter >='0') && (enter <= '9'))){
            evt.consume();
        }
    }//GEN-LAST:event_dateFrSearchFieldKeyTyped

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
    private javax.swing.JButton addStudentButton;
    private javax.swing.JButton addTeacherButton;
    private javax.swing.JTextField communityAmountMostFrTextField;
    private javax.swing.JTextField communityNameMostFrTextField;
    private javax.swing.JPanel communityOverviewPanel;
    private javax.swing.JPanel communityPanel;
    private javax.swing.JTable communityTable;
    private javax.swing.JButton createFocusReportButton;
    private javax.swing.JTextField dateFrSearchField;
    private javax.swing.JButton focusReportRefreshButton;
    private javax.swing.JButton focusReportSearchButton;
    private javax.swing.JPanel focusReportsPanel;
    private javax.swing.JTable focusReportsTable;
    private javax.swing.JTextField homeroomMaxFrTextField;
    private javax.swing.JPanel homeroomOverviewPanel;
    private javax.swing.JPanel homeroomPanel;
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
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel medicationPanel;
    private javax.swing.JTable medicationTable;
    private javax.swing.JPanel mostFrOverviewPanel;
    private javax.swing.JTextField studentIDMedicationtextField;
    private javax.swing.JTextField studentIDMostFrTextField;
    private javax.swing.JButton studentIDSearchButton;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JTextField studentIdFrSearchField;
    private javax.swing.JTextField studentNameMostFrTextField;
    private javax.swing.JPanel studentOverviewPanel;
    private javax.swing.JButton studentResetButton;
    private javax.swing.JTable studentTable;
    private javax.swing.JPanel studentsPanel;
    private javax.swing.JTextField teacherFrSearchField;
    private javax.swing.JButton teacherIDSearchButton;
    private javax.swing.JTextField teacherIDTextField;
    private javax.swing.JTextField teacherMostFrTextField;
    private javax.swing.JTextField teacherNameMostFrTextField;
    private javax.swing.JPanel teacherOverviewPanel;
    private javax.swing.JButton teacherResetButton;
    private javax.swing.JTable teacherTable;
    private javax.swing.JPanel teachersPanel;
    private javax.swing.JTextField timeInFrSearchField;
    // End of variables declaration//GEN-END:variables
}
