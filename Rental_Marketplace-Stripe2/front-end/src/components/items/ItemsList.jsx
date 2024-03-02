import React, { useState, useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import FavoriteIcon from "@mui/icons-material/Favorite";
import "./ItemsList.css";
import { fetchData } from "../fetchData";
import noImage from "../../assets/no-image.avif";
import { jwtDecode } from "jwt-decode";
import SearchIcon from "@mui/icons-material/Search";
import MonetizationOnIcon from "@mui/icons-material/MonetizationOn";
import axios from "axios";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux"; 
import { like } from "../../features/likedItems";


import { updateUser } from "../../features/userSlice";
import {updateIsLoggedIn} from "../../features/userTokenSlice.js"


const endpointItems = "items";


const ItemsList = () => {

  const likedItems = useSelector((state) => new Set(state.likedItems.values));

  const dispatch = useDispatch();

  const [items, setItems] = useState([]);
  const { id: categoryId } = useParams();
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [sortOrder, setSortOrder] = useState("asc");

  const [categories, setCategories] = useState([]);
  const [addresses, setAddresses] = useState([]);

  const [selectedCategory, setSelectedCategory] = useState("");
  const [priceFrom, setPriceFrom] = useState("");
  const [priceTo, setPriceTo] = useState("");
  const [selectedAddress, setSelectedAddress] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const navigate = useNavigate();

  const userId = useSelector((state) => state.userToken.id);
  const isLoggedIn = useSelector((state) => state.userToken.isLoggedIn);

  useEffect(() => {
   

    if (userId === null) {
      dispatch(updateIsLoggedIn({ isLoggedIn: false }));
    }
    

    const fetchAddressAndCategory = async () => {
      try {
        const responseCategory = await fetch(
          "http://localhost:8080/rentify/categories"
        );
        if (!responseCategory.ok) {
          throw new Error(`HTTP error! Status: ${responseCategory.status}`);
        }
        const categoriesResponse = await responseCategory.json();

        setCategories(categoriesResponse);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }

      try {
        const responseAddress = await fetch(
          "http://localhost:8080/rentify/addresses"
        );

        if (!responseAddress.ok) {
          throw new Error(`HTTP error! Status: ${responseAddress.status}`);
        }

        const addressResponse = await responseAddress.json();

        setAddresses(addressResponse);
      } catch (error) {
        console.error("Error fetching Address");
      }
    };

    fetchAddressAndCategory();
  }, []);

  useEffect(() => {
    const fetchItems = async () => {
      try {
        const result = await fetchData(
          `${endpointItems}/filter${
            categoryId ? `/category/${categoryId}` : ""
          }?page=${currentPage}&sortDirection=${sortOrder}&category=${selectedCategory}&priceFrom=${priceFrom}&priceTo=${priceTo}&address=${selectedAddress}&searchTerm=${searchTerm}`
        );

        setItems(result.content);
        setTotalPages(result.totalPages);

        if (currentPage >= totalPages) {
          setCurrentPage(0);
        }
      } catch (error) {
        console.error("Error fetching items:", error.message);
      }
    };
    const fetchLikedItemsFromDB = async () => {
      try {
        const token = localStorage.getItem("token");

        if (token === null) {


        dispatch(like(new Set()))
        
        }
        else{
          const decoded = jwtDecode(token);
          const userId = decoded.jti;

          const response = await fetch(
            `http://localhost:8080/rentify/favourites/userFavourites/${userId}`,
            {
              method: "GET",
            }
          );

          if (response.ok) {
            const likedItemsFromDB = await response.json();
            const likedItemsArray = Array.from(new Set(likedItemsFromDB));
            dispatch(like(likedItemsArray));

          } else {
            throw new Error(`HTTP error! Status: ${response.status}`);
          }
        }
        
      } catch (error) {
        console.error("Error fetching liked items:", error.message);
      }
    };

    fetchLikedItemsFromDB();
    fetchItems();
  }, [totalPages, currentPage, categoryId, sortOrder, selectedCategory, priceFrom, priceTo, searchTerm, selectedAddress]);

  const handleLikeClick = async (itemId) => {
    const token = localStorage.getItem("token");

   

    if(token!== null){ 
      const decoded = jwtDecode(token);
    const userId = decoded.jti;
    
    const updatedLikedItems = new Set(likedItems);
    if (likedItems.has(itemId)) {
      updatedLikedItems.delete(itemId);
    } else {
      updatedLikedItems.add(itemId);
    }

    const isLiked = !likedItems.has(itemId);


    dispatch(like(updatedLikedItems));
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
      dispatch(like(isLiked ? likedItems : updatedLikedItems));

    }
  }
  else{
    
    navigate("/login");
  }
  };

  const handleViewClick = (itemId) => {
    if (userId === null) {
      return;
    }

    const url = "http://localhost:8080/rentify/views";
    const postData = {
      user: { id: userId },
      item: { id: itemId },
    };

    axios.post(url, postData).catch((error) => {
      console.error("Error making post request:", error);
    });
  };

  return (
    <div>
      <div className="container pb-5" style={{"maxWidth" : "10000px"}}>
      <form className="row row-cols-lg-auto g-3 align-items-center ms-lg-3">
          <>
            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <SearchIcon className="search-icon" />
                </div>
                <input
                  type="text"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Search by name"
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                />
              </div>
            </div>

            { categoryId === undefined && <div className="col-12">
              <select
                className="form-select"
                id="inlineFormSelectPref"
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
              >
                <option value="">Category...</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>
            }

            <div className="col-12">
              <label className="visually-hidden">Username</label>
              <div className="input-group">
                <div className="input-group-text">
                  <MonetizationOnIcon className="search-icon" />
                </div>
                <input
                  type="number"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Price From"
                  value={priceFrom}
                  onChange={(e) => setPriceFrom(e.target.value)}
                />
              </div>
            </div>

            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <MonetizationOnIcon className="search-icon" />
                </div>
                <input
                  type="number"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Price To"
                  value={priceTo}
                  onChange={(e) => setPriceTo(e.target.value)}
                />
              </div>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="inlineFormSelectPref"
                value={selectedAddress}
                onChange={(e) => setSelectedAddress(e.target.value)}
              >
                <option value="">City...</option>
                {addresses.map((address, index) => (
                  <option key={index} value={address.name}>
                    {address.city}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="orderBySelect"
                value={sortOrder}
                onChange={(e) => setSortOrder(e.target.value)}
              >
                <option value="asc">Order By Price Asc</option>
                <option value="desc">Order By Price Desc</option>
              </select>
            </div>
          </>
        
      </form>
    </div>

      <div className="items-list">
        {
          items &&
            items.map((item) => (
              <div className="items-list-item" key={item.id}>
                <Link
                  to={`/items/${item.id}`}
                  onClick={() => handleViewClick(item.id)}
                >
                  <div className="card">
                    <img
                      src={item.thumbnail || noImage}
                      className="card-img-top"
                      alt={item.name}
                    />
                    <div className="card-body">
                      <h3 className="card-title">{item.name}</h3>
                      <p className="card-text">{"$" + item.price}</p>
                      <p className="card-text">{item.addresses}</p>
                    </div>
                  </div>
                </Link>
                <button
                  className={`like-button ${
                    likedItems.has(item.id) ? "clicked" : ""
                  }`}
                  onClick={(e) => {
                    e.preventDefault();
                    handleLikeClick(item.id);
                  }}
                >
                  <FavoriteIcon />
                </button>
              </div>
            ))}
      </div>


      <br /> <br />
      { totalPages > 0 ?
      <nav aria-label="Page navigation example">
        <ul className="pagination justify-content-center">
          <li className={`page-item ${currentPage === 0 ? "disabled" : ""}`}>
            <a
              className="page-link"
              href="#"
              onClick={() => setCurrentPage((prev) => Math.max(prev - 1, 0))}
            >
              Previous
            </a>
          </li>
          {[...Array(totalPages).keys()].map((page) => (
            <li
              key={page}
              className={`page-item ${page === currentPage ? "active" : ""}`}
            >
              <a
                className="page-link"
                href="#"
                onClick={() => setCurrentPage(page)}
              >
                {page + 1}
              </a>
            </li>
          ))}
          <li
            className={`page-item ${
              currentPage === totalPages - 1 || totalPages === 0 ? "disabled" : ""
            }`}
          >
            <a
              className="page-link"
              href="#"
              onClick={() =>
                setCurrentPage((prev) => Math.min(prev + 1, totalPages - 1))
              }
            >
              Next
            </a>
          </li>
        </ul>
      </nav> 
      :
      <div style={{ "textAlign": "center" }}>No items matching this criteria</div>
      }
    </div>
  );
};

export default ItemsList;