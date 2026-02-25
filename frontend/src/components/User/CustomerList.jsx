import React, { useEffect, useState } from 'react';
import { DataGrid } from "@mui/x-data-grid";
import { customerColumns } from "../../datatablesource";
import { getCustomer, setAccountState } from "../../services/UserService";
import SearchOutlinedIcon from "@mui/icons-material/SearchOutlined";
import "../../components/datatable/datatable.scss";

const CustomerList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalRows, setTotalRows] = useState(0);
  const [sortModel, setSortModel] = useState([{ field: 'id', sort: 'asc' }]);

  const fetchCustomers = async () => {
    setLoading(true);
    try {
      const sortBy = sortModel.length > 0 ? sortModel[0].field : 'id';
      const sortOrder = sortModel.length > 0 ? sortModel[0].sort : 'asc';
      
      const response = await getCustomer(search, statusFilter, page, pageSize, sortBy, sortOrder);
      setData(response.data.content);
      setTotalRows(response.data.totalElements);
    } catch (error) {
      console.error("Error fetching customers:", error);
      alert("Failed to load customers!");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCustomers();
  }, [page, pageSize, sortModel]);

  const handleSearch = () => {
    setPage(0);
    fetchCustomers();
  };

  const handleToggleStatus = async (id, currentState) => {
    const newState = currentState === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const confirmMessage = `Are you sure you want to ${newState === 'ACTIVE' ? 'activate' : 'deactivate'} this customer?`;
    
    if (!window.confirm(confirmMessage)) return;

    try {
      await setAccountState(id, newState);
      alert(`Customer ${newState === 'ACTIVE' ? 'activated' : 'deactivated'} successfully!`);
      fetchCustomers();
    } catch (error) {
      console.error("Error updating customer status:", error);
      alert("Failed to update customer status!");
    }
  };

  const actionColumn = [
    {
      field: "action",
      headerName: "Action",
      width: 200,
      renderCell: (params) => {
        return (
          <div className="cellAction">
            <button
              className={params.row.state === 'ACTIVE' ? 'deleteButton' : 'viewButton'}
              onClick={() => handleToggleStatus(params.row.id, params.row.state)}
              style={{ cursor: 'pointer', padding: '5px 10px', border: 'none', borderRadius: '5px' }}
            >
              {params.row.state === 'ACTIVE' ? 'Deactivate' : 'Activate'}
            </button>
          </div>
        );
      },
    },
  ];

  const columns = customerColumns.concat(actionColumn);

  return (
    <div className="datatable">
      <div className="datatableTitle">
        Manage Customers
      </div>
      
      <div className="filterSection" style={{ marginBottom: '20px', display: 'flex', gap: '10px', alignItems: 'center' }}>
        <div className="search" style={{ display: "flex", alignItems: "center" }}>
          <input 
            onChange={(e) => setSearch(e.target.value)} 
            value={search} 
            type="text" 
            placeholder="Search by name..." 
            onKeyPress={(e) => e.key === 'Enter' && handleSearch()}
          />
          <SearchOutlinedIcon onClick={handleSearch} style={{ cursor: 'pointer' }} />
        </div>
        
        <select 
          value={statusFilter} 
          onChange={(e) => setStatusFilter(e.target.value)}
          style={{ padding: '8px', borderRadius: '5px', border: '1px solid #ccc' }}
        >
          <option value="">All Status</option>
          <option value="ACTIVE">Active</option>
          <option value="INACTIVE">Inactive</option>
        </select>

        <button 
          onClick={handleSearch}
          style={{ 
            padding: '8px 16px', 
            backgroundColor: '#7451f8', 
            color: 'white', 
            border: 'none', 
            borderRadius: '5px',
            cursor: 'pointer'
          }}
        >
          Filter
        </button>
      </div>

      <DataGrid
        className="datagrid"
        rows={data}
        columns={columns}
        pageSize={pageSize}
        rowsPerPageOptions={[5, 10, 25, 50]}
        rowCount={totalRows}
        paginationMode="server"
        page={page}
        onPageChange={(newPage) => setPage(newPage)}
        onPageSizeChange={(newPageSize) => setPageSize(newPageSize)}
        sortingMode="server"
        sortModel={sortModel}
        onSortModelChange={(newSortModel) => setSortModel(newSortModel)}
        loading={loading}
        autoHeight
      />
    </div>
  );
};

export default CustomerList;
