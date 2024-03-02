import React from 'react';
import "./ShowReviews.css"
import { useEffect } from 'react';
import axios from 'axios';
import { useState } from "react";



import { useDispatch, useSelector } from "react-redux"; 
import { updateAllReviews } from "../../features/allReviewsSlice";


const ShowReviews = ({itemId , loggedInUserId }) => { 

    const [showAllReviews, setShowAllReviews] = useState(false);

    const dispatch = useDispatch();
    const reviews = useSelector((state) => state.allReviews.values);


    useEffect(() => {
        const fetchReviews = async () => {
            try {
                const response = await axios.get(`http://localhost:8080/rentify/reviews/${itemId}`);

                dispatch(updateAllReviews(response.data));

            } catch (error) {
                console.error('Error fetching reviews:', error);
                return [];
            }
        };
      
        fetchReviews();

      }, [reviews]);
      const displayedReviews = showAllReviews ? reviews : reviews.slice(0, 2);
  return (

<div class="container-fluid px-1 py-5 mx-auto">
    <div class="row justify-content-center">
        <div class="col-xl-7 col-lg-8 col-md-10 col-12 text-center mb-5">
            <div>
            <h2>Reviews</h2>
          
            { displayedReviews.length > 0 ? ( 
                <>
            {displayedReviews.map((review, index) => (
                <div className="cardProfileReview" key={index}>
                    <div className="row d-flex">
                        <div> 
                            <img className="profile-pic" src={review.profilePicture} alt="Profile" />
                        </div>
                        <div className="d-flex flex-column">
                            <h3 className="mt-2 mb-0">{review.firstName} {review.lastName}</h3>
                            <div>
                                <p className="text-left">
                                    <span className="text-muted">{review.stars.toFixed(1)}</span>
                               
                                </p>
                            </div>
                        </div>
                    </div>
                    <div className="row text-left">
                        <h4 className="blue-text mt-3">{review.comment}</h4>
                        <p className="content">{review.content}</p>
                       
                    </div>
                </div>
            ))}
            {reviews.length > 2 && (
                <button className="rent-button" onClick={() => setShowAllReviews(!showAllReviews)}>
                    {showAllReviews ? 'Show Less' : 'Show More'}
                </button>


            )} 
             </> 
            ):( 

                
                <h4>Dont have reviews yet.</h4>
            )}
        </div>
        </div>
    </div>
</div>

  );
}



export default ShowReviews;
