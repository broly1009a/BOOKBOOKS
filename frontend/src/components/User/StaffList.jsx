import React, { useEffect, useState } from 'react';
import { DataGrid } from "@mui/x-data-grid";
import { staffColumns } from "../../datatablesource";
import { getStaff, setAccountState, setRole } from "../../services/UserService";
import { Link } from "react-router-dom";
import SearchOutlinedIcon from "@mui/icons-material/SearchOutlined";
import "../../components/datatable/datatable.scss";

const StaffList = () => {
  const [data, setData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");
  const [statusFilter, setStatusFilter] = useState("");
  const [roleFilter, setRoleFilter] = useState("");
  const [page, setPage] = useState(0);
  const [pageSize, setPageSize] = useState(10);
  const [totalRows, setTotalRows] = useState(0);
  const [sortModel, setSortModel] = useState([{ field: 'id', sort: 'asc' }]);

  const fetchStaff = async () => {
    setLoading(true);
    try {
      const sortBy = sortModel.length > 0 ? sortModel[0].field : 'id';
      const sortOrder = sortModel.length > 0 ? sortModel[0].sort : 'asc';
      
      const response = await getStaff(search, statusFilter, roleFilter, page, pageSize, sortBy, sortOrder);
      setData(response.data.content);
      setTotalRows(response.data.totalElements);
    } catch (error) {
      console.error("Error fetching staff:", error);
      alert("Failed to load staff!");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStaff();
  }, [page, pageSize, sortModel]);

  const handleSearch = () => {
    setPage(0);
    fetchStaff();
  };

  const handleToggleStatus = async (id, currentState) => {
    const newState = currentState === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE';
    const confirmMessage = `Are you sure you want to ${newState === 'ACTIVE' ? 'activate' : 'deactivate'} this staff member?`;
    
    if (!window.confirm(confirmMessage)) return;

    try {
      await setAccountState(id, newState);
      alert(`Staff ${newState === 'ACTIVE' ? 'activated' : 'deactivated'} successfully!`);
      fetchStaff();
    } catch (error) {
      console.error("Error updating staff status:", error);
      alert("Failed to update staff status!");
    }
  };

  const handleChangeRole = async (id, currentRole) => {
    const newRole = prompt(`Change role for this staff member.\nCurrent role: ${currentRole}\n\nEnter new role (ADMIN, STAFF, MANAGER):`, currentRole);
    
    if (!newRole || newRole === currentRole) return;
    
    const validRoles = ['ADMIN', 'STAFF', 'MANAGER'];
    if (!validRoles.includes(newRole.toUpperCase())) {
      alert('Invalid role! Please enter one of: ADMIN, STAFF, MANAGER');
      return;
    }

    try {
      await setRole(id, newRole.toUpperCase());
      alert('Role updated successfully!');
      fetchStaff();
    } catch (error) {
      console.error("Error updating role:", error);
      alert("Failed to update role!");
    }
  };

  const actionColumn = [
    {
      field: "action",
      headerName: "Action",
      width: 300,
      renderCell: (params) => {
        return (
          <div className="cellAction" style={{ display: 'flex', gap: '5px' }}>
            <button
              className={params.row.state === 'ACTIVE' ? 'deleteButton' : 'viewButton'}
              onClick={() => handleToggleStatus(params.row.id, params.row.state)}
              style={{ cursor: 'pointer', padding: '5px 10px', border: 'none', borderRadius: '5px' }}
            >
              {params.row.state === 'ACTIVE' ? 'Deactivate' : 'Activate'}
            </button>
            <button
              className="viewButton"
              onClick={() => handleChangeRole(params.row.id, params.row.role)}
              style={{ cursor: 'pointer', padding: '5px 10px', border: 'none', borderRadius: '5px' }}
            >
              Change Role
            </button>
          </div>
        );
      },
    },
  ];

  const columns = staffColumns.concat(actionColumn);

  return (
    <div className="datatable">
      <div className="datatableTitle">
        Manage Staff
        <Link to="/staff/new" className="link">
          Add New Staff
        </Link>
      </div>
      
      <div className="filterSection" style={{ marginBottom: '20px', display: 'flex', gap: '10px', alignItems: 'center', flexWrap: 'wrap' }}>
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

        <select 
          value={roleFilter} 
          onChange={(e) => setRoleFilter(e.target.value)}
          style={{ padding: '8px', borderRadius: '5px', border: '1px solid #ccc' }}
        >
          <option value="">All Roles</option>
          <option value="ADMIN">Admin</option>
          <option value="STAFF">Staff</option>
          <option value="MANAGER">Manager</option>
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

export default StaffList;
