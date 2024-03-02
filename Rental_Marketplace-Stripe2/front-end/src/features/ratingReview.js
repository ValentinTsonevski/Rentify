import { createSlice } from '@reduxjs/toolkit';

export const ratingReviewSlice = createSlice({
  name: "ratingReview",
  initialState: {
   values: []
  },
  reducers: {
    updateRating: (state, action) => {
      state.values = action.payload;
    }
  }
});

export const { updateRating } = ratingReviewSlice.actions;
export default ratingReviewSlice.reducer;
