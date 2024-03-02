import React, { useState } from "react";
import "./Create-item.css";
import CategoryModal from "./CategoryModal";
import axios from "axios";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import { useNavigate } from "react-router-dom";

function CreateItem() {
  const [title, setTitle] = useState("");
  const [selectedCategory, setSelectedCategory] = useState("");
  const [pictures, setPictures] = useState(Array(8).fill(null));
  const [description, setDescription] = useState("");
  const [price, setPrice] = useState("");
  const [deposit, setDeposit] = useState("");
  const [address, setAddress] = useState({
    city: "",
    street: "",
    postCode: "",
    streetNumber: "",
  });

  const [errors, setErrors] = useState({
    title: "",
    price: "",
    deposit: "",
    city: "",
    street: "",
    description: "",
    postCode: "",
    streetNumber: "",
  });


  const navigate = useNavigate();
  const jwt_token = localStorage.getItem('token');
 

  const handleChange = (event) => {
    const inputValue = event.target.value;
    setTitle(inputValue);
  };

  const handleSelectCategory = (category2) => {
    setSelectedCategory(category2);
  };

  const handlePictureClick = (index) => {
    const fileInput = document.getElementById(`fileInput${index}`);
    fileInput && fileInput.click();
  };

  const handleRemovePicture = (index) => {
    const updatedPictures = [...pictures];
    updatedPictures[index] = null;
    setPictures(updatedPictures);
  };

  const handleFileChange = (index, event) => {
    const file = event.target.files[0];
    const updatedPictures = [...pictures];
    updatedPictures[index] = file;
    setPictures(updatedPictures);
  };

  const handleDescriptionChange = (event) => {
    const descriptionValue = event.target.value;

    if (descriptionValue.length <= 500) {
      setDescription(descriptionValue);
    }
  };

  const handleSelectPrice = (event) => {
    const inputValue = event.target.value;
    setPrice(inputValue);
  };

  const handleSelectDeposit = (event) => {
    const inputValue = event.target.value;
    setDeposit(inputValue);
  };

  const handleAddressChange = (field, value) => {
    setAddress((prevAddress) => ({
      ...prevAddress,
      [field]: value,
    }));
  };

  const handleAddItem = async () => {

    const newErrors = {
      title: title ? "" : "Title cannot be empty",
      price: price ? "" : "Price cannot be empty",
      deposit: deposit ? "" : "Deposit cannot be empty",
      city: address.city ? "" : "City cannot be empty",
      street: address.street ? "" : "Street cannot be empty",
      description: description ? "" : "Description cannot be empty",
      postCode: address.postCode ? "" : "Postcode cannot be empty",
      streetNumber: address.streetNumber ? "" : "Street Number cannot be empty",
    };
    setErrors(newErrors);

    if (Object.values(newErrors).some((error) => error !== "")) {
      return;
    }


    try {
      const backendUrl = "http://localhost:8080/rentify/items/create";
      const formData = new FormData();

      formData.append("name", title);
      formData.append("description", description);
      formData.append("price", price);
      formData.append("deposit", deposit);
      formData.append("category", selectedCategory);
      formData.append("city", address.city);
      formData.append("street", address.street);
      formData.append("postCode", address.postCode);
      formData.append("streetNumber", address.streetNumber);

      pictures.forEach((picture, index) => {
        if (picture) {
          formData.append(`pictures[${index}]`, picture);
        }
      });
      
      const response = await axios.post(backendUrl, formData, {
        headers: {
          Authorization: `Bearer ${jwt_token}`,
          'Content-Type': 'multipart/form-data',
        },
      });

      if (response.status === 200) {
        console.log("Item added successfully:", response.data);
      } else {
        console.error("Error adding item. Status:", response.status);
      }
      navigate("/");
    } catch (error) {
      console.error("Error adding item:", error.message);
    }
    
  };

  return (
    <div className="createItem-container">
      <h1>Add Item</h1>
      <section className="info">
        <h2>What you offer?</h2>
        <h3>Title*</h3>
        <input
          className="title-field"
          type="text"
          placeholder="Example: Iphone 13 with warranty"
          value={title}
          onChange={handleChange}
          maxLength={70}
        />
        <p className="character-count">{title.length}/70</p>
        <p className="error">{errors.title}</p>

        <div className="modal-button">
          <CategoryModal onSelectCategory={handleSelectCategory} />
        </div>

        <div className="selected-category">
          {selectedCategory && <p> {selectedCategory}</p>}
        </div>
      </section>

      <section className="cost">
        <h3>Price&Deposit*</h3>
        <div className="fields-container">
          <input
            className="price-field"
            type="text"
            placeholder="Item Price BGN"
            value={price}
            onChange={handleSelectPrice}
          />
          <p className="error">{errors.price}</p>
          <input
            className="price-field"
            type="text"
            placeholder="Item Deposit BGN"
            value={deposit}
            onChange={handleSelectDeposit}
          />
          <p className="error">{errors.deposit}</p>
        </div>
      </section>

      <section className="pictures">
        <h3>Pictures*</h3>
        <div className="picture-grid">
          {pictures.map((picture, index) => (
            <div key={index}>
              <button
                className="remove-picture-btn"
                onClick={() => handleRemovePicture(index)}
              >
                <DeleteForeverIcon></DeleteForeverIcon>
              </button>
              <div
                key={index}
                className="picture-box"
                onClick={() => handlePictureClick(index)}
              >
                <input
                  type="file"
                  id={`fileInput${index}`}
                  style={{ display: "none" }}
                  onChange={(event) => handleFileChange(index, event)}
                />
                {picture ? (
                  <img
                    src={URL.createObjectURL(picture)}
                    alt={`Picture ${index + 1}`}
                    className="picture-image"
                  />
                ) : (
                  <p>Click to add picture</p>
                )}
              </div>
            </div>
          ))}
        </div>
      </section>

      <section className="description">
        <h3>Description*</h3>
        <textarea
          className="description-field"
          cols="133"
          rows="14"
          placeholder="Provide a description for your item..."
          value={description}
          onChange={handleDescriptionChange}
        >
          maxLength={500}
        </textarea>
        <p className="character-count">{description.length}/500</p>
        <p className="error">{errors.description}</p>
      </section>

      <section className="location">
        <h3>Location*</h3>
        <div className="fields-container">
          <input
            className="location-field"
            type="text"
            placeholder="City"
            value={address.city}
            onChange={(e) => handleAddressChange("city", e.target.value)}
          />
           <p className="error">{errors.city}</p>
          <input
            className="location-field"
            type="text"
            placeholder="Street"
            value={address.street}
            onChange={(e) => handleAddressChange("street", e.target.value)}
          />
           <p className="error">{errors.street}</p>
          <input
            className="location-field"
            type="text"
            placeholder="Post-Code"
            value={address.postCode}
            onChange={(e) => handleAddressChange("postCode", e.target.value)}
          />
           <p className="error">{errors.postCode}</p>
          <input
            className="location-field"
            type="text"
            placeholder="Street-Number"
            value={address.streetNumber}
            onChange={(e) =>
              handleAddressChange("streetNumber", e.target.value)
            }
          />
           <p className="error">{errors.streetNumber}</p>
        </div>
      </section>

      <button className="add-btn" onClick={handleAddItem}>
        <b>Add item</b>
      </button>
    </div>
  );
}

export default CreateItem;

