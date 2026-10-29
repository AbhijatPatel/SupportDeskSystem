import React, { useState, useEffect } from 'react';
import { BrowserRouter as Router, Routes, Route, useLocation } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Topbar from './components/Topbar';
import Toast from './components/Toast';
import Dashboard from './pages/Dashboard';
import Tickets from './pages/Tickets';
import TicketDetails from './pages/TicketDetails';
import CreateTicket from './pages/CreateTicket';
import api from './services/api';

function AppLayout({ agents, currentAgent, onAgentChange, toast, setToast }) {
  const location = useLocation();

  const getPageTitle = (pathname) => {
    if (pathname === '/') return 'Dashboard';
    if (pathname.startsWith('/tickets/new')) return 'Create Ticket';
    if (pathname.startsWith('/tickets/')) return 'Ticket Details';
    if (pathname.startsWith('/tickets')) return 'Tickets';
    return 'Support Desk';
  };

  return (
    <div className="app-container">
      <Sidebar
        agents={agents}
        currentAgent={currentAgent}
        onAgentChange={onAgentChange}
      />

      <div className="main-wrapper">
        <Topbar
          title={getPageTitle(location.pathname)}
          currentAgent={currentAgent}
        />

        <main className="content-body">
          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/tickets" element={<Tickets agents={agents} />} />
            <Route
              path="/tickets/new"
              element={
                <CreateTicket
                  agents={agents}
                  currentAgent={currentAgent}
                  showToast={(msg, type) => setToast({ message: msg, type })}
                />
              }
            />
            <Route
              path="/tickets/:id"
              element={
                <TicketDetails
                  agents={agents}
                  currentAgent={currentAgent}
                  showToast={(msg, type) => setToast({ message: msg, type })}
                />
              }
            />
          </Routes>
        </main>
      </div>

      {toast && (
        <Toast
          message={toast.message}
          type={toast.type}
          onClose={() => setToast(null)}
        />
      )}
    </div>
  );
}

export default function App() {
  const [agents, setAgents] = useState([]);
  const [currentAgent, setCurrentAgent] = useState(null);
  const [toast, setToast] = useState(null);

  useEffect(() => {
    const loadAgents = async () => {
      try {
        const data = await api.getAgents();
        setAgents(data);
        if (data.length > 0) {
          const savedAgentId = localStorage.getItem('support_desk_agent_id');
          const matched = data.find((a) => a.id === Number(savedAgentId));
          setCurrentAgent(matched || data[0]);
        }
      } catch (err) {
        console.error('Failed to load agents list:', err);
      }
    };
    loadAgents();
  }, []);

  const handleAgentChange = (agent) => {
    setCurrentAgent(agent);
    localStorage.setItem('support_desk_agent_id', agent.id);
    setToast({
      message: `Active agent switched to ${agent.name}`,
      type: 'success',
    });
  };

  return (
    <Router>
      <AppLayout
        agents={agents}
        currentAgent={currentAgent}
        onAgentChange={handleAgentChange}
        toast={toast}
        setToast={setToast}
      />
    </Router>
  );
}
