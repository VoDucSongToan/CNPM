package com.quanlygiay.demo.service;

import com.quanlygiay.demo.config.DatabaseConnection;
import com.quanlygiay.demo.model.ChiTietPhieuNhap;
import com.quanlygiay.demo.model.ChiTietSanPham;
import com.quanlygiay.demo.repository.ChiTietSanPhamRepository;

import java.sql.*;
import java.util.List;

public class QuanLyKhoService implements IQuanLyKho {

    private ChiTietSanPhamRepository ctspRepo = new ChiTietSanPhamRepository();

    @Override
    public boolean nhapHang(int maNhaCungCap, int maNhanVien, List<ChiTietPhieuNhap> danhSachNhap) {
        String insertPhieuNhap = "INSERT INTO PhieuNhap (MaNhaCungCap, MaNhanVien, TongTien) VALUES (?, ?, ?)";
        String insertChiTietPN = "INSERT INTO ChiTietPhieuNhap (MaPhieuNhap, MaChiTietSanPham, SoLuongNhap, GiaNhap) VALUES (?, ?, ?, ?)";
        String updateTonKho = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon + ? WHERE MaChiTietSanPham = ?";
        
        // Cập nhật giá vốn mới nhất cho sản phẩm để phần Báo Cáo (bạn phần 2) tính lãi/lỗ chính xác
        String updateGiaVon = "UPDATE SanPham SET GiaVonHienTai = ? WHERE MaSanPham = (SELECT MaSanPham FROM ChiTietSanPham WHERE MaChiTietSanPham = ?)";

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Bắt đầu Transaction để đảm bảo an toàn dữ liệu

            // 1. Tính tổng tiền phiếu nhập
            double tongTien = 0;
            for (ChiTietPhieuNhap ct : danhSachNhap) {
                tongTien += ct.getSoLuongNhap() * ct.getGiaNhap();
            }

            // 2. Lưu thông tin PhieuNhap và lấy MaPhieuNhap vừa tạo tự động
            int maPhieuNhap = -1;
            try (PreparedStatement psPN = conn.prepareStatement(insertPhieuNhap, Statement.RETURN_GENERATED_KEYS)) {
                psPN.setInt(1, maNhaCungCap);
                psPN.setInt(2, maNhanVien);
                psPN.setDouble(3, tongTien);
                psPN.executeUpdate();

                try (ResultSet rs = psPN.getGeneratedKeys()) {
                    if (rs.next()) maPhieuNhap = rs.getInt(1);
                }
            }

            if (maPhieuNhap == -1) { conn.rollback(); return false; }

            // 3. Lưu chi tiết phiếu nhập, cập nhật tồn kho và giá vốn
            try (PreparedStatement psCTPN = conn.prepareStatement(insertChiTietPN);
                 PreparedStatement psTonKho = conn.prepareStatement(updateTonKho);
                 PreparedStatement psGiaVon = conn.prepareStatement(updateGiaVon)) {
                
                for (ChiTietPhieuNhap chiTiet : danhSachNhap) {
                    // Lưu ChiTietPhieuNhap
                    psCTPN.setInt(1, maPhieuNhap);
                    psCTPN.setInt(2, chiTiet.getMaChiTietSanPham());
                    psCTPN.setInt(3, chiTiet.getSoLuongNhap());
                    psCTPN.setDouble(4, chiTiet.getGiaNhap());
                    psCTPN.executeUpdate();

                    // Cộng số lượng vào tồn kho
                    psTonKho.setInt(1, chiTiet.getSoLuongNhap());
                    psTonKho.setInt(2, chiTiet.getMaChiTietSanPham());
                    psTonKho.executeUpdate();

                    // Cập nhật giá vốn hiện tại bằng giá vừa nhập
                    psGiaVon.setDouble(1, chiTiet.getGiaNhap());
                    psGiaVon.setInt(2, chiTiet.getMaChiTietSanPham());
                    psGiaVon.executeUpdate();
                }
            }

            conn.commit(); // Lưu mọi thay đổi nếu không có lỗi
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public List<ChiTietSanPham> layDanhSachCanhBaoHetHang() {
        // Gọi lại hàm từ Repository đã viết
        return ctspRepo.layDanhSachCanhBaoHetHang();
    }
}