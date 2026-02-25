import React from 'react';
import SidebarManager from '../../components/sidebar/SidebarManager';
import Navbar from '../../components/navbar/Navbar';
import ChangePassword from './changePass';

const ManagerChangePassword = () => {
    return (
        <div className="list">
            <SidebarManager />
            <div className="listContainer">
                <Navbar />
                <ChangePassword />
            </div>
        </div>
    );
};

export default ManagerChangePassword;
