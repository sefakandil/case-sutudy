import { useEffect, useState } from "react";

const initialForm = {
  name: "",
  country: "",
  city: "",
  locationCode: "",
};

export default function LocationForm({
  selectedLocation,
  onSubmit,
  onCancelEdit,
  loading,
}) {
  const [form, setForm] = useState(initialForm);

  useEffect(() => {
    if (selectedLocation) {
      setForm({
        name: selectedLocation.name || "",
        country: selectedLocation.country || "",
        city: selectedLocation.city || "",
        locationCode: selectedLocation.locationCode || "",
      });
    } else {
      setForm(initialForm);
    }
  }, [selectedLocation]);

  const handleChange = (e) => {
    const { name, value } = e.target;

    setForm((prev) => ({
      ...prev,
      [name]: value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    onSubmit(form);
  };

  return (
    <div className="crud-card">
      <h3>{selectedLocation ? "Edit Location" : "Create Location"}</h3>

      <form className="crud-form" onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Name</label>
          <input
            name="name"
            value={form.name}
            onChange={handleChange}
            placeholder="Location name"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label>Country</label>
          <input
            name="country"
            value={form.country}
            onChange={handleChange}
            placeholder="Country"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label>City</label>
          <input
            name="city"
            value={form.city}
            onChange={handleChange}
            placeholder="City"
            disabled={loading}
          />
        </div>

        <div className="form-group">
          <label>Location Code</label>
          <input
            name="locationCode"
            value={form.locationCode}
            onChange={handleChange}
            placeholder="Location code"
            disabled={loading}
          />
        </div>

        <div className="crud-actions">
          <button type="submit" disabled={loading}>
            {loading ? "Saving..." : selectedLocation ? "Update" : "Create"}
          </button>

          {selectedLocation && (
            <button
              type="button"
              className="secondary-button"
              onClick={onCancelEdit}
              disabled={loading}
            >
              Cancel
            </button>
          )}
        </div>
      </form>
    </div>
  );
}