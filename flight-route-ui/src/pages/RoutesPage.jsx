import { useEffect, useState } from "react";
import RouteSearchForm from "../components/routes/RouteSearchForm";
import RouteList from "../components/routes/RouteList";
import RouteDetailPanel from "../components/routes/RouteDetailPanel";
import { getAllLocations } from "../api/locationApi";
import { searchRoutes } from "../api/routeApi";

export default function RoutesPage() {
  const [locations, setLocations] = useState([]);
  const [filters, setFilters] = useState({
    origin: "",
    destination: "",
    date: "",
  });

  const [routes, setRoutes] = useState([]);
  const [selectedRoute, setSelectedRoute] = useState(null);

  const [locationsLoading, setLocationsLoading] = useState(true);
  const [routesLoading, setRoutesLoading] = useState(false);
  const [error, setError] = useState("");

  useEffect(() => {
    loadLocations();
  }, []);

  const loadLocations = async () => {
    try {
      setLocationsLoading(true);
      setError("");

      const data = await getAllLocations();
      setLocations(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLocationsLoading(false);
    }
  };

  const handleChange = (event) => {
    const { name, value } = event.target;
  
    setFilters((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSearch = async () => {
    if (!filters.origin || !filters.destination || !filters.date) {
      setError("Origin, destination ve date alanları zorunlu.");
      return;
    }

    if (filters.origin === filters.destination) {
      setError("Origin ve destination aynı olamaz.");
      return;
    }

    try {
      setRoutesLoading(true);
      setError("");
      setRoutes([]);
      setSelectedRoute(null);

      const data = await searchRoutes({
        origin: filters.origin,
        destination: filters.destination,
        date: filters.date,
      });

      setRoutes(data);

      if (data.length > 0) {
        setSelectedRoute(data[0]);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setRoutesLoading(false);
    }
  };

  return (
    <div className="routes-page">
      <div className="page-section-header">
        <h2>Route Planner</h2>
        <p>Origin, destination ve tarih seçerek uygun rotaları listele.</p>
      </div>
  
      <RouteSearchForm
        locations={locations}
        filters={filters}
        onChange={handleChange}
        onSearch={handleSearch}
        loading={locationsLoading || routesLoading}
      />
  
      {error && <div className="error-box">{error}</div>}
  
      <div className="routes-content-grid">
        <RouteList
          routes={routes}
          selectedRoute={selectedRoute}
          onSelect={setSelectedRoute}
          loading={routesLoading}
        />
  
        <RouteDetailPanel route={selectedRoute} />
      </div>
    </div>
  );
}