import { useState, useContext } from "react"
import "./login.scss"
import { useNavigate } from "react-router-dom"
import { jwtDecode } from "jwt-decode";
import { login } from "../../service/UserService";
import { AuthContext } from "../../context/AuthContext"
import { useCookies } from 'react-cookie';

const Login = () => {
  const [error, setError] = useState(false)
  const [email, setEmail] = useState("")
  const [password, setPassword] = useState("")
  const { dispatch } = useContext(AuthContext)
  const [cookies, setCookie] = useCookies(['authToken']);
  const navigate = useNavigate()
  
  const handleLogin = (e) => {
    e.preventDefault()
    setError(false) 
    
    login({ email, password}).then(res => {
      const user = jwtDecode(res.data.token)
      
      if (!user.authorities || !Array.isArray(user.authorities) || user.authorities.length === 0) {
        console.error("No authorities found")
        setError(true)
        return
      }
      
      let userRole = user.authorities[0].authority;
      
      // Remove 'ROLE_' prefix if present
      if (userRole && userRole.startsWith('ROLE_')) {
        userRole = userRole.substring(5);
      }
      
      // Check if user has admin, manager, or sale role
      if (!['ADMIN', 'MANAGER', 'SALE'].includes(userRole)) {
        console.error("User does not have admin panel access:", userRole)
        setError(true)
        return
      }
      
      console.log("Login successful, user role:", userRole)
      
      // Lưu user vào localStorage ngay lập tức trước khi redirect
      localStorage.setItem("user", JSON.stringify(user))
      
      const expiresDate = new Date(user.exp * 1000);
      setCookie('authToken', res.data.token, { 
        path: '/',
        expires: expiresDate
      })
      
      // Lưu AuthContext
      dispatch({ type: "LOGIN", payload: user})
      
      // Redirect based on role
      let redirectPath = "/admin";
      switch(userRole) {
        case 'ADMIN':
          redirectPath = "/admin";
          break;
        case 'MANAGER':
          redirectPath = "/manager";
          break;
        case 'SALE':
          redirectPath = "/sale";
          break;
        default:
          redirectPath = "/";
      }
      
      console.log("Cookie set, redirecting to", redirectPath)
      
      // Đợi một chút để đảm bảo cookie và localStorage được lưu
      setTimeout(() => {
        window.location.href = redirectPath
      }, 100);
    })
    .catch(err => {
      console.error("Login error:", err)
      setError(true)
    })
  }

  return (
    <div className="login">
      <form onSubmit={handleLogin}>
        <input type="email"  placeholder="email" onChange={e => setEmail(e.target.value)}/>
        <input type="password" placeholder="password" onChange={e => setPassword(e.target.value)}/>
        <button>Login</button>
        {error && <span>Wrong email or password!</span>}
      </form>
    </div>
  )
}

export default Login