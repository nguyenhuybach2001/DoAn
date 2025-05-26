import axiosClient from "./baseApi";

const requestApi = {
  // 📝 Tạo yêu cầu bảo trì (Khách hàng)
  create: async (requestData) => {
    try {
      const response = await axiosClient.post("/customer/request", requestData);
      return response.data;
    } catch (error) {
      console.error("Error creating request:", error);
      throw error;
    }
  },

  // ✏️ Cập nhật yêu cầu bảo trì (Khách hàng)
  update: async (requestData) => {
    try {
      const response = await axiosClient.put("/customer/request", requestData);
      return response.data;
    } catch (error) {
      console.error("Error updating request:", error);
      throw error;
    }
  },

  // 🔍 Lấy yêu cầu theo ID (Admin)
  getById: async (requestId) => {
    try {
      const response = await axiosClient.get(`/admin/request/${requestId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching request by ID:", error);
      throw error;
    }
  },

  // ✅ Cập nhật trạng thái yêu cầu (Nhân viên)
  updateStatus: async (id, status) => {
    try {
      const response = await axiosClient.put(
        `/staff/request-status/${id}`,
        status
      );
      return response.data;
    } catch (error) {
      console.error("Error updating request status:", error);
      throw error;
    }
  },

  // 📋 Lấy danh sách trạng thái yêu cầu (Admin)
  getAllStatuses: async () => {
    try {
      const response = await axiosClient.get("/admin/request-status");
      return response.data;
    } catch (error) {
      console.error("Error fetching request statuses:", error);
      throw error;
    }
  },

  // ❌ Xoá yêu cầu (Admin)
  delete: async (id) => {
    try {
      const response = await axiosClient.delete("/admin/request", { data: id });
      return response.data;
    } catch (error) {
      console.error("Error deleting request:", error);
      throw error;
    }
  },

  // 🔍 Lọc danh sách yêu cầu (Admin)
  filter: async (params) => {
    try {
      const response = await axiosClient.get("/admin/request", { params });
      return response.data;
    } catch (error) {
      console.error("Error filtering requests:", error);
      throw error;
    }
  },
};

export default requestApi;
