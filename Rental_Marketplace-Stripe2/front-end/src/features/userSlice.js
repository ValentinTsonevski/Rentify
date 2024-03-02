import { createSlice } from '@reduxjs/toolkit';


export const userSlice = createSlice({
  name: "user",
  initialState: {
   values: []
        
        
      
  },
  reducers: {
    updateUser : (state, action) => {
        state.values= action.payload;
    }
  }
});
export const {updateUser} = userSlice.actions;
export default userSlice.reducer;