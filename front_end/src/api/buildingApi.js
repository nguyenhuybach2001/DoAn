import axiosClient from "./baseApi";

const buildingApi = {
  createBuilding: async (name, address, image) => {
    try {
      const response = await axiosClient.post("/admin/building", {
        name,
        address,
        image,
      });
      return response.data;
    } catch (error) {
      console.error("Error creating building:", error);
      throw error; // Hoặc bạn có thể trả về thông báo lỗi tùy ý
    }
  },

  getBuilding: async () => {
    try {
      const response = await axiosClient.get("/admin/building");
      return response.data;
    } catch (error) {
      console.error("Error fetching buildings:", error);
      throw error; // Xử lý lỗi theo cách bạn muốn
    }
  },

  updateBuilding: async (data) => {
    try {
      const response = await axiosClient.put("/admin/building", data);
      return response.data;
    } catch (error) {
      console.error("Error updating building:", error);
      throw error; // Xử lý lỗi theo cách bạn muốn
    }
  },

  deleteBuilding: async (buildingId) => {
    try {
      const response = await axiosClient.delete(
        `/admin/building?building_id=${buildingId}`
      );
      return response.data;
    } catch (error) {
      console.error("Error deleting building:", error);
      throw error; // Xử lý lỗi theo cách bạn muốn
    }
  },

  showBuilding: async (buildingId) => {
    try {
      const response = await axiosClient.put(
        `/admin/building/active?building_id=${buildingId}`
      );
      return response.data;
    } catch (error) {
      console.error("Error showing building:", error);
      throw error; // Xử lý lỗi theo cách bạn muốn
    }
  },
};

export default buildingApi;
