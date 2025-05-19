import axiosClient from "./baseApi";

const contractApi = {
  createContract: async (data) => {
    try {
      const response = await axiosClient.post("/admin/contract", data);
      return response.data;
    } catch (error) {
      console.error("Error creating contract:", error);
      throw error; // Xử lý lỗi hoặc hiển thị thông báo lỗi tùy ý
    }
  },

  updateContract: async (data) => {
    try {
      const response = await axiosClient.put("/admin/contract", data);
      return response.data;
    } catch (error) {
      console.error("Error updating contract:", error);
      throw error; // Xử lý lỗi hoặc hiển thị thông báo lỗi tùy ý
    }
  },

  getContractById: async (contractId) => {
    try {
      const response = await axiosClient.get(`/admin/contract/${contractId}`);
      return response.data;
    } catch (error) {
      console.error("Error fetching contract by ID:", error);
      throw error; // Xử lý lỗi hoặc hiển thị thông báo lỗi tùy ý
    }
  },

  getContractStatus: async () => {
    try {
      const response = await axiosClient.get("/admin/contract-status");
      return response.data;
    } catch (error) {
      console.error("Error fetching contract status:", error);
      throw error; // Xử lý lỗi hoặc hiển thị thông báo lỗi tùy ý
    }
  },

  deleteContract: async (contractId) => {
    try {
      const response = await axiosClient.delete("/admin/contract", {
        data: contractId,
      });
      return response.data;
    } catch (error) {
      console.error("Error deleting contract:", error);
      throw error; // Xử lý lỗi hoặc hiển thị thông báo lỗi tùy ý
    }
  },

  getContractsFilter: async (params) => {
    try {
      const response = await axiosClient.get("/auth/contract", {
        params,
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching contracts with filter:", error);
      throw error; // Xử lý lỗi hoặc hiển thị thông báo lỗi tùy ý
    }
  },
};

export default contractApi;
