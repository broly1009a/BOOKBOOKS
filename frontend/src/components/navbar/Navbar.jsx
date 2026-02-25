import "./navbar.scss";

import SearchOutlinedIcon from "@mui/icons-material/SearchOutlined";
import LanguageOutlinedIcon from "@mui/icons-material/LanguageOutlined";
import DarkModeOutlinedIcon from "@mui/icons-material/DarkModeOutlined";
import LightModeOutlinedIcon from "@mui/icons-material/LightModeOutlined";
import FullscreenExitOutlinedIcon from "@mui/icons-material/FullscreenExitOutlined";
import NotificationsNoneOutlinedIcon from "@mui/icons-material/NotificationsNoneOutlined";
import ChatBubbleOutlineOutlinedIcon from "@mui/icons-material/ChatBubbleOutlineOutlined";
import ListOutlinedIcon from "@mui/icons-material/ListOutlined";
import { DarkModeContext } from "../../context/darkModeContext";
import { useContext, useState, useEffect, useRef } from "react";
import { AuthContext } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

const Navbar = () => {
  const { dispatch, darkMode } = useContext(DarkModeContext);
  const { currentUser, userInfo, dispatch: authDispatch } = useContext(AuthContext);
  const [showNotifications, setShowNotifications] = useState(false);
  const [showProfileDropdown, setShowProfileDropdown] = useState(false);
  const navigate = useNavigate();
  const profileRef = useRef(null);


  const getUserRole = () => {
  
    if (userInfo?.role) {
      return userInfo.role;
    }
    
    if (currentUser?.authorities && Array.isArray(currentUser.authorities)) {
      const role = currentUser.authorities[0]?.authority || "";
      return role.startsWith('ROLE_') ? role.substring(5) : role;
    }
    return "User";
  };


  const getDisplayName = () => {
    if (userInfo?.fullName) return userInfo.fullName;
    if (userInfo?.username) return userInfo.username;
    if (currentUser?.sub) return currentUser.sub;
    return "User";
  };

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (profileRef.current && !profileRef.current.contains(event.target)) {
        setShowProfileDropdown(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, []);

  const handleLogout = () => {
    authDispatch({ type: "LOGOUT" });
    localStorage.removeItem('authToken');
    navigate('/');
  };

  const handleProfileClick = () => {
    navigate('/account-detail');
    setShowProfileDropdown(false);
  };

  return (
    <div className="navbar">
      <div className="wrapper">
        <div className="search">
          <input type="text" placeholder="Search..." />
          <SearchOutlinedIcon className="search-icon" />
        </div>
        <div className="items">
          <div className="item" title="Toggle Dark Mode">
            {darkMode ? (
              <LightModeOutlinedIcon
                className="icon"
                onClick={() => dispatch({ type: "LIGHT" })}
              />
            ) : (
              <DarkModeOutlinedIcon
                className="icon"
                onClick={() => dispatch({ type: "DARK" })}
              />
            )}
          </div>
          
          <div className="item notification-item" onClick={() => setShowNotifications(!showNotifications)}>
            <NotificationsNoneOutlinedIcon className="icon" />
            <div className="counter">3</div>
            {showNotifications && (
              <div className="notification-dropdown">
                <p className="notification-title">Notifications</p>
                <div className="notification-list">
                  <div className="notification-item-content">
                    <span>New order received</span>
                    <small>2 minutes ago</small>
                  </div>
                  <div className="notification-item-content">
                    <span>Product stock low</span>
                    <small>1 hour ago</small>
                  </div>
                  <div className="notification-item-content">
                    <span>New feedback submitted</span>
                    <small>3 hours ago</small>
                  </div>
                </div>
              </div>
            )}
          </div>
          
          <div className="item">
            <ChatBubbleOutlineOutlinedIcon className="icon" />
            <div className="counter">2</div>
          </div>
          
          <div className="item profile-item" ref={profileRef} onClick={() => setShowProfileDropdown(!showProfileDropdown)}>
            <img
              src={userInfo?.avatar || "https://images.pexels.com/photos/941693/pexels-photo-941693.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500"}
              alt="User Avatar"
              className="avatar"
            />
            <div className="profile-info">
              <span className="username">{getDisplayName()}</span>
              <span className="role">{getUserRole()}</span>
            </div>
            {showProfileDropdown && (
              <div className="profile-dropdown">
                <div className="profile-dropdown-item" onClick={handleProfileClick}>
                  <span>Profile</span>
                </div>
                <div className="profile-dropdown-item" onClick={handleLogout}>
                  <span>Logout</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default Navbar;
