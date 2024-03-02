import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import "./Carousel.css";
import { fetchData } from "../../fetchData";
import ArrowBackIosIcon from "@mui/icons-material/ArrowBackIos";
import ArrowForwardIosIcon from "@mui/icons-material/ArrowForwardIos";

const endpoint = "pictures/items/";

const Carousel = () => {
  const [currentIndex, setCurrentIndex] = useState(0);
  const { id } = useParams();
  const navigate = useNavigate();
  const [pictures, setPictures] = useState(null);

  useEffect(() => {
    const fetchPictures = async () => {
      try {
        const result = await fetchData(endpoint + id);
        console.log(result);
        setPictures(result);
      } catch (error) {
        navigate("/notfound");
      }
    };

    console.log(pictures);

    if (!pictures) {
      fetchPictures();
    }
    fetchPictures();
  }, []);

  const nextSlide = () => {
    if (pictures && pictures.length > 0) {
      setCurrentIndex((prevIndex) => (prevIndex + 1) % pictures.length);
    }
  };

  const prevSlide = () => {
    if (pictures && pictures.length > 0) {
      setCurrentIndex(
        (prevIndex) => (prevIndex - 1 + pictures.length) % pictures.length
      );
    }
  };

  return (
    <div className="carousel">
      {pictures && pictures.length > 0 && (
        <div>
          {pictures.length > 1 && (
            <button className="arrow-button prev" onClick={prevSlide}>
              <ArrowBackIosIcon className="arrow-icon" />
            </button>
          )}
          <img
            src={pictures[currentIndex].url}
            alt={`slide ${currentIndex + 1}`}
          />
          {pictures.length > 1 && (
            <button className="arrow-button next" onClick={nextSlide}>
              <ArrowForwardIosIcon className="arrow-icon" />
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default Carousel;
