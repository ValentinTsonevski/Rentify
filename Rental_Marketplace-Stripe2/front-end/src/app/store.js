


import { configureStore } from "@reduxjs/toolkit";
import likedItemsReducer from "../features/likedItems.js";
import userReducer from "../features/userSlice.js";

import userReviewReducer from "../features/userReviewSlice.js"
import allReviewsReducer from "../features/allReviewsSlice.js"
import ratingReviewReducer from "../features/ratingReview.js"
import userTokenReducer from "../features/userTokenSlice.js"


const store = configureStore({
  reducer: {
    likedItems: likedItemsReducer,
    user: userReducer,
    userReview : userReviewReducer,
    allReviews : allReviewsReducer,
    ratingReview: ratingReviewReducer,
    userToken : userTokenReducer
    
  },
});

export default store;