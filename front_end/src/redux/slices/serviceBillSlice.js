// src/redux/slices/serviceBillSlice.js
import serviceBillApi from "@/src/api/serviceBillApi";
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

const initialState = {
  currentBill: null,
  loading: false,
  error: null,
};

export const fetchServiceBillById = createAsyncThunk(
  "serviceBill/fetchById",
  async (id, thunkAPI) => {
    try {
      const res = await serviceBillApi.getServiceBillById(id);
      return res;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);

const serviceBillSlice = createSlice({
  name: "serviceBill",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(fetchServiceBillById.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchServiceBillById.fulfilled, (state, action) => {
        state.loading = false;
        state.currentBill = action.payload;
      })
      .addCase(fetchServiceBillById.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default serviceBillSlice.reducer;
