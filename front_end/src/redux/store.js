import { configureStore } from "@reduxjs/toolkit";
import modalReducer from "./slices/modalSlice";

export const makeStore = () => {
  return configureStore({
    reducer: {
      modal: modalReducer,
    },
  });
};
