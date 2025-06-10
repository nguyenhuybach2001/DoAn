import { message } from "antd";
import axiosClient from "./baseApi";

const roomApi = {
  // Tạo 1 phòng
  createRoom: async (data) => {
    const response = await axiosClient.post("admin/room", data);
    return response.data;
  },

  // Tạo nhiều phòng
  createRooms: async (data) => {
    const response = await axiosClient.post("admin/rooms", data);
    return response.data;
  },

  // Cập nhật phòng
  updateRoom: async (data) => {
    const response = await axiosClient.put("admin/room", data);
    return response.data;
  },

  // Xóa danh sách phòng
  deleteRoom: async (roomIds) => {
    const response = await axiosClient.delete("/admin/rooms", {
      data: roomIds,
    });
    return response.data;
  },

  // Hiện phòng (giả sử là bật trạng thái active lên)
  showRoom: async (roomIds) => {
    try {
      const response = await axiosClient.put("/admin/rooms", roomIds);
      return response.data;
    } catch (error) {
      const msg =
        error?.response?.data?.error ||
        error?.response?.data?.message || // Nếu backend trả về `message`
        error?.message ||
        "Đã có lỗi xảy ra khi mở phòng.";
      message.error(msg);
    }
  },

  // Lấy danh sách phòng (theo filter hoặc buildingId)
  getAllRooms: async (data) => {
    const response = await axiosClient.get("/rooms", {
      params: data,
    });
    return response.data;
  },
  getAllRoomsByRoleUser: async (data) => {
    const response = await axiosClient.get("/auth/rooms", {
      params: data,
    });
    return response.data;
  },

  // Gán quản lý phòng (set manager)
  setRoomManager: async (roomId, email) => {
    const response = await axiosClient.put("/admin/room/manager", null, {
      params: { room_id: roomId, email },
    });
    return response.data;
  },

  // Lấy chi tiết phòng theo ID
  getRoomById: async (roomId) => {
    const response = await axiosClient.get(`/admin/room/${roomId}`);
    return response.data;
  },
};

export default roomApi;
