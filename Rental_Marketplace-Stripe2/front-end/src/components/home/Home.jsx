import React from "react";
import CategoriesList from "../categories/CategoriesList";
import ItemsList from "../items/ItemsList";
import "./Home.css";

import { jwtDecode } from "jwt-decode";

import { useDispatch } from "react-redux";
import { updateUserToken } from "../../features/userTokenSlice";

const Home = () => {
  const dispatch = useDispatch(); 
  const token = localStorage.getItem("token");
  if (token) {
    const decodedToken = jwtDecode(token);
    const id = decodedToken.jti;
    dispatch(updateUserToken({ id }));
    
  }
    return (
    <div className="home">
      <CategoriesList />
      <ItemsList />
    </div>
  );
};

export default Home;
