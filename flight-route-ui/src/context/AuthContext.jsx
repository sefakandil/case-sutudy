import { createContext, useState } from "react";
import { loginRequest } from "../api/authApi";

export const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(
    JSON.parse(localStorage.getItem("user"))
  );

  const login = async (username, password) => {
    const data = await loginRequest(username, password);

    console.log("LOGIN RESPONSE:", data);

    const token = data.token || data.accessToken || data.jwt;
    const role =
      data.role ||
      data.user?.role ||
      data.roles?.[0] ||
      data.authorities?.[0]?.replace("ROLE_", "");

    const normalizedUser = {
      username: data.username || data.userName || data.user?.username || username,
      role: role || "AGENCY",
    };

    if (!token) {
      throw new Error("Token bulunamadı");
    }

    localStorage.setItem("token", token);
    localStorage.setItem("user", JSON.stringify(normalizedUser));
    setUser(normalizedUser);
  };

  const logout = () => {
    localStorage.removeItem("token");
    localStorage.removeItem("user");
    setUser(null);
  };

  return (
    <AuthContext.Provider
      value={{
        user,
        isAuthenticated: !!user,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  );
}