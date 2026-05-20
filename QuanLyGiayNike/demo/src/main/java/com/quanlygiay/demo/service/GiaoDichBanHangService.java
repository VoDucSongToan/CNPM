package com.quanlygiay.demo.service;

import com.quanlygiay.demo.dto.SanPhamMuaDTO;
import java.util.List;

public class GiaoDichBanHangService implements IGiaoDichBanHang {

    @Override
    public int xuLyThanhToan(List<SanPhamMuaDTO> danhSachMua, Integer maGiamGia, String sdtKhachHang, int maNhanVien, double tongTien, double thanhTien) {
        // TODO: Bạn làm phần [2] sẽ code logic lưu hóa đơn vào SQL ở đây
        System.out.println("Đang xử lý thanh toán...");
        return 1; // Trả về mã hóa đơn giả định
    }

    @Override
    public Object traCuuMaGiamGia(String codeVoucher) {
        // TODO: Viết logic check database bảng MaGiamGia
        return null;
    }

    @Override
    public int traCuuDiemKhachHang(String sdtKhachHang) {
        // TODO: Viết logic query bảng KhachHang lấy DiemTichLuy
        return 0;
    }

    @Override
    public Object traCuuSanPhamTheoBarcode(String barcode) {
        // TODO: Viết logic query bảng ChiTietSanPham join SanPham
        return null;
    }
}