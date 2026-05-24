package com.quanlygiay.demo.service;

import com.quanlygiay.demo.dto.Product;
import com.quanlygiay.demo.dto.ProductInfoDTO;
import com.quanlygiay.demo.dto.VoucherDTO;

import java.sql.*;
import java.util.List;

public class GiaoDichBanHangService implements IGiaoDichBanHang {

    // Giả định bạn có class DBConnection để lấy kết nối SQL Server
    // Nếu bạn dùng tên khác (như SQLServerHelper), hãy sửa lại ở đây nhé.
    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection(); 
    }

    @Override
    public String xuLyThanhToan(List<Product> danhSachMua, String codeGiamGia, String sdtKhachHang, 
                                int maNhanVien, double tongTienTruocGiam, double thanhTienPhaiTra, int diemSuDung) {
        
        String sqlGetKhachHang = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ?";
        String sqlGetVoucher = "SELECT MaGiamGia FROM MaGiamGia WHERE CodeVoucher = ?";
        
        String sqlInsertHoaDon = "INSERT INTO HoaDon (MaKhachHang, MaNhanVien, MaGiamGia, TongTienTruocGiam, SoTienGiam, ThanhTien) VALUES (?, ?, ?, ?, ?, ?)";
        String sqlGetChiTietSP = "SELECT CTSP.MaChiTietSanPham, SP.GiaBanHienTai, SP.GiaVonHienTai FROM ChiTietSanPham CTSP JOIN SanPham SP ON CTSP.MaSanPham = SP.MaSanPham WHERE CTSP.Barcode = ?";
        String sqlInsertChiTietHD = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaChiTietSanPham, SoLuongBan, GiaBanTaiThoiDiem, GiaVonTaiThoiDiem) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateTonKho = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaChiTietSanPham = ?";
        String sqlUpdateVoucher = "UPDATE MaGiamGia SET SoLuong = SoLuong - 1 WHERE MaGiamGia = ?";
        String sqlUpdateDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy - ? + ? WHERE MaKhachHang = ?";

        Connection conn = null;
        try {
            conn = getConnection();
            conn.setAutoCommit(false); // BẮT ĐẦU TRANSACTION

            // 1. Lấy MaKhachHang (nếu có)
            Integer maKhachHang = null;
            if (sdtKhachHang != null && !sdtKhachHang.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlGetKhachHang)) {
                    ps.setString(1, sdtKhachHang);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) maKhachHang = rs.getInt("MaKhachHang");
                }
            }

            // 2. Lấy MaGiamGia (nếu có)
            Integer idGiamGia = null;
            if (codeGiamGia != null && !codeGiamGia.isEmpty()) {
                try (PreparedStatement ps = conn.prepareStatement(sqlGetVoucher)) {
                    ps.setString(1, codeGiamGia);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) idGiamGia = rs.getInt("MaGiamGia");
                }
            }

            // 3. Tạo Hóa Đơn
            int maHoaDonVuaTao = -1;
            double soTienGiam = tongTienTruocGiam - thanhTienPhaiTra;
            
            try (PreparedStatement psHoaDon = conn.prepareStatement(sqlInsertHoaDon, Statement.RETURN_GENERATED_KEYS)) {
                if (maKhachHang != null) psHoaDon.setInt(1, maKhachHang); else psHoaDon.setNull(1, Types.INTEGER);
                psHoaDon.setInt(2, maNhanVien);
                if (idGiamGia != null) psHoaDon.setInt(3, idGiamGia); else psHoaDon.setNull(3, Types.INTEGER);
                
                psHoaDon.setDouble(4, tongTienTruocGiam);
                psHoaDon.setDouble(5, soTienGiam);
                psHoaDon.setDouble(6, thanhTienPhaiTra);
                psHoaDon.executeUpdate();

                ResultSet generatedKeys = psHoaDon.getGeneratedKeys();
                if (generatedKeys.next()) maHoaDonVuaTao = generatedKeys.getInt(1);
                else throw new SQLException("Lỗi: Không thể lấy ID Hóa Đơn vừa tạo.");
            }

            // 4. Lặp qua danh sách sản phẩm để thêm Chi Tiết HD & Trừ Tồn Kho
            try (PreparedStatement psGetInfo = conn.prepareStatement(sqlGetChiTietSP);
                 PreparedStatement psInsertCTHD = conn.prepareStatement(sqlInsertChiTietHD);
                 PreparedStatement psUpdateKho = conn.prepareStatement(sqlUpdateTonKho)) {
                
                for (Product p : danhSachMua) {
                    // Lấy giá vốn, giá bán hiện tại và mã chi tiết
                    psGetInfo.setString(1, p.getBarcode());
                    ResultSet rsSP = psGetInfo.executeQuery();
                    if (!rsSP.next()) throw new SQLException("Không tìm thấy sản phẩm có Barcode: " + p.getBarcode());
                    
                    int maCTSP = rsSP.getInt("MaChiTietSanPham");
                    double giaBan = rsSP.getDouble("GiaBanHienTai");
                    double giaVon = rsSP.getDouble("GiaVonHienTai");

                    // Insert Chi Tiết Hóa Đơn
                    psInsertCTHD.setInt(1, maHoaDonVuaTao);
                    psInsertCTHD.setInt(2, maCTSP);
                    psInsertCTHD.setInt(3, p.getQuantity());
                    psInsertCTHD.setDouble(4, giaBan);
                    psInsertCTHD.setDouble(5, giaVon);
                    psInsertCTHD.addBatch(); // Gom lệnh để chạy 1 lần cho nhanh

                    // Update Tồn Kho
                    psUpdateKho.setInt(1, p.getQuantity());
                    psUpdateKho.setInt(2, maCTSP);
                    psUpdateKho.addBatch();
                }
                psInsertCTHD.executeBatch();
                psUpdateKho.executeBatch();
            }

            // 5. Cập nhật lượt sử dụng mã giảm giá
            if (idGiamGia != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateVoucher)) {
                    ps.setInt(1, idGiamGia);
                    ps.executeUpdate();
                }
            }

            // 6. Cập nhật Điểm khách hàng
            if (maKhachHang != null) {
                try (PreparedStatement ps = conn.prepareStatement(sqlUpdateDiem)) {
                    int diemCongThem = (int) (thanhTienPhaiTra / 100000); // Giả định: 100k = 1 điểm
                    ps.setInt(1, diemSuDung);   // Trừ điểm đã dùng
                    ps.setInt(2, diemCongThem); // Cộng điểm mới
                    ps.setInt(3, maKhachHang);
                    ps.executeUpdate();
                }
            }

            conn.commit(); // LƯU TOÀN BỘ THAY ĐỔI XUỐNG DB
            return "HD" + maHoaDonVuaTao; // Thành công

        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            e.printStackTrace();
            return "Lỗi thanh toán: " + e.getMessage();
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    @Override
    public VoucherDTO traCuuMaGiamGia(String codeVoucher) {
        String sql = "SELECT LoaiGiamGia, GiaTriGiam, SoLuong FROM MaGiamGia " +
                     "WHERE CodeVoucher = ? AND TrangThai = N'HoatDong' " +
                     "AND NgayHetHan >= GETDATE() AND SoLuong > 0";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new VoucherDTO(
                        codeVoucher, 
                        rs.getDouble("GiaTriGiam"), 
                        rs.getString("LoaiGiamGia"), 
                        rs.getInt("SoLuong")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null; // Không tìm thấy hoặc mã không hợp lệ
    }

    @Override
    public int traCuuDiemKhachHang(String sdtKhachHang) {
        String sql = "SELECT DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdtKhachHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("DiemTichLuy");
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return 0;
    }

    @Override
    public ProductInfoDTO traCuuSanPhamTheoBarcode(String barcode) {
        String sql = "SELECT SP.TenSanPham, SP.GiaBanHienTai, CTSP.MauSac, CTSP.Size " +
                     "FROM ChiTietSanPham CTSP " +
                     "JOIN SanPham SP ON CTSP.MaSanPham = SP.MaSanPham " +
                     "WHERE CTSP.Barcode = ?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new ProductInfoDTO(
                        rs.getString("TenSanPham"),
                        rs.getDouble("GiaBanHienTai"),
                        rs.getString("MauSac"),
                        rs.getInt("Size")
                );
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
}
