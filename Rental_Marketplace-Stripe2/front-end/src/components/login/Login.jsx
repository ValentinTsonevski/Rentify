import axios from "axios";
import { GoogleLogin } from "@react-oauth/google";
import { Link, useNavigate } from "react-router-dom";
import "./Login.css";
import { useState } from "react";
import { jwtDecode } from "jwt-decode";
import { useDispatch } from "react-redux";
import { updateUserToken } from "../../features/userTokenSlice";



function Login() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [showPassword, setShowPassword] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");
  const navigate = useNavigate();
  const dispatch = useDispatch();

  const { REACT_APP_GOOGLE_CLIENT_ID } = process.env;

  const handleTogglePassword = () => {
    setShowPassword(!showPassword);
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!email || !password) {
      setErrorMessage("Please enter both email and password.");
      return;
    }

    try {
      const response = await axios.post("http://localhost:8080/rentify/login", {
        email,
        password,
      });

      const { token } = response.data;
      localStorage.setItem("token", token);
      

      
      const decodedToken = jwtDecode(token);
      const id = decodedToken.jti;

      dispatch(updateUserToken({ id }));

      navigate("/");
    } catch (error) {
      if (
        error.response &&
        error.response.status === 403 &&
        error.response.data.error === "Account has not been confirmed yet."
      ) {
        setErrorMessage(
          <span>
            {error.response.data.error}{" "}
            <span className="resend-email" onClick={handleResendEmail}>Click here to get confirmation email</span>
          </span>
        );
      } else {
        setErrorMessage("Email or Password are incorrect");
      }
    }
  };

  const handleResendEmail = async () => {
    try {
      const backendUrl = "http://localhost:8080/rentify/verification";
      const response = await axios.post(backendUrl, null, {
        params: {
          email: email,
        },
      });

      setErrorMessage(
        <div className="text-center">
          <p>Please check your email inbox for further instructions.</p>
        </div>
      );
    } catch (error) {
      console.error("Error resending email:", error);
      setErrorMessage("Error resending email.");
    }
  };

  const handleGoogleLogin = async (response) => {
    const jwt = response.credential;
    try {
      const backendResponse = await axios.post(
        "http://localhost:8080/rentify/google-login",
        {},
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: jwt,
          },
        }
      );
   
      const newToken = backendResponse.data;
      localStorage.setItem("token", newToken);
      const decodedToken = jwtDecode(jwt);
      const id = decodedToken.jti;

      dispatch(updateUserToken({ id }));

      navigate("/");
    } catch (error) {
      console.error("Error during Google login");
    }
  };

  return (
    <div className="login-container">
      <div className="login">
        <h1>Sign in</h1>

        <form className="form" onSubmit={handleSubmit}>
          <label>
            Email
            <input type="text" onChange={(e) => setEmail(e.target.value)} />
          </label>
          <label>
            Password
            <input
              type={showPassword ? "text" : "password"}
              onChange={(e) => setPassword(e.target.value)}
            />
          </label>
          {errorMessage && <div className="error-message">{errorMessage}</div>}
          <div className="password-btn">
            <button
              type="button"
              className="toggle-password-button"
              onClick={handleTogglePassword}
            >
              {showPassword ? "Hide" : "Show"}
            </button>
          </div>
          <button type="submit">Submit</button>
          <Link to="/forgot-password">Forgot Password?</Link>
        </form>

        <GoogleLogin
          onSuccess={handleGoogleLogin}
          clientId={REACT_APP_GOOGLE_CLIENT_ID}
        />
      </div>
    </div>
  );
}

export default Login;