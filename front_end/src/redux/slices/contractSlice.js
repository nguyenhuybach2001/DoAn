import contractApi from "@/src/api/contractApi";
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

const initialState = {
  listContracts: null,
  loading: false,
  error: null,
};

const contractSlice = createSlice({
  name: "contract",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getListContracts.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getListContracts.fulfilled, (state, action) => {
        state.loading = false;
        state.listContracts = action.payload;
      })
      .addCase(getListContracts.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const {} = contractSlice.actions;
export default contractSlice.reducer;

export const getListContracts = createAsyncThunk(
  "contract/listContracts",
  async (params = {}, thunkAPI) => {
    try {
      const res = await contractApi.getContractsFilter(params);
      return res.content || []; 
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);
