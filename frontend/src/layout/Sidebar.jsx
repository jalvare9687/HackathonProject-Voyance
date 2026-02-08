import { LayoutDashboard, Truck, ShoppingBag, Boxes, FileText, Wallet, MapPinned, Users, BookOpen, HelpCircle } from "lucide-react";

const items = [
  { label:"Dashboard", icon: LayoutDashboard },
  { label:"Logistics", icon: Truck },
  { label:"Orders", icon: ShoppingBag },
  { label:"Inventory", icon: Boxes },
  { label:"Report", icon: FileText },
  { label:"Cashflow", icon: Wallet },
  { label:"Tracking", icon: MapPinned },
  { label:"Customers", icon: Users },
];

export default function Sidebar() {
  return (
    <aside style={{ padding:16, borderRight:"1px solid var(--line)", background:"#fff" }}>
      <div style={{ display:"flex", alignItems:"center", gap:10, marginBottom:18 }}>
        <div style={{ width:34, height:34, borderRadius:10, background:"#111827", color:"#fff", display:"grid", placeItems:"center", fontWeight:800 }}>V</div>
        <div style={{ fontWeight:800 }}>Voyance</div>
      </div>

      <div style={{ display:"flex", flexDirection:"column", gap:6 }}>
        {items.map((it) => {
          const Icon = it.icon;
          const active = it.label === "Dashboard";
          return (
            <div key={it.label}
              style={{
                display:"flex", alignItems:"center", gap:10,
                padding:"10px 12px", borderRadius:10,
                background: active ? "rgba(59,130,246,.10)" : "transparent",
                border: active ? "1px solid rgba(59,130,246,.20)" : "1px solid transparent",
                cursor:"pointer"
              }}>
              <Icon size={18} color={active ? "var(--blue)" : "#374151"} />
              <span style={{ fontWeight:700, color: active ? "#111827" : "#374151" }}>{it.label}</span>
            </div>
          );
        })}
      </div>

      <div style={{ marginTop:18, paddingTop:14, borderTop:"1px solid var(--line)", display:"flex", flexDirection:"column", gap:8 }}>
        <div style={{ display:"flex", alignItems:"center", gap:10, padding:"10px 12px", borderRadius:10, cursor:"pointer" }}>
          <BookOpen size={18} /> <span style={{ fontWeight:700 }}>User Guide</span>
        </div>
        <div style={{ display:"flex", alignItems:"center", gap:10, padding:"10px 12px", borderRadius:10, cursor:"pointer" }}>
          <HelpCircle size={18} /> <span style={{ fontWeight:700 }}>FAQ</span>
        </div>
      </div>
    </aside>
  );
}

