import { createSlice } from '@reduxjs/toolkit';


export const allReviewsSlice = createSlice({
  name: "allReviews",
  initialState: {
   values:[]
      
  },
  reducers: {
    updateAllReviews : (state, action) => {
        state.values= action.payload;
    }
  }
});


export const {updateAllReviews} = allReviewsSlice.actions;
export default allReviewsSlice.reducer;