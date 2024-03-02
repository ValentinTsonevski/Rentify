import React from "react";

import { useState } from "react";
import { useEffect } from "react";
import axios from "axios";
import { Link, useParams } from "react-router-dom";
import noImage from "../../assets/no-image.avif";
import { jwtDecode } from "jwt-decode";
import PersonalItems from "./PersonalItems";

import { useDispatch, useSelector } from "react-redux"; 
import { updateUser } from "../../features/userSlice";

const ProfilePage = () => {
  const [userItems, setuserItems] = useState([]);
  const [errorFoInputs, setErrorForInputs] = useState(false);
  const [errorForProfilePicture, setErrorForProfilePicture] = useState(false);

  const [iban, setIban] = useState("");

  const userId = useSelector((state) => state.userToken.id);
  const userInfo = useSelector((state) => state.user.values);
  const dispatch = useDispatch();


  const [imageFile, setimageFile] = useState("");

  const [editedUserInfo, setEditedUserInfo] = useState({ ...userInfo });
  const [editMode, setEditMode] = useState(false);
  const [editPictureMode, setEditPictureMode] = useState(false);

  const handleEditClick = () => {
    setEditedUserInfo({ ...userInfo });
    setEditMode(true);
  };

  const handleEditPicture = () => {
    setEditPictureMode(true);
  };


  const handleUpload = async () => {
    try {
        const formData = new FormData();
        formData.append('file', imageFile);

        const response = await axios.put(`http://localhost:8080/rentify/updateProfilePicture/${userId}`, formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            }
        });

        dispatch(updateUser(response.data));
        setEditPictureMode(false);
        setErrorForProfilePicture(false);
        } catch (error) {
        console.error('Error uploading picture:', error);
        setErrorForProfilePicture(true)
    }
};


  const handleFileChange = (event) => {
    setimageFile(event.target.files[0]);
  };

  const handleCancelClickPicture = () => {
    setEditPictureMode(false);

    setEditedUserInfo({ ...userInfo });
  };

  const handleSaveClick = async () => {
    const editedAddress = editedUserInfo.address
      ? {
          city: editedUserInfo.address.city,
          postCode: editedUserInfo.address.postCode,
          street: editedUserInfo.address.street,
          streetNumber: editedUserInfo.address.streetNumber,
        }
      : null;

    const updatedUserInfo = {
      ...editedUserInfo,
      address: editedAddress
        ? {
            ...userInfo.address,
            ...editedAddress,
          }
        : {},
        iban: iban || userInfo.iban
    };

    try {
      const response = await axios.put(
        `http://localhost:8080/rentify/update/${userId}`,
        {
          firstName: updatedUserInfo.firstName || userInfo.firstName,
          lastName: updatedUserInfo.lastName || userInfo.lastName,
          email: updatedUserInfo.email || userInfo.email,
          phoneNumber: updatedUserInfo.phoneNumber || userInfo.phoneNumber,
          addressDto: updatedUserInfo.address,
          iban: updatedUserInfo.iban
        }
      );

      dispatch(updateUser(response.data));

      setEditMode(false);
      setErrorForInputs(false);
      setIban('');
    } catch (error) {
      console.error("Error updating user information:", error);

      setErrorForInputs(true);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;

    if (name.startsWith("address.")) {
      const addressField = name.split(".")[1];
      setEditedUserInfo((prevInfo) => ({
        ...prevInfo,
        address: {
          ...prevInfo.address,
          [addressField]: value,
        },
      }));
    } else {
      setEditedUserInfo((prevInfo) => ({
        ...prevInfo,
        [name]: value,
      }));
    }
  };

  const handleCancelClick = () => {
    setEditMode(false);

    setEditedUserInfo({ ...userInfo });
  };

  useEffect(() => {
    const token = localStorage.getItem("token");

    const decoded = jwtDecode(token);
    const userId = decoded.jti;

    const fetchUserItems = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/rentify/items/user/published/${userId}`
        );

        setuserItems(response.data);
      } catch (error) {
        console.error("Error fetching user items:", error);
      }
    };

    const fetchUserInfo = async () => {
      try {
        const response = await axios.get(
          `http://localhost:8080/rentify/users/${userId}`
        );

        dispatch(updateUser(response.data))
      } catch (error) {
        console.error("Error fetching user Info ", error);
      }
    };

    fetchUserItems();
    fetchUserInfo();
  }, []);

  return (
    <div className="container">
      <div className="main-body">
        <div className="row gutters-sm">
          <div className="col-md-4 mb-3">
            <div className="card">
              <div className="card-body">
                <div className="d-flex flex-column align-items-center text-center">
                  <img
                    alt="Picture"
                    className="rounded-circle"
                    width="150"
                    src={userInfo.profilePicture}
                  />

                  <div className="mt-3">
                    <h4>
                      {userInfo.firstName} {userInfo.lastName}{" "}
                    </h4>
                    {editPictureMode ? (
                      <>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={handleFileChange}
                        />
                        <button
                          className="btn btn-primary m-2"
                          onClick={handleUpload}
                        >
                          Upload picture
                        </button>
                        <button
                          className="btn btn-danger m-2"
                          onClick={handleCancelClickPicture}
                        >
                          Cancel picture
                        </button>

                        {errorForProfilePicture && (
                          <div className="error-message">
                            Please upload the picture.
                          </div>
                        )}
                      </>
                    ) : (
                      <>
                        <button
                          className="btn btn-primary"
                          onClick={handleEditPicture}
                        >
                          Edit picture
                        </button>
                      </>
                    )}
                  </div>
                </div>
              </div>
            </div>
            <div className="card mt-3">
              <ul className="list-group list-group-flush">
                <div className="card mb-3">
                  <div className="card-body">
                    <div className="row">
                      <div className="col-sm-3">
                        <h6 className="mb-0">Full Name</h6>
                      </div>
                      <div className="col-sm-9 text-secondary">
                        {editMode ? (
                          <>
                            <input
                              type="text"
                              name="firstName"
                              value={editedUserInfo.firstName}
                              onChange={handleInputChange}
                            />

                            <input
                              type="text"
                              name="lastName"
                              value={editedUserInfo.lastName}
                              onChange={handleInputChange}
                            />
                          </>
                        ) : (
                          <>
                            <span>{userInfo.firstName} </span>
                            <span>{userInfo.lastName} </span>
                          </>
                        )}
                      </div>
                    </div>
                    <hr />
                    <div className="row">
                      <div className="col-sm-3">
                        <h6 className="mb-0">Email</h6>
                      </div>
                      <div className="col-sm-9 text-secondary">
                        {userInfo.email}
                      </div>
                    </div>
                    <div className="row">
                      <div className="col-sm-3">
                        <h6 className="mb-0">IBAN</h6>
                      </div>
                      <div className="col-sm-9 text-secondary">
                        {editMode ? (
                          <input
                            type="text"
                            name="iban"
                            placeholder="IBAN"
                            value={iban}
                            onChange={(e) => setIban(e.target.value)}
                          />
                        ) : (
                          <span>{userInfo.iban || "N/A"}</span>
                        )}
                      </div>
                    </div>
                    <hr />
                    <div className="row">
                      <div className="col-sm-3">
                        <h6 className="mb-0">Phone</h6>
                      </div>
                      <div className="col-sm-9 text-secondary">
                        {editMode ? (
                          <input
                            type="text"
                            name="phoneNumber"
                            value={editedUserInfo.phoneNumber}
                            onChange={handleInputChange}
                          />
                        ) : (
                          <span>{userInfo.phoneNumber}</span>
                        )}
                      </div>
                    </div>

                    <hr />
                    <div className="row">
                      <div className="col-sm-3">
                        <h6 className="mb-0">Address</h6>
                      </div>
                      <div className="col-sm-9 text-secondary">
                        {editMode ? (
                          <>
                            <input
                              type="text"
                              name="address.city"
                              placeholder="City"
                              value={editedUserInfo.address?.city || ""}
                              onChange={handleInputChange}
                            />
                            <br />
                            <br />
                            <input
                              type="text"
                              name="address.postCode"
                              placeholder="Post code"
                              value={editedUserInfo.address?.postCode || ""}
                              onChange={handleInputChange}
                            />
                            <br />
                            <br />

                            <input
                              type="text"
                              name="address.street"
                              placeholder="Street"
                              value={editedUserInfo.address?.street || ""}
                              onChange={handleInputChange}
                            />
                            <br />
                            <br />
                            <input
                              type="text"
                              name="address.streetNumber"
                              placeholder="Street number"
                              value={editedUserInfo.address?.streetNumber || ""}
                              onChange={handleInputChange}
                            />
                            {errorFoInputs && (
                              <div className="error-message">
                                Please fill all fields in Address
                              </div>
                            )}
                          </>
                        ) : (
                          <>
                            <span>{userInfo.address?.city || ""} </span>
                            <span>{userInfo.address?.postCode || ""} </span>
                            <span>{userInfo.address?.street || ""} </span>
                            <span>{userInfo.address?.streetNumber || ""} </span>
                          </>
                        )}
                      </div>
                    </div>
                    <hr />
                    <div className="row">
                      <div className="col-sm-12 ">
                        {editMode ? (
                          <>
                            <button
                              className="btn btn-primary m-2 "
                              onClick={handleSaveClick}
                            >
                              Save
                            </button>

                            <button
                              className="btn btn-danger m-2"
                              onClick={handleCancelClick}
                            >
                              Cancel
                            </button>
                          </>
                        ) : (
                          <button
                            className="btn btn-primary"
                            onClick={handleEditClick}
                          >
                            Edit
                          </button>
                        )}
                      </div>
                    </div>
                  </div>
                </div>
              </ul>
            </div>
          </div>
          <PersonalItems />
        </div>
      </div>
    </div>
  );
};

export default ProfilePage;