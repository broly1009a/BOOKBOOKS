import { Navigate } from "react-router-dom";
import { useCookies } from 'react-cookie';
import { jwtDecode } from "jwt-decode";
import { useContext } from 'react';
import { AuthContext } from "../context/AuthContext";

const RoleBasedRoute = ({ children, allowedRoles }) => {
  const [cookies] = useCookies([]);
  const { currentUser } = useContext(AuthContext);
  const token = cookies.authToken;
  
  let userRole = null;
  let isAuthenticated = false;

  if (token) {
    try {
      const decodedToken = jwtDecode(token);
      const authorities = decodedToken.authorities;
      
      console.log("Decoded Token:", decodedToken);
      console.log("Authorities:", authorities);
      
      if (authorities && authorities.length > 0) {
        userRole = authorities[0]?.authority;
        // Remove 'ROLE_' prefix if present
        if (userRole && userRole.startsWith('ROLE_')) {
          userRole = userRole.substring(5); // Remove 'ROLE_' prefix
        }
      }
      
      console.log("User Role:", userRole);
      
      isAuthenticated = true;
    } catch (error) {
      console.error("Invalid token:", error);
      return <Navigate to="/" />;
    }
  }

  if (!isAuthenticated) {
    return <Navigate to="/" />;
  }

  // Check if user role is in allowed roles
  if (allowedRoles && !allowedRoles.includes(userRole)) {
    // Redirect to their dashboard based on role
    switch(userRole) {
      case 'ADMIN':
        return <Navigate to="/admin" />;
      case 'MANAGER':
        return <Navigate to="/manager" />;
      case 'SALE':
        return <Navigate to="/sale" />;
      default:
        return <Navigate to="/" />;
    }
  }

  return children;
};

export default RoleBasedRoute;
