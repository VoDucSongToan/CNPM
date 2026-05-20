package com.quanlygiay.demo.repository;

import com.quanlygiay.demo.config.DatabaseConnection;
import com.quanlygiay.demo.model.ChiTietSanPham;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietSanPhamRepository {
    public List<ChiTietSanPham> layDanhSachCanhBaoHetHang() {
        List<ChiTietSanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietSanPham WHERE SoLuongTon <= MucCanhBaoToiThieu";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                ChiTietSanPham sp = new ChiTietSanPham();
                sp.setBarcode(rs.getString("Barcode"));
                sp.setSoLuongTon(rs.getInt("SoLuongTon"));
                list.add(sp);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return list;
    }
}