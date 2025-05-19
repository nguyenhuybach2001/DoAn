import roomApi from "@/src/api/roomApi";
import { createAsyncThunk, createSlice } from "@reduxjs/toolkit";

const initialState = {
  listRooms: null,
  listRoomsByRole: null,
  loading: false,
  error: null,
};

const roomSlice = createSlice({
  name: "rooms",
  initialState,
  reducers: {},
  extraReducers: (builder) => {
    builder
      .addCase(getListRooms.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getListRooms.fulfilled, (state, action) => {
        state.loading = false;
        state.listRooms = action.payload;
      })
      .addCase(getListRooms.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      })
      .addCase(getListRoomsByRole.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(getListRoomsByRole.fulfilled, (state, action) => {
        state.loading = false;
        state.listRoomsByRole = action.payload;
      })
      .addCase(getListRoomsByRole.rejected, (state, action) => {
        state.loading = false;
        state.error = action.payload;
      });
  },
});

export const {} = roomSlice.actions;
export default roomSlice.reducer;

export const getListRooms = createAsyncThunk(
  "rooms/listRooms",
  async (data, thunkAPI) => {
    try {
      const res = await roomApi.getAllRooms(data);
      return res;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);
export const getListRoomsByRole = createAsyncThunk(
  "rooms/listRoomsByRole",
  async (data, thunkAPI) => {
    try {
      const res = await roomApi.getAllRoomsByRoleUser(data);
      return res;
    } catch (error) {
      return thunkAPI.rejectWithValue(error.response?.data || error.message);
    }
  }
);
