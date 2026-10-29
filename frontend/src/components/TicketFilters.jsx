import React from 'react';
import { Search, X, RotateCcw } from 'lucide-react';

export default function TicketFilters({
  search,
  setSearch,
  status,
  setStatus,
  priority,
  setPriority,
  assigneeId,
  setAssigneeId,
  sortBy,
  setSortBy,
  agents = [],
  onReset,
}) {
  const hasActiveFilters =
    Boolean(search) ||
    (status && status !== 'ALL') ||
    (priority && priority !== 'ALL') ||
    (assigneeId && assigneeId !== 'ALL') ||
    sortBy !== 'updatedAt';

  return (
    <div className="filter-bar">
      <div className="filter-group" style={{ flex: 1 }}>
        {/* Search */}
        <div className="search-box">
          <Search size={16} className="search-icon" />
          <input
            type="text"
            className="search-input"
            placeholder="Search by subject, customer name or email..."
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
          {search && (
            <button
              type="button"
              onClick={() => setSearch('')}
              style={{
                position: 'absolute',
                right: '10px',
                top: '50%',
                transform: 'translateY(-50%)',
                background: 'none',
                border: 'none',
                cursor: 'pointer',
                color: '#94a3b8',
              }}
            >
              <X size={14} />
            </button>
          )}
        </div>

        {/* Status Filter */}
        <select
          className="filter-select"
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          aria-label="Filter by Status"
        >
          <option value="ALL">Status: All</option>
          <option value="OPEN">Open</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="WAITING_ON_CUSTOMER">Waiting on Customer</option>
          <option value="RESOLVED">Resolved</option>
        </select>

        {/* Priority Filter */}
        <select
          className="filter-select"
          value={priority}
          onChange={(e) => setPriority(e.target.value)}
          aria-label="Filter by Priority"
        >
          <option value="ALL">Priority: All</option>
          <option value="URGENT">Urgent</option>
          <option value="HIGH">High</option>
          <option value="MEDIUM">Medium</option>
          <option value="LOW">Low</option>
        </select>

        {/* Assignee Filter */}
        <select
          className="filter-select"
          value={assigneeId}
          onChange={(e) => setAssigneeId(e.target.value)}
          aria-label="Filter by Assignee"
        >
          <option value="ALL">Assignee: All</option>
          <option value="-1">Unassigned</option>
          {agents.map((agent) => (
            <option key={agent.id} value={agent.id}>
              {agent.name}
            </option>
          ))}
        </select>
      </div>

      {/* Sort & Reset */}
      <div className="filter-group">
        <select
          className="filter-select"
          value={sortBy}
          onChange={(e) => setSortBy(e.target.value)}
          aria-label="Sort by"
        >
          <option value="updatedAt">Sort: Recently Updated</option>
          <option value="priority">Sort: Priority (Urgent first)</option>
          <option value="createdAt">Sort: Newest Created</option>
        </select>

        {hasActiveFilters && (
          <button
            type="button"
            className="btn btn-outline btn-sm"
            onClick={onReset}
            title="Reset all filters"
          >
            <RotateCcw size={14} />
            <span>Reset</span>
          </button>
        )}
      </div>
    </div>
  );
}
