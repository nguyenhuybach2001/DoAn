import axiosClient from "./baseApi";

const roomUtilityApi = {
  create: async (roomUtilityList) => {
    try {
      const response = await axiosClient.post(
        "/auth/room-utility",
        roomUtilityList
      );
      return response.data;
    } catch (error) {
      console.error("Error creating room utilities:", error);
      throw error;
    }
  },

  update: async (roomUtility) => {
    try {
      const response = await axiosClient.put(
        "/admin/room-utility",
        roomUtility
      );
      return response.data;
    } catch (error) {
      console.error("Error updating room utility:", error);
      throw error;
    }
  },

  // 🔍 Lấy tiện ích phòng theo ID (Admin/Staff)
  getById: async (id) => {
    try {
      const response = await axiosClient.get(`/admin/room-utility/${id}`);
      return response.data;
    } catch (error) {
      console.error("Error getting room utility by ID:", error);
      throw error;
    }
  },

  // 🔍 Lọc danh sách tiện ích phòng (Admin/Staff)
  filter: async (params) => {
    try {
      const response = await axiosClient.get("/admin/room-utility/filter", {
        params,
      });
      return response.data;
    } catch (error) {
      console.error("Error filtering room utilities:", error);
      throw error;
    }
  },
};

export default roomUtilityApi;
