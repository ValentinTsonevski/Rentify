import React, { useState, useEffect } from "react";
import { useParams, useNavigate } from "react-router-dom";
import DateRangePicker from "@wojtekmaj/react-daterange-picker";
import "@wojtekmaj/react-daterange-picker/dist/DateRangePicker.css";
import "react-calendar/dist/Calendar.css";
import axios from "axios";
import { jwtDecode } from "jwt-decode";
import PDF from "../../assets/terms.pdf";

const termsPdfPath = "../../assets/terms.pdf";

const RentItem = () => {
  const [userId, setUserId] = useState("");
  const { id } = useParams();
  const navigate = useNavigate();
  const [dateRange, setDateRange] = useState([new Date(), new Date()]);
  const [reservedDays, setReservedDays] = useState([]);
  const [isIntervalSelected, setIsIntervalSelected] = useState(false);
  const [agreedToTerms, setAgreedToTerms] = useState(false);


  const rentItem = async () => {
    try {
      const startDateString = dateRange[0];
    const endDateString = dateRange[1];

    const startDate = formatDate(startDateString);
    const endDate = formatDate(endDateString);

    const requestBody = {
      userId: userId,
      startDate: startDate,
      endDate: endDate,
    };
    const response = await axios.post(`http://localhost:8080/rentify/stripe/checkout/${id}`,requestBody)
    window.open(response.data);
    } catch (e){
      console.log(e);
    }
  }

  useEffect(() => {
    const token = localStorage.getItem("token");

    if (token !== null) {
      const decoded = jwtDecode(token);
      setUserId(decoded.jti);
    } else {
      navigate("/login");
    }

    const fetchItem = async () => {
      try {
        const backendUrl = `http://localhost:8080/rentify/rents/item/${id}`;
        const result = await axios.get(backendUrl);
        const reserved = result.data.map((r) => ({
          startDate: new Date(
            r.startDate[0],
            r.startDate[1] - 1,
            r.startDate[2]
          ),
          endDate: new Date(r.endDate[0], r.endDate[1] - 1, r.endDate[2]),
        }));
        setReservedDays(reserved);
      } catch (error) {
        navigate("/notfound");
      }
    };

    fetchItem();
  }, [id, navigate, userId]);

  const handleDateChange = (value) => {
    if (Array.isArray(value) && value.length === 2) {
      const [startDate, endDate] = value;

      const isAnyReservedInRange = reservedDays.some((reserved) => {
        const { startDate: reservedStartDate, endDate: reservedEndDate } =
          reserved;
        return (
          (startDate <= reservedEndDate && startDate >= reservedStartDate) ||
          (endDate >= reservedStartDate && endDate <= reservedEndDate) ||
          (startDate <= reservedStartDate && endDate >= reservedEndDate)
        );
      });

      if (isAnyReservedInRange) {
        alert(
          "One or more dates within the selected range are already reserved."
        );
      } else {
        setDateRange(value);
        setIsIntervalSelected(true);
      }
    } else {
      setIsIntervalSelected(false);
    }
  };

  const today = new Date();
  const minDate = new Date(
    today.getFullYear(),
    today.getMonth(),
    today.getDate()
  );

  const isReservedDay = (date) => {
    return reservedDays.some(
      (reservedDate) =>
        date.getFullYear() === reservedDate.startDate.getFullYear() &&
        date.getMonth() === reservedDate.startDate.getMonth() &&
        date.getDate() >= reservedDate.startDate.getDate() &&
        date.getDate() <= reservedDate.endDate.getDate()
    );
  };

  const tileDisabled = ({ date }) => {
    return isReservedDay(date);
  };

  function formatDate(dateString) {
    const date = new Date(dateString);
    const year = date.getFullYear();
    const month = (date.getMonth() + 1).toString().padStart(2, "0");
    const day = date.getDate().toString().padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  const handleSubmit = async () => {
    const startDateString = dateRange[0];
    const endDateString = dateRange[1];

    const startDate = formatDate(startDateString);
    const endDate = formatDate(endDateString);

    const requestBody = {
      item: {
        id: id,
      },
      user: {
        id: userId,
      },
      startDate: startDate,
      endDate: endDate,
    };

    try {
      const backendUrl = `http://localhost:8080/rentify/rents/item/${id}`;
      const result = await axios.post(backendUrl, requestBody);
    } catch (error) {
      console.log(error);
    }
  };

  const handleCheckboxChange = () => {
    setAgreedToTerms(!agreedToTerms);
  };

  return (
    <div>
      <div className="container d-flex flex-column">
        <div className="row align-items-center justify-content-center min-vh-100">
          <div className="col-12 col-md-8 col-lg-4">
            <div className="mb-4">
              <h5>Please select date interval for your reservation</h5>
            </div>

            <DateRangePicker
              value={dateRange}
              onChange={handleDateChange}
              minDate={minDate}
              tileDisabled={tileDisabled}
            />

            <br />
            <br />
            <div style={{ display: "flex", alignItems: "center" }}>
              <input
                type="checkbox"
                checked={agreedToTerms}
                onChange={handleCheckboxChange}
                required
                style={{ width: "auto" }}
              />
              <div style={{ marginLeft: "5px" }}>
                I agree with
                <a
                  href={PDF}
                  target="_blank"
                  rel="noopener noreferrer"
                  style={{ color: "blue", marginLeft: "5px" }}
                >
                  terms and conditions
                </a>
              </div>
            </div>
            <br />
            <div className="mb-3 d-grid">
              <button
                onClick={rentItem}
                disabled={!isIntervalSelected || !agreedToTerms}
                className="btn btn-primary"
              >
                Submit
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
};

export default RentItem;
