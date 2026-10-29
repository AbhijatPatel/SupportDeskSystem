import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import api from '../services/api';
import DashboardCard from '../components/DashboardCard';
import TicketTable from '../components/TicketTable';
import {
  Ticket,
  AlertOctagon,
  Clock,
  CheckCircle,
  PlayCircle,
  Flame,
  ArrowRight,
  RefreshCw,
} from 'lucide-react';

export default function Dashboard() {
  const [stats, setStats] = useState({
    totalTickets: 0,
    openTickets: 0,
    inProgressTickets: 0,
    waitingOnCustomerTickets: 0,
    resolvedTickets: 0,
    urgentTickets: 0,
    overdueTickets: 0,
  });
  const [recentTickets, setRecentTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchDashboardData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [statsData, ticketsData] = await Promise.all([
        api.getDashboardStats(),
        api.getTickets({ sortBy: 'updatedAt', sortDir: 'desc' }),
      ]);
      setStats(statsData);
      setRecentTickets(ticketsData.slice(0, 5));
    } catch (err) {
      console.error('Failed to load dashboard:', err);
      setError('Failed to connect to backend server. Make sure the Spring Boot service is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchDashboardData();
  }, []);

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
        <div>
          <h2 style={{ fontSize: '22px', fontWeight: 700, color: '#0f172a' }}>Support Overview</h2>
          <p style={{ color: '#64748b', fontSize: '13px', marginTop: '2px' }}>
            Live database metrics and real-time support queue status
          </p>
        </div>
        <button
          className="btn btn-outline btn-sm"
          onClick={fetchDashboardData}
          disabled={loading}
        >
          <RefreshCw size={14} className={loading ? 'state-icon' : ''} style={loading ? { animation: 'spin 1s linear infinite' } : {}} />
          <span>Refresh Metrics</span>
        </button>
      </div>

      {/* 6 Real Database Stats Cards */}
      <div className="stats-grid">
        <DashboardCard
          title="Total Tickets"
          value={stats.totalTickets}
          icon={<Ticket size={18} />}
          to="/tickets"
          color="#2563eb"
          bg="#eff6ff"
        />
        <DashboardCard
          title="Open Tickets"
          value={stats.openTickets}
          icon={<AlertOctagon size={18} />}
          to="/tickets?status=OPEN"
          color="#1d4ed8"
          bg="#dbeafe"
        />
        <DashboardCard
          title="In Progress"
          value={stats.inProgressTickets}
          icon={<PlayCircle size={18} />}
          to="/tickets?status=IN_PROGRESS"
          color="#b45309"
          bg="#fef3c7"
        />
        <DashboardCard
          title="Resolved"
          value={stats.resolvedTickets}
          icon={<CheckCircle size={18} />}
          to="/tickets?status=RESOLVED"
          color="#047857"
          bg="#ecfdf5"
        />
        <DashboardCard
          title="Urgent Priority"
          value={stats.urgentTickets}
          icon={<Flame size={18} />}
          to="/tickets?priority=URGENT"
          color="#b91c1c"
          bg="#fee2e2"
        />
        <DashboardCard
          title="Overdue Tickets"
          value={stats.overdueTickets}
          icon={<Clock size={18} />}
          to="/tickets?overdue=true"
          color="#991b1b"
          bg="#ffe4e6"
        />
      </div>

      {/* Recent Activity Table */}
      <div style={{ marginTop: '32px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '16px' }}>
          <div>
            <h3 style={{ fontSize: '16px', fontWeight: 700, color: '#0f172a' }}>Recent Tickets</h3>
            <p style={{ color: '#64748b', fontSize: '12.5px' }}>Recently updated customer tickets</p>
          </div>
          <Link to="/tickets" className="btn btn-outline btn-sm">
            <span>View All Tickets</span>
            <ArrowRight size={14} />
          </Link>
        </div>

        <TicketTable
          tickets={recentTickets}
          loading={loading}
          error={error}
          onRetry={fetchDashboardData}
        />
      </div>
    </div>
  );
}
