import React from 'react';
import { NavLink } from 'react-router-dom';
import { LayoutDashboard, Ticket, PlusCircle, UserCheck, Headphones } from 'lucide-react';

export default function Sidebar({ currentAgent, agents, onAgentChange }) {
  return (
    <aside className="sidebar">
      <div className="sidebar-header">
        <div className="logo-icon">
          <Headphones size={20} />
        </div>
        <div className="logo-text">
          <h1>SupportDesk</h1>
          <p>Agent Workspace</p>
        </div>
      </div>

      <nav className="sidebar-nav">
        <NavLink
          to="/"
          end
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
        >
          <LayoutDashboard size={18} />
          <span>Dashboard</span>
        </NavLink>

        <NavLink
          to="/tickets"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
        >
          <Ticket size={18} />
          <span>All Tickets</span>
        </NavLink>

        <NavLink
          to="/tickets/new"
          className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
        >
          <PlusCircle size={18} />
          <span>Create Ticket</span>
        </NavLink>
      </nav>

      <div className="sidebar-agent-section">
        <div className="sidebar-agent-label">
          <UserCheck size={14} />
          <span>Current Agent</span>
        </div>
        <select
          className="agent-select"
          value={currentAgent?.id || ''}
          onChange={(e) => {
            const selected = agents.find((a) => a.id === Number(e.target.value));
            if (selected) onAgentChange(selected);
          }}
        >
          {agents.map((agent) => (
            <option key={agent.id} value={agent.id}>
              {agent.name}
            </option>
          ))}
        </select>
      </div>
    </aside>
  );
}
