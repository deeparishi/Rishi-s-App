import React, { useState, useEffect } from 'react';
import Login from '../Components/login/Login';
import Register from '../Components/register/Register';
import '../styles/AuthPage.css';
import { useNavigate } from 'react-router-dom';

const AuthPage = () => {
  const navigate = useNavigate();

  useEffect(() => {
    const urlParams = new URLSearchParams(window.location.search);
    const token = urlParams.get("token");

    if (token) {
      localStorage.setItem("token", token);

      urlParams.delete("token");
      const newUrl = `${window.location.pathname}`;
      window.history.replaceState({}, document.title, newUrl);
      navigate("/dashboard")
    }
  }, []);

  const handleGoogleAuth = () => {
    window.location.href = `http://localhost:8098/oauth2/authorization/google?redirect_uri=http://localhost:3000`;
  };

  const handleManualLogin = () => {
    navigate("/dashboard");
  };

  return (
    <div className="auth-container">
      <div className="form-container">
        <h2>Login</h2>
        <button className="google-button" onClick={handleGoogleAuth}>
          Sign in with Google
        </button>
        <div className="separator">or</div>
        <form onSubmit={(e) => {
          e.preventDefault();
          handleManualLogin();
        }}>
          <input type="email" placeholder="Email" required />
          <input type="password" placeholder="Password" required />
          <button type="submit">Login</button>
        </form>
      </div>
    </div>
  );
};

export default AuthPage;
