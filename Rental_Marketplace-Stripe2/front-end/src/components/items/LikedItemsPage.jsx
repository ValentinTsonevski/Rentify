import React, { useState, useEffect } from "react";
import FavoriteIcon from "@mui/icons-material/Favorite";
import { Link } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import noImage from "../../assets/no-image.avif";
import "./LIkedItemsPage.css" 
import {useDispatch, useSelector} from 'react-redux'

import { like } from "../../features/likedItems";

const LikedItemsPage = () => {
  const [likedItems, setLikedItems] = useState([]);

  const userId = useSelector((state) => state.userToken.id);

  useEffect(() => {



    const fetchLikedItemsFromDB = async () => {
      try {
     

        if (userId !== null) {

          const response = await fetch(
            `http://localhost:8080/rentify/favourites/userFavourites/${userId}`,
            {
              method: "GET",
            }
          );

          if (response.ok) {
            const likedItemsIds = await response.json();

            const detailedLikedItems = await Promise.all(
              likedItemsIds.map(async (itemId) => {
                const itemResponse = await fetch(
                  `http://localhost:8080/rentify/items/${itemId}`,
                  {
                    method: "GET",
                  }
                );
                if (itemResponse.ok) {
                  return itemResponse.json();
                }
                throw new Error(`HTTP error! Status: ${itemResponse.status}`);
              })
            );
            setLikedItems(detailedLikedItems);
          } else {
            throw new Error(`HTTP error! Status: ${response.status}`);
          }
        }
      } catch (error) {
        console.error("Error fetching liked items:", error.message);
      }
    };

    fetchLikedItemsFromDB();
  }, []);

  const handleLikeClick = async (itemId) => {


    const updatedLikedItems = likedItems.includes(itemId)
      ? likedItems.filter((id) => id !== itemId)
      : [...likedItems, itemId];

    setLikedItems((prevLikedItems) => {
      if (!isLiked) {
        return prevLikedItems.filter((item) => item.id !== itemId);
      }
    });

    const isLiked = likedItems.includes(itemId);

    const requestBody = {
      itemId: itemId,
      userId: parseInt(userId, 10),
      isLiked: isLiked,
    };
    try {
      const response = await fetch(
        "http://localhost:8080/rentify/favourites/liked",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(requestBody),
        }
      );

      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }
    } catch (error) {
      console.error("Error in handleLikeClick:", error.message);
    }
    setLikedItems((prevLikedItems) =>
      prevLikedItems.filter((id) => id !== itemId)
    );
  };
  return (
    
    <div>
      <h1 className="favourite-title">Favourite Items</h1>
    <div className="items-list">
      {likedItems.map((item) => (
        <div className="items-list-item" key={item.id}>
          <Link to={`/items/${item.id}`}>
            <div className="card">
              <img src={item.thumbnail || noImage} className="card-img-top" />
              <div className="card-body">
                <h3 className="card-title">{item.name}</h3>
                <p className="card-text">{"$" + item.price}</p>
                <p className="card-text">{item.address.city}</p>
              </div>
            </div>
            <button
              className={`like-button ${
                !likedItems.includes(item.id) ? "clicked" : ""
              }`}
              onClick={(e) => {
                e.preventDefault();
                try {
                  handleLikeClick(item.id);
                } catch (error) {
                  console.error("Error in handleLikeClick:", error);
                }
              }}
            >
              <FavoriteIcon />
            </button>
          </Link>
        </div>
      ))}
    </div>
    </div>
  );
};

export default LikedItemsPage;
