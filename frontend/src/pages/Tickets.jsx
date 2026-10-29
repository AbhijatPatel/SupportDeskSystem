import React, { useState, useEffect, useCallback } from 'react';
import { useSearchParams } from 'react-router-dom';
import api from '../services/api';
import TicketFilters from '../components/TicketFilters';
import TicketTable from '../components/TicketTable';
import { Plus } from 'lucide-react';
import { Link } from 'react-router-dom';

export default function Tickets({ agents = [] }) {
  const [searchParams, setSearchParams] = useSearchParams();

  // Initial filter state from URL params
  const [search, setSearch] = useState(searchParams.get('search') || '');
  const [status, setStatus] = useState(searchParams.get('status') || 'ALL');
  const [priority, setPriority] = useState(searchParams.get('priority') || 'ALL');
  const [assigneeId, setAssigneeId] = useState(searchParams.get('assigneeId') || 'ALL');
  const [sortBy, setSortBy] = useState(searchParams.get('sortBy') || 'updatedAt');
  const isOverdueOnly = searchParams.get('overdue') === 'true';

  const [tickets, setTickets] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const fetchTickets = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getTickets({
        search,
        status: status !== 'ALL' ? status : undefined,
        priority: priority !== 'ALL' ? priority : undefined,
        assigneeId: assigneeId !== 'ALL' ? assigneeId : undefined,
        sortBy,
        sortDir: 'desc',
      });

      // If user arrived via overdue filter from Dashboard card, filter for overdue tickets
      let result = data;
      if (isOverdueOnly) {
        result = data.filter((t) => t.overdue);
      }
      setTickets(result);
    } catch (err) {
      console.error('Failed to fetch tickets:', err);
      setError('Unable to load tickets from the backend. Please verify the server is running.');
    } finally {
      setLoading(false);
    }
  }, [search, status, priority, assigneeId, sortBy, isOverdueOnly]);

  useEffect(() => {
    // Debounce search slightly to avoid excessive calls on rapid typing
    const timer = setTimeout(() => {
      fetchTickets();
    }, 250);
    return () => clearTimeout(timer);
  }, [fetchTickets]);

  const handleReset = () => {
    setSearch('');
    setStatus('ALL');
    setPriority('ALL');
    setAssigneeId('ALL');
    setSortBy('updatedAt');
    setSearchParams({});
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <div>
          <h2 style={{ fontSize: '22px', fontWeight: 700, color: '#0f172a' }}>
            {isOverdueOnly ? 'Overdue Tickets' : 'All Support Tickets'}
          </h2>
          <p style={{ color: '#64748b', fontSize: '13px', marginTop: '2px' }}>
            {isOverdueOnly
              ? 'Showing tickets older than 24 hours that are not yet resolved'
              : 'Search, filter, and manage customer tickets'}
          </p>
        </div>
        <Link to="/tickets/new" className="btn btn-primary">
          <Plus size={16} />
          <span>Create Ticket</span>
        </Link>
      </div>

      <TicketFilters
        search={search}
        setSearch={setSearch}
        status={status}
        setStatus={setStatus}
        priority={priority}
        setPriority={setPriority}
        assigneeId={assigneeId}
        setAssigneeId={setAssigneeId}
        sortBy={sortBy}
        setSortBy={setSortBy}
        agents={agents}
        onReset={handleReset}
      />

      <div style={{ marginBottom: '14px', fontSize: '13px', color: '#64748b', display: 'flex', justifyContent: 'space-between' }}>
        <span>Showing <strong>{tickets.length}</strong> {tickets.length === 1 ? 'ticket' : 'tickets'}</span>
      </div>

      <TicketTable
        tickets={tickets}
        loading={loading}
        error={error}
        onRetry={fetchTickets}
      />
    </div>
  );
}
