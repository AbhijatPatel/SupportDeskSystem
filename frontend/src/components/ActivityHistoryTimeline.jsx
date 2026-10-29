import React from 'react';
import { formatDate, formatRelativeTime } from '../utils/formatters';

export default function ActivityHistoryTimeline({ history = [] }) {
  if (!history || history.length === 0) {
    return (
      <div style={{ color: '#64748b', fontSize: '13px', fontStyle: 'italic' }}>
        No activity history recorded.
      </div>
    );
  }

  return (
    <div className="timeline">
      {history.map((item) => (
        <div key={item.id} className="timeline-item">
          <div className="timeline-marker"></div>
          <div className="timeline-desc">{item.description}</div>
          <div className="timeline-meta">
            <span>By {item.author || 'System'}</span>
            <span style={{ margin: '0 4px' }}>•</span>
            <span title={formatDate(item.createdAt)}>{formatRelativeTime(item.createdAt)}</span>
          </div>
        </div>
      ))}
    </div>
  );
}
