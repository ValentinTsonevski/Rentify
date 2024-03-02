import axios from "axios";

const apiUrl = "http://localhost:8080/rentify";

export const fetchData = async (endpoint) => {
  try {
    const response = await axios.get(`${apiUrl}/${endpoint}`);
    return response.data;
  } catch (error) {
    throw error;
  }
};
