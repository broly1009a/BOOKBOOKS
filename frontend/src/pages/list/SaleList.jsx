import "./list.scss"
import SidebarSale from "../../components/sidebar/SidebarSale"
import Navbar from "../../components/navbar/Navbar"
import Datatable from "../../components/datatable/Datatable"

const SaleList = ({type}) => {
  return (
    <div className="list">
      <SidebarSale/>
      <div className="listContainer">
        <Navbar/>
        <Datatable type={type} role="SALE" />
      </div>
    </div>
  )
}

export default SaleList
