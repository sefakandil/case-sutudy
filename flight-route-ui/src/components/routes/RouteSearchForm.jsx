import SimpleAutocomplete from "../common/SimpleAutocomplete";

export default function RouteSearchForm({
  locations,
  filters,
  onChange,
  onSearch,
  loading,
}) {
  const selectedOrigin =
    locations.find((item) => String(item.locationCode) === String(filters.origin)) || null;

  const selectedDestination =
    locations.find((item) => String(item.locationCode) === String(filters.destination)) || null;

  return (
    <div className="route-search-card">
      <h3>Route Search</h3>

      <div className="route-search-grid">
        <SimpleAutocomplete
          label="Origin"
          placeholder="Origin ara..."
          options={locations}
          value={selectedOrigin}
          disabled={loading}
          onSelect={(item) =>
            onChange({
              target: {
                name: "origin",
                value: item ? item.locationCode : "",
              },
            })
          }
        />

        <SimpleAutocomplete
          label="Destination"
          placeholder="Destination ara..."
          options={locations.filter(
            (item) => !filters.origin || String(item.locationCode) !== String(filters.origin)
          )}
          value={selectedDestination}
          disabled={loading}
          onSelect={(item) =>
            onChange({
              target: {
                name: "destination",
                value: item ? item.locationCode : "",
              },
            })
          }
        />

        <div className="form-group">
          <label>Date</label>
          <input
            type="date"
            name="date"
            value={filters.date}
            onChange={onChange}
            disabled={loading}
          />
        </div>

        <div className="form-group route-search-button-wrap">
          <button type="button" onClick={onSearch} disabled={loading}>
            {loading ? "Loading..." : "Search Routes"}
          </button>
        </div>
      </div>
    </div>
  );
}