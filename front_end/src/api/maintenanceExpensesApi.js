// src/api/maintenanceExpensesApi.js
import axiosClient from "./baseApi";

const maintenanceExpensesApi = {
  // 🧾 Tạo hóa đơn bảo trì (Nhân viên)
  create: async (expenseData) => {
    try {
      const response = await axiosClient.post("/staff/maintenance-expense", expenseData);
      return response.data;
    } catch (error) {
      console.error("Error creating maintenance expense:", error);
      throw error;
    }
  },

  // ✏️ Cập nhật hóa đơn bảo trì (Nhân viên)
  update: async (expenseData) => {
    try {
      const response = await axiosClient.put("/staff/maintenance-expense", expenseData);
      return response.data;
    } catch (error) {
      console.error("Error updating maintenance expense:", error);
      throw error;
    }
  },

  // ❌ Xoá hóa đơn bảo trì (Nhân viên/Admin)
  delete: async (id) => {
    try {
      const response = await axiosClient.delete(`/staff/maintenance-expense/${id}`);
      return response.data;
    } catch (error) {
      console.error("Error deleting maintenance expense:", error);
      throw error;
    }
  },

  // 🔍 Lấy chi tiết hóa đơn bảo trì theo ID (Nhân viên/Admin)
  getById: async (id) => {
    try {
      const response = await axiosClient.get(`/staff/maintenance-expense/${id}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching maintenance expense by ID:", error);
      throw error;
    }
  },

  // 📊 Lọc danh sách hóa đơn bảo trì (Nhân viên/Admin)
  filter: async (params) => {
    try {
      const response = await axiosClient.get("/staff/maintenance-expense/filter", {
        params,
      });
      return response.data;
    } catch (error) {
      console.error("Error filtering maintenance expenses:", error);
      throw error;
    }
  },

  // 📋 Lấy danh sách loại chi phí (Dropdown)
  getExpenseTypes: async () => {
    try {
      const response = await axiosClient.get("/staff/maintenance-expense/types");
      return response.data;
    } catch (error) {
      console.error("Error fetching expense types:", error);
      throw error;
    }
  },

  // 📋 Lấy danh sách trạng thái duyệt (Dropdown)
  getApprovalStatuses: async () => {
    try {
      const response = await axiosClient.get("/staff/maintenance-expense/statuses");
      return response.data;
    } catch (error) {
      console.error("Error fetching approval statuses:", error);
      throw error;
    }
  },

  // ✅ Cập nhật trạng thái duyệt (Chủ trọ/Admin)
  setApprovalStatus: async (id, approvalStatus) => {
    try {
      const response = await axiosClient.patch(
        `/landlord/maintenance-expense/${id}/approval`,
        null,
        {
          params: { status: approvalStatus },
        }
      );
      return response.data;
    } catch (error) {
      console.error("Error setting approval status:", error);
      throw error;
    }
  },
};

export default maintenanceExpensesApi;
