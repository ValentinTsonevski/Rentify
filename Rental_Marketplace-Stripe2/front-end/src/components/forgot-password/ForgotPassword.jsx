import React, { useState } from "react";
import { Link } from "react-router-dom";
import axios from "axios";

const ForgotPassword = () => {
  const [email, setEmail] = useState("");
  const [submitted, setSubmitted] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const handleEmailChange = (e) => {
    setEmail(e.target.value);
    setErrorMessage("");
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const backendUrl = "http://localhost:8080/rentify/password/reset";
      const response = await axios.post(backendUrl, null, {
        params: {
          email: email,
        },
      });

      setSubmitted(true);
      setErrorMessage("");
    } catch (error) {
      if (error.response && error.response.status === 404) {
        setErrorMessage("User with this email not found. Please register.");
      } else {
        setErrorMessage("An error occurred. Please try again later.");
      }
    }
  };

  return (
    <div className="container d-flex flex-column">
      <div className="row align-items-center justify-content-center min-vh-100">
        <div className="col-12 col-md-8 col-lg-4">
          <div className="card shadow-sm">
            <div className="card-body">
              <div className="mb-4">
                <h5>Forgot Password?</h5>
                {!submitted && (
                  <p className="mb-2">
                    Enter your registered email to reset the password
                  </p>
                )}
              </div>

              {!submitted ? (
                <form onSubmit={handleSubmit}>
                  <div className="mb-3">
                    <label htmlFor="email" className="form-label">
                      Email
                    </label>
                    <input
                      type="email"
                      id="email"
                      className="form-control"
                      name="email"
                      placeholder="Enter Your Email"
                      value={email}
                      onChange={handleEmailChange}
                      required
                    />
                  </div>
                  <div className="mb-3 d-grid">
                    <button type="submit" className="btn btn-primary">
                      Reset Password
                    </button>
                  </div>
                  <Link to="/register">
                    <span>Don't have an account? Register</span>
                  </Link>
                </form>
              ) : (
                <div className="text-center">
                  <p>Please check your email inbox for further instructions.</p>
                </div>
              )}

              {errorMessage && (
                <div className="alert alert-danger" role="alert">
                  {errorMessage}
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default ForgotPassword;
