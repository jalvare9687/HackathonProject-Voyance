import { ResponsiveContainer, AreaChart, Area, XAxis, YAxis, Tooltip, CartesianGrid } from "recharts";

export default function SalesCard({ data }) {
  return (
    <div className="card p20">
      <div style={{ display:"flex", alignItems:"flex-start", justifyContent:"space-between", marginBottom:6 }}>
        <div>
          <div style={{ fontWeight:900 }}>Total Sales</div>
          <div style={{ marginTop:8, display:"flex", alignItems:"center", gap:10, flexWrap:"wrap" }}>
            <div style={{ fontWeight:900 }}>$84,994.80</div>
            <span className="badge up">↑ 16%</span>
            <span className="muted" style={{ fontSize:13, fontWeight:800 }}>from last month</span>
          </div>
        </div>

        <select className="select">
          <option>Weekly</option>
          <option>Monthly</option>
        </select>
      </div>

      <div style={{ width:"100%", height:250, marginTop:6 }}>
        <ResponsiveContainer>
          <AreaChart data={data} margin={{ top: 10, right: 10, left: -10, bottom: 0 }}>
            <defs>
              <linearGradient id="salesFill" x1="0" y1="0" x2="0" y2="1">
                <stop offset="0%" stopColor="rgba(37,99,235,.35)" />
                <stop offset="100%" stopColor="rgba(37,99,235,0)" />
              </linearGradient>
            </defs>
            <CartesianGrid strokeDasharray="4 4" />
            <XAxis dataKey="day" />
            <YAxis />
            <Tooltip />
            <Area type="monotone" dataKey="sales" stroke="var(--blue2)" strokeWidth={3} fill="url(#salesFill)" />
          </AreaChart>
        </ResponsiveContainer>
      </div>
    </div>
  );
}
