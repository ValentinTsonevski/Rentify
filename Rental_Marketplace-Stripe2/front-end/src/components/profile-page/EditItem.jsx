import { useState, useEffect, React } from "react";
import { useNavigate, useParams } from "react-router-dom";
import axios from "axios";
import DeleteForeverIcon from "@mui/icons-material/DeleteForever";
import "../../components/add-item/Create-item.css";
import CategoryModal from "../add-item/CategoryModal";
import { jwtDecode } from "jwt-decode";

const EditItem = () => {
  const token = localStorage.getItem("token");
  const decoded = jwtDecode(token);
  const userId = decoded.jti;

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

  const navigate = useNavigate();
  const { id } = useParams();
  const jwt_token = localStorage.getItem("token");

  const [deletedPictures, setDeletedPictures] = useState([]);

  useEffect(() => {
    const fetchItemDetails = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/rentify/items/${id}`
        );

        if (userId != response.data.user.id) {
          navigate("/notfound");
        }

        setTitle(response.data.name);
        setSelectedCategory(response.data.category.name);
        setDescription(response.data.description);
        setPrice(response.data.price);
        setDeposit(response.data.deposit);
        setAddress(response.data.address);
      } catch (error) {
        console.error("Error fetching item:", error);
      }
    };

    const fetchPictures = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/rentify/pictures/items/${id}`
        );

        setPictures(
          Array.from({ length: 8 }, (_, index) =>
            response.data[index]
              ? { file: null, url: response.data[index].url }
              : null
          )
        );
      } catch (error) {
        console.error("Error fetching item:", error);
      }
    };

    fetchItemDetails();
    fetchPictures();
  }, []);

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

  const handleRemovePicture = (index, picture) => {
    if (!picture) {
      return;
    }
    setDeletedPictures([...deletedPictures, picture.url]);
    const updatedPictures = [...pictures];
    updatedPictures[index] = null;
    setPictures(updatedPictures);
  };

  const handleFileChange = (index, event) => {
    const file = event.target.files[0];
    const updatedPictures = [...pictures];
    updatedPictures[index] = { file: file, url: URL.createObjectURL(file) };
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

  const handleEditItem = async () => {
    try {
      const backendUrl = `http://localhost:8080/rentify/items/${id}`;
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
      formData.append("deletedPicturesOnEdit", deletedPictures)
    
      pictures.forEach((picture, index) => {
        if (picture && picture.file) {
          formData.append(`pictures[${index}]`, picture.file);
        }
      });

      const response = await axios.put(backendUrl, formData, {
        headers: {
          Authorization: `Bearer ${jwt_token}`,
          "Content-Type": "multipart/form-data",
        },
      });

      if (response.status === 200) {
        console.log("Item updated successfully:", response.data);
      } else {
        console.error("Error updating item. Status:", response.status);
      }
      navigate("/settings");
    } catch (error) {
      console.error("Error updating item:", error.message);
    }
  };

  const handleCancel = () => {
    navigate("/settings");
  };

  return (
    <div className="createItem-container">
      <h1>Edit Item</h1>
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
          <input
            className="price-field"
            type="text"
            placeholder="Item Deposit BGN"
            value={deposit}
            onChange={handleSelectDeposit}
          />
        </div>
      </section>

      <section className="pictures">
        <h3>Pictures*</h3>
        <div className="picture-grid">
          {pictures.map((picture, index) => (
            <div key={index}>
              <button
                className="remove-picture-btn"
                onClick={() => handleRemovePicture(index, picture)}
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
                    src={
                      picture.url
                        ? picture.url
                        : picture.file
                        ? URL.createObjectURL(picture.file)
                        : ""
                    } 
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
          <input
            className="location-field"
            type="text"
            placeholder="Street"
            value={address.street}
            onChange={(e) => handleAddressChange("street", e.target.value)}
          />
          <input
            className="location-field"
            type="text"
            placeholder="Post-Code"
            value={address.postCode}
            onChange={(e) => handleAddressChange("postCode", e.target.value)}
          />
          <input
            className="location-field"
            type="text"
            placeholder="Street-Number"
            value={address.streetNumber}
            onChange={(e) =>
              handleAddressChange("streetNumber", e.target.value)
            }
          />
        </div>
      </section>

      <div style={{ display: "flex" }}>
        <button
          className="add-btn"
          onClick={handleEditItem}
          style={{ backgroundColor: "green", marginRight: "10px" }}
        >
          <b>Save</b>
        </button>
        <button
          className="add-btn"
          onClick={handleCancel}
          style={{ backgroundColor: "red" }}
        >
          <b>Cancel</b>
        </button>
      </div>
    </div>
  );
};

export default EditItem;