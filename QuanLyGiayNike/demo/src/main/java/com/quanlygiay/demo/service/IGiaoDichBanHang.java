package com.quanlygiay.demo.service;

import com.quanlygiay.demo.dto.SanPhamMuaDTO;
import java.util.List;

// Đổi từ class thành interface ở đây
public interface IGiaoDichBanHang {
    
    // Hàm 1: Xử lý Thanh toán
    int xuLyThanhToan(List<SanPhamMuaDTO> danhSachMua, Integer maGiamGia, String sdtKhachHang, int maNhanVien, double tongTien, double thanhTien);

    // Hàm 2: Tra cứu Mã giảm giá
    Object traCuuMaGiamGia(String codeVoucher);

    // Hàm 3: Tra cứu Điểm khách hàng
    int traCuuDiemKhachHang(String sdtKhachHang);

    // Hàm 4: Tra cứu Sản phẩm theo Barcode
    Object traCuuSanPhamTheoBarcode(String barcode);
}