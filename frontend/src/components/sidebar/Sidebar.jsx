import "./sidebar.scss";
import DashboardIcon from "@mui/icons-material/Dashboard";
import PersonOutlineIcon from "@mui/icons-material/PersonOutline";
import LocalShippingIcon from "@mui/icons-material/LocalShipping";
import CreditCardIcon from "@mui/icons-material/CreditCard";
import StoreIcon from "@mui/icons-material/Store";
import InsertChartIcon from "@mui/icons-material/InsertChart";
import SettingsApplicationsIcon from "@mui/icons-material/SettingsApplications";
import ExitToAppIcon from "@mui/icons-material/ExitToApp";
import NotificationsNoneIcon from "@mui/icons-material/NotificationsNone";
import SettingsSystemDaydreamOutlinedIcon from "@mui/icons-material/SettingsSystemDaydreamOutlined";
import PsychologyOutlinedIcon from "@mui/icons-material/PsychologyOutlined";
import AccountCircleOutlinedIcon from "@mui/icons-material/AccountCircleOutlined";
import ChatIcon from '@mui/icons-material/Chat';
import ArticleIcon from '@mui/icons-material/Article';
import QueueIcon from '@mui/icons-material/Queue';
import SlideshowIcon from '@mui/icons-material/Slideshow';
import AssuredWorkloadIcon from '@mui/icons-material/AssuredWorkload';
import MenuBookIcon from '@mui/icons-material/MenuBook';
import GroupIcon from '@mui/icons-material/Group';
import BadgeIcon from '@mui/icons-material/Badge';
import { Link, useLocation } from "react-router-dom";
import { DarkModeContext } from "../../context/darkModeContext";
import { useContext } from "react";
import { AuthContext } from "../../context/AuthContext";

const Sidebar = () => {
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
        <Link to="/admin" style={{ textDecoration: "none" }}>
          <MenuBookIcon className="logo-icon" />
          <span className="logo">Book Store Admin</span>
        </Link>
      </div>
      <hr />
      <div className="center">
        <ul>
          <p className="title">MAIN</p>
          <Link to="/admin" style={{ textDecoration: "none" }}>
            <li className={isActive("/admin") ? "active" : ""}>
              <DashboardIcon className="icon" />
              <span>Dashboard</span>
            </li>
          </Link>
          
          <p className="title">MANAGEMENT</p>
          <Link to="/authors" style={{ textDecoration: "none" }}>
            <li className={isActive("/authors") ? "active" : ""}>
              <PersonOutlineIcon className="icon" />
              <span>Authors</span>
            </li>
          </Link>
          <Link to="/products" style={{ textDecoration: "none" }}>
            <li className={isActive("/products") ? "active" : ""}>
              <StoreIcon className="icon" />
              <span>Products</span>
            </li>
          </Link>
          <Link to="/orders" style={{ textDecoration: "none" }}>
            <li className={isActive("/orders") ? "active" : ""}>
              <CreditCardIcon className="icon" />
              <span>Orders</span>
            </li>
          </Link>
          <Link to="/collections" style={{ textDecoration: 'none' }}>
            <li className={isActive("/collections") ? "active" : ""}>
              <QueueIcon className="icon" />
              <span>Collections</span>
            </li>
          </Link>
          <Link to="/publishers" style={{ textDecoration: 'none' }}>
            <li className={isActive("/publishers") ? "active" : ""}>
              <AssuredWorkloadIcon className="icon" />
              <span>Publishers</span>
            </li>
          </Link>
          
          <p className="title">CONTENT</p>
          <Link to="/sliders" style={{ textDecoration: 'none' }}>
            <li className={isActive("/sliders") ? "active" : ""}>
              <SlideshowIcon className="icon" />
              <span>Sliders</span>
            </li>
          </Link>
          <Link to='/posts' style={{ textDecoration: 'none' }}>
            <li className={isActive("/posts") ? "active" : ""}>
              <ArticleIcon className="icon" />
              <span>Posts</span>
            </li>
          </Link>
           <Link to='/post-categories' style={{ textDecoration: 'none' }}>
            <li className={isActive("/post-categories") ? "active" : ""}>
              <PsychologyOutlinedIcon className="icon" />
              <span>Post Categories</span>
            </li>
          </Link>
          <Link to="/feedbacks" style={{ textDecoration: 'none' }}>
            <li className={isActive("/feedbacks") ? "active" : ""}>
              <ChatIcon className="icon" />
              <span>Feedbacks</span>
            </li>
          </Link>

          <p className="title">USERS</p>
          <Link to="/customers" style={{ textDecoration: 'none' }}>
            <li className={isActive("/customers") ? "active" : ""}>
              <GroupIcon className="icon" />
              <span>Customers</span>
            </li>
          </Link>
          <Link to="/staff" style={{ textDecoration: 'none' }}>
            <li className={isActive("/staff") ? "active" : ""}>
              <BadgeIcon className="icon" />
              <span>Staff</span>
            </li>
          </Link>

          <p className="title">ACCOUNT</p>
          <Link to="/changePass" style={{ textDecoration: 'none' }}>
            <li className={isActive("/changePass") ? "active" : ""}>
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
            className="colorOption light"
            onClick={() => dispatch({ type: "LIGHT" })}
            title="Light Mode"
          ></div>
          <div
            className="colorOption dark"
            onClick={() => dispatch({ type: "DARK" })}
            title="Dark Mode"
          ></div>
        </div>
      </div>
    </div>
  );
};

export default Sidebar;
