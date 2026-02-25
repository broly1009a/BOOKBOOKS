import React, { useState, useContext } from 'react'
import { Container, Row, Col, Form, Button } from 'react-bootstrap'
import Breadscrumb from '../Breadscrumb'
import { login } from '../../services/UserService'
import { useNavigate } from 'react-router-dom'
import { jwtDecode } from "jwt-decode";
import { AuthContext } from "../../context/AuthContext"

const Login = ({ setCookies }) => {
    const [data, setData] = useState({
        email: '',
        password: ''
    })
    const [error, setError] = useState({ formatError: false, loginError: false })
    const { dispatch } = useContext(AuthContext)
    const navigate = useNavigate()

    const handleSubmit = (e) => {
        e.preventDefault()
        const email_regex = /([a-zA-Z0-9._-]+@[a-zA-Z0-9._-]+\.[a-zA-Z0-9_-]+)/g
        const password_regex = /[a-zA-Z\d]{6,}$/g
        if (data.email === '' || data.password === '' || !email_regex.test(data.email) || !password_regex.test(data.password)) {
            setError((prevData) => ({ ...prevData, loginError: true }))
            return;
        }
        else {
            setError((prevData) => ({ ...prevData, loginError: false }))
        }
        let account = { email: data.email, password: data.password }
        login(account).then(res => {
            const user = jwtDecode(res.data.token)
            
            // Lưu vào cookie
            setCookies('authToken', res.data.token, { path: '/' })
            
            // Lấy role từ authorities
            let userRole = null;
            if (user.authorities && user.authorities.length > 0) {
                userRole = user.authorities[0]?.authority;
                // Remove 'ROLE_' prefix if present
                if (userRole && userRole.startsWith('ROLE_')) {
                    userRole = userRole.substring(5);
                }
            }
            
            // Dispatch login cho ADMIN
            if (userRole === "ADMIN") {
                dispatch({ type: "LOGIN", payload: user})
            }
            
            // Navigate theo role
            switch(userRole) {
                case 'ADMIN':
                    navigate('/admin')
                    break;
                case 'MANAGER':
                    navigate('/manager')
                    break;
                case 'SALE':
                    navigate('/sale')
                    break;
                default:
                    navigate('/')
                    break;
            }
        })
            .catch(err => {
                setError((prevData) => ({ ...prevData, loginError: true }))
            })
    }

    const handleChange = (e) => {
        const { name, value } = e.target
        setData({
            ...data,
            [name]: value
        })
    }

    return (
        <div id='page-wrapper' className='mb-5'>
            <Breadscrumb label={'Login'} />
            <Container className='mt-5' style={{display: 'flex', justifyContent: 'center'}}>
                
                        <div className='form-vertical'>
                            <h4 style={{textAlign: 'center'}}>
                                Đăng nhập
                            </h4>
                            <Form onSubmit={handleSubmit}>
                                <Form.Group className="mb-3" controlId="exampleForm.ControlInput1">
                                    <Form.Control type="email" placeholder="Email" name='email' value={data.email} onChange={handleChange} />
                                </Form.Group>
                                <Form.Group className="mb-3" controlId="exampleForm.ControlTextarea1">
                                    <Form.Control type="password" placeholder="Mật khẩu" name='password' value={data.password} onChange={handleChange} />
                                </Form.Group>
                                <button className='btn-signin' type="submit">
                                    Đăng nhập
                                </button>
                                <a href="/forgot-password" className='ms-3'>Quên mật khẩu</a>
                            </Form>
                            {error.loginError && (
                                <div className='error-message'>
                                    <p className='text-danger'>Email hoặc mật khẩu không đúng</p>
                                </div>
                            )}
                        </div>
            </Container>
        </div>
    )
}

export default Login