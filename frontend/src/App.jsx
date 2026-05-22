import React from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import Login from './pages/Login';
import Register from './pages/Register';
import './index.css';

const Dashboard = () => (
  <div className="dashboard">
    <nav className="navbar glass-effect">
      <h1>FlexiPay</h1>
      <button onClick={() => {
        localStorage.removeItem('token');
        window.location.href = '/login';
      }} className="btn-secondary">Logout</button>
    </nav>
    <div className="content">
      <h2>Welcome to your Dashboard</h2>
      <p>BNPL core finance services will be available here soon.</p>
    </div>
  </div>
);

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/register" element={<Register />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/" element={<Navigate to="/login" replace />} />
      </Routes>
    </Router>
  );
}

export default App;
