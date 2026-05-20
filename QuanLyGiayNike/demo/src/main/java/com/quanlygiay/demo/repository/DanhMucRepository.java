package com.quanlygiay.demo.repository;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


import com.quanlygiay.demo.config.DatabaseConnection;
import com.quanlygiay.demo.model.DanhMuc;

public class DanhMucRepository {
    
    public List<DanhMuc> layTatCaDanhMuc() {
        List<DanhMuc> dsDanhMuc = new ArrayList<>();
        String sql = "SELECT * FROM DanhMuc";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
             
            while (rs.next()) {
                dsDanhMuc.add(new DanhMuc(rs.getInt("MaDanhMuc"), rs.getString("TenDanhMuc")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsDanhMuc;
    }
}