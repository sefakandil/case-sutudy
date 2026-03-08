import { useEffect, useState } from "react";
import TransportationForm from "../components/transportation/TransportationForm";
import TransportationTable from "../components/transportation/TransportationTable";
import Pagination from "../components/common/Pagination";
import { getAllLocations } from "../api/locationApi";
import {
  createTransportation,
  getAllTransportations,
} from "../api/transportationApi";

export default function TransportationsPage() {
  const [locations, setLocations] = useState([]);
  const [transportations, setTransportations] = useState([]);

  const [page, setPage] = useState(0);
  const [size] = useState(5);
  const [totalPages, setTotalPages] = useState(0);

  const [loading, setLoading] = useState(false);
  const [listLoading, setListLoading] = useState(true);

  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");

  useEffect(() => {
    loadLocations();
  }, []);

  useEffect(() => {
    loadTransportations(page);
  }, [page]);

  const loadLocations = async () => {
    try {
      const data = await getAllLocations();
      setLocations(data);
    } catch (err) {
      console.error(err);
    }
  };

  const loadTransportations = async (pageNumber = 0) => {
    try {
      setListLoading(true);

      const data = await getAllTransportations(pageNumber, size);

      setTransportations(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (err) {
      console.error(err);
    } finally {
      setListLoading(false);
    }
  };

  const handleCreate = async (form, resetForm) => {
    if (
      !form.origin ||
      !form.destination ||
      !form.transportationType
    ) {
      setError("Origin, destination ve transportation type zorunlu.");
      setSuccess("");
      return;
    }

    if (form.origin.id === form.destination.id) {
      setError("Origin ve destination aynı olamaz.");
      setSuccess("");
      return;
    }

    if (!form.operatingDays.length) {
      setError("En az bir operating day seçmelisin.");
      setSuccess("");
      return;
    }

    try {
      setLoading(true);
      setError("");
      setSuccess("");

      await createTransportation(form);

      setSuccess("Transportation oluşturuldu.");

      resetForm();

      setPage(0);
      await loadTransportations(0);
    } catch (err) {
      console.error(err);
      
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="crud-page">
      <h2>Transportations Management</h2>

      {error && <div className="error-box">{error}</div>}
      {success && <div className="success-box">{success}</div>}

      <TransportationForm
        locations={locations}
        onSubmit={handleCreate}
        loading={loading}
      />

      <TransportationTable
        transportations={transportations}
        loading={listLoading}
      />

      <Pagination
        currentPage={page}
        totalPages={totalPages}
        onPageChange={setPage}
      />
    </div>
  );
}