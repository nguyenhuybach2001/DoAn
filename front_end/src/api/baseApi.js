import axios from "axios";

const axiosClient = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
  headers: {
    "Content-Type": "application/json",
  },
});

const getAccessToken = () => localStorage.getItem("accessToken");
const getRefreshToken = () => localStorage.getItem("refreshToken");

axiosClient.interceptors.request.use((config) => {
  const token = getAccessToken();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

axiosClient.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config;
    if (error.response?.status === 403 && !originalRequest._retry) {
      originalRequest._retry = true;

      try {
        const refreshToken = getRefreshToken();
        console.log(refreshToken);
        if (!refreshToken) throw new Error("Refresh token not found");

        const response = await axios.post(
          `${process.env.NEXT_PUBLIC_API_URL}/refresh-token`,
          { refreshToken }
        );

        const newAccessToken = response.data.accessToken;
        localStorage.setItem("accessToken", newAccessToken);

        originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
        return axiosClient(originalRequest);
      } catch (refreshError) {
        console.error("Failed to refresh token:", refreshError);
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
        window.location.href = "/home";
        return Promise.reject(refreshError);
      }
    }

    return Promise.reject(error);
  }
);

export default axiosClient;
