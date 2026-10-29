import React, { useState } from 'react';
import { formatDate, formatRelativeTime } from '../utils/formatters';
import { Send, Lock, MessageSquare, AlertCircle } from 'lucide-react';

export default function ConversationThread({
  entries = [],
  currentAgent,
  onAddReply,
  onAddNote,
  submitting,
}) {
  const [activeTab, setActiveTab] = useState('PUBLIC_REPLY'); // 'PUBLIC_REPLY' | 'INTERNAL_NOTE'
  const [content, setContent] = useState('');
  const [error, setError] = useState('');

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!content.trim()) {
      setError('Message content cannot be empty.');
      return;
    }
    setError('');

    const author = currentAgent ? currentAgent.name : 'Support Agent';

    try {
      if (activeTab === 'PUBLIC_REPLY') {
        await onAddReply({ content: content.trim(), author });
      } else {
        await onAddNote({ content: content.trim(), author });
      }
      setContent('');
    } catch (err) {
      setError(err?.response?.data?.message || 'Failed to submit entry. Please try again.');
    }
  };

  return (
    <div>
      {/* Compose Box */}
      <div className="entry-form-card">
        <div className="entry-tabs">
          <button
            type="button"
            className={`entry-tab-btn ${activeTab === 'PUBLIC_REPLY' ? 'active-public' : ''}`}
            onClick={() => {
              setActiveTab('PUBLIC_REPLY');
              setError('');
            }}
          >
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <MessageSquare size={14} />
              Public Reply
            </span>
          </button>
          <button
            type="button"
            className={`entry-tab-btn ${activeTab === 'INTERNAL_NOTE' ? 'active-internal' : ''}`}
            onClick={() => {
              setActiveTab('INTERNAL_NOTE');
              setError('');
            }}
          >
            <span style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
              <Lock size={14} />
              Internal Note (Staff only)
            </span>
          </button>
        </div>

        <form onSubmit={handleSubmit}>
          {activeTab === 'INTERNAL_NOTE' && (
            <div
              style={{
                backgroundColor: '#fef3c7',
                border: '1px solid #fde68a',
                color: '#92400e',
                padding: '8px 12px',
                borderRadius: '6px',
                fontSize: '12px',
                marginBottom: '12px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
              }}
            >
              <Lock size={14} />
              <span>
                <strong>Internal Note:</strong> Visible only to support staff agents. Customers cannot see this message.
              </span>
            </div>
          )}

          <div className="form-group" style={{ marginBottom: '12px' }}>
            <textarea
              className={`form-control ${error ? 'error' : ''}`}
              rows={4}
              placeholder={
                activeTab === 'PUBLIC_REPLY'
                  ? 'Type your customer-facing response here...'
                  : 'Add private notes, root cause hypotheses, logs, or handoff instructions...'
              }
              value={content}
              onChange={(e) => {
                setContent(e.target.value);
                if (error) setError('');
              }}
              style={{
                backgroundColor: activeTab === 'INTERNAL_NOTE' ? '#fffbeb' : '#ffffff',
              }}
            />
            {error && <div className="form-error-msg">{error}</div>}
          </div>

          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <span style={{ fontSize: '12px', color: '#64748b' }}>
              Posting as: <strong>{currentAgent ? currentAgent.name : 'Support Agent'}</strong>
            </span>
            <button
              type="submit"
              className={`btn btn-sm ${activeTab === 'INTERNAL_NOTE' ? 'btn-outline' : 'btn-primary'}`}
              disabled={submitting}
              style={
                activeTab === 'INTERNAL_NOTE'
                  ? { backgroundColor: '#d97706', color: '#ffffff', borderColor: '#d97706' }
                  : {}
              }
            >
              <Send size={14} />
              <span>{submitting ? 'Sending...' : activeTab === 'PUBLIC_REPLY' ? 'Send Reply' : 'Add Note'}</span>
            </button>
          </div>
        </form>
      </div>

      {/* Chronological Conversation Thread */}
      <div className="conversation-thread">
        <h4 style={{ fontSize: '14px', fontWeight: 700, color: '#334155', marginTop: '12px' }}>
          Conversation & Notes ({entries.length})
        </h4>

        {entries.length === 0 ? (
          <div
            style={{
              padding: '24px',
              textAlign: 'center',
              backgroundColor: '#f8fafc',
              border: '1px dashed #cbd5e1',
              borderRadius: '8px',
              color: '#64748b',
              fontSize: '13px',
            }}
          >
            No replies or notes recorded yet. Be the first to reply or add an internal note.
          </div>
        ) : (
          entries.map((entry) => {
            const isInternal = entry.type === 'INTERNAL_NOTE';
            return (
              <div
                key={entry.id}
                className={`conversation-entry ${isInternal ? 'entry-internal' : 'entry-public'}`}
              >
                <div className="entry-header">
                  <div className="entry-author">
                    <span>{entry.author}</span>
                    <span className={`entry-type-tag ${isInternal ? 'tag-internal' : 'tag-public'}`}>
                      {isInternal ? '🔒 Internal Note' : 'Public Reply'}
                    </span>
                  </div>
                  <div className="entry-time" title={formatDate(entry.createdAt)}>
                    {formatRelativeTime(entry.createdAt)}
                  </div>
                </div>
                <div className="entry-body">{entry.content}</div>
              </div>
            );
          })
        )}
      </div>
    </div>
  );
}
