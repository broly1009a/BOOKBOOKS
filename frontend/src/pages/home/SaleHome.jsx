import SidebarSale from "../../components/sidebar/SidebarSale";
import Navbar from "../../components/navbar/Navbar";
import "./home.scss";
import Widget from "../../components/widget/Widget";
import Featured from "../../components/featured/Featured";
import Chart from "../../components/chart/Chart";
import Table from "../../components/table/Table";

const SaleHome = () => {
  return (
    <div className="home">
      <SidebarSale />
      <div className="homeContainer">
        <Navbar />
        <div className="widgets">
          <Widget type="order" />
          <Widget type="earning" />
          <Widget type="customer" />
        </div>
        <div className="charts">
          <Featured />
        </div>
        <div className="listContainer">
          <div className="listTitle">Recent Orders</div>
          <Table type="orders" />
        </div>
      </div>
    </div>
  );
};

export default SaleHome;
