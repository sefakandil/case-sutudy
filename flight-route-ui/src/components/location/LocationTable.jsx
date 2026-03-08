export default function LocationTable({
    locations,
    onEdit,
    onDelete,
    loading,
  }) {
    return (
      <div className="crud-card">
        <h3>Locations</h3>
  
        {loading ? (
          <p>Loading locations...</p>
        ) : !locations.length ? (
          <p>No locations found.</p>
        ) : (
          <div className="table-wrap">
            <table className="crud-table">
              <thead>
                <tr>
                  <th>Name</th>
                  <th>Country</th>
                  <th>City</th>
                  <th>Location Code</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                {locations.map((location) => (
                  <tr key={location.id}>
                    <td>{location.name}</td>
                    <td>{location.country}</td>
                    <td>{location.city}</td>
                    <td>{location.locationCode}</td>
                    <td>
                      <div className="table-actions">
                        <button onClick={() => onEdit(location)}>Edit</button>
                        <button
                          className="danger-button"
                          onClick={() => onDelete(location.id)}
                        >
                          Delete
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    );
  }