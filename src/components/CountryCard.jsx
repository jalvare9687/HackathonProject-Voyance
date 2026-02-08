export default function CountryCard({ rows }) {
  const max = Math.max(...rows.map(r => r.value));

  return (
    <div className="card p20">
      <div style={{ display:"flex", alignItems:"center", justifyContent:"space-between", marginBottom:14 }}>
        <div style={{ fontWeight:900 }}>Country Redistribution</div>
        <select className="select">
          <option>Yearly</option>
          <option>Monthly</option>
          <option>Weekly</option>
        </select>
      </div>

      <div style={{ display:"flex", flexDirection:"column", gap:14 }}>
        {rows.map(r => (
          <div key={r.name} style={{ display:"grid", gridTemplateColumns:"80px 1fr 52px", alignItems:"center", gap:12 }}>
            <div style={{ fontWeight:900 }}>{r.name}</div>
            <div style={{ height:12, background:"rgba(59,130,246,.15)", borderRadius:999, overflow:"hidden" }}>
              <div style={{ width:`${(r.value/max)*100}%`, height:"100%", background:"var(--blue2)" }} />
            </div>
            <div className="muted" style={{ fontWeight:900 }}>{r.value} M</div>
          </div>
        ))}
      </div>
    </div>
  );
}
