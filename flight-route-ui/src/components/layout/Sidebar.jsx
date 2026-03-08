import { NavLink, useNavigate } from "react-router-dom";
import { useAuth } from "../../hooks/useAuth";

export default function Sidebar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate("/login");
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-top">
        <div className="sidebar-brand">
          <div className="sidebar-brand-title">Route Planner</div>
          <div className="sidebar-brand-role">
            {user?.role === "ADMIN" ? "Admin Panel" : "Agency User"}
          </div>
        </div>

        <nav className="sidebar-menu">
          {user?.role === "ADMIN" && (
            <>
              <NavLink to="/locations" className="menu-link">
                Locations
              </NavLink>

              <NavLink to="/transportations" className="menu-link">
                Transportations
              </NavLink>
            </>
          )}

          <NavLink to="/routes" className="menu-link">
            Routes
          </NavLink>
        </nav>
      </div>

      <button type="button" className="logout-button" onClick={handleLogout}>
        <span className="logout-icon">↩</span>
        <span>Logout</span>
      </button>
    </aside>
  );
}