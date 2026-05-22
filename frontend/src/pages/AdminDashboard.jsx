import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import api from '../api/axiosConfig';

const AdminDashboard = () => {
  const navigate = useNavigate();
  const [stats, setStats] = useState(null);
  const [users, setUsers] = useState([]);
  const [orders, setOrders] = useState([]);
  const [activeTab, setActiveTab] = useState('overview');
  const [loading, setLoading] = useState(true);
  const [expandedOrder, setExpandedOrder] = useState(null);

  useEffect(() => {
    const token = localStorage.getItem('token');
    if (!token) { navigate('/login'); return; }
    fetchAll();
  }, []);

  const fetchAll = async () => {
    try {
      const [statsRes, usersRes, ordersRes] = await Promise.all([
        api.get('/v1/admin/stats'),
        api.get('/v1/admin/users'),
        api.get('/v1/admin/orders'),
      ]);
      setStats(statsRes.data);
      setUsers(usersRes.data);
      setOrders(ordersRes.data);
    } catch (err) {
      if (err.response?.status === 401 || err.response?.status === 403) {
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

  const getCreditColor = (score) => {
    if (score >= 750) return '#22c55e';
    if (score >= 700) return '#84cc16';
    if (score >= 650) return '#f59e0b';
    return '#ef4444';
  };

  const getStatusColor = (status) => {
    switch (status) {
      case 'PAID': case 'APPROVED': case 'COMPLETED': return '#22c55e';
      case 'OVERDUE': case 'REJECTED': return '#ef4444';
      default: return '#f59e0b';
    }
  };

  const getRoleBadge = (role) => {
    const colors = { ADMIN: '#6366f1', MERCHANT: '#f59e0b', CUSTOMER: '#94a3b8' };
    return colors[role] || '#94a3b8';
  };

  if (loading) return (
    <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: '100vh' }}>
      <p style={{ color: 'var(--text-muted)' }}>Loading admin panel...</p>
    </div>
  );

  return (
    <div className="dashboard">
      {/* Navbar */}
      <nav className="navbar glass-effect">
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <h1>FlexiPay</h1>
          <span style={{
            padding: '0.2rem 0.6rem', borderRadius: '6px', fontSize: '0.7rem',
            fontWeight: '700', background: 'rgba(99,102,241,0.2)', color: '#a5b4fc',
            letterSpacing: '0.05em'
          }}>ADMIN</span>
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <span style={{ color: 'var(--text-muted)', fontSize: '0.875rem' }}>admin@flexipay.com</span>
          <button onClick={handleLogout} className="btn-secondary">Logout</button>
        </div>
      </nav>

      <div className="content">
        {/* Header */}
        <div style={{ marginBottom: '2rem' }}>
          <h2 style={{ marginBottom: '0.25rem' }}>Admin Dashboard</h2>
          <p style={{ color: 'var(--text-muted)' }}>Full platform overview and management</p>
        </div>

        {/* Stats Cards */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '1rem', marginBottom: '2rem' }}>
          {[
            { label: 'Total Users', value: stats?.totalUsers, color: '#a5b4fc' },
            { label: 'Total Orders', value: stats?.totalOrders, color: '#a5b4fc' },
            { label: 'Active Loans', value: stats?.activeLoans, color: '#22c55e' },
            { label: 'Amount Disbursed', value: `₹${parseFloat(stats?.totalAmountDisbursed || 0).toLocaleString('en-IN')}`, color: '#f59e0b' },
            { label: 'Pending EMIs', value: stats?.pendingEmis, color: '#f59e0b' },
            { label: 'Overdue EMIs', value: stats?.overdueEmis, color: '#ef4444' },
          ].map((card) => (
            <div key={card.label} className="glass-effect" style={{ padding: '1.25rem', borderRadius: '16px' }}>
              <p style={{ color: 'var(--text-muted)', fontSize: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em', marginBottom: '0.5rem' }}>{card.label}</p>
              <p style={{ fontSize: '1.75rem', fontWeight: '700', color: card.color }}>{card.value}</p>
            </div>
          ))}
        </div>

        {/* Tabs */}
        <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.5rem', borderBottom: '1px solid var(--surface-border)', paddingBottom: '0' }}>
          {['overview', 'users', 'orders'].map(tab => (
            <button
              key={tab}
              onClick={() => setActiveTab(tab)}
              style={{
                padding: '0.6rem 1.25rem', border: 'none', cursor: 'pointer',
                background: 'transparent', fontFamily: 'inherit', fontSize: '0.875rem',
                fontWeight: activeTab === tab ? '600' : '400',
                color: activeTab === tab ? 'var(--text-main)' : 'var(--text-muted)',
                borderBottom: activeTab === tab ? '2px solid #6366f1' : '2px solid transparent',
                textTransform: 'capitalize', transition: 'all 0.2s'
              }}
            >
              {tab === 'overview' ? 'Overview' : tab === 'users' ? `Users (${users.length})` : `Orders (${orders.length})`}
            </button>
          ))}
        </div>

        {/* Overview Tab */}
        {activeTab === 'overview' && (
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem' }}>
            {/* Recent Users */}
            <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: '600', marginBottom: '1rem' }}>Recent Users</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {users.slice(0, 5).map(u => (
                  <div key={u.userId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <p style={{ fontSize: '0.875rem', fontWeight: '500' }}>{u.email}</p>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{u.role}</p>
                    </div>
                    <span style={{ fontSize: '0.875rem', fontWeight: '700', color: getCreditColor(u.creditScore) }}>
                      {u.creditScore}
                    </span>
                  </div>
                ))}
              </div>
            </div>

            {/* Recent Orders */}
            <div className="glass-effect" style={{ padding: '1.5rem', borderRadius: '16px' }}>
              <h3 style={{ fontSize: '1rem', fontWeight: '600', marginBottom: '1rem' }}>Recent Orders</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '0.75rem' }}>
                {orders.slice(0, 5).map(o => (
                  <div key={o.orderId} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div>
                      <p style={{ fontSize: '0.875rem', fontWeight: '500' }}>Order #{o.orderId}</p>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>
                        {o.tenureMonths} months · {o.interestRate}% p.a.
                      </p>
                    </div>
                    <div style={{ textAlign: 'right' }}>
                      <p style={{ fontSize: '0.875rem', fontWeight: '600' }}>₹{parseFloat(o.totalAmount).toLocaleString('en-IN')}</p>
                      <span style={{ fontSize: '0.7rem', color: getStatusColor(o.status) }}>{o.status}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>
          </div>
        )}

        {/* Users Tab */}
        {activeTab === 'users' && (
          <div className="glass-effect" style={{ borderRadius: '16px', overflow: 'hidden' }}>
            <div style={{ overflowX: 'auto' }}>
              <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.875rem' }}>
                <thead>
                  <tr style={{ background: 'rgba(255,255,255,0.03)' }}>
                    {['ID', 'Email', 'Phone', 'Credit Score', 'Monthly Income', 'Role'].map(h => (
                      <th key={h} style={{ padding: '0.875rem 1rem', textAlign: 'left', color: 'var(--text-muted)', fontWeight: '500', fontSize: '0.8rem', textTransform: 'uppercase', letterSpacing: '0.05em', borderBottom: '1px solid var(--surface-border)' }}>
                        {h}
                      </th>
                    ))}
                  </tr>
                </thead>
                <tbody>
                  {users.map(u => (
                    <tr key={u.userId} style={{ borderBottom: '1px solid var(--surface-border)' }}>
                      <td style={{ padding: '0.875rem 1rem', color: 'var(--text-muted)' }}>#{u.userId}</td>
                      <td style={{ padding: '0.875rem 1rem', fontWeight: '500' }}>{u.email}</td>
                      <td style={{ padding: '0.875rem 1rem', color: 'var(--text-muted)' }}>{u.phone}</td>
                      <td style={{ padding: '0.875rem 1rem' }}>
                        <span style={{ fontWeight: '700', color: getCreditColor(u.creditScore) }}>{u.creditScore}</span>
                      </td>
                      <td style={{ padding: '0.875rem 1rem' }}>₹{parseFloat(u.monthlyIncome || 0).toLocaleString('en-IN')}</td>
                      <td style={{ padding: '0.875rem 1rem' }}>
                        <span style={{
                          padding: '0.25rem 0.65rem', borderRadius: '12px', fontSize: '0.75rem', fontWeight: '600',
                          background: `${getRoleBadge(u.role)}20`, color: getRoleBadge(u.role)
                        }}>
                          {u.role}
                        </span>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        )}

        {/* Orders Tab */}
        {activeTab === 'orders' && (
          <div style={{ display: 'flex', flexDirection: 'column', gap: '1rem' }}>
            {orders.length === 0 ? (
              <div className="glass-effect" style={{ padding: '2.5rem', borderRadius: '16px', textAlign: 'center' }}>
                <p style={{ color: 'var(--text-muted)' }}>No orders yet.</p>
              </div>
            ) : orders.map(order => (
              <div key={order.orderId} className="glass-effect" style={{ borderRadius: '16px', overflow: 'hidden' }}>
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
                      <p style={{ fontWeight: '500' }}>₹{parseFloat(order.emiAmount || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</p>
                    </div>
                    <div>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Tenure</p>
                      <p style={{ fontWeight: '500' }}>{order.tenureMonths} months</p>
                    </div>
                    <div>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Interest</p>
                      <p style={{ fontWeight: '500' }}>{parseFloat(order.interestRate || 0)}% p.a.</p>
                    </div>
                    <div>
                      <p style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginBottom: '0.2rem' }}>Total Payable</p>
                      <p style={{ fontWeight: '500' }}>₹{parseFloat(order.totalPayable || 0).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</p>
                    </div>
                  </div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                    <span style={{
                      padding: '0.3rem 0.75rem', borderRadius: '20px', fontSize: '0.75rem', fontWeight: '600',
                      background: `${getStatusColor(order.status)}20`, color: getStatusColor(order.status)
                    }}>{order.status}</span>
                    <span style={{ color: 'var(--text-muted)' }}>{expandedOrder === order.orderId ? '▲' : '▼'}</span>
                  </div>
                </div>

                {expandedOrder === order.orderId && order.emiSchedule && (
                  <div style={{ borderTop: '1px solid var(--surface-border)', padding: '1rem 1.5rem' }}>
                    <p style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '0.75rem', textTransform: 'uppercase', letterSpacing: '0.05em' }}>
                      EMI Schedule
                    </p>
                    <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.875rem' }}>
                      <thead>
                        <tr style={{ color: 'var(--text-muted)' }}>
                          {['#', 'Due Date', 'Amount', 'Status'].map(h => (
                            <th key={h} style={{ textAlign: h === 'Amount' ? 'right' : h === 'Status' ? 'center' : 'left', padding: '0.5rem 0.75rem', fontWeight: '500' }}>{h}</th>
                          ))}
                        </tr>
                      </thead>
                      <tbody>
                        {order.emiSchedule.map(emi => (
                          <tr key={emi.emiNumber} style={{ borderTop: '1px solid var(--surface-border)' }}>
                            <td style={{ padding: '0.6rem 0.75rem', color: 'var(--text-muted)' }}>{emi.emiNumber}</td>
                            <td style={{ padding: '0.6rem 0.75rem' }}>{new Date(emi.dueDate).toLocaleDateString('en-IN', { day: 'numeric', month: 'short', year: 'numeric' })}</td>
                            <td style={{ padding: '0.6rem 0.75rem', textAlign: 'right', fontWeight: '500' }}>₹{parseFloat(emi.amount).toLocaleString('en-IN', { minimumFractionDigits: 2 })}</td>
                            <td style={{ padding: '0.6rem 0.75rem', textAlign: 'center' }}>
                              <span style={{
                                padding: '0.2rem 0.6rem', borderRadius: '12px', fontSize: '0.75rem', fontWeight: '600',
                                background: `${getStatusColor(emi.status)}20`, color: getStatusColor(emi.status)
                              }}>{emi.status}</span>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                  </div>
                )}
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
};

export default AdminDashboard;
