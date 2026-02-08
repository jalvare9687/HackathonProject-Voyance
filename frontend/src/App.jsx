import "./styles/App.css";
import Sidebar from "./layout/Sidebar";
import Dashboard from "./pages/Dashboard";




export default function App() {
 return (
     <div className="shell">
       <Sidebar/>
       <div className="content">
         <Dashboard/>
       </div>
     </div>
 );
}
