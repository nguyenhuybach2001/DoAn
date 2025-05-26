import axiosClient from "./baseApi";

const assetApi = {
  // ==============================
  // 📦 ASSET IN ROOM
  // ==============================

  // ➕ Tạo danh sách tài sản trong phòng
  createAssetsInRoom: async (assets) => {
    return axiosClient.post("/admin/asset-in-room", assets);
  },

  // 📝 Cập nhật tài sản trong phòng
  updateAssetInRoom: async (asset) => {
    return axiosClient.put("/admin/asset-in-room", asset);
  },

  // 🔍 Lấy thông tin tài sản trong phòng theo ID
  getAssetInRoomById: async (id) => {
    return axiosClient.get(`/admin/asset-in-room/${id}`);
  },

  // 🗑️ Xoá nhiều tài sản trong phòng theo danh sách ID
  deleteAssetsInRoom: async (ids) => {
    return axiosClient.delete("/admin/asset-in-room", { data: ids });
  },

  // 🔍 Lọc tài sản trong phòng theo nhiều tiêu chí
  filterAssetsInRoom: async (params) => {
    return axiosClient.get("/admin/assets-in-room", { params });
  },

  // 📊 Lấy danh sách trạng thái tài sản (enum)
  getAssetStatuses: async () => {
    return axiosClient.get("/admin/asset-status");
  },

  // ==============================
  // 🏢 ASSET OF BUILDING
  // ==============================

  // ➕ Tạo danh sách tài sản của toà nhà
  createAssetsOfBuilding: async (assets) => {
    return axiosClient.post("/admin/asset-of-building", assets);
  },

  // 📝 Cập nhật tài sản của toà nhà
  updateAssetOfBuilding: async (asset) => {
    return axiosClient.put("/admin/asset-of-building", asset);
  },

  // 🔍 Lấy thông tin tài sản của toà nhà theo ID
  getAssetOfBuildingById: async (id) => {
    return axiosClient.get(`/admin/asset-of-building/${id}`);
  },

  // 🗑️ Xoá nhiều tài sản của toà nhà theo danh sách ID
  deleteAssetsOfBuilding: async (ids) => {
    return axiosClient.delete("/admin/asset-of-building", { data: ids });
  },

  // 🔍 Lọc tài sản của toà nhà theo nhiều tiêu chí
  filterAssetsOfBuilding: async (params) => {
    return axiosClient.get("/admin/assets-of-building", { params });
  },
};

export default assetApi;
