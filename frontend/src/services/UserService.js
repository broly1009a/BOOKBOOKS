import axios from "axios";

const ACCOUNT_BASE_URL = "http://localhost:8081/api/v1/auth/";

const createAccount = (account) => {
    return axios.post(ACCOUNT_BASE_URL + 'register', account);
}

const login = (account) => {
    return axios.post(ACCOUNT_BASE_URL + 'authenticate', account);
}

const getUserInfoByEmail = (email) => {
    return axios.get(`http://localhost:8081/api/v1/user/by-email/${email}`);
}

const updateUser = (profile) => {
    return axios.put("http://localhost:8081/api/v1/user", profile);
}

const forgetPassword = (email) => {
    return axios.post(ACCOUNT_BASE_URL + 'forgot-password', email);
}

const resetPassword = (resetData) => {
    return axios.post(ACCOUNT_BASE_URL + 'reset-password', resetData);
}

const activateAccount = (token) => {
    return axios.post("http://localhost:8081/api/v1/auth/activation", token)
}

const changePassword = (data) => {
    return axios.post("http://localhost:8081/api/v1/auth/change-password", data)
}

const getCustomer = (fullName = '', state = '', page = 0, size = 5, sortBy = 'id', sortOrder = 'asc') => {
    const params = new URLSearchParams({
        page: page,
        size: size,
        sortBy: sortBy,
        sortOrder: sortOrder
    });
    
    if (fullName) params.append('fullName', fullName);
    if (state) params.append('state', state);
    
    return axios.get(`http://localhost:8081/api/v1/user/customer?${params.toString()}`);
}

const getStaff = (fullName = '', state = '', role = '', page = 0, size = 5, sortBy = 'id', sortOrder = 'asc') => {
    const params = new URLSearchParams({
        page: page,
        size: size,
        sortBy: sortBy,
        sortOrder: sortOrder
    });
    
    if (fullName) params.append('fullName', fullName);
    if (state) params.append('state', state);
    if (role) params.append('role', role);
    
    return axios.get(`http://localhost:8081/api/v1/user/staff?${params.toString()}`);
}

const setAccountState = (id, state) => {
    return axios.put(`http://localhost:8081/api/v1/user/set-account-state/${id}?state=${state}`);
}

const setRole = (id, role) => {
    return axios.put(`http://localhost:8081/api/v1/user/set-role/${id}?role=${role}`);
}

const addStaff = (registerRequest) => {
    return axios.post("http://localhost:8081/api/v1/user/staff/register", registerRequest);
}

const getAllUsers = () => {
    return axios.post("http://localhost:8081/api/v1/user/get-all");
}

export {
    createAccount, 
    login, 
    getUserInfoByEmail, 
    updateUser, 
    forgetPassword, 
    resetPassword, 
    activateAccount, 
    changePassword,
    getCustomer,
    getStaff,
    setAccountState,
    setRole,
    addStaff,
    getAllUsers
}