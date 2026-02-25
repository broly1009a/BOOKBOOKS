import React from 'react';
import Sidebar from '../../components/sidebar/Sidebar';
import Navbar from '../../components/navbar/Navbar';
import ChangePassword from './changePass';

const AdminChangePassword = () => {
    return (
        <div className="list">
            <Sidebar />
            <div className="listContainer">
                <Navbar />
                <ChangePassword />
            </div>
        </div>
    );
};

export default AdminChangePassword;
