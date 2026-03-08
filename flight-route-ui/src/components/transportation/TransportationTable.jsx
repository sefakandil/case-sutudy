const DAY_LABELS = {
    "MONDAY": "Mon",
    "TUESDAY": "Tue",
    "WEDNESDAY": "Wed",
    "THURSDAY": "Thu",
    "FRIDAY": "Fri",
    "SATURDAY": "Sat",
    "SUNDAY": "Sun",
  };
  
  function formatDays(days = []) {
    return days.map((day) => DAY_LABELS[day] || day).join(", ");
  }
  
  export default function TransportationTable({
    transportations,
    loading,
  }) {
    return (
      <div className="crud-card">
        <h3>Transportations</h3>
  
        {loading ? (
          <p>Loading transportations...</p>
        ) : !transportations.length ? (
          <p>No transportations found.</p>
        ) : (
          <div className="table-wrap">
            <table className="crud-table">
              <thead>
                <tr>
                  <th>Origin</th>
                  <th>Destination</th>
                  <th>Type</th>
                  <th>Operating Days</th>
                </tr>
              </thead>
              <tbody>
                {transportations.map((item) => (
                  <tr key={item.id}>
                    <td>
                      {"("+item.origin?.locationCode + ")-" +
                        item.origin?.name }
                    </td>
                    <td>
                      {"("+item.destination?.locationCode + ")-" +
                        item.destination?.name }
                    </td>
                    <td>{item.transportationType}</td>
                    <td>{formatDays(item.operatingDays)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    );
  }