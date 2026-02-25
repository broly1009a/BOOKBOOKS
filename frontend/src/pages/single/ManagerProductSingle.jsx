import "./single.scss";
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
import { getBookById } from "../../service/BookService";
import { getAllPublishers } from "../../service/PublisherService";
import { getAllCollections } from "../../service/CollectionService"
import { Link, useNavigate, useParams } from "react-router-dom";
import { getAllAuthors } from "../../service/AuthorService";
import { getAllLanguages } from "../../service/LanguageService";
import { updateBook } from "../../service/BookService";

const ManagerProductSingle = () => {
    const [data, setData] = useState([])
    const [publishers, setPublishers] = useState([])
    const [collections, setCollections] = useState([])
    const [authors, setAuthors] = useState([])
    const [languages, setLanguages] = useState([])
    const { productId } = useParams()
    const [error, setError] = useState(false)
    const navigate = useNavigate()

    useEffect(() => {
        getBookById(productId).then(res => {
            setData(res.data)
        }).catch(err => {
            console.log(err)
        })

        getAllPublishers().then(res => {
            setPublishers(res.data)
        }).catch(err => {
            console.log(err)
        })

        getAllCollections().then(res => {
            setCollections(res.data)
        }
        ).catch(err => {
            console.log(err)
        })

        getAllAuthors().then(res => {
            setAuthors(res.data)
        }
        ).catch(err => {
            console.log(err)
        })

        getAllLanguages().then(res => {
            setLanguages(res.data)
        }
        ).catch(err => {
            console.log(err)
        })
    }, [])

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setData({ ...data, [name]: value });
    }

    const handleDescriptionChange = (e) => {
        setData({ ...data, description: e.target.value });
    }


    const handleObjectChange = (e) => {
        const { name, value } = e.target;
        setData({
            ...data,
            [name]: name === 'publisher' ? publishers.find(p => p.id === value) :
                    name === 'collection' ? collections.find(c => c.id === value) :
                    name === 'language' ? languages.find(l => l.id === value) : value
        });
    }

    const handleAuthorsChange = (event) => {
        const {
            target: { value },
        } = event;

        const selectedAuthors = authors.filter(a => value.includes(a.id));
        setData({ ...data, authors: selectedAuthors });
    };

    const handleUpdate = () => {
        updateBook(productId, data).then(res => {
            navigate('/manager/products')
        }).catch(err => {
            setError(true)
            console.log(err)
        })
    }

    const handleCancel = () => {
        navigate('/manager/products')
    }

    return (
        <div className="single">
            <SidebarManager />
            {data.length !== 0 && <div className="singleContainer">
                <Navbar />
                <div className="wrapper">
                    <div className="function spacing">
                        <h3>Product information</h3>
                        <div className="btn-list">
                            {error && <span style={{ color: 'red', marginRight: '20px' }}>Error</span>}
                            <button onClick={handleCancel} className="cancel">Cancel</button>
                            <button onClick={handleUpdate} className="save">Save</button>
                        </div>
                    </div>

                    {/* Rest of the form - keeping same structure */}
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
                                id="slug"
                                name="slug"
                                label="Slug"
                                fullWidth
                                autoComplete="off"
                                value={data.slug}
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
                                onChange={handleDescriptionChange}
                            />
                        </Grid>
                        <Grid item xs={12} sm={6}>
                            <FormControl fullWidth>
                                <InputLabel variant="standard" htmlFor="publisher">
                                    Publisher
                                </InputLabel>
                                <NativeSelect
                                    value={data?.publisher?.id || ''}
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
                                <InputLabel variant="standard" htmlFor="collection">
                                    Collection
                                </InputLabel>
                                <NativeSelect
                                    value={data?.collection?.id || ''}
                                    onChange={handleObjectChange}
                                    inputProps={{
                                        name: 'collection',
                                        id: 'collection',
                                    }}
                                >
                                    <option value="">Select Collection</option>
                                    {collections.map(c => (
                                        <option key={c.id} value={c.id}>{c.name}</option>
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
                                    value={data?.language?.id || ''}
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
                                <InputLabel id="authors-label">Authors</InputLabel>
                                <Select
                                    labelId="authors-label"
                                    id="authors"
                                    multiple
                                    value={data?.authors?.map(a => a.id) || []}
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
                                id="salePrice"
                                name="salePrice"
                                label="Sale Price"
                                type="number"
                                fullWidth
                                value={data.salePrice}
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
                                id="image"
                                name="image"
                                label="Image URL"
                                fullWidth
                                value={data.image}
                                onChange={handleInputChange}
                            />
                        </Grid>
                    </Grid>
                </div>
            </div>
            }
        </div>
    );
};

export default ManagerProductSingle;
