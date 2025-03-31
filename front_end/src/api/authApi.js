import axiosClient from "./baseApi";

const authApi = {
  login: async (email, password) => {
    const response = await axiosClient.post("/auth/login", { email, password });
    if (response.data.accessToken && response.data.refreshToken) {
      localStorage.setItem("accessToken", response.data.accessToken);
      localStorage.setItem("refreshToken", response.data.refreshToken);
    }
    return response.data;
  },

  createAccount: async (fullName, email, password, roleName) => {
    const response = await axiosClient.post("/admin/create-account", {
      fullName,
      email,
      password,
      roleName,
    });
    return response.data;
  },

  register: async (fullName, email, password) => {
    const response = await axiosClient.post("/customer/register", {
      fullName,
      email,
      password,
    });
    return response.data;
  },

  refreshToken: async () => {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) throw new Error("No refresh token available");

    const response = await axiosClient.post("/refresh-token", { refreshToken });

    if (response.data.accessToken) {
      localStorage.setItem("accessToken", response.data.accessToken);
    }

    return response.data.accessToken;
  },

  getUserInfo: async () => {
    const response = await axiosClient.get("/me");
    return response.data;
  },

  activateAccount: async (token) => {
    const response = await axiosClient.post("/active/account", { token });
    return response.data;
  },
};

export default authApi;
