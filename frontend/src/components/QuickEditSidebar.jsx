import React, { useState } from 'react';
import StatusBadge from './StatusBadge';
import PriorityBadge from './PriorityBadge';
import OverdueBadge from './OverdueBadge';
import { formatDate } from '../utils/formatters';
import { Save, User, Clock, Calendar, Mail, Tag } from 'lucide-react';

export default function QuickEditSidebar({
  ticket,
  agents = [],
  currentAgent,
  onUpdateTicket,
  updating,
}) {
  const [status, setStatus] = useState(ticket.status);
  const [priority, setPriority] = useState(ticket.priority);
  const [assigneeId, setAssigneeId] = useState(
    ticket.assignedAgent ? ticket.assignedAgent.id : -1
  );

  const hasChanges =
    status !== ticket.status ||
    priority !== ticket.priority ||
    Number(assigneeId) !== (ticket.assignedAgent ? ticket.assignedAgent.id : -1);

  const handleSave = async () => {
    const author = currentAgent ? currentAgent.name : 'Support Agent';
    await onUpdateTicket({
      status,
      priority,
      assigneeId: Number(assigneeId),
      author,
    });
  };

  return (
    <div className="card">
      <div className="card-header">
        <h3 className="card-title" style={{ fontSize: '15px' }}>Ticket Details & Actions</h3>
        <OverdueBadge isOverdue={ticket.overdue} />
      </div>

      <div className="card-body">
        {/* Quick Edit Form */}
        <div className="form-group">
          <label className="form-label">
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Tag size={14} /> Status
            </span>
          </label>
          <select
            className="form-control"
            value={status}
            onChange={(e) => setStatus(e.target.value)}
          >
            <option value="OPEN">Open</option>
            <option value="IN_PROGRESS">In Progress</option>
            <option value="WAITING_ON_CUSTOMER">Waiting on Customer</option>
            <option value="RESOLVED">Resolved</option>
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Tag size={14} /> Priority
            </span>
          </label>
          <select
            className="form-control"
            value={priority}
            onChange={(e) => setPriority(e.target.value)}
          >
            <option value="LOW">Low</option>
            <option value="MEDIUM">Medium</option>
            <option value="HIGH">High</option>
            <option value="URGENT">Urgent</option>
          </select>
        </div>

        <div className="form-group">
          <label className="form-label">
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <User size={14} /> Assigned Agent
            </span>
          </label>
          <select
            className="form-control"
            value={assigneeId}
            onChange={(e) => setAssigneeId(Number(e.target.value))}
          >
            <option value="-1">Unassigned</option>
            {agents.map((agent) => (
              <option key={agent.id} value={agent.id}>
                {agent.name}
              </option>
            ))}
          </select>
        </div>

        {hasChanges && (
          <button
            type="button"
            className="btn btn-primary"
            style={{ width: '100%', marginBottom: '16px' }}
            onClick={handleSave}
            disabled={updating}
          >
            <Save size={16} />
            <span>{updating ? 'Saving Changes...' : 'Save Changes'}</span>
          </button>
        )}

        {/* Metadata Details */}
        <div
          style={{
            borderTop: '1px solid #e2e8f0',
            paddingTop: '16px',
            marginTop: '8px',
            display: 'flex',
            flexDirection: 'column',
            gap: '12px',
            fontSize: '13px',
          }}
        >
          <div>
            <div style={{ color: '#64748b', fontSize: '11px', textTransform: 'uppercase', fontWeight: 600 }}>
              Customer
            </div>
            <div style={{ fontWeight: 600, color: '#0f172a', marginTop: '2px' }}>
              {ticket.customerName || 'Anonymous Customer'}
            </div>
            {ticket.customerEmail && (
              <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#64748b', marginTop: '2px' }}>
                <Mail size={12} />
                <span>{ticket.customerEmail}</span>
              </div>
            )}
          </div>

          <div>
            <div style={{ color: '#64748b', fontSize: '11px', textTransform: 'uppercase', fontWeight: 600 }}>
              Created At
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#334155', marginTop: '2px' }}>
              <Calendar size={12} />
              <span>{formatDate(ticket.createdAt)}</span>
            </div>
          </div>

          <div>
            <div style={{ color: '#64748b', fontSize: '11px', textTransform: 'uppercase', fontWeight: 600 }}>
              Last Updated
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: '4px', color: '#334155', marginTop: '2px' }}>
              <Clock size={12} />
              <span>{formatDate(ticket.updatedAt)}</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
