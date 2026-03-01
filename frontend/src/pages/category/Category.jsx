import React, { useState, useEffect } from "react"
import "./category.scss"
import Sidebar from "../../components/sidebar/Sidebar"
import SidebarManager from "../../components/sidebar/SidebarManager"
import Navbar from "../../components/navbar/Navbar"
import { DataGrid } from "@mui/x-data-grid";
import { categoryColumns } from "../../datatablesource";
import { getAllCategories, deleteCategory } from "../../service/CategoryService";
import { Link, useNavigate, useLocation } from "react-router-dom";

const Category = ({ role = 'ADMIN' }) => {
    const [data, setData] = useState([])
    const [columns, setColumns] = useState([]);
    const navigate = useNavigate()
    const location = useLocation()
    const isManager = location.pathname.startsWith('/manager')
    const basePath = isManager ? '/manager/categories' : '/categories'

    const handleDelete = (id) => {
        const confirmBox = window.confirm(
            "Do you really want to delete this category?"
        )
        if (!confirmBox) return
        deleteCategory(id).then((res) => {
            window.location.reload()
        })
    }

    const actionColumn = [
        {
            field: "action",
            headerName: "Action",
            width: 230,
            renderCell: (params) => {
                return (
                    <div className="cellAction">
                        <Link to={`${basePath}/${params.row.id}`} style={{ textDecoration: "none" }}>
                            <div className="viewButton">Update</div>
                        </Link>
                        <div
                            className="deleteButton"
                            onClick={() => handleDelete(params.row.id)}
                        >
                            Delete
                        </div>
                    </div>
                );
            },
        },
    ];

    useEffect(() => {
        getAllCategories().then((res) => {
            setData(res.data)
            setColumns(categoryColumns.concat(actionColumn))
        })
    }, [])

    return (
        <div className="list">
            {isManager ? <SidebarManager /> : <Sidebar />}
            <div className="listContainer">
                <Navbar />
                <div className="datatable">
                    <div className="datatableTitle">
                        Manage Categories
                        <Link to={`${basePath}/new`} className="link">
                            Add New Category
                        </Link>
                    </div>
                    <DataGrid
                        className="datagrid"
                        rows={data}
                        columns={columns}
                        pageSize={9}
                        rowsPerPageOptions={[9]}
                    />
                </div>
            </div>
        </div>
    )
}

export default Category
