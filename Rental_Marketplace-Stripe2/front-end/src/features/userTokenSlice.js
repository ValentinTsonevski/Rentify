import { createSlice } from '@reduxjs/toolkit';

export const userTokenSlice = createSlice({
  name: "userToken",
  initialState: {
    id: null,
    isLoggedIn: false
  },
  reducers: {
    updateUserToken: (state, action) => {
      state.id = action.payload.id; 
    },
    updateIsLoggedIn: (state, action) => {
      state.isLoggedIn = action.payload.isLoggedIn; 
    }
  }
});


export const { updateUserToken, updateIsLoggedIn } = userTokenSlice.actions;


export default userTokenSlice.reducer;