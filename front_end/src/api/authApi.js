import Cookies from "js-cookie";
import axiosClient from "./baseApi";
import { message } from "antd";

const authApi = {
  login: async (email, password) => {
    try {
      const response = await axiosClient.post("/login", {
        email,
        password,
      });
      if (response.data.accessToken && response.data.refreshToken) {
        localStorage.setItem("accessToken", response.data.accessToken);
        localStorage.setItem("refreshToken", response.data.refreshToken);
        Cookies.set("accessToken", response.data.accessToken);
        Cookies.set("refreshToken", response.data.refreshToken);
      }

      return response.data;
    } catch (error) {
      const msg =
        error?.response?.data?.error ||
        error?.error ||
        "Đã có lỗi xảy ra khi lấy thông tin người dùng.";
      message.error(msg); // Hiển thị lỗi trên màn hình
      throw error; // ném lại lỗi để caller (ví dụ trong Redux thunk) biết mà xử lý tiếp
    }
  },

  createAccount: async (data) => {
    const response = await axiosClient.post("/admin/create-account", data);
    return response.data;
  },

  refreshToken: async () => {
    const refreshToken = localStorage.getItem("refreshToken");
    if (!refreshToken) throw new Error("No refresh token available");

    const response = await axiosClient.post("/refresh-token", { refreshToken });

    if (response.data.accessToken) {
      localStorage.setItem("accessToken", response.data.accessToken);
      Cookies.set("accessToken", response.data.accessToken);
    }

    return response.data.accessToken;
  },

  getUserInfo: async () => {
    try {
      const response = await axiosClient.get("/auth/me");
      return response.data;
    } catch (error) {
      console.log(error);
      const msg =
        error?.response?.data?.error ||
        error?.error ||
        "Đã có lỗi xảy ra khi lấy thông tin người dùng.";
      message.error(msg); // Hiển thị lỗi trên màn hình
      throw error; // ném lại lỗi để caller (ví dụ trong Redux thunk) biết mà xử lý tiếp
    }
  },
  changePassword: async (data) => {
    const response = await axiosClient.put("/auth/change-password", data);
    return response.data;
  },
  updateUserInfo: async (data) => {
    const response = await axiosClient.put("/auth/me", data);
    return response.data;
  },

  activateAccount: async (token, email) => {
    const response = await axiosClient.post("/active/account", {
      token,
      email,
    });
    return response.data;
  },
  resetPassword: async (data) => {
    const { oldPassword, newPassword } = data;
    const response = await axiosClient.put("/reset-password", {
      oldPassword,
      newPassword,
    });
    return response.data;
  },
};

export default authApi;
