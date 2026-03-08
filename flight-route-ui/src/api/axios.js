import axios from "axios";
import { mapApiError } from "./errorMapper";
import { showErrorToast, showValidationToast } from "../util/toastUtils";

const api = axios.create({
  baseURL: "http://localhost:8080/",
});

api.interceptors.request.use((config) => {
    const token = localStorage.getItem("token");
  
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
  
    return config;
  });
  
  api.interceptors.response.use(
    (response) => response,
      (error) => {

      const mappedError = mapApiError(error);
      
      if (mappedError.type === "VALIDATION") {
         showValidationToast(mappedError.fields);
      } else {
         showErrorToast(mappedError.message);

      }
 
      return Promise.reject(error);
    }
  );
  
  export default api;