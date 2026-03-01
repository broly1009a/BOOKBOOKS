import "./new.scss";
import SidebarManager from "../../components/sidebar/SidebarManager";
import Navbar from "../../components/navbar/Navbar";
import Box from '@mui/material/Box';
import TextField from '@mui/material/TextField';
import InputLabel from '@mui/material/InputLabel';
import OutlinedInput from '@mui/material/OutlinedInput';
import MenuItem from '@mui/material/MenuItem';
import Chip from '@mui/material/Chip';
import FormControl from '@mui/material/FormControl';
import NativeSelect from '@mui/material/NativeSelect';
import Select from '@mui/material/Select';
import Grid from '@mui/material/Grid';
import { useEffect, useState } from "react";
import { addBook } from "../../service/BookService";
import { getAllPublishers } from "../../service/PublisherService";
import { getAllCollections } from "../../service/CollectionService"
import { useNavigate } from "react-router-dom";
import { getAllAuthors } from "../../service/AuthorService";
import { getAllLanguages } from "../../service/LanguageService";
import { getCategories } from "../../services/CategoryService";

const ManagerProductNew = () => {
    const [publishers, setPublishers] = useState([])
    const [collections, setCollections] = useState([])
    const [authors, setAuthors] = useState([])
    const [languages, setLanguages] = useState([])
    const [categories, setCategories] = useState([])
    const [error, setError] = useState(false)
    const [data, setData] = useState({
        title: '',
        description: '',
        publisher: {
            id: ''
        },
        authors: [],
        collections: [],
        stock: 0,
        isbn: '',
        images: [{
            link: '',
            description: 'Illustration'
        }],
        language: {
            id: ''
        },
        category: {
            id: ''
        },
        page: 0,
        weight: 0,
        size: '',
        cover: '',
        price: 0,
        discount: 0,
        sold: 0,
        state: 'HIDDEN'
    })

    const navigate = useNavigate()

    useEffect(() => {
        getAllPublishers().then(res => {
            setPublishers(res.data)
        }).catch(err => {
            console.log(err)
        })

        getAllCollections().then(res => {
            setCollections(res.data)
        }).catch(err => {
            console.log(err)
        })

        getAllAuthors().then(res => {
            setAuthors(res.data)
        }).catch(err => {
            console.log(err)
        })

        getAllLanguages().then(res => {
            setLanguages(res.data)
        }).catch(err => {
            console.log(err)
        })

        getCategories().then(res => {
            setCategories(res.data)
        }).catch(err => {
            console.log(err)
        })
    }, [])

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setData({ ...data, [name]: value });
    }

    const handleObjectChange = (e) => {
        const { name, value } = e.target;
        
        if (name === 'publisher') {
            setData({ ...data, publisher: { id: value } });
        } else if (name === 'language') {
            setData({ ...data, language: { id: value } });
        } else if (name === 'category') {
            setData({ ...data, category: { id: value } });
        }
    }

    const handleAuthorsChange = (event) => {
        const {
            target: { value },
        } = event;

        const selectedAuthors = authors.filter(a => value.includes(a.id));
        setData({ ...data, authors: selectedAuthors });
    };

    const handleCollectionsChange = (event) => {
        const {
            target: { value },
        } = event;

        const selectedCollections = collections.filter(c => value.includes(c.id));
        setData({ ...data, collections: selectedCollections });
    };

    const handleImageChange = (e) => {
        setData({
            ...data,
            images: [{
                link: e.target.value,
                description: 'Illustration'
            }]
        });
    }

    const handleAdd = () => {
        try {
            if (data.title.trim() === '' || data.publisher.id === '' || data.authors.length === 0 || data.collections.length === 0 || data.isbn.trim() === '' || data.images[0].link.trim() === '' || data.language.id === '' || data.category.id === '' || data.page === 0 || data.stock === 0 || data.weight === 0 || data.size.trim() === '' || data.cover.trim() === '' || data.price === 0) {
                setError(true)
                return
            }
            if( parseInt(data.price) < 0 || parseInt(data.page) < 0 || parseInt(data.stock) < 0 || parseInt(data.weight) < 0 || parseFloat(data.discount) < 0){
                setError(true)
                return
            }
            
            const bookData = {
                ...data,
                price: parseInt(data.price),
                page: parseInt(data.page),
                stock: parseInt(data.stock),
                weight: parseInt(data.weight),
                discount: parseFloat(data.discount),
                salePrice: parseInt(data.price) - (parseInt(data.price) * parseFloat(data.discount) / 100)
            }
            
            addBook(bookData).then(res => {
                navigate('/manager/products')
            }).catch(err => {
                setError(true)
                console.log(err)
            })
        }
        catch (err) {
            setError(true)
            return
        }
    }

    const handleCancel = () => {
        navigate('/manager/products')
    }

    return (
        <div className="new">
            <SidebarManager />
            <div className="newContainer">
                <Navbar />
                <div className="wrapper">
                    <div className="function spacing">
                        <h3>Add New Product</h3>
                        <div className="btn-list">
                            {error && <span style={{ color: 'red', marginRight: '20px' }}>Error</span>}
                            <button onClick={handleCancel} className="cancel">Cancel</button>
                            <button onClick={handleAdd} className="save">Add</button>
                        </div>
                    </div>

                    <Grid container spacing={2} className='spacing'>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                id="title"
                                name="title"
                                label="Title"
                                fullWidth
                                autoComplete="off"
                                value={data.title}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                id="isbn"
                                name="isbn"
                                label="ISBN"
                                fullWidth
                                autoComplete="off"
                                value={data.isbn}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12}>
                            <TextField
                                required
                                id="description"
                                name="description"
                                label="Description"
                                fullWidth
                                multiline
                                rows={4}
                                value={data.description}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel variant="standard" htmlFor="publisher">
                                    Publisher
                                </InputLabel>
                                <NativeSelect
                                    value={data.publisher.id}
                                    onChange={handleObjectChange}
                                    inputProps={{
                                        name: 'publisher',
                                        id: 'publisher',
                                    }}
                                >
                                    <option value="">Select Publisher</option>
                                    {publishers.map(p => (
                                        <option key={p.id} value={p.id}>{p.name}</option>
                                    ))}
                                </NativeSelect>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel variant="standard" htmlFor="language">
                                    Language
                                </InputLabel>
                                <NativeSelect
                                    value={data.language.id}
                                    onChange={handleObjectChange}
                                    inputProps={{
                                        name: 'language',
                                        id: 'language',
                                    }}
                                >
                                    <option value="">Select Language</option>
                                    {languages.map(l => (
                                        <option key={l.id} value={l.id}>{l.name}</option>
                                    ))}
                                </NativeSelect>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel variant="standard" htmlFor="category">
                                    Category
                                </InputLabel>
                                <NativeSelect
                                    value={data.category.id}
                                    onChange={handleObjectChange}
                                    inputProps={{
                                        name: 'category',
                                        id: 'category',
                                    }}
                                >
                                    <option value="">Select Category</option>
                                    {categories.map(c => (
                                        <option key={c.id} value={c.id}>{c.name}</option>
                                    ))}
                                </NativeSelect>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel id="authors-label">Authors</InputLabel>
                                <Select
                                    labelId="authors-label"
                                    id="authors"
                                    multiple
                                    value={data.authors.map(a => a.id)}
                                    onChange={handleAuthorsChange}
                                    input={<OutlinedInput id="select-multiple-chip" label="Authors" />}
                                    renderValue={(selected) => (
                                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                                            {selected.map((value) => {
                                                const author = authors.find(a => a.id === value);
                                                return <Chip key={value} label={author?.name || value} />;
                                            })}
                                        </Box>
                                    )}
                                >
                                    {authors.map((author) => (
                                        <MenuItem key={author.id} value={author.id}>
                                            {author.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel id="collections-label">Collections</InputLabel>
                                <Select
                                    labelId="collections-label"
                                    id="collections"
                                    multiple
                                    value={data.collections.map(c => c.id)}
                                    onChange={handleCollectionsChange}
                                    input={<OutlinedInput id="select-multiple-chip" label="Collections" />}
                                    renderValue={(selected) => (
                                        <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 0.5 }}>
                                            {selected.map((value) => {
                                                const collection = collections.find(c => c.id === value);
                                                return <Chip key={value} label={collection?.name || value} />;
                                            })}
                                        </Box>
                                    )}
                                >
                                    {collections.map((collection) => (
                                        <MenuItem key={collection.id} value={collection.id}>
                                            {collection.name}
                                        </MenuItem>
                                    ))}
                                </Select>
                            </FormControl>
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                id="price"
                                name="price"
                                label="Price"
                                type="number"
                                fullWidth
                                value={data.price}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                id="discount"
                                name="discount"
                                label="Discount (%)"
                                type="number"
                                fullWidth
                                value={data.discount}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                required
                                id="stock"
                                name="stock"
                                label="Stock"
                                type="number"
                                fullWidth
                                value={data.stock}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                id="page"
                                name="page"
                                label="Pages"
                                type="number"
                                fullWidth
                                value={data.page}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                id="weight"
                                name="weight"
                                label="Weight (g)"
                                type="number"
                                fullWidth
                                value={data.weight}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                id="size"
                                name="size"
                                label="Size"
                                fullWidth
                                value={data.size}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                id="cover"
                                name="cover"
                                label="Cover Type"
                                fullWidth
                                value={data.cover}
                                onChange={handleInputChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <TextField
                                id="image"
                                name="image"
                                label="Image URL"
                                fullWidth
                                value={data.images[0]?.link || ''}
                                onChange={handleImageChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel variant="standard" htmlFor="state">
                                    State
                                </InputLabel>
                                <NativeSelect
                                    value={data?.state || 'HIDDEN'}
                                    onChange={handleInputChange}
                                    inputProps={{
                                        name: 'state',
                                        id: 'state',
                                    }}
                                >
                                    <option value="HIDDEN">HIDDEN</option>
                                    <option value="ACTIVE">ACTIVE</option>
                                </NativeSelect>
                            </FormControl>
                        </Grid>
                    </Grid>
                </div>
            </div>
        </div>
    );
};

export default ManagerProductNew;
