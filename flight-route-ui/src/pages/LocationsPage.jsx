import { useEffect, useState } from "react";
import LocationForm from "../components/location/LocationForm";
import LocationTable from "../components/location/LocationTable";
import Pagination from "../components/common/Pagination";
import {
  createLocation,
  getById,
  getAllLocations,
  updateLocation
} from "../api/locationApi";

export default function LocationsPage() {
  const [locations, setLocations] = useState([]);
  const [selectedLocation, setSelectedLocation] = useState(null);

  const [loading, setLoading] = useState(false);
  const [listLoading, setListLoading] = useState(true);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [currentPage, setCurrentPage] = useState(1);
  const pageSize = 5;
  const startIndex = (currentPage - 1) * pageSize;
  const endIndex = startIndex + pageSize;
  const paginatedLocations = locations.slice(startIndex, endIndex);
  const totalPages = Math.ceil(locations.length / pageSize);

  useEffect(() => {
    loadLocations();
  }, []);

  const loadLocations = async () => {
    try {
      setListLoading(true);

      const data = await getAllLocations();
      setLocations(data?.slice().reverse());
    } catch (err) {
      console.error(err);
    } finally {
      setListLoading(false);
    }
  };

  const handleSubmit = async (form) => {
    if (!form.name || !form.country || !form.city || !form.locationCode) {
      setError("Tüm alanlar zorunlu.");
      setSuccess("");
      return;
    }

    try {
      setLoading(true);
      setError("");
      setSuccess("");

      if (selectedLocation) {
        // await updateLocation(selectedLocation.id, form);
        // setSuccess("Location güncellendi.");
      } else {
        await createLocation(form);
        setSuccess("Location oluşturuldu.");
        setCurrentPage(1);
      }

      setSelectedLocation(null);
      await loadLocations();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleEdit = (location) => {
    setError("");
    setSuccess("");
    setSelectedLocation(location);
    window.scrollTo({ top: 0, behavior: "smooth" });
  };

  const handleDelete = async (id) => {
    const confirmed = window.confirm("Bu location silinsin mi?");

    if (!confirmed) return;

    try {
      setLoading(true);
      setError("");
      setSuccess("");

    //   await deleteLocation(id);
    //   setSuccess("Location silindi.");

      if (selectedLocation?.id === id) {
        setSelectedLocation(null);
      }

      await loadLocations();
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  const handleCancelEdit = () => {
    setSelectedLocation(null);
    setError("");
    setSuccess("");
  };

  return (
    <div className="crud-page">
      <h2>Locations Management</h2>

      {error && <div className="error-box">{error}</div>}
      {success && <div className="success-box">{success}</div>}

      <LocationForm
        selectedLocation={selectedLocation}
        onSubmit={handleSubmit}
        onCancelEdit={handleCancelEdit}
        loading={loading}
      />

        <LocationTable
        locations={paginatedLocations}
        onEdit={handleEdit}
        onDelete={handleDelete}
        loading={listLoading}
        />

        <Pagination
        currentPage={currentPage}
        totalPages={totalPages}
        onPageChange={setCurrentPage}
        />
    </div>
  );
}