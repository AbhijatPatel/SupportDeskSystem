import React from 'react';
import { Link } from 'react-router-dom';
import { Plus } from 'lucide-react';

export default function Topbar({ title, currentAgent }) {
  return (
    <header className="topbar">
      <div>
        <h2 className="topbar-title">{title}</h2>
      </div>

      <div className="topbar-actions">
        {currentAgent && (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px', fontSize: '13px', color: '#475569' }}>
            <span style={{ width: '8px', height: '8px', borderRadius: '50%', backgroundColor: '#10b981' }}></span>
            <span>Logged in as <strong>{currentAgent.name}</strong></span>
          </div>
        )}
        <Link to="/tickets/new" className="btn btn-primary btn-sm">
          <Plus size={16} />
          <span>New Ticket</span>
        </Link>
      </div>
    </header>
  );
}
