import React, { useState, useEffect } from 'react';
import Box from '@mui/material/Box';
import Button from '@mui/material/Button';
import Modal from '@mui/material/Modal';
import { style } from './CategoryModalStyles';
import axios from 'axios';

export default function CategoryModal({ onSelectCategory }) {
  const [open, setOpen] = useState(false);
  const [categories, setCategories] = useState([]);
  
  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const handleCategoryClick = (category2) => {
    onSelectCategory(category2);
    handleClose();
  };

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await axios.get('http://localhost:8080/rentify/categories');
        setCategories(response.data); 
      } catch (error) {
        console.error('Error fetching categories:', error.message);
      }
    };

    fetchCategories();
  }, []); 

  return (
    <div>
      <Button style={style.text} onClick={handleOpen}>
        Select category
      </Button>
      <Modal open={open} onClose={handleClose}>
        <Box sx={style}>
          <h1>Select Category</h1>
          <ul style={style.ul}>
            {categories.map((category2) => (
              <li
                key={category2.id}
                onClick={() => handleCategoryClick(category2.name)}  
                style={style.li}
              >
                {category2.name}  
              </li>
            ))}
          </ul>
        </Box>
      </Modal>
    </div>
  );
}