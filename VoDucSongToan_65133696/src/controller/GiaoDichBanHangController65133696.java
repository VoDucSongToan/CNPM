package controller;

import model.Product65133696;

import java.sql.*;
import java.util.List;

public class GiaoDichBanHangController65133696 {
    private Connection conn;

    public GiaoDichBanHangController65133696(Connection conn) {
        this.conn = conn;
    }

    public String xuLyThanhToan(List<Product65133696> danhSachSanPhamMua, Integer maGiamGia, String sdtKhachHang,
                                int maNhanVien, double tongTienTruocGiam, double thanhTienPhaiTra) {
        try {
            conn.setAutoCommit(false); // Bắt đầu Transaction

            Integer maKhachHang = null;
            // 1. Tìm MaKhachHang nếu có SDT
            if (sdtKhachHang != null && !sdtKhachHang.isEmpty()) {
                String sqlKH = "SELECT MaKhachHang FROM KhachHang WHERE SoDienThoai = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlKH)) {
                    ps.setString(1, sdtKhachHang);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) maKhachHang = rs.getInt("MaKhachHang");
                }
            }

            // 2. Tạo Hóa Đơn
            String sqlInsertHD = "INSERT INTO HoaDon (MaKhachHang, MaNhanVien, MaGiamGia, TongTienTruocGiam, SoTienGiam, ThanhTien) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            int maHoaDonMoi = -1;
            try (PreparedStatement psHD = conn.prepareStatement(sqlInsertHD, Statement.RETURN_GENERATED_KEYS)) {
                if (maKhachHang != null) psHD.setInt(1, maKhachHang); else psHD.setNull(1, java.sql.Types.INTEGER);
                psHD.setInt(2, maNhanVien);
                if (maGiamGia != null) psHD.setInt(3, maGiamGia); else psHD.setNull(3, java.sql.Types.INTEGER);
                psHD.setDouble(4, tongTienTruocGiam);
                psHD.setDouble(5, tongTienTruocGiam - thanhTienPhaiTra);
                psHD.setDouble(6, thanhTienPhaiTra);
                psHD.executeUpdate();

                ResultSet rsKeys = psHD.getGeneratedKeys();
                if (rsKeys.next()) maHoaDonMoi = rsKeys.getInt(1);
            }

            // 3. Xử lý Chi Tiết Hóa Đơn và Trừ Tồn Kho
            String sqlGetGia = "SELECT CTSP.MaChiTietSanPham, SP.GiaVonHienTai, SP.GiaBanHienTai " +
                    "FROM ChiTietSanPham CTSP JOIN SanPham SP ON CTSP.MaSanPham = SP.MaSanPham " +
                    "WHERE CTSP.Barcode = ?";
            String sqlInsertCTHD = "INSERT INTO ChiTietHoaDon (MaHoaDon, MaChiTietSanPham, SoLuongBan, GiaBanTaiThoiDiem, GiaVonTaiThoiDiem, ThanhTien) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
            String sqlUpdateKho = "UPDATE ChiTietSanPham SET SoLuongTon = SoLuongTon - ? WHERE MaChiTietSanPham = ?";

            try (PreparedStatement psGetGia = conn.prepareStatement(sqlGetGia);
                 PreparedStatement psCTHD = conn.prepareStatement(sqlInsertCTHD);
                 PreparedStatement psUpdateKho = conn.prepareStatement(sqlUpdateKho)) {

                for (Product65133696 p : danhSachSanPhamMua) {
                    psGetGia.setString(1, p.getBarcode());
                    ResultSet rsGia = psGetGia.executeQuery();
                    if (rsGia.next()) {
                        int maCTSP = rsGia.getInt("MaChiTietSanPham");
                        double giaVon = rsGia.getDouble("GiaVonHienTai");
                        double giaBan = rsGia.getDouble("GiaBanHienTai");
                        double thanhTienItem = giaBan * p.getQuantity();

                        // Thêm vào Chi Tiết Hóa Đơn
                        psCTHD.setInt(1, maHoaDonMoi);
                        psCTHD.setInt(2, maCTSP);
                        psCTHD.setInt(3, p.getQuantity());
                        psCTHD.setDouble(4, giaBan);
                        psCTHD.setDouble(5, giaVon);
                        psCTHD.setDouble(6, thanhTienItem);
                        psCTHD.executeUpdate();

                        // Trừ kho
                        psUpdateKho.setInt(1, p.getQuantity());
                        psUpdateKho.setInt(2, maCTSP);
                        psUpdateKho.executeUpdate();
                    } else {
                        throw new Exception("Không tìm thấy sản phẩm với Barcode: " + p.getBarcode());
                    }
                }
            }

