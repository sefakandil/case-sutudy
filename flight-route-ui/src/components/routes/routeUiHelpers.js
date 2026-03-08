export function getTransportationIcon(type) {
    switch (type) {
      case "FLIGHT":
        return "✈️";
      case "BUS":
        return "🚌";
      case "SUBWAY":
        return "🚇";
      case "UBER":
        return "🚖";
      default:
        return "📍";
    }
  }
  
  export function getTransportationLabel(type) {
    switch (type) {
      case "FLIGHT":
        return "Flight";
      case "BUS":
        return "Bus";
      case "SUBWAY":
        return "Subway";
      case "UBER":
        return "Uber";
      default:
        return type || "Transport";
    }
  }
  
  export function getTransportationClass(type) {
    switch (type) {
      case "FLIGHT":
        return "flight";
      case "BUS":
        return "bus";
      case "SUBWAY":
        return "subway";
      case "UBER":
        return "uber";
      default:
        return "default";
    }
  }
  
  export function getRouteSummary(steps = []) {
    const count = steps.length;
  
    if (count <= 1) return "Direct";
    if (count === 2) return "1 Transfer";
    if (count === 3) return "2 Transfers";
  
    return `${count - 1} Transfers`;
  }
  
  export function getVisitedLocations(steps = []) {
    if (!steps.length) return [];
  
    const locations = [];
  
    const firstOrigin = steps[0]?.origin?.name || steps[0]?.from;
    if (firstOrigin) {
      locations.push(firstOrigin);
    }
  
    steps.forEach((step) => {
      const destination = step.destination?.name || step.to;
      if (destination) {
        locations.push(destination);
      }
    });
  
    return locations;
  }
  
  export function getVisitedLocationsText(steps = []) {
    return getVisitedLocations(steps).join(" → ");
  }