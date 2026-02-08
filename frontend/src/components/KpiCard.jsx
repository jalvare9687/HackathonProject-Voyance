import { DollarSign, Box, Receipt, Clock } from "lucide-react";

const iconMap = {
  dollar: DollarSign,
  box: Box,
  receipt: Receipt,
  clock: Clock,
};

export default function KpiCard({ title, value, change = 0, icon }) {
  const positive = change >= 0;
  const Icon = iconMap[icon] || DollarSign;

  return (
    <div
      className="card"
      style={{
        display: "flex",
        justifyContent: "space-between",
        alignItems: "center",
        minHeight: 110,
      }}
    >
      {/* LEFT CONTENT */}
      <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
        {/* ICON */}
        <div
          style={{
            width: 46,
            height: 46,
            borderRadius: 14,
            background: "var(--accentSoft)",
            display: "grid",
            placeItems: "center",
          }}
        >
          <Icon size={20} color="var(--accent)" />
        </div>

        {/* TEXT */}
        <div>
          <div className="kpiTitle">{title}</div>
          <div className="kpiValue">{value}</div>
        </div>
      </div>

      {/* CHANGE BADGE */}
      <div
        className={`badge ${positive ? "up" : "down"}`}
        style={{ fontSize: 13 }}
      >
        {positive ? "▲" : "▼"} {Math.abs(change)}%
      </div>
    </div>
  );
}