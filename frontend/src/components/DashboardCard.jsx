import React from 'react';
import { Link } from 'react-router-dom';

export default function DashboardCard({ title, value, icon, to, color = '#2563eb', bg = '#eff6ff' }) {
  return (
    <Link to={to} className="stat-card">
      <div className="stat-card-header">
        <span className="stat-card-title">{title}</span>
        <div className="stat-card-icon" style={{ backgroundColor: bg, color }}>
          {icon}
        </div>
      </div>
      <div className="stat-card-value">{value}</div>
    </Link>
  );
}
