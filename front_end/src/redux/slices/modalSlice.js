import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  modalLogin: false,
};

const modalSlice = createSlice({
  name: "modal",
  initialState,
  reducers: {
    openLogin: (state) => {
      state.modalLogin = true;
    },
    closeLogin: (state) => {
      state.modalLogin = false;
    },
  },
});

export const { openLogin, closeLogin } = modalSlice.actions;
export default modalSlice.reducer;
