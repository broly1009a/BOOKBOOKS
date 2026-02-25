import SidebarManager from "../../components/sidebar/SidebarManager";
import Navbar from "../../components/navbar/Navbar";
import "./home.scss";
import Widget from "../../components/widget/Widget";
import Featured from "../../components/featured/Featured";
import Chart from "../../components/chart/Chart";
import Table from "../../components/table/Table";

const ManagerHome = () => {
  return (
    <div className="home">
      <SidebarManager />
      <div className="homeContainer">
        <Navbar />
        <div className="widgets">
          <Widget type="product" />
          <Widget type="feedback" />
          <Widget type="lowstock" />
        </div>
        <div className="charts">
          <Featured />
        </div>
        <div className="listContainer">
          <div className="listTitle">Recent Products</div>
          <Table type="products" />
        </div>
      </div>
    </div>
  );
};

export default ManagerHome;
