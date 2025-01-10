
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

public class PelangganForm extends javax.swing.JFrame {

    DefaultTableModel model;

    /**
     * Creates new form PelangganForm
     */
    public PelangganForm() {
        initComponents();      
        tampilkanData();
    }
    
    private void tampilkanData() {
    DefaultTableModel model = (DefaultTableModel) pelangganTable.getModel(); // Gunakan model yang sudah ada
    model.setRowCount(0); // Kosongkan tabel
    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery("SELECT * FROM pelanggan")) {
        while (rs.next()) {
            Object[] data = {
                rs.getInt("id_pelanggan"),
                rs.getString("nama"),
                rs.getString("alamat"),
                rs.getString("telepon"),
                rs.getString("jaminan") 
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan data: " + e.getMessage());
        e.printStackTrace();
    }
}
    
    private void clearForm() {
    namaTextField.setText("");
    AlamatTextArea1.setText("");
    teleponTextField.setText("");
    jaminanTextField.setText("");
}


   private void tambahPelanggan() {
    String nama = namaTextField.getText().trim();
    String alamat = AlamatTextArea1.getText().trim();
    String telepon = teleponTextField.getText().trim();
    String jaminan = jaminanTextField.getText().trim();

    if (nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty() || jaminan.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
        return;
    }

    String sql = "INSERT INTO pelanggan (nama, alamat, telepon, jaminan) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, nama);
        stmt.setString(2, alamat);
        stmt.setString(3, telepon);
        stmt.setString(4, jaminan);
        stmt.executeUpdate();
        JOptionPane.showMessageDialog(this, "Pelanggan berhasil ditambahkan.");
        tampilkanData(); // Refresh tabel
        clearForm(); // Bersihkan form setelah data ditambahkan
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menambahkan data: " + e.getMessage());
        e.printStackTrace();
    }
}



    private void hapusPelanggan() {
        int selectedRow = pelangganTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = Integer.parseInt(pelangganTable.getValueAt(selectedRow, 0).toString());
            String sql = "DELETE FROM pelanggan WHERE id_pelanggan = ?";
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
                JOptionPane.showMessageDialog(this, "Pelanggan berhasil dihapus.");
                 tampilkanData(); // Refresh tabel
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Gagal menghapus data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Pilih data yang ingin dihapus.");
        }
    }
private void updatePelanggan() {
    int selectedRow = pelangganTable.getSelectedRow();
    if (selectedRow != -1) {
        int id = Integer.parseInt(pelangganTable.getValueAt(selectedRow, 0).toString());
        String nama = namaTextField.getText().trim();
        String alamat = AlamatTextArea1.getText().trim();
        String telepon = teleponTextField.getText().trim();
        String jaminan = jaminanTextField.getText().trim();

        if (nama.isEmpty() || alamat.isEmpty() || telepon.isEmpty() || jaminan.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Semua field harus diisi!");
            return;
        }

        String sql = "UPDATE pelanggan SET nama = ?, alamat = ?, telepon = ?, jaminan = ? WHERE id_pelanggan = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nama);
            stmt.setString(2, alamat);
            stmt.setString(3, telepon);
            stmt.setString(4, jaminan);
            stmt.setInt(5, id);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Data pelanggan berhasil diperbarui.");
             tampilkanData(); // Refresh tabel
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Gagal memperbarui data: " + e.getMessage());
            e.printStackTrace();
        }
    } else {
        JOptionPane.showMessageDialog(this, "Pilih data yang ingin diperbarui.");
    }
}

