import React, { useState, useEffect } from "react";
import SearchIcon from "@mui/icons-material/Search";
import MonetizationOnIcon from "@mui/icons-material/MonetizationOn";

const FilterComponent = ({ notShowDropdown, categoryId, onFilterChange }) => {
  const [categories, setCategories] = useState([]);
  const [addresses, setAddresses] = useState([]);
  const [selectedCategory, setSelectedCategory] = useState("");
  const [priceFrom, setPriceFrom] = useState("");
  const [priceTo, setPriceTo] = useState("");
  const [selectedAddress, setSelectedAddress] = useState("");
  const [searchTerm, setSearchTerm] = useState("");
  const [sortOrder, setSortOrder] = useState("asc");
  const [priceError, setPriceError] = useState("");

  const handlePriceChange = (e, setPrice) => {
    const inputValue = e.target.value;

    if (inputValue < 0) {
      setPriceError("Price cannot be lower than zero");
    } else {
      setPriceError("");

      setPrice(inputValue);
    }
  };

  const handleSearchChange = (event) => {
    setSearchTerm(event.target.value);
  };

  useEffect(() => {
    if (notShowDropdown) {
      setSelectedCategory(notShowDropdown["categoryId"]);
    }

    const fetchData = async () => {
      try {
        const responseCategory = await fetch(
          "http://localhost:8080/rentify/categories"
        );
        if (!responseCategory.ok) {
          throw new Error(`HTTP error! Status: ${responseCategory.status}`);
        }
        const categoriesResponse = await responseCategory.json();

        setCategories(categoriesResponse);
      } catch (error) {
        console.error("Error fetching categories:", error);
      }

      try {
        const responseAddress = await fetch(
          "http://localhost:8080/rentify/addresses"
        );

        if (!responseAddress.ok) {
          throw new Error(`HTTP error! Status: ${responseAddress.status}`);
        }

        const addressResponse = await responseAddress.json();

        setAddresses(addressResponse);
      } catch (error) {
        console.error("Error fetching Address");
      }
    };

    fetchData();
  }, []);

  const applyFilters = async (e) => {
    e.preventDefault();
    try {
      const filters = {
        category: selectedCategory,
        priceFrom: parseFloat(priceFrom),
        priceTo: parseFloat(priceTo),
        address: selectedAddress,
        name: searchTerm,
        sortDirection: sortOrder,
      };

      const apiUrl = `http://localhost:8080/rentify/items/filter?category=${
        filters.category
      }&priceFrom=${filters.priceFrom || ""}&priceTo=${
        filters.priceTo || ""
      }&address=${filters.address}&searchTerm=${filters.name}&sortDirection=${
        filters.sortDirection
      }`;

      const response = await fetch(apiUrl);
      if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
      }

      const filteredItems = await response.json();
      onFilterChange(filteredItems);
    } catch (error) {
      console.error("Error applying filters:", error);
    }
  };

  const handleKeyPress = (event) => {
    if (event.keyCode === 13 || event.which === 13) {
      applyFilters(event);
    }
  };
  

  return (
    <div className="container pb-5">
    <div className="row justify-content-center">
      <form className="col-lg-6">
        {!notShowDropdown || Object.keys(notShowDropdown).length === 0 ? (
          <>
            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <SearchIcon className="search-icon" />
                </div>
                <input
                  type="text"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Search by name"
                  value={searchTerm}
                  onChange={handleSearchChange}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="inlineFormSelectPref"
                value={selectedCategory}
                onChange={(e) => setSelectedCategory(e.target.value)}
              >
                <option value="">Category...</option>
                {categories.map((category) => (
                  <option key={category.id} value={category.id}>
                    {category.name}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-12">
              <label className="visually-hidden">Username</label>
              <div className="input-group">
                <div className="input-group-text">
                  <MonetizationOnIcon className="search-icon" />
                </div>
                <input
                  type="number"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Price From"
                  value={priceFrom}
                  onChange={(e) => handlePriceChange(e, setPriceFrom)}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <MonetizationOnIcon className="search-icon" />
                </div>
                <input
                  type="number"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Price To"
                  value={priceTo}
                  onChange={(e) => handlePriceChange(e, setPriceTo)}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="inlineFormSelectPref"
                value={selectedAddress}
                onChange={(e) => setSelectedAddress(e.target.value)}
              >
                <option value="">City...</option>
                {addresses.map((address) => (
                  <option key={address.id} value={address.name}>
                    {address.city}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="orderBySelect"
                value={sortOrder}
                onChange={(e) => setSortOrder(e.target.value)}
              >
                <option value="asc">Order By Price Asc</option>
                <option value="desc">Order By Price Desc</option>
              </select>
            </div>

            <div className="col-12">
              <button
                type="submit"
                className="btn btn-primary"
                onClick={applyFilters}
              >
                Submit
              </button>
            </div>
          </>
        ) : (
          <>
            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <SearchIcon className="search-icon" />
                </div>
                <input
                  className="text form-control"
                  // className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Search by name"
                  value={searchTerm}
                  onChange={handleSearchChange}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <MonetizationOnIcon className="search-icon" />{" "}
                </div>
                <input
                  type="number"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Price from"
                  value={priceFrom}
                  onChange={(e) => handlePriceChange(e, setPriceFrom)}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div className="col-12">
              <div className="input-group">
                <div className="input-group-text">
                  <MonetizationOnIcon className="search-icon" />
                </div>
                <input
                  type="number"
                  className="form-control"
                  id="inlineFormInputGroupUsername"
                  placeholder="Price to"
                  value={priceTo}
                  onChange={(e) => handlePriceChange(e, setPriceTo)}
                  onKeyPress={handleKeyPress}
                />
              </div>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="inlineFormSelectPref"
                value={selectedAddress}
                onChange={(e) => setSelectedAddress(e.target.value)}
              >
                <option selected=""> City...</option>
                {addresses.map((address) => (
                  <option key={address.id} value={address.name}>
                    {address.city}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-12">
              <select
                className="form-select"
                id="orderBySelect"
                value={sortOrder}
                onChange={(e) => setSortOrder(e.target.value)}
              >
                <option value="asc">Order By Price Asc</option>
                <option value="desc">Order By Price Desc</option>
              </select>
            </div>

            <div className="col-12">
              <button 
                type="submit"
                className="btn btn-primary"
                onClick={applyFilters}
              >
                Submit
              </button>
            </div>
          </>
        )}
      </form>
    </div>
    </div>
  );
};

export default FilterComponent;
