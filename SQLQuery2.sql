CREATE DATABASE QuanLyGiay;
GO
USE QuanLyGiay;
GO

-- =========================================================================
-- 1. NHÓM BẢNG ĐỘC LẬP VÀ ĐỐI TƯỢNG VẬN HÀNH
-- =========================================================================
CREATE TABLE DanhMuc (
    MaDanhMuc INT IDENTITY(1,1) PRIMARY KEY,
    TenDanhMuc NVARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE NhaCungCap (
    MaNhaCungCap INT IDENTITY(1,1) PRIMARY KEY,
    TenNhaCungCap NVARCHAR(150) NOT NULL,
    SoDienThoai VARCHAR(15) NULL,
    DiaChi NVARCHAR(255) NULL
);

CREATE TABLE NhanVien (
    MaNhanVien INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    ChucVu NVARCHAR(50) NOT NULL DEFAULT N'NhanVienBanHang'
        CHECK (ChucVu IN (N'QuanLy', N'NhanVienBanHang', N'NhanVienKiemKho'))
);

CREATE TABLE KhachHang (
    MaKhachHang INT IDENTITY(1,1) PRIMARY KEY,
    HoTen NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL UNIQUE,
    DiemTichLuy INT DEFAULT 0 CHECK (DiemTichLuy >= 0)
);

CREATE TABLE MaGiamGia (
    MaGiamGia INT IDENTITY(1,1) PRIMARY KEY,
    CodeVoucher VARCHAR(50) NOT NULL UNIQUE,
    LoaiGiamGia NVARCHAR(50) NOT NULL,
    GiaTriGiam DECIMAL(18,2) NOT NULL CHECK (GiaTriGiam >= 0),
    DiemCanDoi INT DEFAULT 0 CHECK (DiemCanDoi >= 0),
    SoLuong INT DEFAULT 0 CHECK (SoLuong >= 0), -- Đã thêm Số lượng mã giảm giá
    NgayHetHan DATE NOT NULL,
    TrangThai NVARCHAR(50) DEFAULT N'HoatDong'
        CHECK (TrangThai IN (N'HoatDong', N'TamDung', N'HetHan', N'HetLuot'))
);

-- =========================================================================
-- 2. NHÓM BẢNG SẢN PHẨM & KHO HÀNG
-- =========================================================================
CREATE TABLE SanPham (
    MaSanPham INT IDENTITY(1,1) PRIMARY KEY,
    TenSanPham NVARCHAR(150) NOT NULL,
    MaDanhMuc INT FOREIGN KEY REFERENCES DanhMuc(MaDanhMuc) ON DELETE SET NULL,
    GiaVonHienTai DECIMAL(18,2) DEFAULT 0 CHECK (GiaVonHienTai >= 0),
    GiaBanHienTai DECIMAL(18,2) DEFAULT 0 CHECK (GiaBanHienTai >= 0),
    MoTa NVARCHAR(500) NULL
);

CREATE TABLE ChiTietSanPham (
    MaChiTietSanPham INT IDENTITY(1,1) PRIMARY KEY,
    MaSanPham INT FOREIGN KEY REFERENCES SanPham(MaSanPham) ON DELETE CASCADE,
    Barcode VARCHAR(50) NOT NULL UNIQUE,
    Size INT NOT NULL,
    MauSac NVARCHAR(50) NOT NULL,
    SoLuongTon INT DEFAULT 0 CHECK (SoLuongTon >= 0),
    MucCanhBaoToiThieu INT DEFAULT 5
);

-- =========================================================================
-- 3. NHÓM BẢNG NGHIỆP VỤ NHẬP HÀNG (KHO VÀO)
-- =========================================================================
CREATE TABLE PhieuNhap (
    MaPhieuNhap INT IDENTITY(1,1) PRIMARY KEY,
    MaNhaCungCap INT FOREIGN KEY REFERENCES NhaCungCap(MaNhaCungCap),
    MaNhanVien INT FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    NgayNhap DATETIME DEFAULT GETDATE(), -- Kiểu ngày giờ
    TongTien DECIMAL(18,2) DEFAULT 0
);

CREATE TABLE ChiTietPhieuNhap (
    MaChiTietPhieuNhap INT IDENTITY(1,1) PRIMARY KEY,
    MaPhieuNhap INT FOREIGN KEY REFERENCES PhieuNhap(MaPhieuNhap) ON DELETE CASCADE,
    MaChiTietSanPham INT FOREIGN KEY REFERENCES ChiTietSanPham(MaChiTietSanPham),
    SoLuongNhap INT NOT NULL CHECK (SoLuongNhap > 0),
    GiaNhap DECIMAL(18,2) NOT NULL CHECK (GiaNhap >= 0)
);

-- =========================================================================
-- 4. NHÓM BẢNG NGHIỆP VỤ BÁN HÀNG & DOANH THU (KHO RA)
-- =========================================================================
CREATE TABLE HoaDon (
    MaHoaDon INT IDENTITY(1,1) PRIMARY KEY,
    MaKhachHang INT FOREIGN KEY REFERENCES KhachHang(MaKhachHang) ON DELETE SET NULL,
    MaNhanVien INT FOREIGN KEY REFERENCES NhanVien(MaNhanVien),
    MaGiamGia INT FOREIGN KEY REFERENCES MaGiamGia(MaGiamGia) ON DELETE SET NULL,
    NgayTao DATETIME DEFAULT GETDATE(), -- Kiểu ngày giờ
    TongTienTruocGiam DECIMAL(18,2) NOT NULL,
    SoTienGiam DECIMAL(18,2) DEFAULT 0,
    ThanhTien DECIMAL(18,2) NOT NULL
);

CREATE TABLE ChiTietHoaDon (
    MaChiTietHoaDon INT IDENTITY(1,1) PRIMARY KEY,
    MaHoaDon INT FOREIGN KEY REFERENCES HoaDon(MaHoaDon) ON DELETE CASCADE,
    MaChiTietSanPham INT FOREIGN KEY REFERENCES ChiTietSanPham(MaChiTietSanPham),
    SoLuongBan INT NOT NULL CHECK (SoLuongBan > 0),
    GiaBanTaiThoiDiem DECIMAL(18,2) NOT NULL,
    GiaVonTaiThoiDiem DECIMAL(18,2) NOT NULL,
    ThanhTien AS (SoLuongBan * GiaBanTaiThoiDiem) -- Công thức tính Thành Tiền tự động
);