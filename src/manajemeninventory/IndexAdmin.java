/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package manajemeninventory;

import Connection.DB;
import com.mysql.jdbc.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.Timer;
import java.awt.event.ActionListener;
import javax.swing.table.DefaultTableModel;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.xml.JRXmlLoader;
import net.sf.jasperreports.view.JasperViewer;
import java.awt.event.ActionEvent;

/**
 *
 * @author Bule
 */
public class IndexAdmin extends javax.swing.JFrame {
    private String nama;
    private String id;
    private Timer time;
    
    DefaultComboBoxModel<String> cmbgudang = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<String> cmbsupplier = new DefaultComboBoxModel<>();
    DefaultComboBoxModel<String> cmbbarang = new DefaultComboBoxModel<>();
//    private int row;

    /**
     * Creates new form IndexAdmin
     */
    
    public void digitalClock(){
        ActionListener action = new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
               Date date = new Date();
               DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
               String time = timeFormat.format(date);
               timerText.setText(time);
            }
        };
        time = new Timer(1000, action);
        time.setInitialDelay(0);
        time.start();
    }
    
    public IndexAdmin(String nama) {
        initComponents();
        //setting tabel
        data_gudang();data_user();data_supplier();data_barang();data_barang_masuk();data_barang_keluar();
        
        //time
        this.digitalClock();
        
        //set combo gundang dan combo supplier
        this.combo_gudang();
        this.combo_supplier();
        this.combo_barang();
        
        //combobox gudang untuk data supplier
        cmb_gudang.setModel(cmbgudang);
        
        //combobox gudang dan supplier untuk data barang
        cmb_gudang_barang.setModel(cmbgudang);
        cmb_supplier_barang.setModel(cmbsupplier);
        
        //combobox Kode barang masuk
        cmb_barang_barang_masuk.setModel(cmbbarang);
        
        //combobox Kode barang keluar
        cmb_barang_barang_keluar.setModel(cmbbarang);
        
        //disabled combo gudang di data barang
        cmb_gudang_barang.setEnabled(false);
        
        //setting nama
        this.nama = nama;
        jLabel1.setText("SELAMAT DATANG, "+nama);
        jLabel8.setText("Hallo, "+nama);
        
        //menu barang
        data_barang.setVisible(false);barang_masuk.setVisible(false);barang_keluar.setVisible(false);
        
        //larangan edit
        kode_supp.setEnabled(false);
        kd_gudang.setEnabled(false);
        kd_barang.setEnabled(false);
        
        //dilarang resize
        setExtendedState(JFrame.MAXIMIZED_HORIZ);
        setResizable(false);
    }
    
    //gudang
    private void gd_refresh(){
        //agar merefresh setelah disimpan
         DefaultTableModel model = (DefaultTableModel)tabel_gudang.getModel();
         model.setRowCount(0);
         data_gudang();
    }
    
    
    private void data_gudang(){
         DefaultTableModel tbl = new DefaultTableModel();
         tbl.addColumn("KODE GUDANG");
         tbl.addColumn("NAMA GUDANG");
         tbl.addColumn("STATUS");
         tabel_gudang.setModel(tbl);
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT * FROM gudang");
             while(res.next())
             {
                 tbl.addRow(new Object[] {
                    res.getString("kd_gudang"),
                    res.getString("nama_gudang"),
                    res.getString("status"),
                 });
                 tabel_gudang.setModel(tbl);
             }
         }catch(Exception e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
    }
    private String setGudang(){
        String gudang_id = null;
        String gudang = (String) cmb_gudang.getSelectedItem();
        try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT kd_gudang FROM gudang WHERE nama_gudang = '"+gudang+"'");
             while(res.next()){
                 gudang_id = res.getString("kd_gudang");
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
        return gudang_id;
    }
    
    private String getGudangIdBySupplierName(){
        String gudang_id = null;
        String supplier = (String) cmb_supplier_barang.getSelectedItem();
        try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT kd_gudang FROM supplier WHERE nama_supplier = '"+supplier+"'");
             while(res.next()){
                 gudang_id = res.getString("kd_gudang");
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
        return gudang_id;
    }
    
    private String getGudangNameBySupplierName(){
        String nama_gudang = null;
        try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT nama_gudang FROM gudang WHERE kd_gudang = '"+this.getGudangIdBySupplierName()+"'");
             while(res.next()){
                 nama_gudang = res.getString("nama_gudang");
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
        return nama_gudang;
    }
    
    
    private void combo_gudang(){
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             String aktif = "Aktif";
             ResultSet res = statement.executeQuery("SELECT nama_gudang FROM gudang WHERE status='"+aktif+"' ");
             while(res.next()){
                 cmbgudang.addElement(res.getString("nama_gudang"));
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
    }
    
    
    
    //user
    private void data_user(){
         DefaultTableModel tbl = new DefaultTableModel();
         tbl.addColumn("ID");
         tbl.addColumn("NAMA");
         tbl.addColumn("USERNAME");
         tbl.addColumn("PASSWORD");
         tbl.addColumn("Role");
         tbl.addColumn("pw");
         tabel_user.setModel(tbl);
         tabel_user.getColumnModel().getColumn(0).setMaxWidth(0);
         tabel_user.getColumnModel().getColumn(0).setMinWidth(0);
         tabel_user.getColumnModel().getColumn(5).setMaxWidth(0);
         tabel_user.getColumnModel().getColumn(5).setMinWidth(0);

         String x = "*********";
         
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT users.id, users.nama, users.username, users.password, role.role FROM users INNER JOIN role ON users.role = role.id AND users.role = 2");
             while(res.next())
             {
                 
                 tbl.addRow(new Object[] {
                    res.getInt("id"),
                    res.getString("nama"),
                    res.getString("username"),
                    x,
                    res.getString("role"),
                    res.getString("password")
                 });
                 tabel_user.setModel(tbl);
                 tabel_user.getColumnModel().getColumn(0).setMaxWidth(0);
                 tabel_user.getColumnModel().getColumn(0).setMinWidth(0);
                 tabel_user.getColumnModel().getColumn(5).setMaxWidth(0);
                 tabel_user.getColumnModel().getColumn(5).setMinWidth(0);
     
             }
         }catch(Exception e){
             JOptionPane.showMessageDialog(rootPane, "Gagal add password");
         }
    }
    
    private void user_refresh(){
        //agar merefresh setelah disimpan
         DefaultTableModel model = (DefaultTableModel)tabel_user.getModel();
         model.setRowCount(0);
         data_user();
    }
    
    private void setId(String id){
        this.id = id;
    }
    private String getId(){
        return this.id;
    }
    
    
    //supplier
    private void supp_refresh(){
        //agar merefresh setelah disimpan
         DefaultTableModel model = (DefaultTableModel)tabel_supplier.getModel();
         model.setRowCount(0);
         data_supplier();
    }
    
    private void data_supplier(){
         DefaultTableModel tbl = new DefaultTableModel();
         tbl.addColumn("KODE SUPPLIER");
         tbl.addColumn("NAMA SUPPLIIER");
         tbl.addColumn("ALAMAT");
         tbl.addColumn("GUDANG");
         tabel_supplier.setModel(tbl);
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT supplier.kd_supp, supplier.nama_supplier, supplier.alamat, gudang.nama_gudang FROM supplier INNER JOIN gudang ON supplier.kd_gudang = gudang.kd_gudang");
             while(res.next())
             {
                 tbl.addRow(new Object[] {
                    res.getString("kd_supp"),
                    res.getString("nama_supplier"),
                    res.getString("alamat"),
                    res.getString("nama_gudang")
                 });
                 tabel_supplier.setModel(tbl);
             }
         }catch(Exception e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
    }
    
    private void combo_supplier(){    
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT nama_supplier FROM supplier");
             while(res.next()){
                 cmbsupplier.addElement(res.getString("nama_supplier"));
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
    }
    
    private String setSupplier(){
        String supplier_id = null;
        String supplier = (String) cmb_supplier_barang.getSelectedItem();
        try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT kd_supp FROM supplier WHERE nama_supplier = '"+supplier+"'");
             while(res.next()){
                 supplier_id = res.getString("kd_supp");
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
        return supplier_id;
    }
    
    
    
    
    //barang
    private void combo_barang(){
        try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT kd_barang, nama_barang FROM barang");
             while(res.next()){
                 cmbbarang.addElement(res.getString("kd_barang")+" ( "+res.getString("nama_barang")+" )");
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
    }
    
    private int getStokById(String kd_barang){
        int stok = 0;
        try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT stok FROM barang WHERE kd_barang = '"+kd_barang+"'");
             while(res.next()){
                 stok = res.getInt("stok");
             }
             res.close(); statement.close();
         }catch(SQLException e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
        return stok;
    }
    
    private void barang_refresh(){
        //agar merefresh setelah disimpan
         DefaultTableModel model = (DefaultTableModel)tabel_barang.getModel();
         model.setRowCount(0);
         data_barang();
    }
    
    
    private void data_barang(){
        DefaultTableModel tbl = new DefaultTableModel();
         tbl.addColumn("KODE BARANG");
         tbl.addColumn("NAMA BARANG");
         tbl.addColumn("KETERANGAN");
         tbl.addColumn("STOK");
         tbl.addColumn("NAMA SUPPLIER");
         tbl.addColumn("NAMA GUDANG");
         tabel_barang.setModel(tbl);
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT barang.kd_barang, barang.nama_barang, barang.keterangan, barang.stok, supplier.nama_supplier, gudang.nama_gudang FROM barang INNER JOIN supplier ON barang.kd_supplier = supplier.kd_supp INNER JOIN gudang ON barang.kd_gudang = gudang.kd_gudang");
             while(res.next())
             {
                 tbl.addRow(new Object[] {
                    res.getString("kd_barang"),
                    res.getString("nama_barang"),
                    res.getString("keterangan"),
                    res.getInt("stok"),
                    res.getString("nama_supplier"),
                    res.getString("nama_gudang")
                 });
                 tabel_barang.setModel(tbl);
             }
         }catch(Exception e){
             JOptionPane.showMessageDialog(rootPane, "Gagal");
         }
    }
   
    
    //Barang Masuk
    
    private void barang_masuk_refresh(){
        //agar merefresh setelah disimpan
         DefaultTableModel model = (DefaultTableModel)tabel_barang_masuk.getModel();
         model.setRowCount(0);
         data_barang_masuk();
    }
    
    private void data_barang_masuk(){
         DefaultTableModel tbl = new DefaultTableModel();
         tbl.addColumn("ID");
         tbl.addColumn("KODE BARANG");
         tbl.addColumn("NAMA BARANG");
         tbl.addColumn("STOK");
         tbl.addColumn("TANGGAL MASUK");
         tabel_barang_masuk.setModel(tbl);
         tabel_barang_masuk.getColumnModel().getColumn(0).setMaxWidth(0);
         tabel_barang_masuk.getColumnModel().getColumn(0).setMinWidth(0);
         
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT * from barang_masuk");
             while(res.next()){
                tbl.addRow(new Object[] {
                    res.getString("id"),
                    res.getString("kd_barang"),
                    res.getString("nama_barang"),
                    res.getInt("stok"),
                    res.getDate("tanggal_masuk")
                 });
                 tabel_barang_masuk.setModel(tbl);
                 tabel_barang_masuk.getColumnModel().getColumn(0).setMaxWidth(0);
                 tabel_barang_masuk.getColumnModel().getColumn(0).setMinWidth(0);
             }
         }catch(Exception e){
             JOptionPane.showMessageDialog(rootPane, "Gagal tabel barang Masuk");
             
         }
    }
    
    
    
    //Barang Keluar
    private void barang_keluar_refresh(){
        //agar merefresh setelah disimpan
         DefaultTableModel model = (DefaultTableModel)tabel_barang_keluar.getModel();
         model.setRowCount(0);
         data_barang_keluar();
    }
    private void data_barang_keluar(){
         DefaultTableModel tbl = new DefaultTableModel();
         tbl.addColumn("ID");
         tbl.addColumn("KODE BARANG");
         tbl.addColumn("NAMA BARANG");
         tbl.addColumn("STOK");
         tbl.addColumn("TANGGAL KELUAR");
         tabel_barang_keluar.setModel(tbl);
         tabel_barang_keluar.getColumnModel().getColumn(0).setMaxWidth(0);
         tabel_barang_keluar.getColumnModel().getColumn(0).setMinWidth(0);
         
         try{
             Statement statement = (Statement) DB.getConnection().createStatement();
             ResultSet res = statement.executeQuery("SELECT * from barang_keluar");
             while(res.next()){
//                 if(res.getDate("tanggal_keluar") == null){
//                     keluar = "Kosong";
//                 }else{
//                     Date x = res.getDate("tanggal_keluar");
//                     DateFormat date = new SimpleDateFormat("yyyy-MM-dd");
//                     keluar = date.format(x);
//                 }
                tbl.addRow(new Object[] {
                    res.getString("id"),
                    res.getString("kd_barang"),
                    res.getString("nama_barang"),
                    res.getInt("stok"),
                    res.getDate("tanggal_keluar")
                 });
                 tabel_barang_keluar.setModel(tbl);
                 tabel_barang_keluar.getColumnModel().getColumn(0).setMaxWidth(0);
                 tabel_barang_keluar.getColumnModel().getColumn(0).setMinWidth(0);
             }
         }catch(Exception e){
             JOptionPane.showMessageDialog(rootPane, "Gagal tabel barang keluar");
             
         }
    }
    
    
//    private int userRowCount(){
//        int x = 0;
//        PreparedStatement st;
//        ResultSet rs;
//        String query = "SELECT COUNT(*) AS rowcount FROM users WHERE users.role = 2" ;
//        try{
//            st = (PreparedStatement)DB.getConnection().prepareStatement(query);
//            rs = st.executeQuery();
//            while(rs.next()){
//                x = rs.getInt("rowcount");
//            }
//        }catch(Exception e){
//            JOptionPane.showMessageDialog(rootPane, "Gagal hitung row");
//        }
//        return x;
//    } 
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel3 = new javax.swing.JLabel();
        jLabel43 = new javax.swing.JLabel();
        sideBar = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jButton5 = new javax.swing.JButton();
        jButton14 = new javax.swing.JButton();
        data_barang = new javax.swing.JLabel();
        barang_masuk = new javax.swing.JLabel();
        barang_keluar = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        headPanel = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        timerText = new javax.swing.JLabel();
        jLabel23 = new javax.swing.JLabel();
        mainPanel = new javax.swing.JPanel();
        welcomePage = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        dataSupplier = new javax.swing.JPanel();
        jLabel11 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tabel_supplier = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel12 = new javax.swing.JLabel();
        kode_supp = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        nama_supp = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        jButton10 = new javax.swing.JButton();
        jButton11 = new javax.swing.JButton();
        jButton12 = new javax.swing.JButton();
        jButton13 = new javax.swing.JButton();
        alamat_supp = new javax.swing.JTextField();
        jLabel15 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        cmb_gudang = new javax.swing.JComboBox<>();
        cetak_supplier = new javax.swing.JButton();
        dataUser = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        nama_user = new javax.swing.JTextField();
        jLabel27 = new javax.swing.JLabel();
        username_user = new javax.swing.JTextField();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        edit_user = new javax.swing.JButton();
        delete_user = new javax.swing.JButton();
        role_user = new javax.swing.JComboBox<>();
        password_user = new javax.swing.JPasswordField();
        repeat_user = new javax.swing.JPasswordField();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tabel_user = new javax.swing.JTable();
        dataBarang = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tabel_barang = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        kd_barang = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        nama_barang = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        keterangan_barang = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jButton6 = new javax.swing.JButton();
        jButton7 = new javax.swing.JButton();
        jButton8 = new javax.swing.JButton();
        jButton9 = new javax.swing.JButton();
        cmb_supplier_barang = new javax.swing.JComboBox<>();
        cmb_gudang_barang = new javax.swing.JComboBox<>();
        cetak_data_barang = new javax.swing.JButton();
        barangMasuk = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        tabel_barang_masuk = new javax.swing.JTable();
        jPanel6 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        stok_barang_masuk = new javax.swing.JTextField();
        barangMasukNew = new javax.swing.JButton();
        barangMasukSave = new javax.swing.JButton();
        barangMasukDelete = new javax.swing.JButton();
        jLabel40 = new javax.swing.JLabel();
        tanggal_masuk = new com.toedter.calendar.JDateChooser();
        cmb_barang_barang_masuk = new javax.swing.JComboBox<>();
        cetak_barang_masuk = new javax.swing.JButton();
        barangKeluar = new javax.swing.JPanel();
        jLabel33 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tabel_barang_keluar = new javax.swing.JTable();
        jPanel7 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        stok_barang_keluar = new javax.swing.JTextField();
        barangKeluarNew = new javax.swing.JButton();
        barangKeluarSave = new javax.swing.JButton();
        barangKeluarDelete = new javax.swing.JButton();
        jLabel41 = new javax.swing.JLabel();
        tanggal_keluar = new com.toedter.calendar.JDateChooser();
        cmb_barang_barang_keluar = new javax.swing.JComboBox<>();
        cetak_barang_keluar = new javax.swing.JButton();
        dataGudang = new javax.swing.JPanel();
        jLabel17 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel18 = new javax.swing.JLabel();
        kd_gudang = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        nama_gudang = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        jLabel21 = new javax.swing.JLabel();
        gd_new = new javax.swing.JButton();
        gd_save = new javax.swing.JButton();
        gd_edit = new javax.swing.JButton();
        gd_delete = new javax.swing.JButton();
        status = new javax.swing.JComboBox<>();
        cetak_gudang = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tabel_gudang = new javax.swing.JTable();

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Data Barang");

        jLabel43.setText("jLabel43");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        sideBar.setBackground(new java.awt.Color(51, 51, 255));

        jButton2.setBackground(new java.awt.Color(0, 51, 51));
        jButton2.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton2.setForeground(new java.awt.Color(255, 255, 255));
        jButton2.setText("Data Supplier");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setBackground(new java.awt.Color(0, 51, 51));
        jButton3.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton3.setForeground(new java.awt.Color(255, 255, 255));
        jButton3.setText("Data Gudang");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setBackground(new java.awt.Color(0, 51, 51));
        jButton4.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton4.setForeground(new java.awt.Color(255, 255, 255));
        jButton4.setText("Data User");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jButton5.setBackground(new java.awt.Color(255, 0, 0));
        jButton5.setForeground(new java.awt.Color(255, 255, 255));
        jButton5.setText("LOGOUT");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jButton14.setBackground(new java.awt.Color(0, 51, 51));
        jButton14.setFont(new java.awt.Font("Segoe UI Semibold", 0, 12)); // NOI18N
        jButton14.setForeground(new java.awt.Color(255, 255, 255));
        jButton14.setText("Barang");
        jButton14.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton14ActionPerformed(evt);
            }
        });

        data_barang.setForeground(new java.awt.Color(255, 255, 255));
        data_barang.setText("- Data Barang");
        data_barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                data_barangMouseClicked(evt);
            }
        });

        barang_masuk.setForeground(new java.awt.Color(255, 255, 255));
        barang_masuk.setText("- Barang Masuk");
        barang_masuk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barang_masukMouseClicked(evt);
            }
        });

        barang_keluar.setForeground(new java.awt.Color(255, 255, 255));
        barang_keluar.setText("- Barang Keluar");
        barang_keluar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                barang_keluarMouseClicked(evt);
            }
        });

        jLabel8.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout sideBarLayout = new javax.swing.GroupLayout(sideBar);
        sideBar.setLayout(sideBarLayout);
        sideBarLayout.setHorizontalGroup(
            sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(data_barang)
                        .addComponent(barang_masuk)
                        .addComponent(barang_keluar))
                    .addComponent(jButton2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton14, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        sideBarLayout.setVerticalGroup(
            sideBarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(sideBarLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton14, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(data_barang)
                .addGap(18, 18, 18)
                .addComponent(barang_masuk)
                .addGap(18, 18, 18)
                .addComponent(barang_keluar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 95, Short.MAX_VALUE)
                .addComponent(jButton5)
                .addGap(24, 24, 24))
        );

        getContentPane().add(sideBar, java.awt.BorderLayout.LINE_START);

        headPanel.setBackground(new java.awt.Color(153, 153, 255));

        jLabel22.setIcon(new javax.swing.ImageIcon(getClass().getResource("/manajemeninventory/70X70.png"))); // NOI18N

        timerText.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        timerText.setForeground(new java.awt.Color(0, 0, 0));

        jLabel23.setFont(new java.awt.Font("Segoe UI", 0, 48)); // NOI18N
        jLabel23.setText("PT. ISMOYO EXPRESS");

        javax.swing.GroupLayout headPanelLayout = new javax.swing.GroupLayout(headPanel);
        headPanel.setLayout(headPanelLayout);
        headPanelLayout.setHorizontalGroup(
            headPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(headPanelLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(jLabel22)
                .addGap(175, 175, 175)
                .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 598, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 65, Short.MAX_VALUE)
                .addComponent(timerText, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        headPanelLayout.setVerticalGroup(
            headPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headPanelLayout.createSequentialGroup()
                .addContainerGap(35, Short.MAX_VALUE)
                .addGroup(headPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(headPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(timerText, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, headPanelLayout.createSequentialGroup()
                            .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(20, 20, 20))))
                .addContainerGap())
        );

        getContentPane().add(headPanel, java.awt.BorderLayout.PAGE_START);

        mainPanel.setBackground(new java.awt.Color(68, 68, 68));
        mainPanel.setLayout(new java.awt.CardLayout());

        welcomePage.setBackground(new java.awt.Color(35, 35, 35));

        jLabel1.setFont(new java.awt.Font("Swis721 Lt BT", 0, 24)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout welcomePageLayout = new javax.swing.GroupLayout(welcomePage);
        welcomePage.setLayout(welcomePageLayout);
        welcomePageLayout.setHorizontalGroup(
            welcomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, welcomePageLayout.createSequentialGroup()
                .addGap(250, 250, 250)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addGap(225, 225, 225))
        );
        welcomePageLayout.setVerticalGroup(
            welcomePageLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, welcomePageLayout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                .addGap(218, 218, 218))
        );

        mainPanel.add(welcomePage, "card2");

        dataSupplier.setBackground(new java.awt.Color(35, 35, 35));

        jLabel11.setBackground(new java.awt.Color(255, 0, 0));
        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(255, 255, 255));
        jLabel11.setText("DATA SUPPLIER");

        tabel_supplier.setBackground(new java.awt.Color(102, 102, 102));
        tabel_supplier.setForeground(new java.awt.Color(255, 255, 255));
        tabel_supplier.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_supplier.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_supplierMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(tabel_supplier);

        jPanel2.setBackground(new java.awt.Color(51, 51, 51));

        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setText("Kode Supplier");

        kode_supp.setBackground(new java.awt.Color(102, 102, 102));
        kode_supp.setForeground(new java.awt.Color(255, 255, 255));

        jLabel13.setForeground(new java.awt.Color(255, 255, 255));
        jLabel13.setText("Nama Supplier");

        nama_supp.setBackground(new java.awt.Color(102, 102, 102));
        nama_supp.setForeground(new java.awt.Color(255, 255, 255));

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(255, 255, 255));
        jLabel14.setText("FORM INPUT SUPPLIER");

        jButton10.setText("NEW");
        jButton10.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton10ActionPerformed(evt);
            }
        });

        jButton11.setText("SAVE");
        jButton11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton11ActionPerformed(evt);
            }
        });

        jButton12.setText("EDIT");
        jButton12.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton12ActionPerformed(evt);
            }
        });

        jButton13.setText("DELETE");
        jButton13.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton13ActionPerformed(evt);
            }
        });

        alamat_supp.setBackground(new java.awt.Color(102, 102, 102));
        alamat_supp.setForeground(new java.awt.Color(255, 255, 255));

        jLabel15.setForeground(new java.awt.Color(255, 255, 255));
        jLabel15.setText("Alamat");

        jLabel16.setForeground(new java.awt.Color(255, 255, 255));
        jLabel16.setText("Gudang");

        cmb_gudang.setBackground(new java.awt.Color(102, 102, 102));
        cmb_gudang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cetak_supplier.setText("CETAK");
        cetak_supplier.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetak_supplierActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(89, 89, 89)
                        .addComponent(jLabel14))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15)
                            .addComponent(jLabel16)
                            .addComponent(jButton11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(kode_supp)
                            .addComponent(nama_supp)
                            .addComponent(alamat_supp)
                            .addComponent(cmb_gudang, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(jButton13, javax.swing.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jButton12, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cetak_supplier, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel14)
                .addGap(18, 18, 18)
                .addComponent(jLabel12)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kode_supp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nama_supp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel15)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(alamat_supp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cmb_gudang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(33, 33, 33)
                .addComponent(jButton11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton10)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton13)
                    .addComponent(jButton12))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cetak_supplier)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dataSupplierLayout = new javax.swing.GroupLayout(dataSupplier);
        dataSupplier.setLayout(dataSupplierLayout);
        dataSupplierLayout.setHorizontalGroup(
            dataSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSupplierLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(dataSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dataSupplierLayout.createSequentialGroup()
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 474, Short.MAX_VALUE))
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        dataSupplierLayout.setVerticalGroup(
            dataSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataSupplierLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataSupplierLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(dataSupplier, "card2");

        dataUser.setBackground(new java.awt.Color(35, 35, 35));

        jLabel25.setBackground(new java.awt.Color(255, 0, 0));
        jLabel25.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 255));
        jLabel25.setText("DATA USER");

        jPanel4.setBackground(new java.awt.Color(51, 51, 51));

        jLabel26.setForeground(new java.awt.Color(255, 255, 255));
        jLabel26.setText("Nama");

        nama_user.setBackground(new java.awt.Color(102, 102, 102));
        nama_user.setForeground(new java.awt.Color(255, 255, 255));

        jLabel27.setForeground(new java.awt.Color(255, 255, 255));
        jLabel27.setText("Username");

        username_user.setBackground(new java.awt.Color(102, 102, 102));
        username_user.setForeground(new java.awt.Color(255, 255, 255));

        jLabel28.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 255));
        jLabel28.setText("FORM DATA USER");

        jLabel29.setForeground(new java.awt.Color(255, 255, 255));
        jLabel29.setText("Passoword");

        jLabel30.setForeground(new java.awt.Color(255, 255, 255));
        jLabel30.setText("Repeat Password");

        jLabel31.setForeground(new java.awt.Color(255, 255, 255));
        jLabel31.setText("Role");

        edit_user.setText("EDIT");
        edit_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edit_userActionPerformed(evt);
            }
        });

        delete_user.setText("DELETE");
        delete_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delete_userActionPerformed(evt);
            }
        });

        role_user.setBackground(new java.awt.Color(102, 102, 102));
        role_user.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "User", "Admin" }));
        role_user.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                role_userActionPerformed(evt);
            }
        });

        password_user.setBackground(new java.awt.Color(102, 102, 102));
        password_user.setForeground(new java.awt.Color(255, 255, 255));

        repeat_user.setBackground(new java.awt.Color(102, 102, 102));
        repeat_user.setForeground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(nama_user, javax.swing.GroupLayout.PREFERRED_SIZE, 252, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel4Layout.createSequentialGroup()
                            .addGap(34, 34, 34)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel4Layout.createSequentialGroup()
                                    .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel27)
                                        .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGap(132, 132, 132))
                                .addComponent(jLabel28))
                            .addGap(67, 67, 67))
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                                .addComponent(password_user, javax.swing.GroupLayout.DEFAULT_SIZE, 111, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(repeat_user, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jLabel29)
                            .addComponent(jLabel31)
                            .addComponent(username_user)
                            .addComponent(role_user, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(edit_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(delete_user, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGap(163, 163, 163)
                        .addComponent(jLabel30)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(7, 7, 7)
                .addComponent(jLabel28)
                .addGap(18, 18, 18)
                .addComponent(jLabel26)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(nama_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel27)
                .addGap(9, 9, 9)
                .addComponent(username_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel31)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(role_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(jLabel30))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(password_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(repeat_user, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(edit_user)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(delete_user)
                .addGap(24, 24, 24))
        );

        tabel_user.setBackground(new java.awt.Color(102, 102, 102));
        tabel_user.setForeground(new java.awt.Color(255, 255, 255));
        tabel_user.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_user.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_userMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tabel_user);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 427, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 403, Short.MAX_VALUE)
            .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dataUserLayout = new javax.swing.GroupLayout(dataUser);
        dataUser.setLayout(dataUserLayout);
        dataUserLayout.setHorizontalGroup(
            dataUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dataUserLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(dataUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 92, Short.MAX_VALUE)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        dataUserLayout.setVerticalGroup(
            dataUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataUserLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataUserLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(dataUser, "card2");

        dataBarang.setBackground(new java.awt.Color(35, 35, 35));
        dataBarang.setForeground(new java.awt.Color(255, 255, 255));

        jLabel2.setBackground(new java.awt.Color(255, 0, 0));
        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("DATA BARANG");

        tabel_barang.setBackground(new java.awt.Color(102, 102, 102));
        tabel_barang.setForeground(new java.awt.Color(255, 255, 255));
        tabel_barang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_barang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_barangMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tabel_barang);

        jPanel1.setBackground(new java.awt.Color(51, 51, 51));

        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setText("Kode Barang");

        kd_barang.setBackground(new java.awt.Color(102, 102, 102));
        kd_barang.setForeground(new java.awt.Color(255, 255, 255));

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setText("Nama Barang");

        nama_barang.setBackground(new java.awt.Color(102, 102, 102));
        nama_barang.setForeground(new java.awt.Color(255, 255, 255));

        jLabel6.setBackground(new java.awt.Color(255, 255, 255));
        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setText("FORM INPUT BARANG");

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setText("Keterangan");

        keterangan_barang.setBackground(new java.awt.Color(102, 102, 102));
        keterangan_barang.setForeground(new java.awt.Color(255, 255, 255));

        jLabel9.setForeground(new java.awt.Color(255, 255, 255));
        jLabel9.setText("Supplier");

        jLabel10.setForeground(new java.awt.Color(255, 255, 255));
        jLabel10.setText("Gudang");

        jButton6.setText("NEW");
        jButton6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton6ActionPerformed(evt);
            }
        });

        jButton7.setText("SAVE");
        jButton7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton7ActionPerformed(evt);
            }
        });

        jButton8.setText("EDIT");
        jButton8.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton8ActionPerformed(evt);
            }
        });

        jButton9.setText("DELETE");
        jButton9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton9ActionPerformed(evt);
            }
        });

        cmb_supplier_barang.setBackground(new java.awt.Color(102, 102, 102));
        cmb_supplier_barang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cmb_gudang_barang.setBackground(new java.awt.Color(102, 102, 102));
        cmb_gudang_barang.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cetak_data_barang.setText("CETAK");
        cetak_data_barang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetak_data_barangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7)
                                    .addComponent(jLabel9))
                                .addGap(27, 27, 27)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(kd_barang)
                                        .addComponent(keterangan_barang)
                                        .addComponent(nama_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(cmb_supplier_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(56, 56, 56)
                                .addComponent(cmb_gudang_barang, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(cetak_data_barang, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jButton6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addComponent(jButton9, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(jButton8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addComponent(jButton7, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(93, 93, 93)
                        .addComponent(jLabel6)))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6)
                .addGap(12, 12, 12)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9)
                    .addComponent(cmb_supplier_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(kd_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(24, 24, 24)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nama_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(keterangan_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cmb_gudang_barang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(43, 43, 43)
                .addComponent(jButton7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton9)
                    .addComponent(jButton8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cetak_data_barang)
                .addContainerGap(21, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout dataBarangLayout = new javax.swing.GroupLayout(dataBarang);
        dataBarang.setLayout(dataBarangLayout);
        dataBarangLayout.setHorizontalGroup(
            dataBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataBarangLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(dataBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(dataBarangLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)))
                .addContainerGap())
        );
        dataBarangLayout.setVerticalGroup(
            dataBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataBarangLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(dataBarangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(dataBarang, "card2");

        barangMasuk.setBackground(new java.awt.Color(35, 35, 35));
        barangMasuk.setForeground(new java.awt.Color(255, 255, 255));

        jLabel32.setBackground(new java.awt.Color(255, 0, 0));
        jLabel32.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 255));
        jLabel32.setText("DATA BARANG MASUK");

        tabel_barang_masuk.setBackground(new java.awt.Color(102, 102, 102));
        tabel_barang_masuk.setForeground(new java.awt.Color(255, 255, 255));
        tabel_barang_masuk.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_barang_masuk.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_barang_masukMouseClicked(evt);
            }
        });
        jScrollPane3.setViewportView(tabel_barang_masuk);

        jPanel6.setBackground(new java.awt.Color(51, 51, 51));

        jLabel34.setForeground(new java.awt.Color(255, 255, 255));
        jLabel34.setText("Nama Barang");

        jLabel35.setBackground(new java.awt.Color(255, 255, 255));
        jLabel35.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 255));
        jLabel35.setText("FORM INPUT BARANG MASUK");

        jLabel37.setForeground(new java.awt.Color(255, 255, 255));
        jLabel37.setText("Jumlah");

        stok_barang_masuk.setBackground(new java.awt.Color(102, 102, 102));
        stok_barang_masuk.setForeground(new java.awt.Color(255, 255, 255));

        barangMasukNew.setText("NEW");
        barangMasukNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barangMasukNewActionPerformed(evt);
            }
        });

        barangMasukSave.setText("SAVE");
        barangMasukSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barangMasukSaveActionPerformed(evt);
            }
        });

        barangMasukDelete.setText("DELETE");
        barangMasukDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barangMasukDeleteActionPerformed(evt);
            }
        });

        jLabel40.setForeground(new java.awt.Color(255, 255, 255));
        jLabel40.setText("Tanggal Masuk ");

        tanggal_masuk.setBackground(new java.awt.Color(102, 102, 102));

        cmb_barang_barang_masuk.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cetak_barang_masuk.setText("CETAK");
        cetak_barang_masuk.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetak_barang_masukActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jLabel35))
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(barangMasukSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(barangMasukNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel6Layout.createSequentialGroup()
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel40)
                                    .addComponent(jLabel34)
                                    .addComponent(jLabel37))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmb_barang_barang_masuk, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(stok_barang_masuk)
                                    .addComponent(tanggal_masuk, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)))
                            .addComponent(barangMasukDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cetak_barang_masuk, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel35)
                .addGap(18, 18, 18)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34)
                    .addComponent(cmb_barang_barang_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel40)
                    .addComponent(tanggal_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stok_barang_masuk, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel37))
                .addGap(44, 44, 44)
                .addComponent(barangMasukSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(barangMasukDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(barangMasukNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cetak_barang_masuk)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout barangMasukLayout = new javax.swing.GroupLayout(barangMasuk);
        barangMasuk.setLayout(barangMasukLayout);
        barangMasukLayout.setHorizontalGroup(
            barangMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barangMasukLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(barangMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(barangMasukLayout.createSequentialGroup()
                        .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(barangMasukLayout.createSequentialGroup()
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 482, Short.MAX_VALUE)
                        .addGap(27, 27, 27))))
        );
        barangMasukLayout.setVerticalGroup(
            barangMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barangMasukLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(barangMasukLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        mainPanel.add(barangMasuk, "card2");

        barangKeluar.setBackground(new java.awt.Color(35, 35, 35));
        barangKeluar.setForeground(new java.awt.Color(255, 255, 255));

        jLabel33.setBackground(new java.awt.Color(255, 0, 0));
        jLabel33.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 255));
        jLabel33.setText("DATA BARANG KELUAR");

        tabel_barang_keluar.setBackground(new java.awt.Color(102, 102, 102));
        tabel_barang_keluar.setForeground(new java.awt.Color(255, 255, 255));
        tabel_barang_keluar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_barang_keluar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_barang_keluarMouseClicked(evt);
            }
        });
        jScrollPane6.setViewportView(tabel_barang_keluar);

        jPanel7.setBackground(new java.awt.Color(51, 51, 51));

        jLabel36.setForeground(new java.awt.Color(255, 255, 255));
        jLabel36.setText("Nama Barang");

        jLabel38.setBackground(new java.awt.Color(255, 255, 255));
        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(255, 255, 255));
        jLabel38.setText("FORM INPUT BARANG KELUAR");

        jLabel39.setForeground(new java.awt.Color(255, 255, 255));
        jLabel39.setText("Jumlah");

        stok_barang_keluar.setBackground(new java.awt.Color(102, 102, 102));
        stok_barang_keluar.setForeground(new java.awt.Color(255, 255, 255));

        barangKeluarNew.setText("NEW");
        barangKeluarNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barangKeluarNewActionPerformed(evt);
            }
        });

        barangKeluarSave.setText("SAVE");
        barangKeluarSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barangKeluarSaveActionPerformed(evt);
            }
        });

        barangKeluarDelete.setText("DELETE");
        barangKeluarDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                barangKeluarDeleteActionPerformed(evt);
            }
        });

        jLabel41.setForeground(new java.awt.Color(255, 255, 255));
        jLabel41.setText("Tanggal Keluar");

        tanggal_keluar.setBackground(new java.awt.Color(102, 102, 102));

        cmb_barang_barang_keluar.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        cetak_barang_keluar.setText("CETAK");
        cetak_barang_keluar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetak_barang_keluarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(81, 81, 81)
                        .addComponent(jLabel38))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(barangKeluarSave, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(barangKeluarNew, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel41)
                                    .addComponent(jLabel36)
                                    .addComponent(jLabel39))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmb_barang_barang_keluar, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(stok_barang_keluar)
                                    .addComponent(tanggal_keluar, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)))
                            .addComponent(barangKeluarDelete, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cetak_barang_keluar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addComponent(jLabel38)
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(cmb_barang_barang_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel41)
                    .addComponent(tanggal_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(stok_barang_keluar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel39))
                .addGap(44, 44, 44)
                .addComponent(barangKeluarSave)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(barangKeluarDelete)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(barangKeluarNew)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cetak_barang_keluar)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout barangKeluarLayout = new javax.swing.GroupLayout(barangKeluar);
        barangKeluar.setLayout(barangKeluarLayout);
        barangKeluarLayout.setHorizontalGroup(
            barangKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barangKeluarLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(barangKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(barangKeluarLayout.createSequentialGroup()
                        .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 246, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(barangKeluarLayout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.DEFAULT_SIZE, 487, Short.MAX_VALUE)
                        .addGap(27, 27, 27))))
        );
        barangKeluarLayout.setVerticalGroup(
            barangKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(barangKeluarLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(barangKeluarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(43, Short.MAX_VALUE))
        );

        mainPanel.add(barangKeluar, "card2");

        dataGudang.setBackground(new java.awt.Color(35, 35, 35));

        jLabel17.setBackground(new java.awt.Color(255, 0, 0));
        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(255, 255, 255));
        jLabel17.setText("DATA GUDANG");

        jPanel3.setBackground(new java.awt.Color(51, 51, 51));

        jLabel18.setBackground(new java.awt.Color(255, 255, 255));
        jLabel18.setForeground(new java.awt.Color(255, 255, 255));
        jLabel18.setText("Kode Gudang");

        kd_gudang.setBackground(new java.awt.Color(102, 102, 102));
        kd_gudang.setForeground(new java.awt.Color(255, 255, 255));

        jLabel19.setBackground(new java.awt.Color(255, 255, 255));
        jLabel19.setForeground(new java.awt.Color(255, 255, 255));
        jLabel19.setText("Nama Gudang");

        nama_gudang.setBackground(new java.awt.Color(102, 102, 102));
        nama_gudang.setForeground(new java.awt.Color(255, 255, 255));

        jLabel20.setBackground(new java.awt.Color(255, 255, 255));
        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(255, 255, 255));
        jLabel20.setText("FORM INPUT GUDANG");

        jLabel21.setBackground(new java.awt.Color(255, 255, 255));
        jLabel21.setForeground(new java.awt.Color(255, 255, 255));
        jLabel21.setText("Status");

        gd_new.setText("NEW");
        gd_new.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gd_newActionPerformed(evt);
            }
        });

        gd_save.setText("SAVE");
        gd_save.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gd_saveActionPerformed(evt);
            }
        });

        gd_edit.setText("EDIT");
        gd_edit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gd_editActionPerformed(evt);
            }
        });

        gd_delete.setText("DELETE");
        gd_delete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gd_deleteActionPerformed(evt);
            }
        });

        status.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Aktif", "Tidak Aktif" }));

        cetak_gudang.setText("CETAK");
        cetak_gudang.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cetak_gudangActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(kd_gudang, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 50, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel20)
                .addGap(82, 82, 82))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel3Layout.createSequentialGroup()
                        .addComponent(gd_delete, javax.swing.GroupLayout.PREFERRED_SIZE, 107, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(gd_edit, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(nama_gudang, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel21, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(status, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gd_save, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(gd_new, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cetak_gudang, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel20)
                .addGap(32, 32, 32)
                .addComponent(jLabel18)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(kd_gudang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel19)
                .addGap(8, 8, 8)
                .addComponent(nama_gudang, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel21)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(status, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(gd_new)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gd_save)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(gd_delete)
                    .addComponent(gd_edit))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(cetak_gudang)
                .addGap(18, 18, 18))
        );

        tabel_gudang.setBackground(new java.awt.Color(102, 102, 102));
        tabel_gudang.setForeground(new java.awt.Color(255, 255, 255));
        tabel_gudang.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        tabel_gudang.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tabel_gudangMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tabel_gudang);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 498, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 433, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout dataGudangLayout = new javax.swing.GroupLayout(dataGudang);
        dataGudang.setLayout(dataGudangLayout);
        dataGudangLayout.setHorizontalGroup(
            dataGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataGudangLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(dataGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(38, 38, 38)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        dataGudangLayout.setVerticalGroup(
            dataGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dataGudangLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(dataGudangLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(dataGudang, "card2");

        getContentPane().add(mainPanel, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton8ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton8ActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        int row = tabel_barang.getSelectedRow();
        String kode = kd_barang.getText();
        String namaa = nama_barang.getText();
        String keterangan = keterangan_barang.getText();
        int stok = Integer.parseInt(tabel_barang.getModel().getValueAt(row, 3).toString()); 
        String kd_supplier = this.setSupplier();
        String kd_gudangg = this.getGudangIdBySupplierName();
        
        for(int i = 0; i < cmb_gudang_barang.getItemCount();i++){
            if(this.getGudangNameBySupplierName().equals((String) cmb_gudang_barang.getItemAt(i))){
                cmb_gudang_barang.setSelectedIndex(i);
            }
        }
        
        
        if(kode.equals("") || namaa.equals("") || keterangan.equals("")){
            JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
        }else{
            String query = "UPDATE barang SET nama_barang=?, keterangan=?, stok=?, kd_supplier=?, kd_gudang=? WHERE kd_barang=?";
            try{
                      st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                      st.setString(1, namaa);
                      st.setString(2, keterangan);
                      st.setInt(3, stok);
                      st.setString(4, kd_supplier);
                      st.setString(5, kd_gudangg);
                      st.setString(6, kode);
                      st.executeUpdate();
                      st.close();
                      JOptionPane.showMessageDialog(null, "Data Berhasil Diupdate");
                   
                      this.barang_refresh();
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Gagal Dihapus");
                }
        }
        
    }//GEN-LAST:event_jButton8ActionPerformed

    private void jButton12ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton12ActionPerformed
        // TODO add your handling code here:
        if(nama_supp.getText().equals("") || alamat_supp.getText().equals("") || kode_supp.getText().equals("") ){
            JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
        }else{
            PreparedStatement st;
            String query ="update supplier set nama_supplier=?, alamat=?, kd_gudang=? WHERE kd_supp=?";
            try{
                st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                st.setString(1, nama_supp.getText());
                st.setString(2, alamat_supp.getText());
                st.setString(3, setGudang());
                st.setString(4, kode_supp.getText());
                st.executeUpdate();
                st.close();
                JOptionPane.showMessageDialog(null, "Data Berhasil Diupdate");
                this.supp_refresh();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Data Gagal Diupdate");
            }  
        }
        
        
    }//GEN-LAST:event_jButton12ActionPerformed

    private void gd_editActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gd_editActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        String query ="update gudang set nama_gudang=?, status=? WHERE kd_gudang=?";
        if(nama_gudang.getText().equals("")||kd_gudang.getText().equals("")){
            JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
        }else{
            try{
                st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                st.setString(1, nama_gudang.getText());
                st.setString(2, (String)status.getSelectedItem());
                st.setString(3, kd_gudang.getText());
                st.executeUpdate();
                st.close();
                JOptionPane.showMessageDialog(null, "Data Berhasil Diupdate");
                this.gd_refresh();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Data Gagal Diupdate");
            }  
        }
        
        
    }//GEN-LAST:event_gd_editActionPerformed

    private void edit_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_edit_userActionPerformed
        // TODO add your handling code here:
        if(nama_user.getText().equals("") || username_user.getText().equals("") || String.valueOf(password_user.getText()).equals("") || String.valueOf(repeat_user.getText()).equals("")){
            JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
        }else{
            PreparedStatement st;
            String query ="update users set nama=?, username=?, password=?, role=? WHERE id=?";
            if(String.valueOf(password_user.getText()).equals(String.valueOf(repeat_user.getText()))){
                try{
                    st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                    st.setString(1, nama_user.getText());
                    st.setString(2, username_user.getText());
                    st.setString(3, String.valueOf(password_user.getText()));
                    int role = 0;
                    if(role_user.getSelectedItem().equals("Admin")){
                        role = 1;
                    }else if(role_user.getSelectedItem().equals("User")){
                        role = 2;
                    }
                    st.setInt(4, role);
                    st.setInt(5, Integer.parseInt(getId()));
                    st.executeUpdate();
                    st.close();
                    JOptionPane.showMessageDialog(null, "Data Berhasil Diupdate");
                    this.user_refresh();
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Data Gagal Diupdate");
                }
            }else{
                JOptionPane.showMessageDialog(null, "Password Tidak Sama");
            } 
        }
        
        
        
    }//GEN-LAST:event_edit_userActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        //reset cmb gudang
        cmb_gudang.removeAllItems();
        this.combo_gudang();
        cmb_gudang.setModel(cmbgudang);
        
        
        //remove barang
        data_barang.setVisible(false);barang_masuk.setVisible(false);barang_keluar.setVisible(false);
        
        //remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();
        
        //add panel
        mainPanel.add(dataSupplier);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        //remove barang
        data_barang.setVisible(false);barang_masuk.setVisible(false);barang_keluar.setVisible(false);
        //remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();
        
        //add panel
        mainPanel.add(dataGudang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        //remove barang
        data_barang.setVisible(false);barang_masuk.setVisible(false);barang_keluar.setVisible(false);
        //remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();
        
        //add panel
        mainPanel.add(dataUser);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:
        
        int dialogBtn = JOptionPane.YES_NO_OPTION;
        int dialogResult = JOptionPane.showConfirmDialog(this,"Anda YAkin ?", "Peringatan", dialogBtn);
        if(dialogResult == 0){
            Login l = new Login();
            l.setVisible(true);
            l.setLocationRelativeTo(null);
            this.dispose();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void gd_newActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gd_newActionPerformed
        // TODO add your handling code here:
        kd_gudang.setEnabled(true);
        kd_gudang.setText("");
        nama_gudang.setText("");
    }//GEN-LAST:event_gd_newActionPerformed

    private void gd_saveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gd_saveActionPerformed
        // TODO add your handling code here:
      String kode = kd_gudang.getText();
      String nama = nama_gudang.getText();
      String stat = (String) status.getSelectedItem();
      if(kode.equals("")||nama.equals("")){
          JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
      }else{
            try{
                Statement statement = (Statement)DB.getConnection().createStatement();
                statement.executeUpdate("insert into gudang VALUES ('"+ kode +"','"+ nama +"','"+ stat +"');");
                statement.close();
                JOptionPane.showMessageDialog(null, "Data Berhasil disimpan");
                //agar merefresh setelah disimpan
                this.gd_refresh();
            } catch(Exception e){
                JOptionPane.showMessageDialog(null, "Data Gagal Disimpan");
            }
      }
      
     
    }//GEN-LAST:event_gd_saveActionPerformed

    private void gd_deleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gd_deleteActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        DefaultTableModel model = (DefaultTableModel)tabel_gudang.getModel();
        int row = tabel_gudang.getSelectedRow();
        if(row>=0){
            int ok=JOptionPane.showConfirmDialog(null, "Yakin Mau Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if(ok==0){
                
                String query = "DELETE from gudang WHERE `kd_gudang` = ? AND `nama_gudang`= ?";
                try{
                      int column1 = 0;
                      String value1 = tabel_gudang.getModel().getValueAt(row, column1).toString();
                      int column2 = 1;
                      String value2 = tabel_gudang.getModel().getValueAt(row, column2).toString();
                      
                      st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                      st.setString(1, value1);
                      st.setString(2, value2);
                      st.executeUpdate();
                      st.close();
                      JOptionPane.showMessageDialog(null, "Data Dihapus");
                      model.removeRow(row);
                      this.gd_refresh();
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Gagal Dihapus");
                }
            }
        }
    }//GEN-LAST:event_gd_deleteActionPerformed

    private void tabel_userMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_userMouseClicked
        // TODO add your handling code here:
        int row = tabel_user.getSelectedRow();
        
        int column1 = 0;
        setId(tabel_user.getModel().getValueAt(row, column1).toString());
        nama_user.setText(nama);
        
        int column2 = 1;
        String namaa = tabel_user.getModel().getValueAt(row, column2).toString();
        nama_user.setText(namaa);
        
        int column3 = 2;
        String username = tabel_user.getModel().getValueAt(row, column3).toString();
        username_user.setText(username);
        
        int column5 = 5;
        String pw = tabel_user.getModel().getValueAt(row, column5).toString();
        password_user.setText(pw);
        repeat_user.setText(pw);
    }//GEN-LAST:event_tabel_userMouseClicked

    private void delete_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delete_userActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        DefaultTableModel model = (DefaultTableModel)tabel_user.getModel();
        int row = tabel_user.getSelectedRow();
        if(row>=0){
            int ok=JOptionPane.showConfirmDialog(null, "Yakin Mau Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if(ok==0){
                
                String query = "DELETE from users WHERE username = ?";
                try{
                      int column2 = 1;
                      String value2 = tabel_user.getModel().getValueAt(row, column2).toString();
     
                      st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                      st.setString(1, value2);
                      st.executeUpdate();
                      st.close();
                      JOptionPane.showMessageDialog(null, "Data Dihapus");
                      model.removeRow(row);
                      this.user_refresh();
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Gagal Dihapus");
                }
            }
        }
        
    }//GEN-LAST:event_delete_userActionPerformed

    private void role_userActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_role_userActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_role_userActionPerformed

    private void jButton14ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton14ActionPerformed
        // TODO add your handling code here:
        data_barang.setVisible(true);barang_masuk.setVisible(true);barang_keluar.setVisible(true);
    }//GEN-LAST:event_jButton14ActionPerformed

    private void data_barangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_data_barangMouseClicked
        // TODO add your handling code here:
        this.barang_refresh();
        //reset combo gudang
        cmb_gudang_barang.removeAllItems();
        this.combo_gudang();
        cmb_gudang_barang.setModel(cmbgudang);
        
        //reset combo supplier
        cmb_supplier_barang.removeAllItems();
        this.combo_supplier();
        cmb_supplier_barang.setModel(cmbsupplier);
        
        //remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();
        
        //add panel
        mainPanel.add(dataBarang);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_data_barangMouseClicked

    private void barang_masukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barang_masukMouseClicked
        // TODO add your handling code here:
        
        //reset tabel barang masuk
        this.barang_masuk_refresh();
        
        //reset combo barang
        cmb_barang_barang_masuk.removeAllItems();
        this.combo_barang();
        cmb_barang_barang_masuk.setModel(cmbbarang);
        
        //setting tanggal
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);       
        tanggal_masuk.setDate(date);
        tanggal_masuk.setEnabled(false);
        
        
        //remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();
        
        //add panel
        mainPanel.add(barangMasuk);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_barang_masukMouseClicked

    private void barang_keluarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_barang_keluarMouseClicked
        // TODO add your handling code here:
        //reset combo barang
        cmb_barang_barang_masuk.removeAllItems();
        this.combo_barang();
        cmb_barang_barang_masuk.setModel(cmbbarang);
        
        //remove panel
        mainPanel.removeAll();
        mainPanel.repaint();
        mainPanel.revalidate();
        
        //setting tanggal
        long millis = System.currentTimeMillis();
        java.sql.Date date = new java.sql.Date(millis);       
        tanggal_keluar.setDate(date);
        tanggal_keluar.setEnabled(false);
        
        //add panel
        mainPanel.add(barangKeluar);
        mainPanel.repaint();
        mainPanel.revalidate();
    }//GEN-LAST:event_barang_keluarMouseClicked

    private void jButton10ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton10ActionPerformed
        // TODO add your handling code here:
        kode_supp.setEnabled(true);
        kode_supp.setText("");
        nama_supp.setText("");
        alamat_supp.setText("");
        
    }//GEN-LAST:event_jButton10ActionPerformed

    private void jButton11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton11ActionPerformed
        // TODO add your handling code here:
        String kode = kode_supp.getText();
        String nama = nama_supp.getText();
        String alamat = alamat_supp.getText();
        String kd_gudang = this.setGudang();
        if(kode.equals("") || nama.equals("") || alamat.equals("")){
            JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
        }else{
          try{
                Statement statement = (Statement)DB.getConnection().createStatement();
                statement.executeUpdate("insert into supplier VALUES ('"+ kode +"','"+ nama +"','"+ alamat +"', '"+kd_gudang+"');");
                statement.close();
                JOptionPane.showMessageDialog(null, "Data Berhasil disimpan");
                //agar merefresh setelah disimpan
                this.supp_refresh();
            } catch(Exception e){
                JOptionPane.showMessageDialog(null, "Data Gagal Disimpan");
            }  
        }
      
        
        
    }//GEN-LAST:event_jButton11ActionPerformed

    private void tabel_supplierMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_supplierMouseClicked
        // TODO add your handling code here:
        int row = tabel_supplier.getSelectedRow();
        
        int column1 = 0;
        String kd = tabel_supplier.getModel().getValueAt(row, column1).toString();
        kode_supp.setText(kd);
        
        int column2 = 1;
        String namaa = tabel_supplier.getModel().getValueAt(row, column2).toString();
        nama_supp.setText(namaa);
        
        int column3 = 2;
        String alamat = tabel_supplier.getModel().getValueAt(row, column3).toString();
        alamat_supp.setText(alamat);
        
        int column4 = 3;
        String gudang = tabel_supplier.getModel().getValueAt(row, column4).toString();
        for(int i = 0; i < cmb_gudang.getItemCount();i++){
            if(gudang.equals((String) cmb_gudang.getItemAt(i))){
                cmb_gudang.setSelectedIndex(i);
            }
        }
        
    }//GEN-LAST:event_tabel_supplierMouseClicked

    private void jButton13ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton13ActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        DefaultTableModel model = (DefaultTableModel)tabel_supplier.getModel();
        int row = tabel_supplier.getSelectedRow();
        if(row>=0){
            int ok=JOptionPane.showConfirmDialog(null, "Yakin Mau Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if(ok==0){
                
                String query = "DELETE from supplier WHERE kd_supp = ?";
                try{
                      int column1 = 0;
                      String kd = tabel_supplier.getModel().getValueAt(row, column1).toString();
                      kode_supp.setText(kd);
     
                      st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                      st.setString(1, kd);
                      st.executeUpdate();
                      st.close();
                      JOptionPane.showMessageDialog(null, "Data Dihapus");
                      model.removeRow(row);
                      this.supp_refresh();
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Gagal Dihapus");
                }
            }
        }
    }//GEN-LAST:event_jButton13ActionPerformed

    private void tabel_gudangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_gudangMouseClicked
        // TODO add your handling code here:
        int row = tabel_gudang.getSelectedRow();

        int column1 = 0;
        String kode = tabel_gudang.getModel().getValueAt(row, column1).toString();
        kd_gudang.setText(kode);

        int column2 = 1;
        String nama = tabel_gudang.getModel().getValueAt(row, column2).toString();
        nama_gudang.setText(nama);

        int index = 0;
        int column3 = 2;
        String stat = tabel_gudang.getModel().getValueAt(row, column3).toString();
        if(stat.equals("Aktif")){
            index = 0;
        }else if(stat.equals("Tidak Aktif")){
            index = 1;
        }
        status.setSelectedIndex(index);
    }//GEN-LAST:event_tabel_gudangMouseClicked

    private void jButton6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton6ActionPerformed
        // TODO add your handling code here:
        kd_barang.setEnabled(true);
        kd_barang.setText("");
        nama_barang.setText("");
        keterangan_barang.setText("");
        cmb_supplier_barang.setSelectedIndex(0);
        cmb_gudang_barang.setSelectedIndex(0);
    }//GEN-LAST:event_jButton6ActionPerformed

    private void tabel_barangMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_barangMouseClicked
        // TODO add your handling code here:
        int row = tabel_barang.getSelectedRow();
        
        int column1 = 0;
        String kode = tabel_barang.getModel().getValueAt(row, column1).toString();
        kd_barang.setText(kode);
        
        int column2 = 1;
        String namaa = tabel_barang.getModel().getValueAt(row, column2).toString();
        nama_barang.setText(namaa);
            
        int column3 = 2;
        String keterangan = tabel_barang.getModel().getValueAt(row, column3).toString();
        keterangan_barang.setText(keterangan);
        
        int column4 = 3;
        String stok = tabel_barang.getModel().getValueAt(row, column4).toString();
        
        int column5 = 4;
        String supplier = tabel_barang.getModel().getValueAt(row, column5).toString();
        for(int i = 0; i < cmb_supplier_barang.getItemCount();i++){
            if(supplier.equals((String) cmb_supplier_barang.getItemAt(i))){
                cmb_supplier_barang.setSelectedIndex(i);
            }
        }
        
        int column6 = 5;
        String gudang = tabel_barang.getModel().getValueAt(row, column6).toString();
        for(int i = 0; i < cmb_gudang_barang.getItemCount();i++){
            if(gudang.equals((String) cmb_gudang_barang.getItemAt(i))){
                cmb_gudang_barang.setSelectedIndex(i);
            }
        }
        
