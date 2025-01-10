
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Putra
 */
public class PenyewaanForm extends javax.swing.JFrame {

    DefaultTableModel model;
    
    public PenyewaanForm() {
        initComponents();
        isiComboBoxPelanggan();
        isiComboBoxPC();
        inisialisasiTabel();
        tampilkanData();
    }
    
    private void inisialisasiTabel() {
    String[] kolom = {"ID", "Pelanggan", "PC", "Tanggal Sewa", "Lama Sewa", "Total Biaya"};
    model = new DefaultTableModel(null, kolom);
    penyewaanTable.setModel(model);
}
    
    private void isiComboBoxPelanggan() {
    pelangganComboBox.removeAllItems();
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT id_pelanggan, nama FROM pelanggan")) {
        while (rs.next()) {
            pelangganComboBox.addItem(rs.getInt("id_pelanggan") + " - " + rs.getString("nama"));
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data pelanggan: " + e.getMessage());
    }
}

private void isiComboBoxPC() {
    pcComboBox.removeAllItems();
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT id_pc, jenis_pc FROM pc")) {
        while (rs.next()) {
            pcComboBox.addItem(rs.getInt("id_pc") + " - " + rs.getString("jenis_pc"));
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal memuat data PC: " + e.getMessage());
    }
}

private void clearForm() {
    pelangganComboBox.setSelectedIndex(0); // Reset ComboBox ke item pertama
    pcComboBox.setSelectedIndex(0); // Reset ComboBox ke item pertama
    lamaSewaTextField.setText(""); // Kosongkan field lama sewa
}


