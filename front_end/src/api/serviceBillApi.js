import axiosClient from "./baseApi";

const serviceBillApi = {
  createServiceBill: async (data) => {
    try {
      const res = await axiosClient.post("/admin/service-bill", data);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  updateServiceBill: async (data) => {
    try {
      const res = await axiosClient.put("/admin/service-bill", data);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  getServiceBillById: async (id) => {
    try {
      const res = await axiosClient.get(`/auth/service-bill/${id}`);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  deleteServiceBill: async (id) => {
    try {
      const res = await axiosClient.delete(`/admin/service-bill`, {
        data: id,
      });
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  getAllBillStatuses: async () => {
    try {
      const res = await axiosClient.get(`/admin/bill-status`);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  getAllServiceBillNames: async () => {
    try {
      const res = await axiosClient.get(`/admin/service-name`);
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },

  filterServiceBills: async (params) => {
    try {
      const res = await axiosClient.get(`/admin/service-bill`, {
        params,
      });
      return res.data;
    } catch (error) {
      throw error.response?.data || error;
    }
  },
};

export default serviceBillApi;
