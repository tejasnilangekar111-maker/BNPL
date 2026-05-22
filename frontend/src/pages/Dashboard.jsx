import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const Dashboard = () => {
  const navigate = useNavigate();
  const [profile, setProfile] = useState(null);
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showApplyForm, setShowApplyForm] = useState(false);
  const [applyForm, setApplyForm] = useState({ amount: '', tenureMonths: '3' });
  const [applyLoading, setApplyLoading] = useState(false);
  const [applyError, setApplyError] = useState('');
  const [applySuccess, setApplySuccess] = useState('');
  const [expandedOrder, setExpandedOrder] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) { navigate('/login'); return; }
    fetchData();
  }, []);

  useEffect(() => {
    if (profile?.role === 'ADMIN') navigate('/admin');
  }, [profile]);

  const fetchData = async () => {
    try {
      const [profileRes, ordersRes] = await Promise.all([
        api.get('/v1/user/profile'),
        api.get('/v1/orders'),
      ]);
      setProfile(profileRes.data);
      setOrders(ordersRes.data);
    } catch (err) {
      if (err.response?.status === 401) {
        localStorage.removeItem('token');
        navigate('/login');
      }
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    navigate('/login');
  };

  const handleApply = async (e) => {
    e.preventDefault();
    setApplyLoading(true);
    setApplyError('');
    setApplySuccess('');
    try {
      await api.post('/v1/orders', {
        amount: parseFloat(applyForm.amount),
        tenureMonths: parseInt(applyForm.tenureMonths),
      });
      setApplySuccess('BNPL plan approved! Your EMI schedule has been created.');
      setApplyForm({ amount: '', tenureMonths: '3' });
      setShowApplyForm(false);
      fetchData();
    } catch (err) {
      setApplyError(err.response?.data?.message || 'Order was rejected. Check your credit score and income.');
    } finally {
      setApplyLoading(false);
    }
  };

  const getCreditLabel = (score) => {
    if (score >= 750) return { label: 'Excellent', color: '#22c55e' };
    if (score >= 700) return { label: 'Good', color: '#84cc16' };
    if (score >= 650) return { label: 'Fair', color: '#f59e0b' };
    return { label: 'Poor', color: '#ef4444' };
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PAID': return '#22c55e';
      case 'OVERDUE': return '#ef4444';
      case 'APPROVED': return '#6366f1';
      case 'COMPLETED': return '#22c55e';
      case 'REJECTED': return '#ef4444';
      default: return '#f59e0b';
    }
  };

  if (loading) {
    return (
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
        <p style={{ color: 'var(--text-muted)' }}>Loading...</p>
      </div>
    );
  }

  const creditInfo = profile ? getCreditLabel(profile.creditScore) : null;
  const activeOrders = orders.filter(o => o.status === 'APPROVED');
  const totalEmiDue = activeOrders.reduce((sum, o) => {
    const pending = (o.emiSchedule || []).filter(e => e.status === 'PENDING').length;
    return sum + (pending * parseFloat(o.emiAmount || 0));
  }, 0);

  return (
    <div className="dashboard">
      {/* Navbar */}
      <nav className="navbar glass-effect">
        <h1>FlexiPay</h1>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>{profile?.email}</span>
          <button onClick={handleLogout} className="btn-secondary">Logout</button>
        </div>
      </nav>

      <div className="content">
        {/* Welcome */}
        <div style={{ marginBottom: '2rem' }}>
          <h2 style={{ marginBottom: '0.25rem' }}>Welcome back</h2>
          <p style={{ color: 'var(--text-muted)' }}>Here's your financial overview</p>
        </div>

        {/* Success message */}
        {applySuccess && (
          <div style={{
            background: 'rgba(34,197,94,0.1)', color: '#22c55e',
            padding: '0.75rem 1rem', borderRadius: '8px',
            border: '1px solid rgba(34,197,94,0.2)', marginBottom: '1.5rem', fontSize: '0.875rem'
          }}>
            {applySuccess}
          </div>
        )}

        {/* Stats Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          {/* Credit Score */}
          <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px' }}>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Credit Score</p>
            <p style={{ fontSize: '2.5rem', fontWeight: '700', color: creditInfo?.color }}>{profile?.creditScore}</p>
            <p style={{ color: creditInfo?.color, fontSize: '0.875rem', marginTop: '0.25rem' }}>{creditInfo?.label}</p>
          </div>

          {/* Monthly Income */}
          <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px' }}>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Monthly Income</p>
            <p style={{ fontSize: '2rem', fontWeight: '700', color: 'var(--text-main)' }}>
              ₹{profile?.monthlyIncome?.toLocaleString('en-IN') ?? '—'}
            </p>
          </div>

          {/* Active Loans */}
          <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px' }}>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Active Loans</p>
            <p style={{ fontSize: '2.5rem', fontWeight: '700', color: 'var(--text-main)' }}>{activeOrders.length}</p>
          </div>

          {/* Total EMI Due */}
          <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px' }}>
            <p style={{ color: 'var(--text-muted)', fontSize: '0.8rem', marginBottom: '0.5rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>Total EMI Due</p>
            <p style={{ fontSize: '2rem', fontWeight: '700', color: 'var(--text-main)' }}>
              ₹{totalEmiDue.toLocaleString('en-IN', { minimumFractionDigits: 2 })}
            </p>
          </div>
        </div>

        {/* Apply for BNPL */}
        <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px', marginBottom: '2rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: showApplyForm ? '1.5rem' : '0' }}>
            <div>
              <h3 style={{ fontSize: '1.1rem', fontWeight: '600', marginBottom: '0.25rem' }}>Apply for BNPL</h3>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>Split any purchase into easy monthly instalments</p>
            </div>
            <button
              onClick={() => { setShowApplyForm(!showApplyForm); setApplyError(''); }}
              className="btn-primary"
              style={{ width: 'auto', padding: '0.6rem 1.25rem', marginTop: '0' }}
            >
              {showApplyForm ? 'Cancel' : '+ Apply Now'}
            </button>
          </div>

          {showApplyForm && (
            <form onSubmit={handleApply}>
              {applyError && (
                <div className="error-message">{applyError}</div>
              )}
              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
                <div className="input-group" style={{ marginBottom: '0' }}>
                  <label>Purchase Amount (₹)</label>
                  <input
                    type="number" min="1000" step="100"
                    placeholder="e.g. 25000"
                    value={applyForm.amount}
                    onChange={e => setApplyForm({ ...applyForm, amount: e.target.value })}
                    required
                  />
                </div>
                <div className="input-group" style={{ marginBottom: '0' }}>
                  <label>Tenure</label>
                  <select
                    value={applyForm.tenureMonths}
                    onChange={e => setApplyForm({ ...applyForm, tenureMonths: e.target.value })}
                    style={{
                      width: '100%', padding: '0.875rem 1rem', borderRadius: '12px',
                      border: '1px solid var(--surface-border)', background: 'var(--input-bg)',
                      color: 'var(--text-main)', fontSize: '1rem', fontFamily: 'inherit'
                    }}
                  >
                    <option value="3">3 months</option>
                    <option value="6">6 months</option>
                    <option value="12">12 months</option>
                    <option value="24">24 months</option>
                  </select>
                </div>
              </div>

              {/* Interest Rate Guide */}
              <div style={{ marginTop: '1rem', padding: '0.875rem', borderRadius: '10px', background: 'rgba(99,102,241,0.08)', border: '1px solid rgba(99,102,241,0.2)' }}>
                <p style={{ fontSize: '0.8rem', color: '#a5b4fc', marginBottom: '0.4rem', fontWeight: '600' }}>Your interest rate based on credit score {profile?.creditScore}</p>
                <p style={{ fontSize: '0.875rem', color: 'var(--text-main)', fontWeight: '700' }}>
                  {profile?.creditScore >= 750 ? '0% p.a. — Interest Free!'
                    : profile?.creditScore >= 700 ? '8.5% p.a.'
                    : profile?.creditScore >= 650 ? '14.0% p.a.'
                    : '24.0% p.a.'}
                </p>
              </div>

              <button type="submit" disabled={applyLoading} className="btn-primary" style={{ marginTop: '1rem' }}>
                {applyLoading ? 'Processing...' : 'Submit Application'}
              </button>
            </form>
          )}
        </div>

        {/* Orders & EMI Schedule */}
        <div>
          <h3 style={{ fontSize: '1.1rem', fontWeight: '600', marginBottom: '1rem' }}>My BNPL Plans</h3>

          {orders.length === 0 ? (
            <div className="glass-effect" style={{ padding: '2.5rem', borderRadius: '16px', textAlign: 'center' }}>
              <p style={{ color: 'var(--text-muted)' }}>No BNPL plans yet. Apply above to get started.</p>
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
              {orders.map(order => (
                <div key={order.orderId} className="glass-effect" style={{ borderRadius: '16px', overflow: 'hidden' }}>
                  {/* Order Header */}
                  <div
                    style={{ padding: '1.25rem 1.5rem', cursor: 'pointer', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}
                    onClick={() => setExpandedOrder(expandedOrder === order.orderId ? null : order.orderId)}
                  >
                    <div style={{ display: 'flex', gap: '2rem', alignItems: 'center' }}>
                      <div>
                        <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Order #{order.orderId}</p>
                        <p style={{ fontWeight: '600', fontSize: '1.1rem' }}>₹{parseFloat(order.totalAmount).toLocaleString('en-IN')}</p>
                      </div>
                      <div>
                        <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Monthly EMI</p>
                        <p style={{ fontWeight: '600' }}>₹{parseFloat(order.emiAmount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</p>
                      </div>
                      <div>
                        <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Tenure</p>
                        <p style={{ fontWeight: '600' }}>{order.tenureMonths} months</p>
                      </div>
                      <div>
                        <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Interest</p>
                        <p style={{ fontWeight: '600' }}>{parseFloat(order.interestRate)}% p.a.</p>
                      </div>
                    </div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                      <span style={{
                        padding: '0.3rem 0.75rem', borderRadius: '20px', fontSize: '0.75rem', fontWeight: '600',
                        background: `${getStatusColor(order.status)}20`, color: getStatusColor(order.status)
                      }}>
                        {order.status}
                      </span>
                      <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>
                        {expandedOrder === order.orderId ? '▲' : '▼'}
                      </span>
                    </div>
                  </div>

                  {/* EMI Schedule (expanded) */}
                  {expandedOrder === order.orderId && order.emiSchedule && (
                    <div style={{ borderTop: '1px solid var(--surface-border)', padding: '1rem 1.5rem' }}>
                      <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                        EMI Schedule · Total Payable: ₹{parseFloat(order.totalPayable).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                      </p>
                      <div style={{ overflowX: 'auto' }}>
                        <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.875rem' }}>
                          <thead>
                            <tr style={{ color: 'var(--text-muted)' }}>
                              <th style={{ textAlign: 'left', padding: '0.5rem 0.75rem', fontWeight: '500' }}>#</th>
                              <th style={{ textAlign: 'left', padding: '0.5rem 0.75rem', fontWeight: '500' }}>Due Date</th>
                              <th style={{ textAlign: 'right', padding: '0.5rem 0.75rem', fontWeight: '500' }}>Amount</th>
                              <th style={{ textAlign: 'center', padding: '0.5rem 0.75rem', fontWeight: '500' }}>Status</th>
                            </tr>
                          </thead>
                          <tbody>
                            {order.emiSchedule.map(emi => (
                              <tr key={emi.emiNumber} style={{ borderTop: '1px solid var(--surface-border)' }}>
                                <td style={{ padding: '0.6rem 0.75rem', color: 'var(--text-muted)' }}>{emi.emiNumber}</td>
                                <td style={{ padding: '0.6rem 0.75rem' }}>{new Date(emi.dueDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}</td>
                                <td style={{ padding: '0.6rem 0.75rem', textAlign: 'right', fontWeight: '500' }}>
                                  ₹{parseFloat(emi.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}
                                </td>
                                <td style={{ padding: '0.6rem 0.75rem', textAlign: 'center' }}>
                                  <span style={{
                                    padding: '0.2rem 0.6rem', borderRadius: '12px', fontSize: '0.75rem', fontWeight: '600',
                                    background: `${getStatusColor(emi.status)}20`, color: getStatusColor(emi.status)
                                  }}>
                                    {emi.status}
                                  </span>
                                </td>
                              </tr>
                            ))}
                          </tbody>
                        </table>
                      </div>
                    </div>
                  )}
                </div>
              ))}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default Dashboard;
