import axiosClient from "./baseApi";

const statisticApi = {
  //  Doanh thu dịch vụ
  getTotalIncome: async (params) => {
    try {
      const response = await axiosClient.get("/admin/statistic/service-bill", { params });
      return response.data;
    } catch (error) {
      console.error("Error fetching total income:", error);
      throw error;
    }
  },

  getTotalIncomePerMonth: async ({ year, room_id, building_id }) => {
    try {
      const response = await axiosClient.get("/admin/statistic/service-bill/monthly", {
        params: { year, room_id, building_id },
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching income per month:", error);
      throw error;
    }
  },

  //  Chi phí bảo trì
  getTotalExpenses: async (params) => {
    try {
      const response = await axiosClient.get("/admin/statistic/expenses", { params });
      return response.data;
    } catch (error) {
      console.error("Error fetching total expenses:", error);
      throw error;
    }
  },

  getTotalExpensesPerMonth: async ({ year, room_id, building_id }) => {
    try {
      const response = await axiosClient.get("/admin/statistic/expenses/monthly", {
        params: { year, room_id, building_id },
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching expenses per month:", error);
      throw error;
    }
  },

  // 💰 Lợi nhuận
  getProfit: async (params) => {
    try {
      const response = await axiosClient.get("/admin/statistic/profit", { params });
      return response.data;
    } catch (error) {
      console.error("Error fetching profit:", error);
      throw error;
    }
  },

  getProfitPerMonth: async ({ year, room_id, building_id }) => {
    try {
      const response = await axiosClient.get("/admin/statistic/profit/monthly", {
        params: { year, room_id, building_id },
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching profit per month:", error);
      throw error;
    }
  },

  // 📄 Số lượng hợp đồng
  getContractQuantity: async (params) => {
    try {
      const response = await axiosClient.get("/admin/statistic/contracts", { params });
      return response.data;
    } catch (error) {
      console.error("Error fetching contract quantity:", error);
      throw error;
    }
  },

  getContractsPerMonth: async ({ year, room_id, building_id, status }) => {
    try {
      const response = await axiosClient.get("/admin/statistic/contracts/monthly", {
        params: { year, room_id, building_id, status },
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching contracts per month:", error);
      throw error;
    }
  },

  // ⚡ Sử dụng tiện ích
  getUtilityUsage: async (params) => {
    try {
      const response = await axiosClient.get("/admin/statistic/usage", { params });
      return response.data;
    } catch (error) {
      console.error("Error fetching utility usage:", error);
      throw error;
    }
  },

  getUtilityUsagePerMonth: async ({ year, room_id, building_id, utilityType }) => {
    try {
      const response = await axiosClient.get("/admin/statistic/usage/monthly", {
        params: { year, room_id, building_id, utilityType },
      });
      return response.data;
    } catch (error) {
      console.error("Error fetching utility usage per month:", error);
      throw error;
    }
  },
};

export default statisticApi;
