export function formatDate(dateString) {
  if (!dateString) return '—';
  try {
    const d = new Date(dateString);
    return d.toLocaleString(undefined, {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit',
    });
  } catch (e) {
    return dateString;
  }
}

export function formatRelativeTime(dateString) {
  if (!dateString) return '—';
  try {
    const date = new Date(dateString);
    const now = new Date();
    const diffMs = now - date;
    const diffSec = Math.floor(diffMs / 1000);
    const diffMin = Math.floor(diffSec / 60);
    const diffHour = Math.floor(diffMin / 60);
    const diffDay = Math.floor(diffHour / 24);

    if (diffDay > 30) {
      return formatDate(dateString);
    }
    if (diffDay > 1) {
      return `${diffDay} days ago`;
    }
    if (diffDay === 1) {
      return 'Yesterday';
    }
    if (diffHour >= 1) {
      return `${diffHour}h ago`;
    }
    if (diffMin >= 1) {
      return `${diffMin}m ago`;
    }
    return 'Just now';
  } catch (e) {
    return dateString;
  }
}

export const STATUS_LABELS = {
  OPEN: 'Open',
  IN_PROGRESS: 'In Progress',
  WAITING_ON_CUSTOMER: 'Waiting on Customer',
  RESOLVED: 'Resolved',
};

export const PRIORITY_LABELS = {
  LOW: 'Low',
  MEDIUM: 'Medium',
  HIGH: 'High',
  URGENT: 'Urgent',
};
