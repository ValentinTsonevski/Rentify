import React, { useState, useEffect } from "react";
import axios from "axios";

import { Link } from "react-router-dom";
import noImage from "../../assets/no-image.avif";
import { useDispatch, useSelector } from "react-redux"; 


const PersonalItems = () => {
  const [userItems, setUserItems] = useState(null);


  const userId = useSelector((state) => state.userToken.id);


  const handleChangeStatusOfItem = async (itemId) => {
    try {
      const response = await axios.put(
        `http://localhost:8080/rentify/items/status/${itemId}`
      );

      console.log(response.data);
      setUserItems((prevUserItems) =>
        prevUserItems.map((item) =>
          item.id === itemId ? { ...item, isActive: !item.isActive } : item
        )
      );
    } catch (error) {
      console.error("Error fetching user items:", error);
    }
  };

  useEffect(() => {
    const fetchUserItems = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/rentify/items/user/published/${userId}`
        );

        setUserItems(response.data);
        console.log("uu", userItems);
      } catch (error) {
        console.error("Error fetching user items:", error);
      }
    };

    fetchUserItems();
  }, []);

  return (
    <div className="col-md-8">
      <div className="card mb-3">
        <div className="card-body">
          <h5 className="card-title">Published Items:</h5>
          <div className="items-list">
            {userItems === null ||
            !Array.isArray(userItems) ||
            userItems.length === 0 ? (
              <h4>Not published Items yet :</h4>
            ) : (
              <>
                {userItems &&
                  userItems.map((item) => (
                    <div
                      className="items-list-item"
                      key={item.id}
                      style={{
                        backgroundColor: !item.isActive ? "#505050" : "inherit",
                      }}
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
                          <p className="card-text">{item.address.city}</p>
                        </div>
                      </div>

                      <br />
                      <button
                        className="btn btn-info"
                        onClick={() => handleChangeStatusOfItem(item.id)}
                      >
                        {!item.isActive ? "Activate" : "Deactivate"}
                      </button>
                      <br />

                      <Link to={`/edit/${item.id}`}>
                        <button className="btn btn-info">Edit</button>
                      </Link>

                      <br />
                      <Link to={`/items/${item.id}`}>
                        <button className="btn btn-info">View</button>
                      </Link>
                    </div>
                  ))}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default PersonalItems;
