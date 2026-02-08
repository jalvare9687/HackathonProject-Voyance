export default function MetricCard({ icon, title, value, change, isPositive }) {
  return (
    <div style={{
      background: "#FFFFFF",
      borderRadius: 12,
      padding: 20,
      display: "flex",
      gap: 16,
      alignItems: "flex-start",
      boxShadow: "0 2px 8px rgba(0,0,0,0.05)"
    }}>
      <div style={{
        width: 48, height: 48, borderRadius: 10,
        background: "#F5F6FA",
        display: "grid", placeItems: "center",
        fontSize: 24
      }}>
        {icon}
      </div>

      <div style={{ flex: 1 }}>
        <div style={{ fontSize: 14, color: "#6B7280", marginBottom: 8, fontWeight: 500 }}>{title}</div>
        <div style={{ fontSize: 28, fontWeight: 700, color: "#1A1D1F", marginBottom: 8 }}>{value}</div>

        <span style={{
          display: "inline-block",
          padding: "4px 8px",
          borderRadius: 6,
          fontSize: 12,
          fontWeight: 600,
          background: isPositive ? "#D1FAE5" : "#FEE2E2",
          color: isPositive ? "#059669" : "#DC2626"
        }}>
          {isPositive ? "↑" : "↓"} {Math.abs(change)}%
        </span>
      </div>
    </div>
  );
}
