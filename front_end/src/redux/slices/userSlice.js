import userApi from "@/src/api/userApi";
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

const initialState = {
  listStaff: null,
  loading: false,
  listCustomer: null,
  error: null,
};

const userSlice = createSlice({
  name: "user",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getListStaff.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getListStaff.fulfilled, (state, action) => {
        state.loading = false;
        state.listStaff = action.payload;
      })
      .addCase(getListStaff.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(getListCustomer.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getListCustomer.fulfilled, (state, action) => {
        state.loading = false;
        state.listCustomer = action.payload;
      })
      .addCase(getListCustomer.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default userSlice.reducer;

export const getListStaff = createAsyncThunk(
  "user/listStaff",
  async (thunkAPI) => {
    try {
      const res = await userApi.getUsers({ role: "STAFF" });
      return res;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);
export const getListCustomer = createAsyncThunk(
  "user/listCustomer",
  async (thunkAPI) => {
    try {
      const res = await userApi.getUsers({ role: "CUSTOMER" });
      return res;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);
