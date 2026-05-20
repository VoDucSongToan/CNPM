package com.quanlygiay.demo.service;

import com.quanlygiay.demo.model.ChiTietPhieuNhap;
import com.quanlygiay.demo.model.ChiTietSanPham;
import java.util.List;

public interface IQuanLyKho {
    // Hàm nhập hàng
    boolean nhapHang(int maNhaCungCap, int maNhanVien, List<ChiTietPhieuNhap> danhSachNhap);

    // Hàm lấy danh sách sản phẩm sắp hết
    List<ChiTietSanPham> layDanhSachCanhBaoHetHang();
}