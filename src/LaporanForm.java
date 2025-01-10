
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileOutputStream;


public class LaporanForm extends javax.swing.JFrame {

    DefaultTableModel model;

    public LaporanForm() {
        initComponents();
        inisialisasiTabelPendapatan();
        isiComboBoxPelanggan();
    }
    
   private void inisialisasiTabelPendapatan() {
    String[] kolom = {"Periode", "Total Pendapatan"};
    DefaultTableModel pendapatanModel = new DefaultTableModel(null, kolom);
    pendapatanTable.setModel(pendapatanModel);
}

    
    private void inisialisasiTabelPelanggan() {
    String[] kolom = {"ID Transaksi", "Tanggal Sewa", "Lama Sewa (jam)", "Total Biaya"};
    model = new DefaultTableModel(null, kolom);
    pendapatanTable.setModel(model);
}
    
    private void inisialisasiTabelPC() {
    String[] kolom = {"Jenis PC", "Total Disewa", "Total Pendapatan"};
    DefaultTableModel pcModel = new DefaultTableModel(null, kolom);
    pcTable.setModel(pcModel);
}

    private void tampilkanTabelPendapatan() {
    pendapatanTable.setVisible(true);
    pelangganTable.setVisible(false);
    pcTable.setVisible(false);
}

private void tampilkanTabelPelanggan() {
    pendapatanTable.setVisible(false);
    pelangganTable.setVisible(true);
    pcTable.setVisible(false);
}

private void tampilkanTabelPC() {
    pendapatanTable.setVisible(false);
    pelangganTable.setVisible(false);
    pcTable.setVisible(true);
}

    
   private void tampilkanLaporanPendapatan() {
    // Ambil tanggal awal dan tanggal akhir
    String tanggalAwal = getTanggalAwal();  // Metode getTanggalAwal() sudah Anda buat sebelumnya
    String tanggalAkhir = getTanggalAkhir(); // Metode getTanggalAkhir() sudah Anda buat sebelumnya

    // Query dasar
    String sql = "SELECT DATE(tanggal_sewa) AS periode, SUM(total_biaya) AS total_pendapatan " +
                 "FROM penyewaan ";

    // Kondisi jika tanggal awal dan akhir dipilih
    if (tanggalAwal != null && tanggalAkhir != null) {
        sql += "WHERE DATE(tanggal_sewa) BETWEEN ? AND ? ";
    }

    sql += "GROUP BY DATE(tanggal_sewa)";  // Kelompokkan berdasarkan tanggal sewa

    DefaultTableModel model = (DefaultTableModel) pendapatanTable.getModel();
    model.setRowCount(0); // Kosongkan tabel sebelum menampilkan data

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        // Set parameter tanggal jika ada rentang tanggal
        if (tanggalAwal != null && tanggalAkhir != null) {
            stmt.setString(1, tanggalAwal);
            stmt.setString(2, tanggalAkhir);
        }

        // Eksekusi query dan ambil data
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            Object[] data = {
                rs.getDate("periode"),
                rs.getDouble("total_pendapatan")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan pendapatan: " + e.getMessage());
        e.printStackTrace();
    }
}



    
    private String getTanggalAwal() { 
    if (tanggalAwalChooser.getDate() != null) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(tanggalAwalChooser.getDate());
    } else {
        return null;
    }
}

private String getTanggalAkhir() {
    if (tanggalAkhirChooser.getDate() != null) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(tanggalAkhirChooser.getDate());
    } else {
        return null;
    }
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

private void tampilkanLaporanPelanggan() {
    String pelanggan = (String) pelangganComboBox.getSelectedItem();
    if (pelanggan == null || pelanggan.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Pilih pelanggan terlebih dahulu!");
        return;
    }

    int idPelanggan = Integer.parseInt(pelanggan.split(" - ")[0]);
    String sql = "SELECT penyewaan.id_penyewaan, penyewaan.tanggal_sewa, penyewaan.lama_sewa, penyewaan.total_biaya " +
                 "FROM penyewaan " +
                 "WHERE penyewaan.id_pelanggan = ?";
    DefaultTableModel model = (DefaultTableModel) pelangganTable.getModel();
    model.setRowCount(0);

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setInt(1, idPelanggan);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Object[] data = {
                rs.getInt("id_penyewaan"),
                rs.getDate("tanggal_sewa"),
                rs.getInt("lama_sewa"),
                rs.getDouble("total_biaya")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan pelanggan: " + e.getMessage());
        e.printStackTrace();
    }
}


