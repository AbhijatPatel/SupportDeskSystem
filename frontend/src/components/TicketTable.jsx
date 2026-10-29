import React from 'react';
import { useNavigate } from 'react-router-dom';
import StatusBadge from './StatusBadge';
import PriorityBadge from './PriorityBadge';
import OverdueBadge from './OverdueBadge';
import { formatRelativeTime } from '../utils/formatters';
import { Inbox, AlertCircle, RefreshCw } from 'lucide-react';

export default function TicketTable({ tickets, loading, error, onRetry }) {
  const navigate = useNavigate();

  if (loading) {
    return (
      <div className="card">
        <div className="state-box">
          <RefreshCw className="state-icon" style={{ animation: 'spin 1s linear infinite' }} />
          <h3 className="state-title">Loading tickets...</h3>
          <p className="state-desc">Fetching tickets from the database...</p>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="card">
        <div className="state-box">
          <AlertCircle className="state-icon" style={{ color: '#ef4444' }} />
          <h3 className="state-title">Unable to load tickets</h3>
          <p className="state-desc">{error}</p>
          {onRetry && (
            <button className="btn btn-outline btn-sm" onClick={onRetry}>
              Try Again
            </button>
          )}
        </div>
      </div>
    );
  }

  if (!tickets || tickets.length === 0) {
    return (
      <div className="card">
        <div className="state-box">
          <Inbox className="state-icon" />
          <h3 className="state-title">No tickets found</h3>
          <p className="state-desc">
            No tickets match your search or filter criteria. Try clearing some filters or create a new ticket.
          </p>
        </div>
      </div>
    );
  }

  return (
    <div className="card">
      <div className="table-responsive">
        <table className="ticket-table">
          <thead>
            <tr>
              <th style={{ width: '60px' }}>ID</th>
              <th>Subject</th>
              <th>Customer</th>
              <th>Status</th>
              <th>Priority</th>
              <th>Assignee</th>
              <th>Updated</th>
            </tr>
          </thead>
          <tbody>
            {tickets.map((ticket) => (
              <tr
                key={ticket.id}
                className="ticket-row"
                onClick={() => navigate(`/tickets/${ticket.id}`)}
              >
                <td style={{ color: '#64748b', fontWeight: 600 }}>#{ticket.id}</td>
                <td>
                  <div className="ticket-subject-cell">
                    <span className="ticket-subject-text">{ticket.subject}</span>
                    <div style={{ display: 'flex', gap: '6px', alignItems: 'center' }}>
                      <OverdueBadge isOverdue={ticket.overdue} />
                    </div>
                  </div>
                </td>
                <td>
                  <div style={{ display: 'flex', flexDirection: 'column' }}>
                    <span style={{ fontWeight: 500 }}>
                      {ticket.customerName || 'Anonymous Customer'}
                    </span>
                    {ticket.customerEmail && (
                      <span className="customer-meta">{ticket.customerEmail}</span>
                    )}
                  </div>
                </td>
                <td>
                  <StatusBadge status={ticket.status} />
                </td>
                <td>
                  <PriorityBadge priority={ticket.priority} />
                </td>
                <td>
                  {ticket.assignedAgent ? (
                    <div className="agent-cell">
                      <div className="agent-avatar">
                        {ticket.assignedAgent.name
                          .split(' ')
                          .map((n) => n[0])
                          .join('')}
                      </div>
                      <span>{ticket.assignedAgent.name}</span>
                    </div>
                  ) : (
                    <span className="unassigned-text">Unassigned</span>
                  )}
                </td>
                <td style={{ color: '#64748b', fontSize: '13px', whiteSpace: 'nowrap' }}>
                  {formatRelativeTime(ticket.updatedAt)}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}
