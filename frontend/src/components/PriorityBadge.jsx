import React from 'react';
import { PRIORITY_LABELS } from '../utils/formatters';

export default function PriorityBadge({ priority }) {
  if (!priority) return null;

  const priorityKey = priority.toUpperCase();
  const label = PRIORITY_LABELS[priorityKey] || priority;

  let className = 'badge ';
  let icon = null;

  switch (priorityKey) {
    case 'URGENT':
      className += 'badge-priority-urgent';
      icon = '⚡';
      break;
    case 'HIGH':
      className += 'badge-priority-high';
      icon = '▲';
      break;
    case 'MEDIUM':
      className += 'badge-priority-medium';
      icon = '■';
      break;
    case 'LOW':
      className += 'badge-priority-low';
      icon = '▼';
      break;
    default:
      className += 'badge-priority-medium';
  }

  return (
    <span className={className}>
      {icon && <span style={{ fontSize: '10px' }}>{icon}</span>}
      {label}
    </span>
  );
}
