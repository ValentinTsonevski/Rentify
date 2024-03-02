import React, { useState, useEffect } from 'react';
import HomeIcon from '@mui/icons-material/Home';
import DirectionsCarIcon from '@mui/icons-material/DirectionsCar';
import PhoneAndroidIcon from '@mui/icons-material/PhoneAndroid';
import ParkIcon from '@mui/icons-material/Park';
import CelebrationIcon from '@mui/icons-material/Celebration';
import CheckroomIcon from '@mui/icons-material/Checkroom';
import FitnessCenterIcon from '@mui/icons-material/FitnessCenter';
import PaletteIcon from '@mui/icons-material/Palette';
import BookIcon from '@mui/icons-material/Book';
import QuestionMarkIcon from '@mui/icons-material/QuestionMark';
import './CategoriesList.css';
import { fetchData } from '../fetchData';
import { Link } from "react-router-dom";

const endpoint = 'categories';

const CategoriesList = () => {
  const [categories, setCategories] = useState(null);

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const result = await fetchData(endpoint);
        setCategories(result);
      } catch (error) {
        console.log(error);
      }
    };

    fetchCategories();
  }, []);

  const buttons = [
    HomeIcon,
    DirectionsCarIcon,
    PhoneAndroidIcon,
    ParkIcon,
    CelebrationIcon,
    CheckroomIcon,
    FitnessCenterIcon,
    PaletteIcon,
    BookIcon,
    QuestionMarkIcon,
  ];

  return (
    <div className="category-container">
      <h2 style={{"paddingBottom" : "30px"}}>All Categories</h2>
      <div className="category-list">
        {categories && categories.map((category, index) => (
          <div key={index} className="category-item" >
            <div className="category-icon">
              <Link to={`/items/category/${index + 1}`} >{React.createElement(buttons[index])}</Link>

              <p className="category-name">{category.name}</p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

export default CategoriesList