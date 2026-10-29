import React from 'react';
import { Clock } from 'lucide-react';

export default function OverdueBadge({ isOverdue }) {
  if (!isOverdue) return null;

  return (
    <span className="badge badge-overdue" title="Overdue: Created more than 24h ago and not resolved">
      <Clock size={11} strokeWidth={2.5} />
      OVERDUE
    </span>
  );
}
