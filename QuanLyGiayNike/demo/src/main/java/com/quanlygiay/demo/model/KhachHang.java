package com.quanlygiay.demo.model;

public class KhachHang {
    private Integer maKhachHang;
    private String hoTen;
    private String soDienThoai;
    private Integer diemTichLuy = 0;

    // --- CONSTRUCTOR ---
    public KhachHang() {}

    public KhachHang(Integer maKhachHang, String hoTen, String soDienThoai, Integer diemTichLuy) {
        this.maKhachHang = maKhachHang;
        this.hoTen = hoTen;
        this.soDienThoai = soDienThoai;
        this.diemTichLuy = diemTichLuy;
    }

    // --- GETTERS & SETTERS ---
    public Integer getMaKhachHang() { return maKhachHang; }
    public void setMaKhachHang(Integer maKhachHang) { this.maKhachHang = maKhachHang; }

    public String getHoTen() { return hoTen; }
    public void setHoTen(String hoTen) { this.hoTen = hoTen; }

    public String getSoDienThoai() { return soDienThoai; }
    public void setSoDienThoai(String soDienThoai) { this.soDienThoai = soDienThoai; }

    public Integer getDiemTichLuy() { return diemTichLuy; }
    public void setDiemTichLuy(Integer diemTichLuy) { this.diemTichLuy = diemTichLuy; }
}