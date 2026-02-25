import React from 'react';
import Sidebar from "../../components/sidebar/Sidebar";
import Navbar from "../../components/navbar/Navbar";
import StaffList from "../../components/User/StaffList";
import "../list/list.scss";

const Staff = () => {
  return (
    <div className="list">
      <Sidebar />
      <div className="listContainer">
        <Navbar />
        <StaffList />
      </div>
    </div>
  );
};

export default Staff;
