import React, { useState, useEffect } from 'react';
import { DataGrid } from '@mui/x-data-grid';
import Sidebar from '../sidebar/Sidebar';
import Navbar from '../navbar/Navbar';
import { getAllPostCategories, createPostCategory, updatePostCategory, deletePostCategory } from '../../service/PostService';

const PostCategoryManager = () => {
    const [data, setData] = useState([]);
    const [loading, setLoading] = useState(false);
    const [formData, setFormData] = useState({ name: '', description: '' });
    const [selectedCategory, setSelectedCategory] = useState(null);

    useEffect(() => {
        loadCategories();
    }, []);

    useEffect(() => {
        if (selectedCategory) {
            setFormData({ name: selectedCategory.name || '', description: selectedCategory.description || '' });
        } else {
            setFormData({ name: '', description: '' });
        }
    }, [selectedCategory]);

    const loadCategories = async () => {
        setLoading(true);
        try {
            const response = await getAllPostCategories();
            setData(response.data);
        } catch (error) {
            console.error('Error loading categories:', error);
        } finally {
            setLoading(false);
        }
    };

    const handleCreate = async () => {
        try {
            await createPostCategory(formData);
            setFormData({ name: '', description: '' });
            setSelectedCategory(null);
            loadCategories();
        } catch (error) {
            console.error('Error creating category:', error);
        }
    };

    const handleUpdate = async () => {
        if (!selectedCategory) return;
        try {
            await updatePostCategory({ ...selectedCategory, ...formData });
            setSelectedCategory(null);
            setFormData({ name: '', description: '' });
            loadCategories();
        } catch (error) {
            console.error('Error updating category:', error);
        }
    };

    const handleDelete = async (id) => {
        if (!window.confirm('Do you really want to delete this category?')) return;
        try {
            await deletePostCategory(id);
            loadCategories();
        } catch (error) {
            console.error('Error deleting category:', error);
        }
    };

    const actionColumn = [
        {
            field: 'action',
            headerName: 'Action',
            width: 230,
            renderCell: (params) => (
                <div className="cellAction">
                    <div className="viewButton" onClick={() => setSelectedCategory(params.row)}>Edit</div>
                    <div className="deleteButton" onClick={() => handleDelete(params.row.id)}>Delete</div>
                </div>
            ),
        },
    ];

    const columns = [
        { field: 'id', headerName: 'STT', width: 90 },
        { field: 'name', headerName: 'Name', width: 200 },
        { field: 'description', headerName: 'Description', width: 250 },
    ].concat(actionColumn);

    return (
        <div className="list">
            <Sidebar />
            <div className="listContainer">
                <Navbar />
                <div className="datatable">
                    <div className="datatableTitle">
                        Manage Post Category
                        {!selectedCategory && (
                            <button
                                onClick={() => setSelectedCategory({})}
                                style={{ marginLeft: 20, padding: '8px 22px', borderRadius: '6px', background: '#6439ff', color: '#fff', border: 'none', fontWeight: 500, cursor: 'pointer', fontSize: '15px' }}
                            >
                                Add New Category
                            </button>
                        )}
                    </div>
                    {!selectedCategory ? (
                        <DataGrid
                            className="datagrid"
                            rows={data}
                            columns={columns}
                            pageSize={9}
                            rowsPerPageOptions={[9]}
                            loading={loading}
                            getRowId={(row) => row.id}
                        />
                    ) : (
                        <div style={{
                            position: 'fixed',
                            top: 0,
                            left: 0,
                            width: '100vw',
                            height: '100vh',
                        
                            display: 'flex',
                            alignItems: 'center',
                            justifyContent: 'center',
                            zIndex: 9999
                        }}>
                            <form
                                style={{
                                   
                                  
                                    padding: '32px 40px',
                                    minWidth: '340px',
                                    display: 'flex',
                                    flexDirection: 'column',
                                    gap: '18px',
                                    alignItems: 'center',
                                }}
                                onSubmit={e => {
                                    e.preventDefault();
                                    selectedCategory && selectedCategory.id ? handleUpdate() : handleCreate();
                                }}
                            >
                                <h2 style={{ color: '#6439ff', fontWeight: 700, marginBottom: 8 }}>{selectedCategory && selectedCategory.id ? 'Edit Category' : 'Create New Category'}</h2>
                                <input
                                    type="text"
                                    placeholder="Name"
                                    value={formData.name}
                                    onChange={e => setFormData({ ...formData, name: e.target.value })}
                                    style={{ padding: '10px 18px', borderRadius: '8px', border: '1px solid #ccc', minWidth: '220px', fontSize: '16px' }}
                                    required
                                />
                                <textarea
                                    placeholder="Description"
                                    value={formData.description}
                                    onChange={e => setFormData({ ...formData, description: e.target.value })}
                                    style={{ padding: '10px 18px', borderRadius: '8px', border: '1px solid #ccc', minWidth: '220px', fontSize: '16px', minHeight: '100px' }}
                                    required
                                />
                                <div style={{ display: 'flex', gap: '16px', marginTop: 8 }}>
                                    <button
                                        type="submit"
                                        className={selectedCategory && selectedCategory.id ? 'save' : 'add'}
                                        style={{ padding: '10px 28px', borderRadius: '8px', background: '#6439ff', color: '#fff', border: 'none', fontWeight: 600, cursor: 'pointer', fontSize: '16px' }}
                                    >
                                        {selectedCategory && selectedCategory.id ? 'Update' : 'Create'}
                                    </button>
                                    <button
                                        type="button"
                                        onClick={() => { setSelectedCategory(null); setFormData({ name: '', description: '' }); }}
                                        className="cancel"
                                        style={{ padding: '10px 28px', borderRadius: '8px', background: '#ccc', color: '#333', border: 'none', fontWeight: 600, cursor: 'pointer', fontSize: '16px' }}
                                    >
                                        Cancel
                                    </button>
                                </div>
                            </form>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default PostCategoryManager;