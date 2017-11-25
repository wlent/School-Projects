/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


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

/**
 *
 * @author wlent
 */
public class DatabaseGUI2 extends javax.swing.JFrame {

    /**
     * Creates new form DatabaseGUI2
     */
    public DatabaseGUI2() throws SQLException {
        initComponents();
        showStudents();
        showTeachers();
        showFocusReports();
        showCommunities();
        showHomerooms();
        showMedication();
    }
    
    
    public Connection getConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection connection = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/wlent1db", "wlent1", "Cosc*8pcy");
            return connection;
        }
        
        catch(Exception e){
            System.out.println("Could not connect to database.");
        }
        return null;
    }
    
    private Connection con = getConnection();
    
    
    public ArrayList<Student> studentList() throws SQLException{ //creates an ArrayList based on the student table
        ArrayList<Student> list = new ArrayList<>();
        Statement st = null;
        ResultSet rs = null;
        try{
            st = con.createStatement();
            rs = st.executeQuery("SELECT * FROM Student");
        
            while(rs.next()){
                Student student = new Student(rs.getString("S_ID"), rs.getString("F_Name"), rs.getString("L_Name"), rs.getString("DOB"), rs.getString("Room_No"));
                list.add(student);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        
        finally{
            rs.close();
            st.close();
        }
        return list;
    }
    
    public ArrayList<Teacher> teacherList(){ //creates an ArrayList based on the teacher table
        ArrayList<Teacher> list = new ArrayList<>();
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Teacher");
        
            while(rs.next()){
                Teacher teacher = new Teacher(rs.getString("T_ID"), rs.getString("F_Name"), rs.getString("L_Name"), rs.getString("Subject"));
                list.add(teacher);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        return list;
    }
    
    public ArrayList<FocusReport> FocusReportList(){ //creates an ArrayList based on the FocusReport table
        ArrayList<FocusReport> list = new ArrayList<>();
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Focus_Report");
        
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
        return list;
    }
    
    public ArrayList<Community> CommunityList(){ //creates an ArrayList based on the Community table
        ArrayList<Community> list = new ArrayList<>();
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Community");
        
            while(rs.next()){
                Community community = new Community(rs.getString("Community_Name"), rs.getString("Leader_ID"));
                list.add(community);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        return list;
    }
    
    public ArrayList<Homeroom> HomeroomList(){ //creates an ArrayList based on the Homeroom table
        ArrayList<Homeroom> list = new ArrayList<>();
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Homeroom");
        
            while(rs.next()){
                Homeroom hr = new Homeroom(rs.getString("Room_No"), rs.getString("T_ID"), rs.getString("Community"));
                list.add(hr);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        return list;
    }
    
    public ArrayList<Medication> MedicationList(){ //creates an ArrayList based on the Medication table
        ArrayList<Medication> list = new ArrayList<>();
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Medication");
        
            while(rs.next()){
                Medication med = new Medication(rs.getString("S_ID"), rs.getString("Clinical_Name"), 
                rs.getString("Brand_Name"), rs.getString("Dosage"), rs.getString("Side_Effects"),
                rs.getString("ADM_HS"), rs.getString("M_ID"));
                list.add(med);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
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
        Object row[] = new Object[5];
        Statement st = null;
        ResultSet count = null;
        for(int i = 0; i < list.size(); i++){
            try{
                String teacherToCount = list.get(i).getT_ID();
                st = con.createStatement();
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
                st.close();
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
        
        Object row[] = new Object[4];
        for(int i = 0; i < list.size(); i++){
            try{
                Statement st = con.createStatement();
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
            row[0] = list.get(i).getRoomNo();
            row[1] = list.get(i).getTID();
            row[2] = list.get(i).getCommunity();
            model.addRow(row);
        }
    }
    
    public final void showMedication(){ //displays the full medication table
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
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        focusReportsTable = new javax.swing.JTable();
        focusReportSearchButton = new javax.swing.JButton();
        createFocusReportButton = new javax.swing.JButton();
        focusReportRefreshButton = new javax.swing.JButton();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Student ID");

        studentTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "First Name", "Last Name", "D.O.B.", "Homeroom", "# of Focus Reports"
            }
        ));
        jScrollPane1.setViewportView(studentTable);

        studentIDSearchButton.setText("Search");
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(studentsPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 556, Short.MAX_VALUE))
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

        teacherTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Teacher ID", "First Name", "Last Name", "Subject", "# of Focus Reports"
            }
        ));
        jScrollPane2.setViewportView(teacherTable);

        teacherIDSearchButton.setText("Search");
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
                    .addGroup(teachersPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherResetButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(addTeacherButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(544, Short.MAX_VALUE))
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

        jLabel4.setText("Time In");

        jLabel5.setText("Date");

        focusReportsTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Teacher ID", "Time In", "Time Out", "Date", "Teacher Description", "Student Response", "Type", "Debrief"
            }
        ));
        jScrollPane3.setViewportView(focusReportsTable);

        focusReportSearchButton.setText("Search");
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

        javax.swing.GroupLayout focusReportsPanelLayout = new javax.swing.GroupLayout(focusReportsPanel);
        focusReportsPanel.setLayout(focusReportsPanelLayout);
        focusReportsPanelLayout.setHorizontalGroup(
            focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(focusReportsPanelLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(focusReportSearchButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(createFocusReportButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(focusReportRefreshButton)
                        .addGap(0, 285, Short.MAX_VALUE)))
                .addContainerGap())
        );
        focusReportsPanelLayout.setVerticalGroup(
            focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(focusReportsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(focusReportsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(focusReportSearchButton)
                    .addComponent(createFocusReportButton)
                    .addComponent(focusReportRefreshButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(201, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Focus Reports", focusReportsPanel);

        communityTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Community Name", "Leader Name", "# of Focus Rooms"
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
        try{
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Student WHERE S_ID LIKE '" 
                    + studentIDTextField.getText() + "%'");
        
            while(rs.next()){
                Student student = new Student(rs.getString("S_ID"), rs.getString("F_Name"), 
                        rs.getString("L_Name"), rs.getString("DOB"), rs.getString("Room_No"));
                list.add(student);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        model.setRowCount(0);
        Object row[] = new Object[6];
        for(int i = 0; i < list.size(); i++){
            try{
                String studentToCount = list.get(i).getS_ID();
                Statement st = con.createStatement();
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
            ResultSet rs = st.executeQuery("SELECT * FROM Focus_Report WHERE S_ID = " + jTextField1.getText());
        
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
      JTextArea studentResponseArea = new JTextArea(5, 20);
      JTextField typeField = new JTextField(9);
      JTextArea commLeaderDebriefArea = new JTextArea(5, 20);
      
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
    private javax.swing.JPanel communityPanel;
    private javax.swing.JTable communityTable;
    private javax.swing.JButton createFocusReportButton;
    private javax.swing.JButton focusReportRefreshButton;
    private javax.swing.JButton focusReportSearchButton;
    private javax.swing.JPanel focusReportsPanel;
    private javax.swing.JTable focusReportsTable;
    private javax.swing.JPanel homeroomPanel;
    private javax.swing.JTable homeroomTable;
    private javax.swing.JTextField homeroomTeacherIDTextField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JPanel medicationPanel;
    private javax.swing.JTable medicationTable;
    private javax.swing.JTextField studentIDMedicationtextField;
    private javax.swing.JButton studentIDSearchButton;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JButton studentResetButton;
    private javax.swing.JTable studentTable;
    private javax.swing.JPanel studentsPanel;
    private javax.swing.JButton teacherIDSearchButton;
    private javax.swing.JTextField teacherIDTextField;
    private javax.swing.JButton teacherResetButton;
    private javax.swing.JTable teacherTable;
    private javax.swing.JPanel teachersPanel;
    // End of variables declaration//GEN-END:variables
}
