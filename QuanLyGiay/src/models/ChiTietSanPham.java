package models;

import jakarta.persistence.*;
@Entity
@Table(name = "ChiTietSanPham")
public class ChiTietSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MaChiTietSanPham")
    private Integer maChiTietSanPham;

    @ManyToOne
    @JoinColumn(name = "MaSanPham")
    private SanPham sanPham; // Liên kết với bảng SanPham

    @Column(name = "Barcode", unique = true)
    private String barcode;

    @Column(name = "Size")
    private Integer size;

    @Column(name = "MauSac")
    private String mauSac;

    @Column(name = "SoLuongTon")
    private Integer soLuongTon;

    

    // Các Getters và Setters...
    public String getBarcode() { return barcode; }
    public Integer getSoLuongTon() { return soLuongTon; }
    public void setSoLuongTon(Integer soLuongTon) { this.soLuongTon = soLuongTon; }
    public SanPham getSanPham() { return sanPham; }
}
