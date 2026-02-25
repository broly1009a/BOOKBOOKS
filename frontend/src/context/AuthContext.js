import { createContext, useEffect, useReducer } from "react";
import AuthReducer from "./AuthReducer";

const INITIAL_STATE = {
  currentUser: JSON.parse(localStorage.getItem("user")) || null,
  userInfo: JSON.parse(localStorage.getItem("userInfo")) || null,
};

export const AuthContext = createContext(INITIAL_STATE);

export const AuthContextProvider = ({ children }) => {
  const [state, dispatch] = useReducer(AuthReducer, INITIAL_STATE);

  useEffect(() => {
    localStorage.setItem("user", JSON.stringify(state.currentUser));
  }, [state.currentUser]);

  useEffect(() => {
    localStorage.setItem("userInfo", JSON.stringify(state.userInfo));
  }, [state.userInfo]);

  console.log("AuthContext currentUser:", state.currentUser);
  console.log("AuthContext userInfo:", state.userInfo);
  return (
    <AuthContext.Provider value={{ currentUser: state.currentUser, userInfo: state.userInfo, dispatch }}>
      {children}
    </AuthContext.Provider>
  );
};
