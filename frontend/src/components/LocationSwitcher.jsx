export default function LocationSwitcher({ locationId, setLocationId, locations }) {
  return (
    <select
      className="select"
      value={locationId}
      onChange={(e) => setLocationId(Number(e.target.value))}
      style={{ minWidth: 190 }}
    >
      {locations.map((loc) => (
        <option key={loc.id} value={loc.id}>
          {loc.name}
        </option>
      ))}
    </select>
  );
}
