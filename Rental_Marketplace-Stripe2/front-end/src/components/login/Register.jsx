import axios from "axios";
import "./Login.css";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

function Register() {

  const navigate = useNavigate();
  const [formValues, setFormValues] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: ""
  });

  const [errorMessages, setErrorMessages] = useState({
    firstName: "",
    lastName: "",
    email: "",
    password: "",
    confirmPassword: "",
    phoneNumber: ""
  });

  const [loading, setLoading] = useState(false);

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormValues({
      ...formValues,
      [name]: value,
    });
  };

  const handleSubmit =  async(e) => {

    e.preventDefault();
    
    setErrorMessages({
      firstName: "",
      lastName: "",
      email: "",
      password: "",
      confirmPassword: "",
      phoneNumber: "",

    });

    let isValid = true;
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;

    if (!emailRegex.test(formValues.email)) {
      setErrorMessages((prevErrors) => ({
        ...prevErrors,
        email: "Invalid email format",
      }));
      isValid = false;
    }

    if (formValues.password.length < 8) {
      setErrorMessages((prevErrors) => ({
        ...prevErrors,
        password: "Password must be at least 8 characters long",
      }));
      isValid = false;
    }

     if (formValues.password !== formValues.confirmPassword) {
    setErrorMessages((prevErrors) => ({
      ...prevErrors,
      confirmPassword: "Password do not match",
    }));
    isValid = false;
  }

  for (const key in formValues) {
    if (formValues[key] === "") {
      setErrorMessages((prevErrors) => ({
        ...prevErrors,
        [key]: "This field is required",
      }));
      isValid = false;
    }
  }


    if (isValid) {

      for (const key in formValues) {
        if (formValues[key] === "") {
          setErrorMessages((prevErrors) => ({
            ...prevErrors,
            [key]: "This field is required",
          }));
          isValid = false;
        }
      }

      try {
        setLoading(true);
        const response = await axios.post(
          "http://localhost:8080/rentify/register",
          formValues
          
        ).then((response) => {
          navigate("/login");
        });  
      } catch (error) {
        if (error.response && error.response.data) {
          const { data } = error.response;
      
          const errorMessage = data.errorMessage || '';
          
          const fieldNameMatch = errorMessage.match(/User with (\w+) .+ already exists/);
          const fieldName = fieldNameMatch ? fieldNameMatch[1].toLowerCase() : '';
      
          if (fieldName === "email") {
            setErrorMessages((prevErrors) => ({
              ...prevErrors,
              email: errorMessage,
            }));
          } else if (fieldName === "phonenumber") {
            setErrorMessages((prevErrors) => ({
              ...prevErrors,
              phoneNumber: errorMessage,
            }));
          } else {
        
            setErrorMessages((prevErrors) => ({
              ...prevErrors,
              email: errorMessage,
            }));
          }
        }

      } finally {
        setLoading(false);
      }

      }

  };

  const [showPassword, setShowPassword] = useState(false);

  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  return (
    <div className="login-container">
      <div className="login">
        <h1>Sign up</h1>

        <form className="form">
          <label>
            First name
            <input type="text" name="firstName" onChange={handleInputChange} />
            <p className="error-message">{errorMessages.firstName}</p>
          </label>

          <label>
            Last name
            <input type="text" name="lastName" onChange={handleInputChange} />
            <p className="error-message">{errorMessages.lastName}</p>
          </label>

          <label>
            Email
            <input type="text" name="email" onChange={handleInputChange} />
            <p className="error-message">{errorMessages.email}</p>
          </label>

          <label>
            Password
            <input
              type={showPassword ? "text" : "password"}
              name="password"
              onChange={handleInputChange}
            />
            
            <p className="error-message">{errorMessages.password}</p>
          </label>

          <label>
            Confirm Password
            <input
              type={showPassword ? "text" : "password"}
              name="confirmPassword"
              onChange={handleInputChange}
            />
            <p className="error-message">{errorMessages.confirmPassword}</p>
          </label>

          <label>
            Phone
            <input type="text" name="phoneNumber" onChange={handleInputChange} />
            <p className="error-message">{errorMessages.phoneNumber}</p>
          </label>
        
        </form>
        <div className="password-btn">
            <button
              type="button"
              className="toggle-password-button"
              onClick={handleTogglePassword}
            >
              {showPassword ? "Hide password" : "Show password"}
            </button>
          </div>
        <button type="submit" onClick={handleSubmit}>
          Submit
        </button>
      </div>
    </div>
  );
  }



export default Register;
