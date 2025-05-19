import buildingApi from "@/src/api/buildingApi";
import roomApi from "@/src/api/roomApi";
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

const initialState = {
  listBuilding: null,
  loading: false,
  error: null,
};

const buildingSlice = createSlice({
  name: "building",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getListBuilding.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getListBuilding.fulfilled, (state, action) => {
        state.loading = false;
        state.listBuilding = action.payload;
      })
      .addCase(getListBuilding.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const {} = buildingSlice.actions;
export default buildingSlice.reducer;

export const getListBuilding = createAsyncThunk(
  "building/listBuilding",
  async ( thunkAPI) => {
    try {
      const res = await buildingApi.getBuilding();
      return res;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);