            // 4. Trừ lượt mã giảm giá (nếu có)
            if (maGiamGia != null) {
                String sqlUpdateMGG = "UPDATE MaGiamGia SET SoLuong = SoLuong - 1 WHERE MaGiamGia = ? AND SoLuong > 0";
                try (PreparedStatement psMGG = conn.prepareStatement(sqlUpdateMGG)) {
                    psMGG.setInt(1, maGiamGia);
                    psMGG.executeUpdate();
                }
            }

            // 5. Cộng điểm tích lũy cho khách hàng (Giả sử 10.000đ = 1 điểm)
            if (maKhachHang != null) {
                int diemCong = (int) (thanhTienPhaiTra / 10000);
                String sqlUpdateDiem = "UPDATE KhachHang SET DiemTichLuy = DiemTichLuy + ? WHERE MaKhachHang = ?";
                try (PreparedStatement psDiem = conn.prepareStatement(sqlUpdateDiem)) {
                    psDiem.setInt(1, diemCong);
                    psDiem.setInt(2, maKhachHang);
                    psDiem.executeUpdate();
                }
            }

            conn.commit(); // Hoàn tất giao dịch
            return "Thành công. Mã hóa đơn: " + maHoaDonMoi;

        } catch (Exception e) {
            try { conn.rollback(); } catch (SQLException ex) {} // Hoàn tác nếu lỗi
            return "Lỗi thanh toán: " + e.getMessage();
        } finally {
            try { conn.setAutoCommit(true); } catch (SQLException ex) {}
        }
    }

    // =====================================================================
    // HÀM 2: TRA CỨU MÃ GIẢM GIÁ
    // =====================================================================
    public String traCuuMaGiamGia(String codeVoucher) {
        String sql = "SELECT LoaiGiamGia, GiaTriGiam FROM MaGiamGia " +
                "WHERE CodeVoucher = ? AND TrangThai = N'HoatDong' " +
                "AND SoLuong > 0 AND NgayHetHan >= GETDATE()";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, codeVoucher);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String loai = rs.getString("LoaiGiamGia");
                double giaTri = rs.getDouble("GiaTriGiam");
                return "Chương trình: " + loai + " - Giảm: " + giaTri;
            } else {
                return "Lỗi: Mã giảm giá không hợp lệ, đã hết hạn hoặc hết lượt.";
            }
        } catch (SQLException e) {
            return "Lỗi DB: " + e.getMessage();
        }
    }

    // =====================================================================
    // HÀM 3: TRA CỨU ĐIỂM KHÁCH HÀNG
    // =====================================================================
    public int traCuuDiemKhachHang(String sdtKhachHang) {
        String sql = "SELECT DiemTichLuy FROM KhachHang WHERE SoDienThoai = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, sdtKhachHang);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt("DiemTichLuy");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // Trả về 0 nếu không tìm thấy hoặc lỗi
    }

    // =====================================================================
    // HÀM 4: TRA CỨU SẢN PHẨM THEO BARCODE
    // =====================================================================
    public String traCuuSanPhamTheoBarcode(String barcode) {
        String sql = "SELECT SP.TenSanPham, SP.GiaBanHienTai, CTSP.MauSac, CTSP.Size " +
                "FROM ChiTietSanPham CTSP JOIN SanPham SP ON CTSP.MaSanPham = SP.MaSanPham " +
                "WHERE CTSP.Barcode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String ten = rs.getString("TenSanPham");
                double giaBan = rs.getDouble("GiaBanHienTai");
                String mau = rs.getString("MauSac");
                int size = rs.getInt("Size");
                return String.format("Sản phẩm: %s | Giá: %,.0f | Màu: %s | Size: %d", ten, giaBan, mau, size);
            } else {
                return "Không tìm thấy sản phẩm với Barcode này.";
            }
        } catch (SQLException e) {
            return "Lỗi truy vấn: " + e.getMessage();
        }
    }
}
