import api from "./axios";

export async function getAllLocations() {
    const response = await api.get("/v1/locations");
    return response.data;
}
  
export async function createLocation(payload) {
    const response = await api.post("/v1/locations", payload);
    return response.data;
}

export async function getById(id, payload) {
    const response = await api.get(`/v1/locations/${id}`, payload);
    return response.data;
}

export async function updateLocation(id, payload) {
    const response = await api.put(`/v1/locations/${id}`, payload);
    return response.data;
}