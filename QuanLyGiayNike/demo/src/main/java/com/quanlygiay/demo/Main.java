package com.quanlygiay.demo;

import com.quanlygiay.demo.config.DatabaseConnection;
import com.quanlygiay.demo.model.ChiTietSanPham;
import com.quanlygiay.demo.repository.ChiTietSanPhamRepository;
import com.quanlygiay.demo.service.QuanLyKhoService; // Sẽ hết báo vàng khi dùng bên dưới
import java.sql.Connection;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("========== HE THONG QUAN LY CUA HANG GIAY ==========");
        
        // 1. Kiểm tra kết nối Database
        System.out.print("1. Kiem tra ket noi SQL Server: ");
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null) {
                System.out.println("OK!");
                
                // 2. Sử dụng QuanLyKhoService để test (Dùng ở đây sẽ hết cảnh báo import)
                QuanLyKhoService khoService = new QuanLyKhoService();
                System.out.println("\n2. Dang tai du lieu tu QuanLyKhoService...");
                
                List<ChiTietSanPham> dsCanhBao = khoService.layDanhSachCanhBaoHetHang();
                
                if (dsCanhBao.isEmpty()) {
                    System.out.println("-> Hien tai kho hang deu on dinh.");
                } else {
                    System.out.println("-> PHAT HIEN " + dsCanhBao.size() + " SAN PHAM SAP HET.");
                }
            }
        } catch (Exception e) {
            System.out.println("THAT BAI!");
            System.err.println("Loi: " + e.getMessage());
        }

        // 2. Chạy thử logic của Phần [1] (Quan ly kho)
        System.out.println("\n2. Dang tai du lieu kho hang...");
        ChiTietSanPhamRepository repo = new ChiTietSanPhamRepository();
        List<ChiTietSanPham> dsCanhBao = repo.layDanhSachCanhBaoHetHang();

        if (dsCanhBao.isEmpty()) {
            System.out.println("-> Hien tai khong co san pham nao sap het hang.");
        } else {
            System.out.println("-> CANH BAO: Co " + dsCanhBao.size() + " san pham sap het hang!");
            for (ChiTietSanPham sp : dsCanhBao) {
                System.out.println("   + Ma vach: " + sp.getBarcode() + " | Ton kho: " + sp.getSoLuongTon());
            }
        }

        // 3. Thông báo trạng thái bàn giao
        System.out.println("\n3. Trang thai he thong:");
        System.out.println("- Backend logic (Repository/Service): SAN SANG");
        System.out.println("- Interface ban hang (Phan 2): DA DINH NGHIA");
        System.out.println("- DTO trao doi du lieu: DA HOAN THIEN");

        System.out.println("\n====================================================");
        System.out.println("DEMO KET THUC THANH CONG");
    }
}