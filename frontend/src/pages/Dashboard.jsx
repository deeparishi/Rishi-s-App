import React, { useEffect, useState } from "react";

const Dashboard = () => {
  const userName = "User";
  const [isTokenPresent, setIsTokenPresent] = useState(false);

  useEffect(() => {
    const token = localStorage.getItem("token");
    setIsTokenPresent(!!token);
  }, []);

  return (
    <div style={styles.dashboardContainer}>
      {isTokenPresent ? (
        <>
          <h1>Welcome to Your Dashboard, {userName}!</h1>
          <p>
            This is your personalized space to manage your tasks and activities.
          </p>
          <div style={styles.welcomeCard}>
            <h2>Welcome Board</h2>
            <p>Here are some quick actions:</p>
            <ul>
              <li>View your profile</li>
              <li>Check recent notifications</li>
              <li>Manage settings</li>
            </ul>
          </div>
        </>
      ) : (
        <>
          <h1>Access Denied</h1>
          <p>You must log in to view this page.</p>
          <button onClick={() => (window.location.href = "/")}>
            Go to Login
          </button>
        </>
      )}
    </div>
  );
};

const styles = {
  dashboardContainer: {
    padding: "20px",
    textAlign: "center",
    backgroundColor: "#f9f9f9",
    height: "100vh",
  },
  welcomeCard: {
    marginTop: "20px",
    padding: "20px",
    border: "1px solid #ddd",
    borderRadius: "8px",
    backgroundColor: "#fff",
  },
};

export default Dashboard;
