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
  createAccount: async (email) => {
    const response = await api.post("/auth/create-account", { email });
    return response.data;
  },
  getUserInfo: async () => {
    const accessToken = localStorage.getItem("accessToken");
    if (!accessToken) throw new Error("No access token available");

    const response = await api.get(`/user/info?accessToken=${accessToken}`);
    return response.data;
  },
  register: async (fullName, email, password, roleName) => {
    const response = await axiosClient.post("/admin/register", {
      fullName,
      email,
      password,
      roleName,
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

  logout: () => {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("refreshToken");
    window.location.href = "/home";
  },
};

export default authApi;
