import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { addStaff } from "../../services/UserService";
import Sidebar from "../../components/sidebar/Sidebar";
import Navbar from "../../components/navbar/Navbar";
import "../../pages/new/new.scss";

const AddStaff = () => {
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [formData, setFormData] = useState({
    fullName: '',
    email: '',
    password: '',
    phone: '',
    role: 'STAFF',
    address: '',
    city: '',
    district: '',
    ward: ''
  });

  const handleInput = (e) => {
    const { id, value } = e.target;
    setFormData({ ...formData, [id]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    // Validation
    if (!formData.fullName || !formData.email || !formData.password || !formData.phone) {
      alert('Please fill in all required fields!');
      return;
    }

    if (formData.password.length < 6) {
      alert('Password must be at least 6 characters long!');
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(formData.email)) {
      alert('Please enter a valid email address!');
      return;
    }

    setLoading(true);
    try {
      await addStaff(formData);
      alert('Staff member added successfully!');
      navigate('/staff');
    } catch (error) {
      console.error("Error adding staff:", error);
      if (error.response && error.response.data) {
        alert(`Failed to add staff: ${error.response.data.message || 'Unknown error'}`);
      } else {
        alert('Failed to add staff member!');
      }
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="new">
      <Sidebar />
      <div className="newContainer">
        <Navbar />
        <div className="top">
          <h1>Add New Staff Member</h1>
        </div>
        <div className="bottom">
          <div className="right">
            <form onSubmit={handleSubmit}>
              <div className="formInput">
                <label>Full Name *</label>
                <input 
                  id="fullName"
                  type="text" 
                  placeholder="John Doe" 
                  value={formData.fullName}
                  onChange={handleInput}
                  required
                />
              </div>

              <div className="formInput">
                <label>Email *</label>
                <input 
                  id="email"
                  type="email" 
                  placeholder="john.doe@example.com" 
                  value={formData.email}
                  onChange={handleInput}
                  required
                />
              </div>

              <div className="formInput">
                <label>Password *</label>
                <input 
                  id="password"
                  type="password" 
                  placeholder="Minimum 6 characters" 
                  value={formData.password}
                  onChange={handleInput}
                  required
                  minLength={6}
                />
              </div>

              <div className="formInput">
                <label>Phone *</label>
                <input 
                  id="phone"
                  type="tel" 
                  placeholder="+84 123 456 789" 
                  value={formData.phone}
                  onChange={handleInput}
                  required
                />
              </div>

              <div className="formInput">
                <label>Role *</label>
                <select 
                  id="role"
                  value={formData.role}
                  onChange={handleInput}
                  style={{ 
                    width: '100%', 
                    padding: '10px', 
                    borderRadius: '5px',
                    border: '1px solid gray'
                  }}
                  required
                >
                  <option value="STAFF">Staff</option>
                  <option value="MANAGER">Manager</option>
                  <option value="ADMIN">Admin</option>
                </select>
              </div>

              <div className="formInput">
                <label>Address</label>
                <input 
                  id="address"
                  type="text" 
                  placeholder="123 Main Street" 
                  value={formData.address}
                  onChange={handleInput}
                />
              </div>

              <div className="formInput">
                <label>City</label>
                <input 
                  id="city"
                  type="text" 
                  placeholder="Ho Chi Minh" 
                  value={formData.city}
                  onChange={handleInput}
                />
              </div>

              <div className="formInput">
                <label>District</label>
                <input 
                  id="district"
                  type="text" 
                  placeholder="District 1" 
                  value={formData.district}
                  onChange={handleInput}
                />
              </div>

              <div className="formInput">
                <label>Ward</label>
                <input 
                  id="ward"
                  type="text" 
                  placeholder="Ward 1" 
                  value={formData.ward}
                  onChange={handleInput}
                />
              </div>

              <button type="submit" disabled={loading}>
                {loading ? 'Adding...' : 'Add Staff'}
              </button>
              
              <button 
                type="button" 
                onClick={() => navigate('/staff')}
                style={{ 
                  marginLeft: '10px', 
                  backgroundColor: '#6c757d' 
                }}
              >
                Cancel
              </button>
            </form>
          </div>
        </div>
      </div>
    </div>
  );
};

export default AddStaff;
