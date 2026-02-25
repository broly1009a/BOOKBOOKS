import "./list.scss"
import SidebarManager from "../../components/sidebar/SidebarManager"
import Navbar from "../../components/navbar/Navbar"
import Datatable from "../../components/datatable/Datatable"

const ManagerList = ({type}) => {
  return (
    <div className="list">
      <SidebarManager/>
      <div className="listContainer">
        <Navbar/>
        <Datatable type={type} role="MANAGER" />
      </div>
    </div>
  )
}

export default ManagerList
