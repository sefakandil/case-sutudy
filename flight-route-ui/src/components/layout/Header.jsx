import { useAuth } from "../../hooks/useAuth";

export default function Header() {
  const { user } = useAuth();

  return (
    <header className="header">
      <div className="header-left">Flight Route System</div>
      <div className="header-right">
        <span>
          {user?.username} - {user?.role}
        </span>
      </div>
    </header>
  );
}