/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package databaseproject2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author wlent
 */
public class DatabaseGUI2 extends javax.swing.JFrame {

    /**
     * Creates new form DatabaseGUI2
     */
    public DatabaseGUI2() {
        initComponents();
        showStudents();
        showTeachers();
        showFocusReports();
    }
    
    public Connection getConnection(){
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL);
            return con;
        }
        
        catch(Exception e){
            System.out.println("Could not connect to database.");
        }
        return null;
    }
    
    public ArrayList<Student> studentList(){ //creates an ArrayList based on the student table
        ArrayList<Student> list = new ArrayList<>();
        try{
            //Class.forName("com.mysql.jdbc.Driver");
            //Connection con = DriverManager.getConnection(URL);
            Connection con = getConnection();
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Student");
        
            while(rs.next()){
                Student student = new Student(rs.getString("S_ID"), rs.getString("F_Name"), rs.getString("L_Name"), rs.getString("DOB"), rs.getString("Room_No"));
                list.add(student);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        return list;
    }
    
    public ArrayList<Teacher> teacherList(){ //creates an ArrayList based on the teacher table
        ArrayList<Teacher> list = new ArrayList<>();
        try{
            //Class.forName("com.mysql.jdbc.Driver");
            //Connection con = DriverManager.getConnection(URL);
            Connection con = getConnection();
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
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL);
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
    
    public final void showStudents(){ //displays the full student table
        ArrayList<Student> list = studentList();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        Object row[] = new Object[5];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getS_ID();
            row[1] = list.get(i).getF_Name();
            row[2] = list.get(i).getL_Name();
            row[3] = list.get(i).getDOB();
            row[4] = list.get(i).getRoom_No();
            model.addRow(row);
        }
    }
    
    public final void showTeachers(){ //displays the full teacher table
        ArrayList<Teacher> list = teacherList();
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        Object row[] = new Object[4];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getT_ID();
            row[1] = list.get(i).getF_Name();
            row[2] = list.get(i).getL_Name();
            row[3] = list.get(i).getSubject();
            model.addRow(row);
        }
    }
    
    public final void showFocusReports(){ //displays the full focus report table
        ArrayList<FocusReport> list = FocusReportList();
        DefaultTableModel model = (DefaultTableModel) jTable3.getModel();
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        studentIDTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        studentIDSearchButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        teacherIDTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        teacherIDSearchButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jTextField2 = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jTextField3 = new javax.swing.JTextField();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("Student ID");

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "First Name", "Last Name", "D.O.B.", "Homeroom"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        studentIDSearchButton.setText("Search");
        studentIDSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                studentIDSearchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(studentIDSearchButton)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 481, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(studentIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(studentIDSearchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(151, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Students", jPanel1);

        jLabel2.setText("Teacher ID");

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Teacher ID", "First Name", "Last Name", "Subject"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        teacherIDSearchButton.setText("Search");
        teacherIDSearchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                teacherIDSearchButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(teacherIDSearchButton))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(469, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(teacherIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(teacherIDSearchButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(151, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Teachers", jPanel2);

        jLabel3.setText("Student ID");

        jLabel4.setText("Time In");

        jLabel5.setText("Date");

        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Student ID", "Teacher ID", "Time In", "Time Out", "Date", "Teacher Description", "Student Response", "Type", "Debrief"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3)
                    .addGroup(jPanel3Layout.createSequentialGroup()
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
                        .addGap(0, 446, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jTextField3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(155, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Focus Reports", jPanel3);

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
        ArrayList<Student> list = new ArrayList<Student>();
        DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://triton.towson.edu:3360/wlent1db", "wlent1", "Cosc*8pcy");
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Student WHERE S_ID = " + studentIDTextField.getText());
        
            while(rs.next()){
                Student student = new Student(rs.getString("S_ID"), rs.getString("F_Name"), rs.getString("L_Name"), rs.getString("DOB"), rs.getString("Room_No"));
                list.add(student);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        model.setRowCount(0);
        Object row[] = new Object[5];
        for(int i = 0; i < list.size(); i++){
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
        DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
        try{
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL);
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM Teacher WHERE T_ID = " + teacherIDTextField.getText());
        
            while(rs.next()){
                Teacher teacher = new Teacher(rs.getString("T_ID"), rs.getString("F_Name"), rs.getString("L_Name"), rs.getString("Subject"));
                list.add(teacher);
            }
        }
        
        catch(Exception e){
            System.out.println("Error");
        }
        model.setRowCount(0);
        Object row[] = new Object[5];
        for(int i = 0; i < list.size(); i++){
            row[0] = list.get(i).getT_ID();
            row[1] = list.get(i).getF_Name();
            row[2] = list.get(i).getL_Name();
            row[3] = list.get(i).getSubject();
            model.addRow(row);
        }
    }//GEN-LAST:event_teacherIDSearchButtonActionPerformed

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
                new DatabaseGUI2().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextField2;
    private javax.swing.JTextField jTextField3;
    private javax.swing.JButton studentIDSearchButton;
    private javax.swing.JTextField studentIDTextField;
    private javax.swing.JButton teacherIDSearchButton;
    private javax.swing.JTextField teacherIDTextField;
    // End of variables declaration//GEN-END:variables
}
