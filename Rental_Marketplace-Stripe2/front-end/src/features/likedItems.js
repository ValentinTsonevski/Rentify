import { createSlice } from '@reduxjs/toolkit';

export const likedItemsSlice = createSlice({
  name: "likedItems",
  initialState: {
    values: [] 
  },
  reducers: {
    like: (state, action) => {
      state.values = action.payload; 
    },
  }
});

export const { like } = likedItemsSlice.actions;
export default likedItemsSlice.reducer;