private void tampilkanLaporanPC() {
    inisialisasiTabelPC(); // Atur ulang kolom tabel
    String sql = "SELECT pc.jenis_pc, COUNT(penyewaan.id_penyewaan) AS total_disewa, SUM(penyewaan.total_biaya) AS total_pendapatan " +
                 "FROM penyewaan " +
                 "JOIN pc ON penyewaan.id_pc = pc.id_pc " +
                 "GROUP BY pc.jenis_pc";

    DefaultTableModel model = (DefaultTableModel) pcTable.getModel();
    model.setRowCount(0); // Kosongkan tabel

    try (Connection conn = DatabaseConnection.getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        while (rs.next()) {
            Object[] data = {
                rs.getString("jenis_pc"),
                rs.getInt("total_disewa"),
                rs.getDouble("total_pendapatan")
            };
            model.addRow(data);
        }
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menampilkan laporan PC: " + e.getMessage());
        e.printStackTrace();
    }
}

private void exportToPdf(JTable table, String filePath) {
    Document document = new Document();
    try {
        PdfWriter.getInstance(document, new FileOutputStream(filePath));
        document.open();

        // Tambahkan judul dokumen
        Font titleFont = new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD);
        Paragraph title = new Paragraph("Laporan Data", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph("\n")); // Spasi setelah judul

        // Membuat tabel PDF
        PdfPTable pdfTable = new PdfPTable(table.getColumnCount());
        pdfTable.setWidthPercentage(100);
        pdfTable.setSpacingBefore(10f);

        // Tambahkan header tabel
        for (int i = 0; i < table.getColumnCount(); i++) {
            PdfPCell cell = new PdfPCell(new Phrase(table.getColumnName(i)));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            pdfTable.addCell(cell);
        }

        // Tambahkan data baris
        for (int i = 0; i < table.getRowCount(); i++) {
            for (int j = 0; j < table.getColumnCount(); j++) {
                Object value = table.getValueAt(i, j);
                pdfTable.addCell(value == null ? "" : value.toString());
            }
        }

        // Tambahkan tabel ke dokumen
        document.add(pdfTable);

        JOptionPane.showMessageDialog(this, "Data berhasil diexport ke PDF: " + filePath);
    } catch (Exception e) {
        JOptionPane.showMessageDialog(this, "Gagal menulis file PDF: " + e.getMessage());
        e.printStackTrace();
    } finally {
        document.close();
    }
}






    
    

   
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        tampilkanPendapatanButton = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        pendapatanTable = new javax.swing.JTable();
        tanggalAwalChooser = new com.toedter.calendar.JDateChooser();
        tanggalAkhirChooser = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        exportPendapatanButton = new javax.swing.JButton();
        kembaliPendapatanButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        pcTable = new javax.swing.JTable();
        tampilkanPCButton = new javax.swing.JButton();
        exportPCButton = new javax.swing.JButton();
        KembaliPcButton = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        pelangganComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        tampilkanPelangganButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pelangganTable = new javax.swing.JTable();
        exportPelangganButton = new javax.swing.JButton();
        KembaliPelangganButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));
        jPanel1.setForeground(new java.awt.Color(57, 62, 70));

        tampilkanPendapatanButton.setBackground(new java.awt.Color(0, 173, 181));
        tampilkanPendapatanButton.setForeground(new java.awt.Color(255, 255, 255));
        tampilkanPendapatanButton.setText("Tampilkan");
        tampilkanPendapatanButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tampilkanPendapatanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampilkanPendapatanButtonActionPerformed(evt);
            }
        });

        pendapatanTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "Periode", "Total Pendapatan"
            }
        ));
        jScrollPane1.setViewportView(pendapatanTable);

        tanggalAwalChooser.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        tanggalAkhirChooser.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel1.setText("Tanggal Awal");

        jLabel2.setText("Tanggal Akhir");

        exportPendapatanButton.setBackground(new java.awt.Color(0, 173, 181));
        exportPendapatanButton.setForeground(new java.awt.Color(255, 255, 255));
        exportPendapatanButton.setText("Export");
        exportPendapatanButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exportPendapatanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportPendapatanButtonActionPerformed(evt);
            }
        });

        kembaliPendapatanButton.setBackground(new java.awt.Color(0, 173, 181));
        kembaliPendapatanButton.setForeground(new java.awt.Color(255, 255, 255));
        kembaliPendapatanButton.setText("Kembali");
        kembaliPendapatanButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        kembaliPendapatanButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kembaliPendapatanButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(55, 55, 55)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(tanggalAwalChooser, javax.swing.GroupLayout.DEFAULT_SIZE, 133, Short.MAX_VALUE)
                            .addComponent(tanggalAkhirChooser, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(kembaliPendapatanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel1Layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(tampilkanPendapatanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(exportPendapatanButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel1)
                            .addComponent(tanggalAwalChooser, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(kembaliPendapatanButton)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tanggalAkhirChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(exportPendapatanButton)
                    .addComponent(tampilkanPendapatanButton))
                .addContainerGap(108, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Pendapatan", jPanel1);

        jPanel3.setBackground(new java.awt.Color(238, 238, 238));
        jPanel3.setForeground(new java.awt.Color(57, 62, 70));

        pcTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null},
                {null, null, null}
            },
            new String [] {
                "Jenis PC", "Total Disewa", "Total Pendapatan"
            }
        ));
        jScrollPane3.setViewportView(pcTable);

        tampilkanPCButton.setBackground(new java.awt.Color(0, 173, 181));
        tampilkanPCButton.setForeground(new java.awt.Color(255, 255, 255));
        tampilkanPCButton.setText("Tampilkan");
        tampilkanPCButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tampilkanPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampilkanPCButtonActionPerformed(evt);
            }
        });

        exportPCButton.setBackground(new java.awt.Color(0, 173, 181));
        exportPCButton.setForeground(new java.awt.Color(255, 255, 255));
        exportPCButton.setText("Export");
        exportPCButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exportPCButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportPCButtonActionPerformed(evt);
            }
        });

        KembaliPcButton.setBackground(new java.awt.Color(0, 173, 181));
        KembaliPcButton.setForeground(new java.awt.Color(255, 255, 255));
        KembaliPcButton.setText("Kembali");
        KembaliPcButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        KembaliPcButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KembaliPcButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 609, Short.MAX_VALUE)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(KembaliPcButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tampilkanPCButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(exportPCButton, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(211, 211, 211))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(KembaliPcButton)
                .addGap(17, 17, 17)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tampilkanPCButton)
                    .addComponent(exportPCButton))
                .addContainerGap(181, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Penyawaan PC", jPanel3);

        jPanel2.setBackground(new java.awt.Color(238, 238, 238));
        jPanel2.setForeground(new java.awt.Color(57, 62, 70));

        pelangganComboBox.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel3.setText("Pilih Pelanggan");

        tampilkanPelangganButton.setBackground(new java.awt.Color(0, 173, 181));
        tampilkanPelangganButton.setForeground(new java.awt.Color(255, 255, 255));
        tampilkanPelangganButton.setText("Tampilkan");
        tampilkanPelangganButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        tampilkanPelangganButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tampilkanPelangganButtonActionPerformed(evt);
            }
        });

        pelangganTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Id", "Tanggal sewa", "Lama sewa", "Total sewa"
            }
        ));
        jScrollPane2.setViewportView(pelangganTable);

        exportPelangganButton.setBackground(new java.awt.Color(0, 173, 181));
        exportPelangganButton.setForeground(new java.awt.Color(255, 255, 255));
        exportPelangganButton.setText("Export");
        exportPelangganButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exportPelangganButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportPelangganButtonActionPerformed(evt);
            }
        });

        KembaliPelangganButton.setBackground(new java.awt.Color(0, 173, 181));
        KembaliPelangganButton.setForeground(new java.awt.Color(255, 255, 255));
        KembaliPelangganButton.setText("Kembali");
        KembaliPelangganButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        KembaliPelangganButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KembaliPelangganButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 565, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(31, 31, 31)
                        .addComponent(pelangganComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 233, Short.MAX_VALUE)
                        .addComponent(KembaliPelangganButton, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(tampilkanPelangganButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(exportPelangganButton, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(196, 196, 196))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(37, 37, 37)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(pelangganComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(KembaliPelangganButton)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tampilkanPelangganButton)
                    .addComponent(exportPelangganButton))
                .addContainerGap(156, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Penyewaan per Pelanggan", jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 638, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 434, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void tampilkanPendapatanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tampilkanPendapatanButtonActionPerformed
      tampilkanLaporanPendapatan();
    }//GEN-LAST:event_tampilkanPendapatanButtonActionPerformed

    private void tampilkanPelangganButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tampilkanPelangganButtonActionPerformed
     tampilkanLaporanPelanggan();
    }//GEN-LAST:event_tampilkanPelangganButtonActionPerformed

    private void tampilkanPCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tampilkanPCButtonActionPerformed
       tampilkanLaporanPC(); // TODO add your handling code here:
    }//GEN-LAST:event_tampilkanPCButtonActionPerformed

    private void exportPendapatanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPendapatanButtonActionPerformed
     JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan Laporan Pendapatan sebagai PDF");
    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".pdf")) {
            filePath += ".pdf"; // Tambahkan ekstensi jika belum ada
        }

        exportToPdf(pendapatanTable, filePath);
    }
    }//GEN-LAST:event_exportPendapatanButtonActionPerformed

    private void exportPelangganButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPelangganButtonActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan Laporan Penyewaan per Pelanggan sebagai PDF");
    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".pdf")) {
            filePath += ".pdf"; // Tambahkan ekstensi jika belum ada
        }

        exportToPdf(pelangganTable, filePath);
    }
    }//GEN-LAST:event_exportPelangganButtonActionPerformed

    private void exportPCButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exportPCButtonActionPerformed
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Simpan Laporan Penyewaan PC sebagai PDF");
    int userSelection = fileChooser.showSaveDialog(this);

    if (userSelection == JFileChooser.APPROVE_OPTION) {
        String filePath = fileChooser.getSelectedFile().getAbsolutePath();
        if (!filePath.endsWith(".pdf")) {
            filePath += ".pdf"; // Tambahkan ekstensi jika belum ada
        }

        exportToPdf(pcTable, filePath);
    }
    }//GEN-LAST:event_exportPCButtonActionPerformed

    private void kembaliPendapatanButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kembaliPendapatanButtonActionPerformed
        this.dispose(); // Menutup LaporanForm
        MainMenu mainMenu = MainMenu.getInstance(); // Gunakan instance MainMenu yang sudah ada
        mainMenu.setVisible(true); // Tampilkan MainMenu        // TODO add your handling code here:
    }//GEN-LAST:event_kembaliPendapatanButtonActionPerformed

    private void KembaliPelangganButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KembaliPelangganButtonActionPerformed
        this.dispose(); // Menutup LaporanForm
        MainMenu mainMenu = MainMenu.getInstance(); // Gunakan instance MainMenu yang sudah ada
        mainMenu.setVisible(true); // Tampilkan MainMenu 
    }//GEN-LAST:event_KembaliPelangganButtonActionPerformed

    private void KembaliPcButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_KembaliPcButtonActionPerformed
        this.dispose(); // Menutup LaporanForm
        MainMenu mainMenu = MainMenu.getInstance(); // Gunakan instance MainMenu yang sudah ada
        mainMenu.setVisible(true); // Tampilkan MainMenu 
    }//GEN-LAST:event_KembaliPcButtonActionPerformed

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
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(LaporanForm.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new LaporanForm().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton KembaliPcButton;
    private javax.swing.JButton KembaliPelangganButton;
    private javax.swing.JButton exportPCButton;
    private javax.swing.JButton exportPelangganButton;
    private javax.swing.JButton exportPendapatanButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JButton kembaliPendapatanButton;
    private javax.swing.JTable pcTable;
    private javax.swing.JComboBox<String> pelangganComboBox;
    private javax.swing.JTable pelangganTable;
    private javax.swing.JTable pendapatanTable;
    private javax.swing.JButton tampilkanPCButton;
    private javax.swing.JButton tampilkanPelangganButton;
    private javax.swing.JButton tampilkanPendapatanButton;
    private com.toedter.calendar.JDateChooser tanggalAkhirChooser;
    private com.toedter.calendar.JDateChooser tanggalAwalChooser;
    // End of variables declaration//GEN-END:variables
}
