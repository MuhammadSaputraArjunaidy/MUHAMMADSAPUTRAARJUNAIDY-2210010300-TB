
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
public class PcForm extends javax.swing.JFrame {

    DefaultTableModel model;

    private void inisialisasiTabel() {
        String[] kolom = {"ID", "Jenis PC", "Spesifikasi", "Harga Per Jam"};
        model = new DefaultTableModel(null, kolom);
        pcTable.setModel(model);
    }
    public PcForm() {
        initComponents();
        inisialisasiTabel();
        tampilkanData();
    }
    
    private void tampilkanData() {
    model.setRowCount(0); // Kosongkan tabel
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM pc")) {
        while (rs.next()) {
            Object[] data = {
                rs.getInt("id_pc"),
                rs.getString("jenis_pc"),
                rs.getString("spesifikasi"),
                rs.getDouble("harga_per_jam")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        e.printStackTrace();
    }
}

    private void clearForm() {
    jenisPcTextField.setText("");
    spesifikasiTextField.setText("");
    hargaTextField.setText("");
}

    
    private void tambahPc() {
    String jenisPc = jenisPcTextField.getText().trim();
    String spesifikasi = spesifikasiTextField.getText().trim();
    String harga = hargaTextField.getText().trim();

    if (jenisPc.isEmpty() || spesifikasi.isEmpty() || harga.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
        return;
    }

    String sql = "INSERT INTO pc (jenis_pc, spesifikasi, harga_per_jam) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, jenisPc);
        stmt.setString(2, spesifikasi);
        stmt.setDouble(3, Double.parseDouble(harga));
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "PC berhasil ditambahkan.");
        tampilkanData(); // Refresh tabel
        clearForm(); // Bersihkan form
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menambahkan data: " + e.getMessage());
        e.printStackTrace();
    }
}


    private void hapusPc() {
    int selectedRow = pcTable.getSelectedRow();
    if (selectedRow != -1) {
        int id = Integer.parseInt(pcTable.getValueAt(selectedRow, 0).toString());
        String sql = "DELETE FROM pc WHERE id_pc = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "PC berhasil dihapus.");
            tampilkanData(); // Refresh tabel
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
    }
}

    private void updatePc() {
    int selectedRow = pcTable.getSelectedRow();
    if (selectedRow != -1) {
        int id = Integer.parseInt(pcTable.getValueAt(selectedRow, 0).toString());
        String jenisPc = jenisPcTextField.getText().trim();
        String spesifikasi = spesifikasiTextField.getText().trim();
        String harga = hargaTextField.getText().trim();

        if (jenisPc.isEmpty() || spesifikasi.isEmpty() || harga.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        String sql = "UPDATE pc SET jenis_pc = ?, spesifikasi = ?, harga_per_jam = ? WHERE id_pc = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, jenisPc);
            stmt.setString(2, spesifikasi);
            stmt.setDouble(3, Double.parseDouble(harga));
            stmt.setInt(4, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "PC berhasil diperbarui.");
            tampilkanData(); // Refresh tabel
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diperbarui.");
    }
}
    
    private void cariPc(java.awt.event.ActionEvent evt) {
    String jenisPcCari = cariJenisPcTextField.getText().trim();  // Ambil input dari pencarian

    if (jenisPcCari.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan jenis PC yang ingin dicari.");
        return;  // Jangan lanjutkan jika input kosong
    }

    DefaultTableModel model = (DefaultTableModel) pcTable.getModel();
    model.setRowCount(0);  // Kosongkan tabel sebelum menampilkan hasil pencarian

    String sql = "SELECT * FROM pc WHERE jenis_pc LIKE ?";  // Query pencarian berdasarkan jenis PC

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + jenisPcCari + "%");  // Gunakan wildcard '%' untuk pencarian

        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Object[] data = {
                rs.getInt("id_pc"),
                rs.getString("jenis_pc"),
                rs.getString("spesifikasi"),
                rs.getDouble("harga_per_jam")
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
        jenisPcTextField = new javax.swing.JTextField();
        spesifikasiTextField = new javax.swing.JTextField();
        hargaTextField = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        pcTable = new javax.swing.JTable();
        tambahButton = new javax.swing.JButton();
        hapusButton = new javax.swing.JButton();
        updateButton = new javax.swing.JButton();
        cariButton = new javax.swing.JButton();
        kembaliPCButton = new javax.swing.JButton();
        cariJenisPcTextField = new javax.swing.JTextField();
        refreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "PC", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 36))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(57, 62, 70));

        jenisPcTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Jenis PC"));

        spesifikasiTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Spesifikasi"));

        hargaTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Harga/Jam"));
        hargaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hargaTextFieldActionPerformed(evt);
            }
        });
        hargaTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                hargaTextFieldKeyTyped(evt);
            }
        });

        pcTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Jenis PC", "Specifikasi", "Harga/Jam"
            }
        ));
        pcTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pcTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(pcTable);

        tambahButton.setBackground(new java.awt.Color(0, 173, 181));
        tambahButton.setForeground(new java.awt.Color(255, 255, 255));
        tambahButton.setText("Tambah");
        tambahButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tambahButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        tambahButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahButtonActionPerformed(evt);
            }
        });

        hapusButton.setBackground(new java.awt.Color(0, 173, 181));
        hapusButton.setForeground(new java.awt.Color(255, 255, 255));
        hapusButton.setText("Hapus");
        hapusButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hapusButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        hapusButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusButtonActionPerformed(evt);
            }
        });

        updateButton.setBackground(new java.awt.Color(0, 173, 181));
        updateButton.setForeground(new java.awt.Color(255, 255, 255));
        updateButton.setText("Update");
        updateButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        updateButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        updateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updateButtonActionPerformed(evt);
            }
        });

        cariButton.setBackground(new java.awt.Color(0, 173, 181));
        cariButton.setForeground(new java.awt.Color(255, 255, 255));
        cariButton.setText("Cari");
        cariButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        cariButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        cariButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariButtonActionPerformed(evt);
            }
        });

        kembaliPCButton.setBackground(new java.awt.Color(0, 173, 181));
        kembaliPCButton.setForeground(new java.awt.Color(255, 255, 255));
        kembaliPCButton.setText("Kembali");
        kembaliPCButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kembaliPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembaliPCButtonActionPerformed(evt);
            }
        });

        refreshButton.setBackground(new java.awt.Color(0, 173, 181));
        refreshButton.setForeground(new java.awt.Color(255, 255, 255));
        refreshButton.setText("Refresh");
        refreshButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refreshButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jenisPcTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spesifikasiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(hargaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(kembaliPCButton, javax.swing.GroupLayout.PREFERRED_SIZE, 63, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(21, 21, 21))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(hapusButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(tambahButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(updateButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(cariJenisPcTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(cariButton, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(33, 33, 33)
                                .addComponent(jenisPcTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap()
                                .addComponent(kembaliPCButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(spesifikasiTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(hargaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(43, 43, 43)
                        .addComponent(tambahButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(hapusButton)
                        .addGap(18, 18, 18)
                        .addComponent(updateButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refreshButton)))
                .addGap(23, 23, 23)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cariButton)
                    .addComponent(cariJenisPcTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(145, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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

    private void hargaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hargaTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hargaTextFieldActionPerformed

    private void tambahButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahButtonActionPerformed
       tambahPc(); // TODO add your handling code here:
    }//GEN-LAST:event_tambahButtonActionPerformed

    private void hapusButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusButtonActionPerformed
        hapusPc();// TODO add your handling code here:
    }//GEN-LAST:event_hapusButtonActionPerformed

    private void updateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updateButtonActionPerformed
        updatePc();// TODO add your handling code here:
    }//GEN-LAST:event_updateButtonActionPerformed

    private void cariButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariButtonActionPerformed
         cariPc(evt);// TODO add your handling code here:
    }//GEN-LAST:event_cariButtonActionPerformed

    private void kembaliPCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembaliPCButtonActionPerformed
        this.dispose(); // Menutup form saat ini
    MainMenu mainMenu = MainMenu.getInstance(); // Dapatkan instance MainMenu yang sudah ada
    mainMenu.setVisible(true); // Tampilkan MainMenu
    }//GEN-LAST:event_kembaliPCButtonActionPerformed

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
        tampilkanData();        // TODO add your handling code here:
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void hargaTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_hargaTextFieldKeyTyped
        // Pada bagian inisialisasi komponen, tambahkan KeyListener
        hargaTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                char c = evt.getKeyChar();
                // Memastikan hanya angka dan titik desimal yang bisa dimasukkan
                if (!Character.isDigit(c) && c != '.' && c != '\b') {
                    evt.consume();  // Menyaring input yang tidak diinginkan
                }
            }
        });
        // TODO add your handling code here:
    }//GEN-LAST:event_hargaTextFieldKeyTyped

    private void pcTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pcTableMouseClicked
        int selectedRow = pcTable.getSelectedRow(); // Ambil baris yang diklik
    if (selectedRow != -1) {
        // Ambil data dari setiap kolom pada baris yang diklik
        String id = pcTable.getValueAt(selectedRow, 0).toString();
        String jenisPc = pcTable.getValueAt(selectedRow, 1).toString();
        String spesifikasi = pcTable.getValueAt(selectedRow, 2).toString();
        String harga = pcTable.getValueAt(selectedRow, 3).toString();

        // Tampilkan data ke JTextField
        jenisPcTextField.setText(jenisPc);
        spesifikasiTextField.setText(spesifikasi);
        hargaTextField.setText(harga);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_pcTableMouseClicked

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
            java.util.logging.Logger.getLogger(PcForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(PcForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(PcForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(PcForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new PcForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cariButton;
    private javax.swing.JTextField cariJenisPcTextField;
    private javax.swing.JButton hapusButton;
    private javax.swing.JTextField hargaTextField;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jenisPcTextField;
    private javax.swing.JButton kembaliPCButton;
    private javax.swing.JTable pcTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JTextField spesifikasiTextField;
    private javax.swing.JButton tambahButton;
    private javax.swing.JButton updateButton;
    // End of variables declaration//GEN-END:variables
}
