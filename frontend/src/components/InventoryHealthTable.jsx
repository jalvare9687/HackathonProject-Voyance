function pillClass(status) {
  const s = String(status).toLowerCase();
  if (s.includes("critical")) return "red";
  if (s.includes("warning")) return "amber";
  return "green";
}

export default function InventoryHealthTable({ rows }) {
  return (
    <div className="card p20">
      <div style={{ display:"flex", alignItems:"center", justifyContent:"space-between", marginBottom:12 }}>
        <div style={{ fontWeight:900 }}>Predicted Stockouts & Reorder Suggestions</div>

        <button
          className="iconBtn"
          style={{ width:"auto", padding:"10px 12px" }}
          onClick={() => {
            // Updated CSV export logic to include Markup and Rationale
            const csv = [
              ["Ingredient", "Current Stock", "Daily Burn Rate", "Days Until Stockout", "Markup", "Rationale", "Recommendation", "Status"].join(","),
              ...rows.map(r =>
                [
                  `"${String(r.item).replaceAll('"','""')}"`,
                  r.currentStock,
                  r.dailyBurnRate,
                  r.daysRemaining,
                  `"${(r.markup * 100).toFixed(0)}%"`,
                  `"${String(r.rationale || "Standard").replaceAll('"','""')}"`,
                  `"${String(r.recommendation).replaceAll('"','""')}"`,
                  r.status
                ].join(",")
              )
            ].join("\n");

            const blob = new Blob([csv], { type: "text/csv;charset=utf-8" });
            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "inventory_health_with_pricing.csv";
            a.click();
            URL.revokeObjectURL(url);
          }}
        >
          <span style={{ fontWeight:900 }}>Export</span>
        </button>
      </div>

      <table className="table">
        <thead>
          <tr>
            <th>Ingredient</th>
            <th>Current Stock</th>
            <th>Daily Burn</th>
            <th>Days Left</th>
            {/* NEW COLUMNS */}
            <th>Markup</th>
            <th>Pricing Rationale</th>
            <th>Recommendation</th>
            <th>Status</th>
          </tr>
        </thead>

        <tbody>
          {rows.map((r) => (
            <tr key={r.id}>
              <td style={{ fontWeight:900 }}>{r.item}</td>
              <td>{r.currentStock}</td>
              <td>{r.dailyBurnRate}</td>
              <td style={{ fontWeight:900 }}>{r.daysRemaining}</td>
              
              {/* DISPLAYING MARKUP (Green if positive) */}
              <td style={{ color: r.markup > 0 ? "#10b981" : "inherit", fontWeight: 700 }}>
                {r.markup > 0 ? `+${(r.markup * 100).toFixed(0)}%` : "0%"}
              </td>

              {/* DISPLAYING RATIONALE */}
              <td style={{ fontSize: "0.85rem", maxWidth: "200px" }}>
                {r.rationale}
              </td>

              <td>{r.recommendation}</td>
              <td><span className={`pill ${pillClass(r.status)}`}>{r.status}</span></td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}