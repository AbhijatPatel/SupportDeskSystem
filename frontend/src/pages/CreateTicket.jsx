import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { ArrowLeft, Save, PlusCircle, AlertCircle } from 'lucide-react';

export default function CreateTicket({ agents = [], currentAgent, showToast }) {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    subject: '',
    customerName: '',
    customerEmail: '',
    description: '',
    priority: 'MEDIUM',
    status: 'OPEN',
    assigneeId: '',
  });

  const [errors, setErrors] = useState({});
  const [serverError, setServerError] = useState('');
  const [submitting, setSubmitting] = useState(false);

  const validate = () => {
    const errs = {};
    if (!formData.subject.trim()) {
      errs.subject = 'Subject is required.';
    }
    const hasName = formData.customerName.trim().length > 0;
    const hasEmail = formData.customerEmail.trim().length > 0;
    if (!hasName && !hasEmail) {
      errs.customer = 'Please provide a customer identifier (either name or email).';
    }
    if (!formData.description.trim()) {
      errs.description = 'Description is required.';
    }
    return errs;
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
    if (errors[name] || errors.customer) {
      setErrors((prev) => ({ ...prev, [name]: undefined, customer: undefined }));
    }
    if (serverError) setServerError('');
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const validationErrors = validate();
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setSubmitting(true);
    setServerError('');

    try {
      const payload = {
        subject: formData.subject.trim(),
        customerName: formData.customerName.trim(),
        customerEmail: formData.customerEmail.trim(),
        description: formData.description.trim(),
        priority: formData.priority,
        status: formData.status,
        assigneeId: formData.assigneeId ? Number(formData.assigneeId) : null,
        author: currentAgent ? currentAgent.name : formData.customerName.trim() || 'System',
      };

      const created = await api.createTicket(payload);
      showToast(`Ticket #${created.id} created successfully!`, 'success');
      navigate(`/tickets/${created.id}`);
    } catch (err) {
      console.error('Ticket creation error:', err);
      const msg =
        err?.response?.data?.message ||
        'Unable to create ticket. Please verify all inputs and try again.';
      setServerError(msg);
      if (err?.response?.data?.validationErrors) {
        setErrors(err.response.data.validationErrors);
      }
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <div style={{ maxWidth: '800px', margin: '0 auto' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '24px' }}>
        <Link to="/tickets" className="btn btn-outline btn-sm">
          <ArrowLeft size={14} />
          <span>Back to Tickets</span>
        </Link>
        <h2 style={{ fontSize: '22px', fontWeight: 700, color: '#0f172a' }}>
          Create New Support Ticket
        </h2>
      </div>

      <div className="card">
        <div className="card-header">
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <PlusCircle size={18} style={{ color: '#2563eb' }} />
            <h3 className="card-title">Ticket Information</h3>
          </div>
          <span style={{ fontSize: '12px', color: '#64748b' }}>
            <span style={{ color: '#dc2626' }}>*</span> Required fields
          </span>
        </div>

        <div className="card-body">
          {serverError && (
            <div
              style={{
                backgroundColor: '#fef2f2',
                border: '1px solid #fecaca',
                color: '#b91c1c',
                padding: '12px 16px',
                borderRadius: '6px',
                marginBottom: '20px',
                display: 'flex',
                alignItems: 'center',
                gap: '8px',
                fontSize: '13.5px',
              }}
            >
              <AlertCircle size={16} />
              <span>{serverError}</span>
            </div>
          )}

          <form onSubmit={handleSubmit}>
            {/* Subject */}
            <div className="form-group">
              <label className="form-label" htmlFor="subject">
                Subject <span className="required">*</span>
              </label>
              <input
                id="subject"
                name="subject"
                type="text"
                className={`form-control ${errors.subject ? 'error' : ''}`}
                placeholder="e.g. Cannot process credit card payment at checkout"
                value={formData.subject}
                onChange={handleChange}
              />
              {errors.subject && <div className="form-error-msg">{errors.subject}</div>}
            </div>

            {/* Customer Identifier */}
            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="customerName">
                  Customer Name
                </label>
                <input
                  id="customerName"
                  name="customerName"
                  type="text"
                  className={`form-control ${errors.customer ? 'error' : ''}`}
                  placeholder="e.g. Jordan Lee"
                  value={formData.customerName}
                  onChange={handleChange}
                />
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="customerEmail">
                  Customer Email
                </label>
                <input
                  id="customerEmail"
                  name="customerEmail"
                  type="email"
                  className={`form-control ${errors.customer ? 'error' : ''}`}
                  placeholder="e.g. jordan@example.com"
                  value={formData.customerEmail}
                  onChange={handleChange}
                />
              </div>
            </div>
            {errors.customer && (
              <div className="form-error-msg" style={{ marginTop: '-10px', marginBottom: '14px' }}>
                {errors.customer}
              </div>
            )}

            {/* Description */}
            <div className="form-group">
              <label className="form-label" htmlFor="description">
                Description <span className="required">*</span>
              </label>
              <textarea
                id="description"
                name="description"
                rows={5}
                className={`form-control ${errors.description ? 'error' : ''}`}
                placeholder="Provide detailed description of the issue or request..."
                value={formData.description}
                onChange={handleChange}
              />
              {errors.description && <div className="form-error-msg">{errors.description}</div>}
            </div>

            {/* Priority, Status, Assignee */}
            <div className="form-row">
              <div className="form-group">
                <label className="form-label" htmlFor="priority">
                  Priority
                </label>
                <select
                  id="priority"
                  name="priority"
                  className="form-control"
                  value={formData.priority}
                  onChange={handleChange}
                >
                  <option value="LOW">Low</option>
                  <option value="MEDIUM">Medium (Default)</option>
                  <option value="HIGH">High</option>
                  <option value="URGENT">Urgent</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="status">
                  Initial Status
                </label>
                <select
                  id="status"
                  name="status"
                  className="form-control"
                  value={formData.status}
                  onChange={handleChange}
                >
                  <option value="OPEN">Open (Default)</option>
                  <option value="IN_PROGRESS">In Progress</option>
                  <option value="WAITING_ON_CUSTOMER">Waiting on Customer</option>
                  <option value="RESOLVED">Resolved</option>
                </select>
              </div>

              <div className="form-group">
                <label className="form-label" htmlFor="assigneeId">
                  Assigned Agent
                </label>
                <select
                  id="assigneeId"
                  name="assigneeId"
                  className="form-control"
                  value={formData.assigneeId}
                  onChange={handleChange}
                >
                  <option value="">Unassigned (Default)</option>
                  {agents.map((agent) => (
                    <option key={agent.id} value={agent.id}>
                      {agent.name}
                    </option>
                  ))}
                </select>
              </div>
            </div>

            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '24px' }}>
              <Link to="/tickets" className="btn btn-outline">
                Cancel
              </Link>
              <button type="submit" className="btn btn-primary" disabled={submitting}>
                <Save size={16} />
                <span>{submitting ? 'Creating Ticket...' : 'Create Ticket'}</span>
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
