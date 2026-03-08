import { toast } from "react-toastify";

export function showErrorToast(message) {
  toast.error(message || "Bir hata oluştu.");
  
}

export function showSuccessToast(message) {
  toast.success(message || "İşlem başarılı.");
}

export function showInfoToast(message) {
  toast.info(message || "Bilgilendirme.");
}

export function showWarningToast(message) {
  toast.warning(message || "Uyarı.");
}

export function showValidationToast(fields) {
  const messages = Object.values(fields || {});

  messages.forEach((message) => {
    toast.error(message);
  });
}