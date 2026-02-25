const AuthReducer = (state, action) => {
    switch (action.type) {
      case "LOGIN": {
        return {
          ...state,
          currentUser: action.payload,
        };
      }
      case "SET_USER_INFO": {
        return {
          ...state,
          userInfo: action.payload,
        };
      }
      case "LOGOUT": {
        return {
          currentUser: null,
          userInfo: null,
        };
      }
      default:
        return state;
    }
  };
  
  export default AuthReducer;
  