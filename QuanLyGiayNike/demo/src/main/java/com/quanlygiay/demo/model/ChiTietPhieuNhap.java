package com.quanlygiay.demo.model;

public class ChiTietPhieuNhap {
    private int maChiTietPhieuNhap;
    private int maPhieuNhap;
    private int maChiTietSanPham;
    private int soLuongNhap;
    private double giaNhap;

    public ChiTietPhieuNhap(int maChiTietSanPham, int soLuongNhap, double giaNhap) {
        this.maChiTietSanPham = maChiTietSanPham;
        this.soLuongNhap = soLuongNhap;
        this.giaNhap = giaNhap;
    }
    public int getMaChiTietPhieuNhap() { return maChiTietPhieuNhap; }
    public void setMaChiTietPhieuNhap(int maChiTietPhieuNhap) { this.maChiTietPhieuNhap = maChiTietPhieuNhap; }
    public int getMaPhieuNhap() { return maPhieuNhap; }
    public void setMaPhieuNhap(int maPhieuNhap) { this.maPhieuNhap = maPhieuNhap; }
    public int getMaChiTietSanPham() { return maChiTietSanPham; }
    public void setMaChiTietSanPham(int maChiTietSanPham) { this.maChiTietSanPham = maChiTietSanPham; }
    public int getSoLuongNhap() { return soLuongNhap; }
    public void setSoLuongNhap(int soLuongNhap) { this.soLuongNhap = soLuongNhap; }
    public double getGiaNhap() { return giaNhap; }
    public void setGiaNhap(double giaNhap) { this.giaNhap = giaNhap; }
}
