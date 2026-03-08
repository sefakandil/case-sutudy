import {
  getRouteSummary,
  getTransportationClass,
  getTransportationIcon,
  getTransportationLabel,
  getVisitedLocationsText,
} from "./routeUiHelpers";

export default function RouteList({
  routes,
  selectedRoute,
  onSelect,
  loading,
}) {
  if (loading) {
    return (
      <div className="route-list-card">
        <h3>Available Routes</h3>
        <p>Routes yükleniyor...</p>
      </div>
    );
  }

  if (!routes.length) {
    return (
      <div className="route-list-card">
        <h3>Available Routes</h3>
        <p>Henüz route bulunamadı.</p>
      </div>
    );
  }

  return (
    <div className="route-list-card">
      <h3>Available Routes</h3>

      <div className="route-list">
        {routes.map((route, index) => {
          const routeId = route.id ?? index;
          const isActive =
            (selectedRoute?.id && selectedRoute?.id === route.id) ||
            (!selectedRoute?.id && selectedRoute === route);

            const steps = route.steps || route.transportations || [];
            const summary = getRouteSummary(steps);
            const visitedLocationsText = getVisitedLocationsText(steps);
            
            return (
              <div
                key={routeId}
                className={`route-list-item ${isActive ? "active" : ""}`}
                onClick={() => onSelect(route)}
              >
                <div className="route-card-header">
                  <div>
                    <div className="route-card-title">
                      {route.title || `Alternative ${index + 1}`}
                    </div>
                    <div className="route-card-subtitle">
                      {route.originName} → {route.destinationName}
                    </div>
                  </div>
            
                  <div className="route-card-meta">
                    <div className="route-summary-badge">{summary}</div>
                    <div className="route-step-count">{steps.length} step</div>
                  </div>
                </div>
            
                {visitedLocationsText && (
                  <div className="route-visited-path">
                    {visitedLocationsText}
                  </div>
                )}
            
                <div className="route-journey-line">
                  {steps.map((step, stepIndex) => {
                    const type = step.type || step.transportationType;
                    const transportClass = getTransportationClass(type);
            
                    return (
                      <div
                        key={stepIndex}
                        className={`route-journey-chip ${transportClass}`}
                      >
                        <span>{getTransportationIcon(type)}</span>
                        <span>{getTransportationLabel(type)}</span>
                      </div>
                    );
                  })}
                </div>
              </div>
            );
        })}
      </div>
    </div>
  );
}