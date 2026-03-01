import React, { useState, useEffect } from 'react'
import Sidebar from "../../components/sidebar/Sidebar";
import SidebarManager from "../../components/sidebar/SidebarManager";
import Navbar from "../../components/navbar/Navbar";
import { getCategoryById, updateCategory } from '../../service/CategoryService';
import { useParams, useLocation } from 'react-router-dom';
import Grid from '@mui/material/Grid';
import TextField from '@mui/material/TextField';

const CategorySingle = () => {
    const [data, setData] = useState({})
    const { id } = useParams()
    const location = useLocation()
    const isManager = location.pathname.startsWith('/manager')
    const basePath = isManager ? '/manager/categories' : '/categories'
    const [error, setError] = useState(false)

    const handleCancel = () => {
        window.location.replace(basePath)
    }

    const handleSave = () => {
        if (data.name.trim() === ''){
            setError(true)
            return
        }
        updateCategory(data).then(res => {
            if (res.status === 200) {
                window.location.replace(basePath)
            }
            else {
                setError(true)
            }
        })
    }

    useEffect(() => {
        getCategoryById(id).then((res) => {
            setData(res.data)
        })
    }, [])

    return (
        <div>
            <div className="single">
                {isManager ? <SidebarManager /> : <Sidebar />}
                {data.length !== 0 && <div className="singleContainer">
                    <Navbar />
                    <div className="wrapper">
                        <div className="function spacing">
                            <h3>Update Category</h3>
                            <div className="btn-list">
                                {error && <span style={{ color: 'red', marginRight: '20px' }}>Error</span>}
                                <button onClick={handleCancel} className="cancel">Cancel</button>
                                <button onClick={handleSave} className="save">Save</button>
                            </div>
                        </div>

                        <Grid container spacing={2} className='spacing'>
                            <Grid item xs={12}>
                                <TextField
                                    required
                                    id="name"
                                    name="name"
                                    label="Category Name"
                                    fullWidth
                                    autoComplete="off"
                                    value={data.name}
                                    onChange={(e) => setData({ ...data, name: e.target.value })}
                                />
                            </Grid>
                        </Grid>
                    </div>
                </div>
                }
            </div>
        </div>
    )
}

export default CategorySingle
