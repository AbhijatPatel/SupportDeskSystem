import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link } from 'react-router-dom';
import api from '../services/api';
import StatusBadge from '../components/StatusBadge';
import PriorityBadge from '../components/PriorityBadge';
import OverdueBadge from '../components/OverdueBadge';
import QuickEditSidebar from '../components/QuickEditSidebar';
import ConversationThread from '../components/ConversationThread';
import ActivityHistoryTimeline from '../components/ActivityHistoryTimeline';
import {
  ArrowLeft,
  RefreshCw,
  AlertCircle,
  FileText,
  History,
  MessageSquare,
} from 'lucide-react';

export default function TicketDetails({ agents = [], currentAgent, showToast }) {
  const { id } = useParams();
  const [ticket, setTicket] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [activeTab, setActiveTab] = useState('conversation'); // 'conversation' | 'history'
  const [updating, setUpdating] = useState(false);
  const [submittingEntry, setSubmittingEntry] = useState(false);

  const fetchTicket = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const data = await api.getTicketById(id);
      setTicket(data);
    } catch (err) {
      console.error('Failed to load ticket:', err);
      setError(
        err?.response?.status === 404
          ? 'Ticket not found. It may have been removed or does not exist.'
          : 'Unable to load ticket details. Please try again.'
      );
    } finally {
      setLoading(false);
    }
  }, [id]);

  useEffect(() => {
    fetchTicket();
  }, [fetchTicket]);

  const handleUpdateTicket = async (updateData) => {
    setUpdating(true);
    try {
      await api.updateTicket(id, updateData);
      showToast('Ticket updated successfully', 'success');
      await fetchTicket();
    } catch (err) {
      showToast(err?.response?.data?.message || 'Failed to update ticket', 'error');
    } finally {
      setUpdating(false);
    }
  };

  const handleAddReply = async (replyData) => {
    setSubmittingEntry(true);
    try {
      await api.addPublicReply(id, replyData);
      showToast('Public reply sent successfully', 'success');
      await fetchTicket();
    } catch (err) {
      throw err;
    } finally {
      setSubmittingEntry(false);
    }
  };

  const handleAddNote = async (noteData) => {
    setSubmittingEntry(true);
    try {
      await api.addInternalNote(id, noteData);
      showToast('Internal note saved successfully', 'success');
      await fetchTicket();
    } catch (err) {
      throw err;
    } finally {
      setSubmittingEntry(false);
    }
  };

  if (loading) {
    return (
      <div className="card">
        <div className="state-box">
          <RefreshCw className="state-icon" style={{ animation: 'spin 1s linear infinite' }} />
          <h3 className="state-title">Loading ticket details...</h3>
          <p className="state-desc">Fetching ticket #{id} from the database...</p>
        </div>
      </div>
    );
  }

  if (error || !ticket) {
    return (
      <div>
        <Link to="/tickets" className="btn btn-outline btn-sm" style={{ marginBottom: '16px' }}>
          <ArrowLeft size={14} />
          <span>Back to Tickets</span>
        </Link>
        <div className="card">
          <div className="state-box">
            <AlertCircle className="state-icon" style={{ color: '#ef4444' }} />
            <h3 className="state-title">{error || 'Ticket Not Found'}</h3>
            <p className="state-desc">The requested ticket could not be found or loaded.</p>
            <button className="btn btn-outline btn-sm" onClick={fetchTicket}>
              Try Again
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div>
      {/* Navigation & Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <Link to="/tickets" className="btn btn-outline btn-sm">
          <ArrowLeft size={14} />
          <span>Back to Tickets</span>
        </Link>
        <button className="btn btn-outline btn-sm" onClick={fetchTicket}>
          <RefreshCw size={14} />
          <span>Refresh</span>
        </button>
      </div>

      {/* Ticket Subject Header */}
      <div className="card" style={{ marginBottom: '24px' }}>
        <div className="card-body" style={{ padding: '20px 24px' }}>
          <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: '16px', flexWrap: 'wrap' }}>
            <div>
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px', marginBottom: '8px', flexWrap: 'wrap' }}>
                <span style={{ fontSize: '14px', fontWeight: 700, color: '#64748b' }}>
                  #{ticket.id}
                </span>
                <StatusBadge status={ticket.status} />
                <PriorityBadge priority={ticket.priority} />
                <OverdueBadge isOverdue={ticket.overdue} />
              </div>
              <h1 style={{ fontSize: '20px', fontWeight: 700, color: '#0f172a' }}>
                {ticket.subject}
              </h1>
            </div>
          </div>
        </div>
      </div>

      {/* Main Detail Grid */}
      <div className="detail-layout">
        {/* Left Column: Description & Conversation / History */}
        <div className="detail-main">
          {/* Ticket Description */}
          <div className="card">
            <div className="card-header">
              <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                <FileText size={16} style={{ color: '#2563eb' }} />
                <h3 className="card-title">Description</h3>
              </div>
            </div>
            <div className="card-body">
              <div style={{ fontSize: '14.5px', lineHeight: 1.6, color: '#1e293b', whiteSpace: 'pre-wrap' }}>
                {ticket.description}
              </div>
            </div>
          </div>

          {/* Section Tabs: Conversation & Activity History */}
          <div className="card">
            <div className="card-header" style={{ padding: '8px 16px' }}>
              <div style={{ display: 'flex', gap: '8px' }}>
                <button
                  type="button"
                  className={`btn btn-sm ${activeTab === 'conversation' ? 'btn-primary' : 'btn-outline'}`}
                  onClick={() => setActiveTab('conversation')}
                >
                  <MessageSquare size={14} />
                  <span>Conversation ({ticket.entries ? ticket.entries.length : 0})</span>
                </button>
                <button
                  type="button"
                  className={`btn btn-sm ${activeTab === 'history' ? 'btn-primary' : 'btn-outline'}`}
                  onClick={() => setActiveTab('history')}
                >
                  <History size={14} />
                  <span>Activity History ({ticket.history ? ticket.history.length : 0})</span>
                </button>
              </div>
            </div>

            <div className="card-body">
              {activeTab === 'conversation' ? (
                <ConversationThread
                  entries={ticket.entries}
                  currentAgent={currentAgent}
                  onAddReply={handleAddReply}
                  onAddNote={handleAddNote}
                  submitting={submittingEntry}
                />
              ) : (
                <div>
                  <h4 style={{ fontSize: '14px', fontWeight: 700, color: '#334155', marginBottom: '16px' }}>
                    Audit Log & Events
                  </h4>
                  <ActivityHistoryTimeline history={ticket.history} />
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Right Column: Edit Controls & Metadata */}
        <div className="detail-sidebar">
          <QuickEditSidebar
            ticket={ticket}
            agents={agents}
            currentAgent={currentAgent}
            onUpdateTicket={handleUpdateTicket}
            updating={updating}
          />
        </div>
      </div>
    </div>
  );
}
