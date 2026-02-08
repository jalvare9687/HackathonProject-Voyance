import { Bell, Search } from "lucide-react";

export default function Topbar() {
  return (
  <div className="topbar">
    <h1 className="h1">Dashboard</h1>

    <div style={{display:"flex",alignItems:"center",gap:16,marginLeft:16}}>
      <div className="search">
        <Search size={18}/>
        <input placeholder="Search anything..." />
      </div>

      <button className="card" style={{width:44,height:44,display:"grid",placeItems:"center",padding:0}}>
        <Bell size={18}/>
      </button>

      <div style={{
        width:44,
        height:44,
        borderRadius:999,
        background:"linear-gradient(135deg,#3B82F6,#60A5FA)",
        display:"grid",
        placeItems:"center",
        color:"#fff",
        fontWeight:800
      }}>
        T
      </div>
    </div>
  </div>
);
}