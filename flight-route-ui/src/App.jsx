import { BrowserRouter, Navigate, Route, Routes } from "react-router-dom";
import LoginPage from "./pages/LoginPage";
import RoutesPage from "./pages/RoutesPage";
import LocationsPage from "./pages/LocationsPage";
import TransportationsPage from "./pages/TransportationsPage";
import NotFoundPage from "./pages/NotFoundPage";
import ProtectedRoute from "./components/common/ProtectedRoute";
import MainLayout from "./components/layout/MainLayout";
import GlobalErrorBoundary from "./error/GlobalErrorBoundary";

export default function App() {
  return (
    <GlobalErrorBoundary>
      <BrowserRouter>
        <Routes>
          <Route path="/login" element={<LoginPage />} />

          <Route element={<ProtectedRoute />}>
            <Route element={<MainLayout />}>
              <Route path="/routes" element={<RoutesPage />} />

              <Route element={<ProtectedRoute roles={["ADMIN"]} />}>
                <Route path="/locations" element={<LocationsPage />} />
                <Route path="/transportations" element={<TransportationsPage />}
                />
              </Route>
            </Route>
          </Route>

          <Route path="/" element={<Navigate to="/routes" replace />} />
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
    </GlobalErrorBoundary>
  );
}
