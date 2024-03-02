import React, { useState, useEffect } from 'react';
import { fetchData } from '../fetchData';
import { useParams, useNavigate } from 'react-router-dom';
import ItemsList from '../items/ItemsList';
const endpoint = 'categories/';

const CategoryDetails = () => {
  const { id } = useParams();
  const [category, setCategory] = useState();
  const navigate = useNavigate();







  const notShowDropdown = true;
  console.log(notShowDropdown);
  const categoryId  = id; 

  useEffect(() => {
    const fetchCategory = async () => {
      try {
        const result = await fetchData(endpoint + id) ;
        setCategory(result);
      } catch (error) {
        navigate("/notfound");
      }
    };

    fetchCategory();
  }, []);




  

  return (

    <div>
      { category && <h2>Items in category {category.name} </h2> }
      { category && <p>Category description: {category.description}</p> }
      <ItemsList notShowDropdown={notShowDropdown} categoryId={categoryId}  /> 
    </div>
  )
}

export default CategoryDetails