//        int column7 = 6;
//        try{
//            String value1 = tabel_barang.getModel().getValueAt(row, column7).toString();  
//            if(value1.equals("Kosong")){
//                tanggal_masuk.setDate(null);
//            }else{
//                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value1);
//                tanggal_masuk.setDate(date);   
//            }
//        }catch(Exception e){
//          JOptionPane.showMessageDialog(null, "tanggal Masuk Eror");
//        }
//        
//        int column8 = 7;
//        try{
//            String value2 = tabel_barang.getModel().getValueAt(row, column8).toString();
//            if(value2.equals("Kosong")){
//                tanggal_keluar.setDate(null);
//            }else{
//                java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value2);
//                tanggal_keluar.setDate(date);   
//            }
//        }catch(Exception e){
//          JOptionPane.showMessageDialog(null, "tanggal keluar Eror");
//          
//        }
        
        
    }//GEN-LAST:event_tabel_barangMouseClicked

    private void jButton7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton7ActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        String kode = kd_barang.getText();
        String namaa = nama_barang.getText();
        String keterangan = keterangan_barang.getText();
        int stok = 0;
        String kd_supplier = this.setSupplier();
        String kd_gudangg = this.getGudangIdBySupplierName();
        
    
        for(int i = 0; i < cmb_gudang_barang.getItemCount();i++){
            if(this.getGudangNameBySupplierName().equals((String) cmb_gudang_barang.getItemAt(i))){
                cmb_gudang_barang.setSelectedIndex(i);
            }
        }
        
        if(kode.equals("") || namaa.equals("") || keterangan.equals("")){
            JOptionPane.showMessageDialog(null, "Terdapat Field Kosong");
        }else{
            String query = "INSERT into barang VALUES(?, ?, ?, ?, ?, ?)";
            try{
                st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                st.setString(1, kode);
                st.setString(2, namaa);
                st.setString(3, keterangan);
                st.setInt(4, stok);
                st.setString(5, kd_supplier);
                st.setString(6, kd_gudangg);
                st.executeUpdate();
                st.close();
                JOptionPane.showMessageDialog(null, "Data Disimpan");
                this.barang_refresh();
            }catch(SQLException e){
                JOptionPane.showMessageDialog(null, "Gagal Disimpan");
            }
        }
        
        
    }//GEN-LAST:event_jButton7ActionPerformed

    private void tabel_barang_masukMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_barang_masukMouseClicked
        // TODO add your handling code here:
        int row = tabel_barang_masuk.getSelectedRow();
        
        int column2 = 1;
        int column3 = 2;
        String barang = tabel_barang_masuk.getModel().getValueAt(row, column2).toString();
        String namaBarang = tabel_barang_masuk.getModel().getValueAt(row, column3).toString();
        String x = barang+" ( "+namaBarang+" )";
        for(int i = 0; i < cmb_barang_barang_masuk.getItemCount();i++){
            if(x.equals((String) cmb_barang_barang_masuk.getItemAt(i))){
                cmb_barang_barang_masuk.setSelectedIndex(i);
            }
        }
        
        
        int column4 = 3;
        String stok = tabel_barang_masuk.getModel().getValueAt(row, column4).toString();
        stok_barang_masuk.setText(stok);
        
        int column5 = 4;
        try{
            String value1 = tabel_barang_masuk.getModel().getValueAt(row, column5).toString();
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value1);
            tanggal_masuk.setDate(date);   
            
        }catch(Exception e){
          JOptionPane.showMessageDialog(null, "tanggal Masuk Eror");
        }
        
    }//GEN-LAST:event_tabel_barang_masukMouseClicked

    private void barangMasukNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barangMasukNewActionPerformed
        // TODO add your handling code here:
        tanggal_masuk.setDate(null);
        stok_barang_masuk.setText("");
    }//GEN-LAST:event_barangMasukNewActionPerformed

    private void barangMasukSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barangMasukSaveActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        String x = (String)cmb_barang_barang_masuk.getSelectedItem();
        String[] y = x.split(" ");
        String kdBarang = y[0];
        String namaBarang = y[2];
        int stok = Integer.parseInt(stok_barang_masuk.getText());
        
        
        String query = "INSERT into barang_masuk(kd_barang, nama_barang, stok, tanggal_masuk) VALUES(?, ?, ?, ?)";
        try{
            st = (PreparedStatement)DB.getConnection().prepareStatement(query);
            st.setString(1, kdBarang);
            st.setString(2, namaBarang);
            st.setInt(3, stok);
            if(tanggal_masuk.getDate() == null){
                st.setDate(4, (java.sql.Date) tanggal_masuk.getDate());
            }else{
                java.sql.Date masuk = new java.sql.Date(tanggal_masuk.getDate().getTime());
                st.setDate(4, masuk);
            }
            
            JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
            st.executeUpdate();
            st.close();
            this.barang_masuk_refresh();
        } catch(Exception e){
          JOptionPane.showMessageDialog(null, "Data Gagal Disimpan");
        }
        String query2 = "UPDATE barang SET stok = stok+? WHERE kd_barang=? ";
        try{
            st = (PreparedStatement)DB.getConnection().prepareStatement(query2);
            st.setInt(1, stok);
            st.setString(2, kdBarang);
            st.executeUpdate();st.close();
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Penambahan Stok Gagal");
        }
    }//GEN-LAST:event_barangMasukSaveActionPerformed

    private void jButton9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton9ActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        DefaultTableModel model = (DefaultTableModel)tabel_barang.getModel();
        int row = tabel_barang.getSelectedRow();
        if(row>=0){
            int ok=JOptionPane.showConfirmDialog(null, "Yakin Mau Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if(ok==0){
                
                String query = "DELETE from barang WHERE kd_barang = ?";
                try{
                      int column1 = 0;
                      String kd = tabel_barang.getModel().getValueAt(row, column1).toString();
     
                      st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                      st.setString(1, kd);
                      st.executeUpdate();
                      st.close();
                      JOptionPane.showMessageDialog(null, "Data Berhasil Dihapus");
                      model.removeRow(row);
                      this.supp_refresh();
                }catch(SQLException e){
                    JOptionPane.showMessageDialog(null, "Gagal Dihapus");
                }
            }
        }
    }//GEN-LAST:event_jButton9ActionPerformed

    private void barangMasukDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barangMasukDeleteActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        
        int row = tabel_barang_masuk.getSelectedRow();
        int id = Integer.parseInt(tabel_barang_masuk.getModel().getValueAt(row, 0).toString());
        String kdBarang = tabel_barang_masuk.getModel().getValueAt(row, 1).toString();
        String namaBarang = tabel_barang_masuk.getModel().getValueAt(row, 2).toString();
        int stok = Integer.parseInt(tabel_barang_masuk.getModel().getValueAt(row, 3).toString());
        DefaultTableModel model = (DefaultTableModel)tabel_barang_masuk.getModel();
        
        if(row>=0){
            int ok=JOptionPane.showConfirmDialog(null, "Yakin Mau Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if(ok==0){
                String query = "DELETE from barang_masuk WHERE id = ? ";
                try{
                    st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                    st.setInt(1, id);
                    st.executeUpdate();
                    st.close();
                    JOptionPane.showMessageDialog(null, "Data Dihapus");
                    model.removeRow(row);
                    this.barang_masuk_refresh();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Data Gagal Dihapus");
                }
                String query2 = "UPDATE barang SET stok = stok-? WHERE kd_barang=? ";
                try{
                    st = (PreparedStatement)DB.getConnection().prepareStatement(query2);
                    st.setInt(1, stok);
                    st.setString(2, kdBarang);
                    st.executeUpdate();st.close();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Pengurangan Stok Gagal(hapus barang masuk)");
                } 
            }
        }
        
    }//GEN-LAST:event_barangMasukDeleteActionPerformed

    private void tabel_barang_keluarMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tabel_barang_keluarMouseClicked
        // TODO add your handling code here:
         int row = tabel_barang_keluar.getSelectedRow();
        
        int column2 = 1;
        int column3 = 2;
        String barang = tabel_barang_keluar.getModel().getValueAt(row, column2).toString();
        String namaBarang = tabel_barang_keluar.getModel().getValueAt(row, column3).toString();
        String x = barang+" ( "+namaBarang+" )";
        for(int i = 0; i < cmb_barang_barang_masuk.getItemCount();i++){
            if(x.equals((String) cmb_barang_barang_masuk.getItemAt(i))){
                cmb_barang_barang_keluar.setSelectedIndex(i);
            }
        }
        
        
        int column4 = 3;
        String stok = tabel_barang_keluar.getModel().getValueAt(row, column4).toString();
        stok_barang_keluar.setText(stok);
        
        int column5 = 4;
        try{
            String value1 = tabel_barang_keluar.getModel().getValueAt(row, column5).toString();
            java.util.Date date = new SimpleDateFormat("yyyy-MM-dd").parse(value1);
            tanggal_keluar.setDate(date);   
            
        }catch(Exception e){
          JOptionPane.showMessageDialog(null, "tanggal Masuk Eror");
        }
        
    }//GEN-LAST:event_tabel_barang_keluarMouseClicked

    private void barangKeluarNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barangKeluarNewActionPerformed
        // TODO add your handling code here:
        cmb_barang_barang_keluar.setSelectedIndex(0);
        tanggal_keluar.setDate(null);
        stok_barang_keluar.setText("");
    }//GEN-LAST:event_barangKeluarNewActionPerformed

    private void barangKeluarSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barangKeluarSaveActionPerformed
        // TODO add your handling code here:
        PreparedStatement st;
        String x = (String)cmb_barang_barang_keluar.getSelectedItem();
        String[] y = x.split(" ");
        String kdBarang = y[0];
        String namaBarang = y[2];
        int stok = Integer.parseInt(stok_barang_keluar.getText());
        int stokByKd_barang = this.getStokById(kdBarang);
        int a = stokByKd_barang - stok;
        if(a<0){
            JOptionPane.showMessageDialog(null, "Stok Barang Habis");
        }else{
            String query = "INSERT into barang_keluar(kd_barang, nama_barang, stok, tanggal_keluar) VALUES(?, ?, ?, ?)";
            try{
                st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                st.setString(1, kdBarang);
                st.setString(2, namaBarang);
                st.setInt(3, stok);
                if(tanggal_keluar.getDate() == null){
                    st.setDate(4, (java.sql.Date) tanggal_keluar.getDate());
                }else{
                    java.sql.Date masuk = new java.sql.Date(tanggal_keluar.getDate().getTime());
                    st.setDate(4, masuk);
                }

                JOptionPane.showMessageDialog(null, "Data Berhasil Disimpan");
                st.executeUpdate();
                st.close();
                this.barang_keluar_refresh();
            } catch(Exception e){
              JOptionPane.showMessageDialog(null, "Data Gagal Disimpan");
            }
            String query2 = "UPDATE barang SET stok = stok-? WHERE kd_barang=? ";
            try{
                st = (PreparedStatement)DB.getConnection().prepareStatement(query2);
                st.setInt(1, stok);
                st.setString(2, kdBarang);
                st.executeUpdate();st.close();
            }catch(Exception e){
                JOptionPane.showMessageDialog(null, "Pengurangan Stok Gagal");
            }
        }
        
        
    }//GEN-LAST:event_barangKeluarSaveActionPerformed

    private void barangKeluarDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_barangKeluarDeleteActionPerformed
        // TODO add your handling code here:
        int row = tabel_barang_keluar.getSelectedRow();
        PreparedStatement st;
        int id = Integer.parseInt(tabel_barang_keluar.getModel().getValueAt(row, 0).toString());
        String kdBarang = tabel_barang_keluar.getModel().getValueAt(row, 1).toString();
        String namaBarang = tabel_barang_keluar.getModel().getValueAt(row, 2).toString();
        int stok = Integer.parseInt(tabel_barang_keluar.getModel().getValueAt(row, 3).toString());
        DefaultTableModel model = (DefaultTableModel)tabel_barang_keluar.getModel();
        if(row>=0){
            int ok=JOptionPane.showConfirmDialog(null, "Yakin Mau Hapus?","Konfirmasi",JOptionPane.YES_NO_OPTION);
            if(ok==0){
                String query = "DELETE from barang_keluar WHERE id = ? ";
                try{
                    st = (PreparedStatement)DB.getConnection().prepareStatement(query);
                    st.setInt(1, id);
                    st.executeUpdate();
                    st.close();
                    JOptionPane.showMessageDialog(null, "Data Dihapus");
                    model.removeRow(row);
                    this.barang_keluar_refresh();
                    

                }catch(Exception e){

                }
                String query2 = "UPDATE barang SET stok = stok+? WHERE kd_barang=? ";
                try{
                    st = (PreparedStatement)DB.getConnection().prepareStatement(query2);
                    st.setInt(1, stok);
                    st.setString(2, kdBarang);
                    st.executeUpdate();st.close();
                }catch(Exception e){
                    JOptionPane.showMessageDialog(null, "Penambahan Stok Gagal(hapus barang keluar)");
                } 
            }
        }
    }//GEN-LAST:event_barangKeluarDeleteActionPerformed

    private void cetak_barang_masukActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cetak_barang_masukActionPerformed
        // TODO add your handling code here:
        try{
            String report = ("../../ManajemenInventory/src/reportData/barangMasuk.jrxml"); 
            HashMap hash = new HashMap();
            JasperReport jrr = JasperCompileManager.compileReport(report);
            JasperPrint jpp = JasperFillManager.fillReport(jrr, null, DB.getConnection());
            JasperViewer.viewReport(jpp, false);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Gagal karena "+e);
        }
    }//GEN-LAST:event_cetak_barang_masukActionPerformed

    private void cetak_barang_keluarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cetak_barang_keluarActionPerformed
        // TODO add your handling code here:
        try{
            String report = ("../../ManajemenInventory/src/reportData/barangKeluar.jrxml"); 
            HashMap hash = new HashMap();
            JasperReport jrr = JasperCompileManager.compileReport(report);
            JasperPrint jpp = JasperFillManager.fillReport(jrr, null, DB.getConnection());
            JasperViewer.viewReport(jpp, false);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Gagal karena "+e);
        }
    }//GEN-LAST:event_cetak_barang_keluarActionPerformed

    private void cetak_data_barangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cetak_data_barangActionPerformed
        // TODO add your handling code here:
        int row = tabel_barang.getSelectedRow();
        
        int column1 = 0;
        String kode = tabel_barang.getModel().getValueAt(row, column1).toString();
        kd_barang.setText(kode);
        try{
            if(row > 0){
                String report = ("../../ManajemenInventory/src/reportData/dataBarang.jrxml"); 
                HashMap hash = new HashMap<String, Object>();
                JasperReport jrr = JasperCompileManager.compileReport(report);
                hash.put("kode_barang", kd_barang.getText());
                JasperPrint jpp = JasperFillManager.fillReport(jrr, hash, DB.getConnection());
                JasperViewer.viewReport(jpp, false);
            }else{
                cetak_data_barang.setEnabled(false);
            }
            
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Gagal karena "+e);
        }
    }//GEN-LAST:event_cetak_data_barangActionPerformed

    private void cetak_gudangActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cetak_gudangActionPerformed
        // TODO add your handling code here:
         try{
            String report = ("../../ManajemenInventory/src/reportData/dataGudang.jrxml"); 
            HashMap hash = new HashMap();
            JasperReport jrr = JasperCompileManager.compileReport(report);
            JasperPrint jpp = JasperFillManager.fillReport(jrr, null, DB.getConnection());
            JasperViewer.viewReport(jpp, false);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Gagal karena "+e);
        }
    }//GEN-LAST:event_cetak_gudangActionPerformed

    private void cetak_supplierActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cetak_supplierActionPerformed
        // TODO add your handling code here:
        try{
            String report = ("../../ManajemenInventory/src/reportData/dataSupplier.jrxml"); 
            HashMap hash = new HashMap();
            JasperReport jrr = JasperCompileManager.compileReport(report);
            JasperPrint jpp = JasperFillManager.fillReport(jrr, null, DB.getConnection());
            JasperViewer.viewReport(jpp, false);
            
        }catch(Exception e){
            JOptionPane.showMessageDialog(null, "Gagal karena "+e);
        }
    }//GEN-LAST:event_cetak_supplierActionPerformed

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
            java.util.logging.Logger.getLogger(IndexAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(IndexAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(IndexAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(IndexAdmin.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                IndexAdmin admin = new IndexAdmin("DEFAULT");
                admin.setVisible(true);
                admin.setLocationRelativeTo(null);
                
                
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField alamat_supp;
    private javax.swing.JPanel barangKeluar;
    private javax.swing.JButton barangKeluarDelete;
    private javax.swing.JButton barangKeluarNew;
    private javax.swing.JButton barangKeluarSave;
    private javax.swing.JPanel barangMasuk;
    private javax.swing.JButton barangMasukDelete;
    private javax.swing.JButton barangMasukNew;
    private javax.swing.JButton barangMasukSave;
    private javax.swing.JLabel barang_keluar;
    private javax.swing.JLabel barang_masuk;
    private javax.swing.JButton cetak_barang_keluar;
    private javax.swing.JButton cetak_barang_masuk;
    private javax.swing.JButton cetak_data_barang;
    private javax.swing.JButton cetak_gudang;
    private javax.swing.JButton cetak_supplier;
    private javax.swing.JComboBox<String> cmb_barang_barang_keluar;
    private javax.swing.JComboBox<String> cmb_barang_barang_masuk;
    private javax.swing.JComboBox<String> cmb_gudang;
    private javax.swing.JComboBox<String> cmb_gudang_barang;
    private javax.swing.JComboBox<String> cmb_supplier_barang;
    private javax.swing.JPanel dataBarang;
    private javax.swing.JPanel dataGudang;
    private javax.swing.JPanel dataSupplier;
    private javax.swing.JPanel dataUser;
    private javax.swing.JLabel data_barang;
    private javax.swing.JButton delete_user;
    private javax.swing.JButton edit_user;
    private javax.swing.JButton gd_delete;
    private javax.swing.JButton gd_edit;
    private javax.swing.JButton gd_new;
    private javax.swing.JButton gd_save;
    private javax.swing.JPanel headPanel;
    private javax.swing.JButton jButton10;
    private javax.swing.JButton jButton11;
    private javax.swing.JButton jButton12;
    private javax.swing.JButton jButton13;
    private javax.swing.JButton jButton14;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButton6;
    private javax.swing.JButton jButton7;
    private javax.swing.JButton jButton8;
    private javax.swing.JButton jButton9;
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
    private javax.swing.JLabel jLabel43;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JTextField kd_barang;
    private javax.swing.JTextField kd_gudang;
    private javax.swing.JTextField keterangan_barang;
    private javax.swing.JTextField kode_supp;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField nama_barang;
    private javax.swing.JTextField nama_gudang;
    private javax.swing.JTextField nama_supp;
    private javax.swing.JTextField nama_user;
    private javax.swing.JPasswordField password_user;
    private javax.swing.JPasswordField repeat_user;
    private javax.swing.JComboBox<String> role_user;
    private javax.swing.JPanel sideBar;
    private javax.swing.JComboBox<String> status;
    private javax.swing.JTextField stok_barang_keluar;
    private javax.swing.JTextField stok_barang_masuk;
    private javax.swing.JTable tabel_barang;
    private javax.swing.JTable tabel_barang_keluar;
    private javax.swing.JTable tabel_barang_masuk;
    private javax.swing.JTable tabel_gudang;
    private javax.swing.JTable tabel_supplier;
    private javax.swing.JTable tabel_user;
    private com.toedter.calendar.JDateChooser tanggal_keluar;
    private com.toedter.calendar.JDateChooser tanggal_masuk;
    private javax.swing.JLabel timerText;
    private javax.swing.JTextField username_user;
    private javax.swing.JPanel welcomePage;
    // End of variables declaration//GEN-END:variables
}
