import {
  getRouteSummary,
  getTransportationClass,
  getTransportationIcon,
  getTransportationLabel,
  getVisitedLocationsText,
} from "./routeUiHelpers";

export default function RouteDetailPanel({ route }) {
  if (!route) {
    return (
      <div className="route-detail-card">
        <h3>Route Details</h3>
        <p>Detay görmek için bir route seç.</p>
      </div>
    );
  }

  const steps = route.steps || route.transportations || [];
  const summary = getRouteSummary(steps);
  const visitedLocationsText = getVisitedLocationsText(steps);

  return (
    <div className="route-detail-card">
      <h3>Route Details</h3>

      <div className="route-detail-header">
        <strong>{route.title || "Selected Route"}</strong>
        <p>
          {route.originName} → {route.destinationName}
        </p>
      </div>

      <div className="route-detail-overview">
        <div className="route-overview-box">
          <span className="route-overview-label">Journey Type</span>
          <span className="route-overview-value">{summary}</span>
        </div>

        <div className="route-overview-box">
          <span className="route-overview-label">Step Count</span>
          <span className="route-overview-value">{steps.length}</span>
        </div>
      </div>

      {visitedLocationsText && (
        <div className="route-visited-card">
          <div className="route-overview-label">Visited Locations</div>
          <div className="route-visited-value">{visitedLocationsText}</div>
        </div>
      )}

      <div className="journey-timeline">
        {steps.map((step, index) => {
          const type = step.type || step.transportationType;
          const transportClass = getTransportationClass(type);

          const fromName = step.origin?.name || step.from || "-";
          const toName = step.destination?.name || step.to || "-";

          const fromCode = step.origin?.locationCode || "";
          const toCode = step.destination?.locationCode || "";

          const fromCity = step.origin?.city || "";
          const toCity = step.destination?.city || "";

          return (
            <div key={step.id || index} className="journey-step">
              <div className="journey-left">
                <div className="journey-dot" />
                {index !== steps.length - 1 && <div className="journey-line" />}
              </div>

              <div className="journey-right">
                <div className={`journey-type-badge ${transportClass}`}>
                  <span className="journey-type-icon">
                    {getTransportationIcon(type)}
                  </span>
                  <span>{getTransportationLabel(type)}</span>
                </div>

                <div className="journey-location-block">
                  <div className="journey-location-label">From</div>
                  <div className="journey-location-value">
                    {fromName} {fromCode ? `(${fromCode})` : ""}
                  </div>
                  {fromCity && (
                    <div className="journey-location-meta">{fromCity}</div>
                  )}
                </div>

                <div className="journey-arrow">↓</div>

                <div className="journey-location-block">
                  <div className="journey-location-label">To</div>
                  <div className="journey-location-value">
                    {toName} {toCode ? `(${toCode})` : ""}
                  </div>
                  {toCity && (
                    <div className="journey-location-meta">{toCity}</div>
                  )}
                </div>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}