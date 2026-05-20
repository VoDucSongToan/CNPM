package com.quanlygiay.demo.model;

import java.util.Date;

public class PhieuNhap {
    private int maPhieuNhap;
    private int maNhaCungCap;
    private int maNhanVien;
    private Date ngayNhap;
    private double tongTien;

    public int getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(int maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }
    public int getMaNhaCungCap() { return maNhaCungCap; }
    public void setMaNhaCungCap(int maNhaCungCap) { this.maNhaCungCap = maNhaCungCap; }
    public int getMaNhanVien() { return maNhanVien; }
    public void setMaNhanVien(int maNhanVien) { this.maNhanVien = maNhanVien; }
    public Date getNgayNhap() { return ngayNhap; }
    public void setNgayNhap(Date ngayNhap) { this.ngayNhap = ngayNhap; }
    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}
