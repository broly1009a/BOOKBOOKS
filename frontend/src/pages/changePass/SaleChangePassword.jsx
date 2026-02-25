import React from 'react';
import SidebarSale from '../../components/sidebar/SidebarSale';
import Navbar from '../../components/navbar/Navbar';
import ChangePassword from './changePass';

const SaleChangePassword = () => {
    return (
        <div className="list">
            <SidebarSale />
            <div className="listContainer">
                <Navbar />
                <ChangePassword />
            </div>
        </div>
    );
};

export default SaleChangePassword;
