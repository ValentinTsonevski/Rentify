import { createSlice } from '@reduxjs/toolkit';


export const userReviewSlice = createSlice({
  name: "userReview",
  initialState: {
   values:[]
      
  },
  reducers: {
    updateUserReview : (state, action) => {
        state.values= action.payload;
    }
  }
});


export const {updateUserReview} = userReviewSlice.actions;
export default userReviewSlice.reducer;