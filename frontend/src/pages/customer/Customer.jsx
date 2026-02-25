import React from 'react';
import Sidebar from "../../components/sidebar/Sidebar";
import Navbar from "../../components/navbar/Navbar";
import CustomerList from "../../components/User/CustomerList";
import "../list/list.scss";

const Customer = () => {
  return (
    <div className="list">
      <Sidebar />
      <div className="listContainer">
        <Navbar />
        <CustomerList />
      </div>
    </div>
  );
};

export default Customer;
