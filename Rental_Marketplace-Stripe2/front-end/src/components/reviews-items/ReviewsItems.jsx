import React, { useState } from 'react';
import "./Review.css"
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { useEffect } from 'react';



import { useDispatch, useSelector } from "react-redux"; 
import { updateUserReview } from "../../features/userReviewSlice";



const ReviewsItems = ({ itemId }) => {
  const [rating, setRating] = useState(0);
  const [comment, setComment] = useState('');

  const [addedReview, setAddedReview] = useState(false);
  const [editReview, setEditedReview] = useState(false);

  const [showForm, setShowForm] = useState(false);

  const [userInfo, setUserInfo] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    address: {
      city: '',
      postCode: '',
      street: '',
      streetNumber: '',
    }
    , profilePicture: ''



  });

  const userId = useSelector((state) => state.userToken.id);
  const dispatch = useDispatch();
  const review = useSelector((state) => state.userReview.values);


  useEffect(() => {

    const fetchUserInfo = async () => {

      try {
        const response = await axios.get(`http://localhost:8080/rentify/users/${userId}`);
        setUserInfo(response.data);

      }
      catch (error) {
        console.error("Error fetching user Info ", error);
      }

    };

    const fetchReviewUserAdd = async () => {

      try {
        const response = await axios.get(`http://localhost:8080/rentify/reviews/${userId}/${itemId}`);

        setAddedReview(response.data);
        

      }
      catch (error) {
        console.error("Error fetching user Info ", error);
      }

    };

    const fetchReview = async () => {
      try {
        const response = await axios.get(`http://localhost:8080/rentify/reviews/userReview/${userId}/${itemId}`);

        dispatch(updateUserReview(response.data));
      } catch (error) {
        console.error('Error fetching reviews:', error);
        
      }
    };

    fetchUserInfo();
    fetchReviewUserAdd();
    fetchReview();

  }, [review]);

  const handleRatingChange = (event) => {
    setRating(parseInt(event.target.value));
  };

  const handleCommentChange = (event) => {
    setComment(event.target.value);
  };

  const handleCancel = () => {

    setEditedReview(false);
  };

  const handleSend = async () => {
    try {
      const response = await axios.post(`http://localhost:8080/rentify/reviews/addReview/${userId}/${itemId}`, {
        ratingStars: rating,
        comments: comment
      });

      setAddedReview(true);
      setRating(0);
      setComment('');
      dispatch(updateUserReview(response.data));
    } catch (error) {
      console.error(error);

    }

  };

  const handleEditReview = () => {
    setEditedReview(true);

  };

  const handleUpdateSend = async () => {

    try {
    
      const response = await axios.put(`http://localhost:8080/rentify/reviews/updateReview/${userId}/${itemId}`, {
        ratingStars: rating,
        comments: comment
      });

      setRating(0);
      setComment('');

      setEditedReview(false);

    } catch (error) {
      console.error(error);

    }
  }
  return (
    <>
      {!addedReview ? (
        <div className="cardRevieRating">
          <div className="row">
            <div className="col-2">
              <img src={userInfo.profilePicture} width="70" className="rounded-circle mt-2" alt="User" />
            </div>
            <div className="col-10">
              <div className="comment-box ml-2">
                <h4>Add a comment</h4>
                <div className="rating">
                  <input type="radio" name="rating" value="5" id="5" checked={rating === 5} onChange={handleRatingChange} /><label htmlFor="5">☆</label>
                  <input type="radio" name="rating" value="4" id="4" checked={rating === 4} onChange={handleRatingChange} /><label htmlFor="4">☆</label>
                  <input type="radio" name="rating" value="3" id="3" checked={rating === 3} onChange={handleRatingChange} /><label htmlFor="3">☆</label>
                  <input type="radio" name="rating" value="2" id="2" checked={rating === 2} onChange={handleRatingChange} /><label htmlFor="2">☆</label>
                  <input type="radio" name="rating" value="1" id="1" checked={rating === 1} onChange={handleRatingChange} /><label htmlFor="1">☆</label>
                </div>
                <div className="comment-area">
                  <textarea className="form-control" placeholder="What is your view?" rows="4" value={comment} onChange={handleCommentChange}></textarea>
                </div>
                <div className="comment-btns mt-2">
                  <div className="row">
                    <div className="col-6">
                    </div>
                    <div className="col-6">
                      <div className="pull-right">
                        <button className="btn btn-success send btn-sm custom-blue-button" onClick={handleSend}>Send <i className="fa fa-long-arrow-right ml-1"></i></button>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      ) : (

        <>
          {!editReview ? (
            <div className="cardProfileReview">
              <div className="row d-flex align-items-center">
                <div>
                  <img className="profile-pic" src={review.profilePicture} alt="Profile" />
                </div>
                <div className="d-flex flex-column">
                  <h3 className="mt-2 mb-0">{review.firstName} {review.lastName}</h3>
                  <div>
                    <br></br>
                    <p className="text-center">
                      <span className="text-muted">{review.stars}.0</span>
                      
                    </p>
                    <p className="text-center">
                      

                      <p className="blue-text mt-3">{review.comment}</p>

                      
                    </p>
                    <div className="row text-center">
                      <button className="rent-button"  onClick={handleEditReview}>Edit Review</button>
                    </div> 
                  </div>
                </div>
              </div>

            </div>
          ) : (
            <div className="cardRevieRating">
              <div className="row">
                <div className="col-2">
                  <img src={userInfo.profilePicture} width="70" className="rounded-circle mt-2" alt="User" />
                </div>
                <div className="col-10">
                  <div className="comment-box ml-2">
                    <h4>Add a comment</h4>
                    <div className="rating">
                      <input type="radio" name="rating" value="5" id="5" checked={rating === 5} onChange={handleRatingChange} /><label htmlFor="5">☆</label>
                      <input type="radio" name="rating" value="4" id="4" checked={rating === 4} onChange={handleRatingChange} /><label htmlFor="4">☆</label>
                      <input type="radio" name="rating" value="3" id="3" checked={rating === 3} onChange={handleRatingChange} /><label htmlFor="3">☆</label>
                      <input type="radio" name="rating" value="2" id="2" checked={rating === 2} onChange={handleRatingChange} /><label htmlFor="2">☆</label>
                      <input type="radio" name="rating" value="1" id="1" checked={rating === 1} onChange={handleRatingChange} /><label htmlFor="1">☆</label>
                    </div>
                    <div className="comment-area">
                      <textarea className="form-control" placeholder="What is your view?" rows="4" value={comment} onChange={handleCommentChange}></textarea>
                    </div>
                    <div className="comment-btns mt-2">
                      <div className="row">
                        <div className="col-6">
                        </div>
                        <div className="col-6">
                          <div className="pull-right">
                            <button className="btn btn-success send btn-sm custom-blue-button" onClick={handleUpdateSend}>Send <i className="fa fa-long-arrow-right ml-1"></i></button>
                            <button className="btn btn-success send btn-sm custom-blue-button" onClick={handleCancel}>Cancel <i className="fa fa-long-arrow-right ml-1"></i></button>

                          </div>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          )}
        </>

      )}


    </>
  );
};


export default ReviewsItems;