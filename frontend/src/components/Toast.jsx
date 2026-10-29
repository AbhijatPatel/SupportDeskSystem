import React, { useEffect } from 'react';
import { CheckCircle2, AlertCircle, X } from 'lucide-react';

export default function Toast({ message, type = 'success', onClose, duration = 3500 }) {
  useEffect(() => {
    if (!message) return;
    const timer = setTimeout(() => {
      if (onClose) onClose();
    }, duration);
    return () => clearTimeout(timer);
  }, [message, duration, onClose]);

  if (!message) return null;

  return (
    <div className={`toast ${type === 'success' ? 'toast-success' : 'toast-error'}`}>
      {type === 'success' ? (
        <CheckCircle2 size={18} style={{ color: '#10b981' }} />
      ) : (
        <AlertCircle size={18} style={{ color: '#ef4444' }} />
      )}
      <span>{message}</span>
      <button
        onClick={onClose}
        style={{
          background: 'none',
          border: 'none',
          color: '#94a3b8',
          cursor: 'pointer',
          padding: '2px',
          marginLeft: '8px',
        }}
      >
        <X size={14} />
      </button>
    </div>
  );
}
