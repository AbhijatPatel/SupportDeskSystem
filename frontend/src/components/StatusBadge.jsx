import React from 'react';
import { STATUS_LABELS } from '../utils/formatters';

export default function StatusBadge({ status }) {
  if (!status) return null;

  const statusKey = status.toUpperCase();
  const label = STATUS_LABELS[statusKey] || status;

  let className = 'badge ';
  switch (statusKey) {
    case 'OPEN':
      className += 'badge-status-open';
      break;
    case 'IN_PROGRESS':
      className += 'badge-status-inprogress';
      break;
    case 'WAITING_ON_CUSTOMER':
      className += 'badge-status-waiting';
      break;
    case 'RESOLVED':
      className += 'badge-status-resolved';
      break;
    default:
      className += 'badge-status-open';
  }

  return (
    <span className={className}>
      <span style={{ fontSize: '8px', marginRight: '2px' }}>●</span>
      {label}
    </span>
  );
}
