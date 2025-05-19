import axiosClient from "./baseApi";

const notificationApi = {
  // 📥 Lấy tất cả thông báo của user
  getAll: async (userId) => {
    try {
      const response = await axiosClient.get("/auth/notification", {
        params: { user_id: userId },
      });
      return response.data;
    } catch (error) {
      console.error("Error getting notifications:", error);
      throw error;
    }
  },

  // ✅ Đánh dấu đã đọc
  markAsRead: async (id) => {
    try {
      const response = await axiosClient.put(`/auth/notification/${id}`);
      return response.data;
    } catch (error) {
      console.error("Error marking notification as read:", error);
      throw error;
    }
  },

  // ❌ Xoá nhiều thông báo theo ID
  delete: async (ids) => {
    try {
      const response = await axiosClient.delete(`/auth/notification`, {
        data: ids,
      });
      return response.data;
    } catch (error) {
      console.error("Error deleting notifications:", error);
      throw error;
    }
  },

  // 🚀 Gửi thông báo (nếu cần dùng)
  send: async (notificationData) => {
    try {
      const response = await axiosClient.post(`/auth/notification`, notificationData);
      return response.data;
    } catch (error) {
      console.error("Error sending notification:", error);
      throw error;
    }
  },
};

export default notificationApi;
