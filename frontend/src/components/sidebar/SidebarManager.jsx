import "./sidebar.scss";
import DashboardIcon from "@mui/icons-material/Dashboard";
import StoreIcon from "@mui/icons-material/Store";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import ChatIcon from '@mui/icons-material/Chat';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import { Link, useLocation } from "react-router-dom";
import { DarkModeContext } from "../../context/darkModeContext";
import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";

const SidebarManager = () => {
  const { dispatch } = useContext(DarkModeContext);
  const authContext = useContext(AuthContext);
  const location = useLocation();

  const logout = () => {
    authContext.dispatch({ type: "LOGOUT" });
    localStorage.removeItem("user");
    document.cookie = "authToken=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/;";
    window.location.href = "/";
  }

  const isActive = (path) => {
    return location.pathname === path || location.pathname.startsWith(path + '/');
  }

  return (
    <div className="sidebar">
      <div className="top">
        <Link to="/manager" style={{ textDecoration: "none" }}>
          <MenuBookIcon className="logo-icon" />
          <span className="logo">Manager Panel</span>
        </Link>
      </div>
      <hr />
      <div className="center">
        <ul>
          <p className="title">MAIN</p>
          <Link to="/manager" style={{ textDecoration: "none" }}>
            <li className={isActive("/manager") && location.pathname === "/manager" ? "active" : ""}>
              <DashboardIcon className="icon" />
              <span>Dashboard</span>
            </li>
          </Link>
          
          <p className="title">MANAGEMENT</p>
          <Link to="/manager/products" style={{ textDecoration: "none" }}>
            <li className={isActive("/manager/products") ? "active" : ""}>
              <StoreIcon className="icon" />
              <span>Products</span>
            </li>
          </Link>
          
          <p className="title">FEEDBACK</p>
          <Link to="/manager/feedbacks" style={{ textDecoration: 'none' }}>
            <li className={isActive("/manager/feedbacks") ? "active" : ""}>
              <ChatIcon className="icon" />
              <span>Feedbacks</span>
            </li>
          </Link>

          <p className="title">ACCOUNT</p>
          <Link to="/manager/change-password" style={{ textDecoration: 'none' }}>
            <li className={isActive("/manager/change-password") ? "active" : ""}>
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

export default SidebarManager;