private void cariPelanggan() {
    String namaCari = cariNamaTextField.getText().trim();  // Ambil nama dari field pencarian
    DefaultTableModel model = (DefaultTableModel) pelangganTable.getModel();
    model.setRowCount(0);  // Kosongkan tabel sebelum menampilkan hasil pencarian

    if (namaCari.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan nama pelanggan yang ingin dicari.");
        return; // Jangan lanjutkan jika tidak ada nama yang dicari
    }

    String sql = "SELECT * FROM pelanggan WHERE nama LIKE ?";  // Query untuk pencarian

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, "%" + namaCari + "%");  // Gunakan wildcard '%' untuk pencarian

        ResultSet rs = stmt.executeQuery();
        if (!rs.next()) {
            JOptionPane.showMessageDialog(this, "Data tidak ditemukan.");
        } else {
            do {
                Object[] data = {
                    rs.getInt("id_pelanggan"),
                    rs.getString("nama"),
                    rs.getString("alamat"),
                    rs.getString("telepon"),
                    rs.getString("jaminan")
                };
                model.addRow(data);
            } while (rs.next());
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
        namaTextField = new javax.swing.JTextField();
        teleponTextField = new javax.swing.JTextField();
        tambahPelanggan = new javax.swing.JButton();
        hapusPelanggan = new javax.swing.JButton();
        updatePelanggan = new javax.swing.JButton();
        caribtn = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pelangganTable = new javax.swing.JTable();
        kembaliPelangganButton = new javax.swing.JButton();
        jaminanTextField = new javax.swing.JTextField();
        jScrollPane2 = new javax.swing.JScrollPane();
        AlamatTextArea1 = new javax.swing.JTextArea();
        cariNamaTextField = new javax.swing.JTextField();
        refreshButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));
        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Pelanggan", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 36))); // NOI18N
        jPanel1.setForeground(new java.awt.Color(57, 62, 70));

        namaTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Nama"));
        namaTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaTextFieldActionPerformed(evt);
            }
        });

        teleponTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Telpon"));
        teleponTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                teleponTextFieldKeyTyped(evt);
            }
        });

        tambahPelanggan.setBackground(new java.awt.Color(0, 173, 181));
        tambahPelanggan.setForeground(new java.awt.Color(255, 255, 255));
        tambahPelanggan.setText("Tambah");
        tambahPelanggan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tambahPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahPelangganActionPerformed(evt);
            }
        });

        hapusPelanggan.setBackground(new java.awt.Color(0, 173, 181));
        hapusPelanggan.setForeground(new java.awt.Color(255, 255, 255));
        hapusPelanggan.setText("Hapus");
        hapusPelanggan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        hapusPelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusPelangganActionPerformed(evt);
            }
        });

        updatePelanggan.setBackground(new java.awt.Color(0, 173, 181));
        updatePelanggan.setForeground(new java.awt.Color(255, 255, 255));
        updatePelanggan.setText("Update");
        updatePelanggan.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        updatePelanggan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                updatePelangganActionPerformed(evt);
            }
        });

        caribtn.setBackground(new java.awt.Color(0, 173, 181));
        caribtn.setForeground(new java.awt.Color(255, 255, 255));
        caribtn.setText("Cari");
        caribtn.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        caribtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                caribtnActionPerformed(evt);
            }
        });

        pelangganTable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pelangganTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "Id", "Nama", "Alamat", "Telpon", "Jaminan"
            }
        ));
        pelangganTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                pelangganTableMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(pelangganTable);

        kembaliPelangganButton.setBackground(new java.awt.Color(0, 173, 181));
        kembaliPelangganButton.setForeground(new java.awt.Color(255, 255, 255));
        kembaliPelangganButton.setText("Kembali");
        kembaliPelangganButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kembaliPelangganButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembaliPelangganButtonActionPerformed(evt);
            }
        });

        jaminanTextField.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Jaminan"));

        AlamatTextArea1.setColumns(20);
        AlamatTextArea1.setRows(5);
        AlamatTextArea1.setBorder(javax.swing.BorderFactory.createTitledBorder("Alamat"));
        jScrollPane2.setViewportView(AlamatTextArea1);

        refreshButton.setBackground(new java.awt.Color(0, 173, 181));
        refreshButton.setForeground(new java.awt.Color(255, 255, 255));
        refreshButton.setText("Refresh");
        refreshButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cariNamaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(caribtn, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(52, 52, 52)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(teleponTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                                            .addComponent(namaTextField)
                                            .addComponent(jaminanTextField))
                                        .addGroup(jPanel1Layout.createSequentialGroup()
                                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addGap(18, 18, 18)
                                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                .addComponent(hapusPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(tambahPelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(updatePelanggan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(refreshButton, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addGroup(jPanel1Layout.createSequentialGroup()
                                    .addGap(20, 20, 20)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(0, 15, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(kembaliPelangganButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(kembaliPelangganButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(namaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(teleponTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(tambahPelanggan)
                        .addGap(18, 18, 18)
                        .addComponent(hapusPelanggan)
                        .addGap(21, 21, 21)
                        .addComponent(updatePelanggan)
                        .addGap(18, 18, 18)
                        .addComponent(refreshButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jaminanTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cariNamaTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(caribtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(196, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(25, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void namaTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaTextFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaTextFieldActionPerformed

    private void tambahPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahPelangganActionPerformed
         tambahPelanggan();// TODO add your handling code here:
    }//GEN-LAST:event_tambahPelangganActionPerformed

    private void hapusPelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusPelangganActionPerformed
        hapusPelanggan();// TODO add your handling code here:
    }//GEN-LAST:event_hapusPelangganActionPerformed

    private void updatePelangganActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_updatePelangganActionPerformed
        updatePelanggan();// TODO add your handling code here:
    }//GEN-LAST:event_updatePelangganActionPerformed

    private void caribtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_caribtnActionPerformed
        cariPelanggan();        // TODO add your handling code here:
    }//GEN-LAST:event_caribtnActionPerformed

    private void kembaliPelangganButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembaliPelangganButtonActionPerformed
         this.dispose(); // Menutup PelangganForm
    MainMenu mainMenu = MainMenu.getInstance(); // Gunakan instance MainMenu yang sudah ada
    mainMenu.setVisible(true); // Tampilkan MainMenu        // TODO add your handling code here:
    }//GEN-LAST:event_kembaliPelangganButtonActionPerformed

    private void pelangganTableMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_pelangganTableMouseClicked
        // Ambil baris yang dipilih
    int selectedRow = pelangganTable.getSelectedRow();

    // Pastikan ada baris yang dipilih
    if (selectedRow != -1) {
        // Ambil data dari tabel
        String nama = pelangganTable.getValueAt(selectedRow, 1).toString();
        String alamat = pelangganTable.getValueAt(selectedRow, 2).toString();
        String telepon = pelangganTable.getValueAt(selectedRow, 3).toString();
        String jaminan = pelangganTable.getValueAt(selectedRow, 4).toString();

        // Tampilkan data di JTextField
        namaTextField.setText(nama);
        AlamatTextArea1.setText(alamat);
        teleponTextField.setText(telepon);
        jaminanTextField.setText(jaminan);
    }        // TODO add your handling code here:
    }//GEN-LAST:event_pelangganTableMouseClicked

    private void refreshButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshButtonActionPerformed
         tampilkanData();       // TODO add your handling code here:
    }//GEN-LAST:event_refreshButtonActionPerformed

    private void teleponTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_teleponTextFieldKeyTyped
        teleponTextField.addKeyListener(new java.awt.event.KeyAdapter() {
    public void keyTyped(java.awt.event.KeyEvent evt) {
        char c = evt.getKeyChar();
        // Memastikan hanya angka yang bisa dimasukkan dan maksimal 13 digit
        if (!Character.isDigit(c)) {
            evt.consume();  // Menyaring input yang bukan angka
        }
        // Membatasi panjang input menjadi 13 karakter
        if (teleponTextField.getText().length() >= 13) {
            evt.consume();  // Tidak mengizinkan karakter lebih dari 13
        }
    }
});
        // TODO add your handling code here:
    }//GEN-LAST:event_teleponTextFieldKeyTyped

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
       java.awt.EventQueue.invokeLater(() -> new PelangganForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextArea AlamatTextArea1;
    private javax.swing.JTextField cariNamaTextField;
    private javax.swing.JButton caribtn;
    private javax.swing.JButton hapusPelanggan;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jaminanTextField;
    private javax.swing.JButton kembaliPelangganButton;
    private javax.swing.JTextField namaTextField;
    private javax.swing.JTable pelangganTable;
    private javax.swing.JButton refreshButton;
    private javax.swing.JButton tambahPelanggan;
    private javax.swing.JTextField teleponTextField;
    private javax.swing.JButton updatePelanggan;
    // End of variables declaration//GEN-END:variables
}
