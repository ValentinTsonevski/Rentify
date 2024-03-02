import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Navbar from "./components/navbar/Navbar";
import Home from "./components/home/Home";
import NotFound from "./components/not-found/NotFound";
import Footer from "./components/footer/Footer";
import CategoryDetails from "./components/categories/CategoryDetails";
import ItemDetails from "./components/items/ItemDetails";
import Login from "./components/login/Login";
import CreateItem from "./components/add-item/Create-item";
import Register from "./components/login/Register";
import FilterComponent from "./components/filter/FilterComponent";
import Views from "./components/views/Views";
import LikedItemsPage from "./components/items/LikedItemsPage";
import ProfilePage from "./components/profile-page/ProfilePage";

import "./App.css";
import ForgotPassword from "./components/forgot-password/ForgotPassword";
import ResetPassword from "./components/forgot-password/ResetPassword";
import AccountVerification from "./components/account-verification/AccountVerification";
import EditItem from "./components/profile-page/EditItem";
import RentItem from "./components/rents/RentItem";

function App() {
  return (
    <Router>
      <div className="App">
        <Navbar />

        <Routes>
          
          <Route path="/" element={<Home />} />
          <Route path="/items/category/:id" element={<CategoryDetails />} />
          <Route path="items/:id" element={<ItemDetails />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="*" element={<NotFound />} />
          <Route path="/notfound" element={<NotFound />} />
          <Route path="/filter" element={<FilterComponent />} />
          <Route path="/items/create" element={<CreateItem />} />
          <Route path="/views" element={<Views />} />
          <Route exact path="/likes" element={<LikedItemsPage />} />
          <Route path="/notfound" element={<NotFound />} />
          <Route path="*" element={<NotFound />} />
          <Route path="/settings" element={<ProfilePage />} />
          <Route path = "/edit/:id" element = {<EditItem/>}/>
          <Route path="/forgot-password" element={<ForgotPassword />} />
          <Route path="/reset-password" element={<ResetPassword />} />
          <Route path="/account-verification" element={<AccountVerification />} />
          <Route path="/rent-item/:id" element={<RentItem />} />
        </Routes>
        <Footer />
      </div>
    </Router>
  );
}

export default App;
