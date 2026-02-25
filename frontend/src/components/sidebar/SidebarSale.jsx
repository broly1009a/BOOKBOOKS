import "./sidebar.scss";
import DashboardIcon from "@mui/icons-material/Dashboard";
import CreditCardIcon from "@mui/icons-material/CreditCard";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import ChatIcon from '@mui/icons-material/Chat';
import GroupIcon from '@mui/icons-material/Group';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import { Link, useLocation } from "react-router-dom";
import { DarkModeContext } from "../../context/darkModeContext";
import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";

const SidebarSale = () => {
  const { dispatch } = useContext(DarkModeContext);
  const authContext = useContext(AuthContext);
  const location = useLocation();

  const logout = () => {
    authContext.dispatch({ type: "LOGOUT" });
    localStorage.removeItem("user");
    document.cookie = "authToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    window.location.href = "/admin/login";
  }

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  }

  return (
    <div className="sidebar">
      <div className="top">
        <Link to="/sale" style={{ textDecoration: "none" }}>
          <MenuBookIcon className="logo-icon" />
          <span className="logo">Sale Panel</span>
        </Link>
      </div>
      <hr />
      <div className="center">
        <ul>
          <p className="title">MAIN</p>
          <Link to="/sale" style={{ textDecoration: "none" }}>
            <li className={isActive("/sale") && location.pathname === "/sale" ? "active" : ""}>
              <DashboardIcon className="icon" />
              <span>Dashboard</span>
            </li>
          </Link>
          
          <p className="title">MANAGEMENT</p>
          <Link to="/sale/orders" style={{ textDecoration: "none" }}>
            <li className={isActive("/sale/orders") ? "active" : ""}>
              <CreditCardIcon className="icon" />
              <span>Orders</span>
            </li>
          </Link>
          
          <p className="title">CUSTOMERS</p>
          <Link to="/sale/customers" style={{ textDecoration: 'none' }}>
            <li className={isActive("/sale/customers") ? "active" : ""}>
              <GroupIcon className="icon" />
              <span>Customers</span>
            </li>
          </Link>
          
          <p className="title">FEEDBACK</p>
          <Link to="/sale/feedbacks" style={{ textDecoration: 'none' }}>
            <li className={isActive("/sale/feedbacks") ? "active" : ""}>
              <ChatIcon className="icon" />
              <span>Feedbacks</span>
            </li>
          </Link>

          <p className="title">ACCOUNT</p>
          <Link to="/sale/change-password" style={{ textDecoration: 'none' }}>
            <li className={isActive("/sale/change-password") ? "active" : ""}>
              <AccountCircleOutlinedIcon className="icon" />
              <span>Change Password</span>
            </li>
          </Link>
          <li onClick={logout} className="logout-item">
            <ExitToAppIcon className="icon" />
            <span>Logout</span>
          </li>
        </ul>
      </div>
      <div className="bottom">
        <p className="theme-title">Theme</p>
        <div className="theme-options">
          <div
            className="colorOption"
            onClick={() => dispatch({ type: "LIGHT" })}
          ></div>
          <div
            className="colorOption"
            onClick={() => dispatch({ type: "DARK" })}
          ></div>
        </div>
      </div>
    </div>
  );
};

export default SidebarSale;
