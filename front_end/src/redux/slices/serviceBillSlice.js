// src/redux/slices/serviceBillSlice.js
import serviceBillApi from "@/src/api/serviceBillApi";
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

// Initial state
const initialState = {
  currentBill: null,
  bills: [],
  statuses: [],
  serviceNames: [],
  loading: false,
  error: null,
};

// Async Thunks
export const fetchServiceBillById = createAsyncThunk(
  "serviceBill/fetchById",
  async (id, thunkAPI) => {
    try {
      return await serviceBillApi.getServiceBillById(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

export const createServiceBill = createAsyncThunk(
  "serviceBill/create",
  async (data, thunkAPI) => {
    try {
      return await serviceBillApi.createServiceBill(data);
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

export const updateServiceBill = createAsyncThunk(
  "serviceBill/update",
  async (data, thunkAPI) => {
    try {
      return await serviceBillApi.updateServiceBill(data);
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

export const deleteServiceBill = createAsyncThunk(
  "serviceBill/delete",
  async (id, thunkAPI) => {
    try {
      return await serviceBillApi.deleteServiceBill(id);
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

export const fetchAllBillStatuses = createAsyncThunk(
  "serviceBill/fetchStatuses",
  async (_, thunkAPI) => {
    try {
      return await serviceBillApi.getAllBillStatuses();
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

export const fetchAllServiceBillNames = createAsyncThunk(
  "serviceBill/fetchNames",
  async (_, thunkAPI) => {
    try {
      return await serviceBillApi.getAllServiceBillNames();
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

export const filterServiceBills = createAsyncThunk(
  "serviceBill/filter",
  async (params, thunkAPI) => {
    try {
      return await serviceBillApi.filterServiceBills(params);
    } catch (error) {
      return thunkAPI.rejectWithValue(error);
    }
  }
);

// Slice
const serviceBillSlice = createSlice({
  name: "serviceBill",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder

      // Fetch single bill
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
      })

      // Create bill
      .addCase(createServiceBill.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createServiceBill.fulfilled, (state, action) => {
        state.loading = false;
        state.bills.push(action.payload);
      })
      .addCase(createServiceBill.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Update bill
      .addCase(updateServiceBill.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateServiceBill.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.bills.findIndex((b) => b.id === action.payload.id);
        if (index !== -1) {
          state.bills[index] = action.payload;
        }
      })
      .addCase(updateServiceBill.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Delete bill
      .addCase(deleteServiceBill.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteServiceBill.fulfilled, (state, action) => {
        state.loading = false;
        state.bills = state.bills.filter((b) => b.id !== action.meta.arg);
      })
      .addCase(deleteServiceBill.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch statuses
      .addCase(fetchAllBillStatuses.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllBillStatuses.fulfilled, (state, action) => {
        state.loading = false;
        state.statuses = action.payload;
      })
      .addCase(fetchAllBillStatuses.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Fetch service names
      .addCase(fetchAllServiceBillNames.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchAllServiceBillNames.fulfilled, (state, action) => {
        state.loading = false;
        state.serviceNames = action.payload;
      })
      .addCase(fetchAllServiceBillNames.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })

      // Filter service bills
      .addCase(filterServiceBills.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(filterServiceBills.fulfilled, (state, action) => {
        state.loading = false;
        state.bills = action.payload;
      })
      .addCase(filterServiceBills.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export default serviceBillSlice.reducer;
