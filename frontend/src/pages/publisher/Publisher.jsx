import React, { useState, useEffect } from "react"
import "./publisher.scss"
import Sidebar from "../../components/sidebar/Sidebar"
import SidebarManager from "../../components/sidebar/SidebarManager"
import Navbar from "../../components/navbar/Navbar"
import { DataGrid } from "@mui/x-data-grid";
import { publisherColumns } from "../../datatablesource";
import { getAllPublishers, deletePublisher } from "../../service/PublisherService";
import { Link, useNavigate, useLocation } from "react-router-dom";

const Publisher = () => {
    const [data, setData] = useState([])
    const [columns, setColumns] = useState([]);
    const navigate = useNavigate()
    const location = useLocation()
    const isManager = location.pathname.startsWith('/manager')
    const basePath = isManager ? '/manager/publishers' : '/publishers'

    const handleDelete = (id) => {
        const confirmBox = window.confirm(
            "Do you really want to delete this publisher?"
        )
        if (!confirmBox) return
        deletePublisher(id).then((res) => {
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
        getAllPublishers().then((res) => {
            setData(res.data)
            setColumns(publisherColumns.concat(actionColumn))
        })
    }, [])

    return (
        <div className="list">
            {isManager ? <SidebarManager /> : <Sidebar />}
            <div className="listContainer">
                <Navbar />
                <div className="datatable">
                    <div className="datatableTitle">
                        Manage Publishers
                        <Link to={`${basePath}/new`} className="link">
                            Add New Publisher
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

export default Publisher