 private void tambahTransaksi() {
    String pelanggan = pelangganComboBox.getSelectedItem().toString();
    String pc = pcComboBox.getSelectedItem().toString();
    int lamaSewa = Integer.parseInt(lamaSewaTextField.getText());

    int idPelanggan = Integer.parseInt(pelanggan.split(" - ")[0]);
    int idPC = Integer.parseInt(pc.split(" - ")[0]);

    String sqlPC = "SELECT harga_per_jam FROM pc WHERE id_pc = ?";
    String sqlInsert = "INSERT INTO penyewaan (id_pelanggan, id_pc, tanggal_sewa, lama_sewa, total_biaya) VALUES (?, ?, NOW(), ?, ?)";

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmtPC = conn.prepareStatement(sqlPC);
         PreparedStatement stmtInsert = conn.prepareStatement(sqlInsert)) {

        // Hitung total biaya
        stmtPC.setInt(1, idPC);
        ResultSet rs = stmtPC.executeQuery();
        rs.next();
        double hargaPerJam = rs.getDouble("harga_per_jam");
        double totalBiaya = lamaSewa * hargaPerJam;

        // Masukkan transaksi
        stmtInsert.setInt(1, idPelanggan);
        stmtInsert.setInt(2, idPC);
        stmtInsert.setInt(3, lamaSewa);
        stmtInsert.setDouble(4, totalBiaya);
        stmtInsert.executeUpdate();

        JOptionPane.showMessageDialog(this, "Transaksi berhasil ditambahkan!");
        tampilkanData(); // Refresh tabel
        clearForm(); // Bersihkan form
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menambahkan transaksi: " + e.getMessage());
        e.printStackTrace();
    }
}


    private void tampilkanData() {
    model.setRowCount(0); // Kosongkan tabel
    String sql = "SELECT penyewaan.id_penyewaan, pelanggan.nama, pc.jenis_pc, penyewaan.tanggal_sewa, penyewaan.lama_sewa, penyewaan.total_biaya " +
                 "FROM penyewaan " +
                 "JOIN pelanggan ON penyewaan.id_pelanggan = pelanggan.id_pelanggan " +
                 "JOIN pc ON penyewaan.id_pc = pc.id_pc";
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            Object[] data = {
                rs.getInt("id_penyewaan"),
                rs.getString("nama"),
                rs.getString("jenis_pc"),
                rs.getDate("tanggal_sewa"),
                rs.getInt("lama_sewa"),
                rs.getDouble("total_biaya")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void hapusTransaksi() {
    int selectedRow = penyewaanTable.getSelectedRow();
    if (selectedRow != -1) {
        int id = Integer.parseInt(penyewaanTable.getValueAt(selectedRow, 0).toString());
        String sql = "DELETE FROM penyewaan WHERE id_penyewaan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Transaksi berhasil dihapus.");
            tampilkanData(); // Refresh tabel
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Pilih transaksi yang ingin dihapus.");
    }
}
    
    private void cariPenyewaan(java.awt.event.ActionEvent evt) {
    String namaPelanggan = cariNamaTextField.getText().trim();  // Ambil nama pelanggan dari input

    DefaultTableModel model = (DefaultTableModel) penyewaanTable.getModel();
    model.setRowCount(0);  // Kosongkan tabel sebelum menampilkan hasil pencarian

    String sql = "SELECT penyewaan.id_penyewaan, pelanggan.nama, pc.jenis_pc, penyewaan.tanggal_sewa, penyewaan.lama_sewa, penyewaan.total_biaya " +
                 "FROM penyewaan " +
                 "JOIN pelanggan ON penyewaan.id_pelanggan = pelanggan.id_pelanggan " +
                 "JOIN pc ON penyewaan.id_pc = pc.id_pc " +
                 "WHERE pelanggan.nama LIKE ? OR pc.jenis_pc LIKE ?";  // Pencarian berdasarkan nama atau jenis PC

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Siapkan parameter pencarian
        stmt.setString(1, "%" + namaPelanggan + "%");
        stmt.setString(2, "%" + namaPelanggan + "%");  // Mencari di kedua kolom: nama dan jenis PC

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Object[] data = {
                rs.getInt("id_penyewaan"),
                rs.getString("nama"),
                rs.getString("jenis_pc"),
                rs.getDate("tanggal_sewa"),
                rs.getInt("lama_sewa"),
                rs.getDouble("total_biaya")
            };
            model.addRow(data);  // Menambahkan hasil pencarian ke tabel
        }

        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal mencari data: " + e.getMessage());
        e.printStackTrace();
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

        jPanel1 = new javax.swing.JPanel();
        pelangganComboBox = new javax.swing.JComboBox<>();
        pcComboBox = new javax.swing.JComboBox<>();
        lamaSewaTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        penyewaanTable = new javax.swing.JTable();
        tambahButton = new javax.swing.JButton();
        hapusButton = new javax.swing.JButton();
        tampilkanButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        kembaliPenyewaanButton = new javax.swing.JButton();
        cariNamaTextField = new javax.swing.JTextField();
        cariButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Penyewaan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 36))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(57, 62, 70));

        pelangganComboBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        pelangganComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pelangganComboBoxActionPerformed(evt);
            }
        });

        pcComboBox.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        lamaSewaTextField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        lamaSewaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lamaSewaTextFieldActionPerformed(evt);
            }
        });
        lamaSewaTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                lamaSewaTextFieldKeyTyped(evt);
            }
        });

        jLabel4.setText("Lama Sewa");

        penyewaanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "Id", "Pelanggan", "PC", "Tanggal Sewa", "Lama Sewa", "Total Biaya"
            }
        ));
        jScrollPane1.setViewportView(penyewaanTable);

        tambahButton.setBackground(new java.awt.Color(0, 173, 181));
        tambahButton.setForeground(new java.awt.Color(255, 255, 255));
        tambahButton.setText("Tambah Transaksi");
        tambahButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tambahButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahButtonActionPerformed(evt);
            }
        });

        hapusButton.setBackground(new java.awt.Color(0, 173, 181));
        hapusButton.setForeground(new java.awt.Color(255, 255, 255));
        hapusButton.setText("Hapus Transaksi");
        hapusButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hapusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusButtonActionPerformed(evt);
            }
        });

        tampilkanButton.setBackground(new java.awt.Color(0, 173, 181));
        tampilkanButton.setForeground(new java.awt.Color(255, 255, 255));
        tampilkanButton.setText("Refresh");
        tampilkanButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tampilkanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampilkanButtonActionPerformed(evt);
            }
        });

        jLabel1.setText("Nama Pelanggan");

        jLabel2.setText("Jenis PC");

        kembaliPenyewaanButton.setBackground(new java.awt.Color(0, 173, 181));
        kembaliPenyewaanButton.setForeground(new java.awt.Color(255, 255, 255));
        kembaliPenyewaanButton.setText("Kembali");
        kembaliPenyewaanButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kembaliPenyewaanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembaliPenyewaanButtonActionPerformed(evt);
            }
        });

        cariButton.setBackground(new java.awt.Color(0, 173, 181));
        cariButton.setForeground(new java.awt.Color(255, 255, 255));
        cariButton.setText("Cari");
        cariButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cariButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(53, 53, 53)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel2)
                    .addComponent(tambahButton))
                .addGap(26, 26, 26)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lamaSewaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(pcComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, 113, Short.MAX_VALUE)
                        .addComponent(pelangganComboBox, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(hapusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(42, 42, 42)
                        .addComponent(tampilkanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(33, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 596, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(cariNamaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(cariButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)))
                        .addGap(26, 26, 26))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(kembaliPenyewaanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(kembaliPenyewaanButton)
                .addGap(20, 20, 20)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(pelangganComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(pcComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lamaSewaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(38, 38, 38)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tambahButton)
                    .addComponent(hapusButton)
                    .addComponent(tampilkanButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cariNamaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cariButton))
                .addGap(62, 62, 62))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void lamaSewaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lamaSewaTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lamaSewaTextFieldActionPerformed

    private void tambahButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahButtonActionPerformed
        tambahTransaksi();// TODO add your handling code here:
    }//GEN-LAST:event_tambahButtonActionPerformed

    private void hapusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusButtonActionPerformed
       hapusTransaksi(); // TODO add your handling code here:
    }//GEN-LAST:event_hapusButtonActionPerformed

    private void tampilkanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tampilkanButtonActionPerformed
       tampilkanData(); // TODO add your handling code here:
    }//GEN-LAST:event_tampilkanButtonActionPerformed

    private void kembaliPenyewaanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembaliPenyewaanButtonActionPerformed
        this.dispose(); // Menutup PenyewaanForm
    MainMenu mainMenu = MainMenu.getInstance(); // Gunakan instance MainMenu yang sudah ada
    mainMenu.setVisible(true); // Tampilkan MainMenu        // TODO add your handling code here:
    }//GEN-LAST:event_kembaliPenyewaanButtonActionPerformed

    private void pelangganComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pelangganComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_pelangganComboBoxActionPerformed

    private void cariButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariButtonActionPerformed
        cariPenyewaan(evt);         // TODO add your handling code here:
    }//GEN-LAST:event_cariButtonActionPerformed

    private void lamaSewaTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_lamaSewaTextFieldKeyTyped
        // Pada bagian inisialisasi komponen, tambahkan KeyListener
        lamaSewaTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Memastikan hanya angka dan titik desimal yang bisa dimasukkan
                if (!Character.isDigit(c) && c != '.' && c != '\b') {
                    evt.consume();  // Menyaring input yang tidak diinginkan
                }
            }
        });
        // TODO add your handling code here:
    }//GEN-LAST:event_lamaSewaTextFieldKeyTyped

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
            java.util.logging.Logger.getLogger(PenyewaanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PenyewaanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PenyewaanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PenyewaanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PenyewaanForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cariButton;
    private javax.swing.JTextField cariNamaTextField;
    private javax.swing.JButton hapusButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JButton kembaliPenyewaanButton;
    private javax.swing.JTextField lamaSewaTextField;
    private javax.swing.JComboBox<String> pcComboBox;
    private javax.swing.JComboBox<String> pelangganComboBox;
    private javax.swing.JTable penyewaanTable;
    private javax.swing.JButton tambahButton;
    private javax.swing.JButton tampilkanButton;
    // End of variables declaration//GEN-END:variables
}
