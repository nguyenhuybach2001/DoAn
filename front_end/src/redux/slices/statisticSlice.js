import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";
import statisticApi from "@/src/api/statisticApi";

const initialState = {
  totalIncome: null,
  incomePerMonth: null,
  totalExpenses: null,
  expensesPerMonth: null,
  profit: null,
  profitPerMonth: null,
  contractQuantity: null,
  contractsPerMonth: null,
  utilityUsage: null,
  utilityUsagePerMonth: null,
  loading: false,
  error: null,
};

// Thunks
export const getTotalIncome = createAsyncThunk("statistic/totalIncome", async (params, thunkAPI) => {
  try {
    return await statisticApi.getTotalIncome(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getTotalIncomePerMonth = createAsyncThunk("statistic/incomePerMonth", async (params, thunkAPI) => {
  try {
    return await statisticApi.getTotalIncomePerMonth(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getTotalExpenses = createAsyncThunk("statistic/totalExpenses", async (params, thunkAPI) => {
  try {
    return await statisticApi.getTotalExpenses(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getTotalExpensesPerMonth = createAsyncThunk("statistic/expensesPerMonth", async (params, thunkAPI) => {
  try {
    return await statisticApi.getTotalExpensesPerMonth(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getProfit = createAsyncThunk("statistic/profit", async (params, thunkAPI) => {
  try {
    return await statisticApi.getProfit(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getProfitPerMonth = createAsyncThunk("statistic/profitPerMonth", async (params, thunkAPI) => {
  try {
    return await statisticApi.getProfitPerMonth(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getContractQuantity = createAsyncThunk("statistic/contractQuantity", async (params, thunkAPI) => {
  try {
    return await statisticApi.getContractQuantity(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getContractsPerMonth = createAsyncThunk("statistic/contractsPerMonth", async (params, thunkAPI) => {
  try {
    return await statisticApi.getContractsPerMonth(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getUtilityUsage = createAsyncThunk("statistic/utilityUsage", async (params, thunkAPI) => {
  try {
    return await statisticApi.getUtilityUsage(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

export const getUtilityUsagePerMonth = createAsyncThunk("statistic/utilityUsagePerMonth", async (params, thunkAPI) => {
  try {
    return await statisticApi.getUtilityUsagePerMonth(params);
  } catch (error) {
    return thunkAPI.rejectWithValue(error.response?.data || error.message);
  }
});

// Slice
const statisticSlice = createSlice({
  name: "statistic",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    const asyncThunks = [
      [getTotalIncome, "totalIncome"],
      [getTotalIncomePerMonth, "incomePerMonth"],
      [getTotalExpenses, "totalExpenses"],
      [getTotalExpensesPerMonth, "expensesPerMonth"],
      [getProfit, "profit"],
      [getProfitPerMonth, "profitPerMonth"],
      [getContractQuantity, "contractQuantity"],
      [getContractsPerMonth, "contractsPerMonth"],
      [getUtilityUsage, "utilityUsage"],
      [getUtilityUsagePerMonth, "utilityUsagePerMonth"],
    ];

    asyncThunks.forEach(([thunk, key]) => {
      builder
        .addCase(thunk.pending, (state) => {
          state.loading = true;
          state.error = null;
        })
        .addCase(thunk.fulfilled, (state, action) => {
          state.loading = false;
          state[key] = action.payload;
        })
        .addCase(thunk.rejected, (state, action) => {
          state.loading = false;
          state.error = action.payload;
        });
    });
  },
});

export default statisticSlice.reducer;
