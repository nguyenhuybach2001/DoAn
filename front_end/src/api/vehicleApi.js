// src/api/vehicleApi.js
import axiosClient from "./baseApi";

const vehicleApi = {
  // 🚗 Tạo danh sách phương tiện (Admin)
  create: async (vehicleList) => {
    try {
      const response = await axiosClient.post("/admin/vehicles", vehicleList);
      return response.data;
    } catch (error) {
      console.error("Error creating vehicles:", error);
      throw error;
    }
  },

  // ✏️ Cập nhật phương tiện (Admin)
  update: async (vehicleData) => {
    try {
      const response = await axiosClient.put("/admin/vehicle", vehicleData);
      return response.data;
    } catch (error) {
      console.error("Error updating vehicle:", error);
      throw error;
    }
  },

  // ❌ Xoá phương tiện (Admin)
  delete: async (ids) => {
    try {
      const response = await axiosClient.delete("/admin/vehicle", {
        data: ids,
      });
      return response.data;
    } catch (error) {
      console.error("Error deleting vehicles:", error);
      throw error;
    }
  },

  // 📋 Lấy danh sách loại phương tiện (Admin)
  getVehicleTypes: async () => {
    try {
      const response = await axiosClient.get("/admin/vehicle-type");
      return response.data;
    } catch (error) {
      console.error("Error fetching vehicle types:", error);
      throw error;
    }
  },

  // 🔍 Lọc danh sách phương tiện (Admin)
  filter: async (params) => {
    try {
      const response = await axiosClient.get("/admin/vehicles", { params });
      return response.data;
    } catch (error) {
      console.error("Error filtering vehicles:", error);
      throw error;
    }
  },
};

export default vehicleApi;
