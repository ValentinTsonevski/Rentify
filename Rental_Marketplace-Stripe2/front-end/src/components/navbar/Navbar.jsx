import React, { useEffect, useState } from "react";
import { Link, useLocation } from "react-router-dom";
import logo from "../../assets/rentify-logo.svg";
import FavoriteBorderIcon from "@mui/icons-material/FavoriteBorder";
import LogoutIcon from "@mui/icons-material/Logout";
import PersonIcon from "@mui/icons-material/Person";
import VisibilityIcon from "@mui/icons-material/Visibility";
import "./Navbar.css";

import axios from 'axios';
import { useDispatch, useSelector } from "react-redux"; 
import { like } from "../../features/likedItems";
import {updateUser} from "../../features/userSlice"
import {updateIsLoggedIn} from "../../features/userTokenSlice.js"
import {updateUserToken} from "../../features/userTokenSlice.js";



const Navbar = () => {
  const location = useLocation();
  const dispatch = useDispatch();
  const userProfile = useSelector((state) => state.user.values);
  const userId = useSelector((state) => state.userToken.id);
  const isLoggedIn = useSelector((state) => state.userToken.isLoggedIn);

  useEffect(() => {

    if (userId !== null) {
     dispatch(updateIsLoggedIn({ isLoggedIn: true }));

     
      const fetchUserInfo = async () => {

        try {
          const response = await axios.get(`http://localhost:8080/rentify/users/${userId}`);

          dispatch(updateUser(response.data));


        }
        catch (error) {
          console.error("Error fetching user Info ", error);
        }
      };

      fetchUserInfo();
    }

    
  }, [location , userProfile.profilePicture , isLoggedIn , userId]);


  const handleLogout = () => {
    Object.keys(localStorage).forEach(key => { localStorage.removeItem(key); });

    dispatch(like([]));
    dispatch(updateIsLoggedIn({ isLoggedIn: false }));
    dispatch(updateUserToken({id : null}))
  };

  return (
    <nav className="navbar">
      <Link to="/">
        {" "}
        <img src={logo} width={"200px"} alt="Logo" />{" "}
      </Link>
      <div className="links">
        <Link to="/">Home</Link>

        {isLoggedIn ? (
          <>


<Link to="/items/create">Add Item</Link>
            <Link to="/likes"> <FavoriteBorderIcon /> </Link>
            <Link to="/views"><VisibilityIcon /> </Link>
            {userProfile.profilePicture ? (
              <Link to="/settings">  <img
                src={userProfile.profilePicture}
                alt="Profile"
                className="profile-picture"
              /> </Link>
         
            ) : (
              <Link to="/settings">
                {" "}
                <PersonIcon />{" "}
              </Link>
            )}
            <Link to="/" onClick={handleLogout}>
              {" "}
              <LogoutIcon />{" "}
            </Link>
          </>
        ) : (
          <>
            <Link to="/login">Login</Link>
            <Link to="/register">Register</Link>
          </>
        )}
      </div>
    </nav>
  );
};

export default Navbar;
