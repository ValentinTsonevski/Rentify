import React from "react";
import ReactDOM from "react-dom/client";
import App from "./App";
import { GoogleOAuthProvider } from '@react-oauth/google';
import 'bootstrap/dist/css/bootstrap.css';
import store from './app/store'
import {Provider} from 'react-redux'

const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <React.StrictMode>
    <Provider store = {store} >
    <GoogleOAuthProvider clientId="1022611064919-anjhq49aic100ll017uci89hnctoqf6g.apps.googleusercontent.com">
      <App />
    </GoogleOAuthProvider>
    </Provider>
  </React.StrictMode>
);
