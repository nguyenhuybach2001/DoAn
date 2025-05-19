import { createSlice } from "@reduxjs/toolkit";

const initialState = {
  dataProvince: null,
};

const provinceSlice = createSlice({
  name: "province",
  initialState,
  reducers: {
    addDataProvince: (state, action) => {
      state.dataProvince = action.payload;
    },
  },
});

export const { addDataProvince } = provinceSlice.actions;
export default provinceSlice.reducer;
