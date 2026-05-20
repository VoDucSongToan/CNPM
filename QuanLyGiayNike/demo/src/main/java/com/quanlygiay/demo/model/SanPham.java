package com.quanlygiay.demo.model;

public class SanPham {
    private Integer maSanPham;
    private String tenSanPham;
    private Double giaVonHienTai;
    private Double giaBanHienTai;
    private String moTa;

    // --- CONSTRUCTOR ---
    public SanPham() {}

    public SanPham(Integer maSanPham, String tenSanPham, Double giaVonHienTai, Double giaBanHienTai, String moTa) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaVonHienTai = giaVonHienTai;
        this.giaBanHienTai = giaBanHienTai;
        this.moTa = moTa;
    }

    // --- GETTERS & SETTERS ---
    public Integer getMaSanPham() { return maSanPham; }
    public void setMaSanPham(Integer maSanPham) { this.maSanPham = maSanPham; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public Double getGiaVonHienTai() { return giaVonHienTai; }
    public void setGiaVonHienTai(Double giaVonHienTai) { this.giaVonHienTai = giaVonHienTai; }

    public Double getGiaBanHienTai() { return giaBanHienTai; }
    public void setGiaBanHienTai(Double giaBanHienTai) { this.giaBanHienTai = giaBanHienTai; }

    public String getMoTa() { return moTa; }
    public void setMoTa(String moTa) { this.moTa = moTa; }
}