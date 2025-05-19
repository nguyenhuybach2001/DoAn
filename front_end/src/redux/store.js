import { configureStore } from "@reduxjs/toolkit";
import modalReducer from "./slices/modalSlice";
import authReducer from "./slices/authSlice";
import roomReducer from "./slices/roomSlice";
import provinceReducer from "./slices/provincesSlice";
import buildingReducer from "./slices/buildingSlice";
import userReducer from "./slices/userSlice";
import contractReducer from "./slices/contractSlice";
import serviceBillReducer from "./slices/serviceBillSlice";
import statisticReducer from "./slices/statisticSlice";

export const makeStore = () => {
  return configureStore({
    reducer: {
      modal: modalReducer,
      auth: authReducer,
      province: provinceReducer,
      rooms: roomReducer,
      building: buildingReducer,
      contract: contractReducer,
      user: userReducer,
      serviceBill: serviceBillReducer,
      statistic: statisticReducer,
    },
  });
};
