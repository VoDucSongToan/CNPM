package com.quanlygiay.demo.dto;

// 1. Class đại diện cho sản phẩm khách mua
public class Product {
    private String barcode;
    private int quantity;
    // Getters and Setters, Constructor...
    public String getBarcode() { return barcode; }
    public int getQuantity() { return quantity; }
}

// 2. Class chứa thông tin trả về khi tra cứu mã giảm giá
public class VoucherDTO {
    public String tenChuongTrinh; // Dùng CodeVoucher làm tên chương trình
    public double giaTriGiam;
    public String loaiGiamGia;
    public int soLuongConLai;
    
    public VoucherDTO(String ten, double giaTri, String loai, int soLuong) {
        this.tenChuongTrinh = ten; this.giaTriGiam = giaTri;
        this.loaiGiamGia = loai; this.soLuongConLai = soLuong;
    }
}

// 3. Class chứa thông tin trả về khi bắn mã vạch Barcode
public class ProductInfoDTO {
    public String tenSanPham;
    public double giaBanHienTai;
    public String mauSac;
    public int size;

    public ProductInfoDTO(String ten, double giaBan, String mau, int size) {
        this.tenSanPham = ten; this.giaBanHienTai = giaBan;
        this.mauSac = mau; this.size = size;
    }
}
