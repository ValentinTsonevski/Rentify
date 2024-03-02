import React, { useState } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import axios from "axios";

const AccountVerification = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const token = searchParams.get("token");

  const [errorResponse, setErrorResponse] = useState("");

  const handleSubmit = async (e) => {
    e.preventDefault();

    try {
      const backendUrl = "http://localhost:8080/rentify/verification/confirm";
      const response = await axios.post(backendUrl, null, {
        params: {
          token: token,
        },
      });
      navigate("/login");
    } catch (error) {
        if (error.response && error.response.status === 404) {
            setErrorResponse(error.response.data.error);
          } else {
            setErrorResponse("An error occurred. Please try again later.");
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
                <h5>Confirm Account</h5>
                <p className="mb-2">
                  Please click the button to confirm your account
                </p>
              </div>

              <form onSubmit={handleSubmit}>
                <div className="mb-3 d-grid">
                  <button type="submit" className="btn btn-primary">
                    Confirm
                  </button>
                </div>

                {errorResponse && (
                  <div className="alert alert-danger" role="alert">
                    {errorResponse}
                  </div>
                )}
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AccountVerification;
