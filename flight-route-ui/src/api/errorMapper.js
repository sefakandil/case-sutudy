export function mapApiError(error) {
  if (!error.response) {
    return {
      type: "NETWORK",
      message: "Ağ hatası oluştu. Lütfen bağlantınızı kontrol edin."
    };
  }

  const data = error.response.data;

  if (data?.validationErrors) {
    return {
      type: "VALIDATION",
      message: data.message || "Validation failed",
      fields: data.validationErrors,
      status: data.status
    };
  }

  return {
    type: "GENERAL",
    message: data?.message || "Beklenmeyen bir hata oluştu.",
    status: data?.status || error.response.status
  };
}