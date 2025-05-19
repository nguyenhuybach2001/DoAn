import axiosClient from "./baseApi";

const userApi = {
  getUsers: async (params) => {
    const response = await axiosClient.get("/auth/user", { params });
    return response.data;
  },

  getUserById: async (userId) => {
    const response = await axiosClient.get(`/admin/user/${userId}`);
    return response.data;
  },
};

export default userApi;
