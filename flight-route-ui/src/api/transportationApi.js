import api from "./axios";

export async function createTransportation(payload) {
  const response = await api.post("/api/v1/transportations", payload);
  return response.data;
}

export async function getAllTransportations(page = 0, size = 5) {
  const response = await api.get("/api/v1/transportations", {
    params: {
      page,
      size,
    },
  });

  return response.data;
}