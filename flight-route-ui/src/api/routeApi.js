import api from "./axios";

function mapRouteResponseItem(route, index) {
  const segments = route.segments || [];

  const firstSegment = segments[0];
  const lastSegment = segments[segments.length - 1];

  return {
    id: `${index}-${firstSegment?.origin?.id || "x"}-${lastSegment?.destination?.id || "y"}`,
    title: route.routeLabel || `Alternative ${index + 1}`,
    travelDate: route.travelDate || "",
    originName: firstSegment?.origin?.name || "-",
    destinationName: lastSegment?.destination?.name || "-",
    numberOfTransportations:
      route.numberOfTransportations || segments.length,
    description: route.description || "",
    steps: segments.map((segment, segmentIndex) => ({
      id: `${index}-${segmentIndex}`,
      type: segment.transportationType,
      from: segment.origin?.name || "-",
      to: segment.destination?.name || "-",
      originName: segment.origin?.name || "-",
      destinationName: segment.destination?.name || "-",
      origin: segment.origin || null,
      destination: segment.destination || null,
      transportationId: segment.transportationId || null,
      segmentLabel: segment.segmentLabel || null,
    })),
  };
}
export async function searchRoutes(params) {
  const response = await api.post("/v1/routes/search", {
     
    originLocationCode: params.origin,
    destinationLocationCode: params.destination,
    travelDate: params.date,
   
  });

  const rawData = response.data || [];

  return rawData.map((route, index) => mapRouteResponseItem(route, index));

}
