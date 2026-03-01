import React, { useEffect, useRef, useState } from 'react'
import { getBooksByQuery, getBookByQuery } from "../../services/BookService"
import { useNavigate, useParams } from 'react-router-dom'
import Breadscrumb from '../Breadscrumb'
import { Link } from 'react-router-dom'
import { getCollections, getCollectionById } from '../../services/CollectionService'
import { getCategories } from '../../services/CategoryService'
import Pagination from '../Pagination'
const BooksByCollection = () => {
    const navigate = useNavigate()
    const [books, setBooks] = useState([])
    const [collections, setCollections] = useState([])
    const book_length = useRef(0)
    const [categories, setCategories] = useState([])
    const [curCollection, setCurCollection] = useState()
    const [selectedCategory, setSelectedCategory] = useState(null)
    const { id } = useParams()
    const urlParams = new URLSearchParams(window.location.search);
    const [page, setPage] = useState(parseInt(urlParams.get('page')) || 1);
    const [limit,] = useState(12);
    const totalPage = Math.ceil(book_length.current / limit);
   
    const fetchData = (id) => {
        getBooksByQuery(id, page, urlParams.get('min'), urlParams.get('max'), urlParams.get('category'))
            .then(res => {
                setBooks(res.data.content)
                book_length.current = res.data.totalElements
            })
            .catch(error => console.error(error))
        getCollections()
            .then(res => setCollections(res.data))
            .catch(error => console.error(error))
        getCategories()
            .then(res => setCategories(res.data))
            .catch(error => console.error(error))
        if (id !== 'all')
            getCollectionById(id)
                .then(res => setCurCollection(res.data))
                .catch(error => console.error(error))
    }

    const collection_items = collections.map(collection => (
        collection.isDisplay ? (
            <li key={collection.id}>
                <Link to={`/collections/${collection.id}?page=1`}>{collection.name}</Link>
            </li>
        ) : null
    ));

    const setCurrentPage = (value) => {
        window.scrollTo(0, 0);
        let newPage = page;
        
        if (value === '&laquo;') {
            newPage = 1;
        }
        else if (value === '&lsaquo;') {
            if (page !== 1) {
                newPage = page - 1;
            } else {
                return;
            }
        }
        else if (value === '&rsaquo;') {
            if (page !== totalPage) {
                newPage = page + 1;
            } else {
                return;
            }
        }
        else if (value === '&raquo;') {
            newPage = totalPage;
        }
        else if (value === '...') {
            return;
        }
        else {
            newPage = value;
        }
        
        setPage(newPage);
        
        // Build URL with all filters
        const params = new URLSearchParams();
        params.set('page', newPage);
        
        const category = urlParams.get('category');
        if (category) params.set('category', category);
        
        const min = urlParams.get('min');
        if (min) params.set('min', min);
        
        const max = urlParams.get('max');
        if (max) params.set('max', max);
        
        navigate(`/collections/${id}?${params.toString()}`);
    }

    const handlePrice = (e) => {
        const minValue = Number(e.target.dataset.min)
        const maxValue = Number(e.target.dataset.max)
        const categoryParam = urlParams.get('category') ? `&category=${urlParams.get('category')}` : ''
        
        if (minValue && maxValue)
        {
            navigate(`/collections/${id}?page=1&min=${minValue}&max=${maxValue}${categoryParam}`)
        }
        else if (minValue)
        {
            navigate(`/collections/${id}?page=1&min=${minValue}${categoryParam}`)
        }
        else if (maxValue)
        {
            navigate(`/collections/${id}?page=1&max=${maxValue}${categoryParam}`)
        }
        else
        {
            navigate(`/collections/${id}?page=1${categoryParam}`)
        }
    }
    const handleChange = (e) => {
        const value = e.target.value
        const category = urlParams.get('category');
        const min = urlParams.get('min');
        const max = urlParams.get('max');
        
        let query = 'sorted-and-paged/by-collection?size=12';
        
        if (id !== 'all') {
            query += `&collection=${id}`;
        }
        
        if (category) {
            query += `&category=${category}`;
        }
        
        if (min) {
            query += `&min=${min}`;
        }
        
        if (max) {
            query += `&max=${max}`;
        }
        
        switch (value) {
            case 'manual':
                fetchData(id);
                break;
            case 'best-selling':
                getBookByQuery(query + '&sortBy=sold')
                    .then(res => setBooks(res.data.content))
                break;
            case 'title-ascending':
                getBookByQuery(query + '&sortBy=title')
                    .then(res => setBooks(res.data.content))
                break;
            case 'title-descending':
                getBookByQuery(query + '&sortBy=title&sortOrder=desc')
                    .then(res => setBooks(res.data.content))
                break;
            case 'price-ascending':
                getBookByQuery(query + '&sortBy=price')
                    .then(res => setBooks(res.data.content))
                break;
            case 'price-descending':
                getBookByQuery(query + '&sortBy=price&sortOrder=desc')
                    .then(res => setBooks(res.data.content))
                break;
            default:
                break;
        }
    }

    useEffect(() => {
        fetchData(id)
    }, [id, page, window.location.search])

    useEffect(() => {
        setPage(1)
        setSelectedCategory(null)
    }, [id])

    useEffect(() => {
        const categoryParam = urlParams.get('category')
        setSelectedCategory(categoryParam ? parseInt(categoryParam) : null)
    }, [window.location.search])
    return (
        <>
            <Breadscrumb />
            <div id='PageContaner'>
                <section id='collection-wrapper'>
                    <div className='container'>
                        <div className='row'>
                            <div className='col-lg-3'>
                                <div className='collection-sidebar-wrapper'>
                                    <div className='row'>
                                        <div className='col-lg-12'>
                                            <div className="accordion" id="accordionPanelsStayOpenExample">
                                                <div className="accordion-item">
                                                    <h2 className="accordion-header" id="panelsStayOpen-headingOne">
                                                        <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseOne" aria-expanded="true" aria-controls="panelsStayOpen-collapseOne">
                                                            Danh Mục Sản Phẩm
                                                        </button>
                                                    </h2>
                                                    <div id="panelsStayOpen-collapseOne" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingOne">
                                                        <div className='panel'>
                                                            <ul className='no-bullets'>
                                                                <li>
                                                                    <Link to={`/collections/all?page=1`}>TẤT CẢ SẢN PHẨM</Link>
                                                                </li>
                                                                {collection_items}
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="accordion" id="accordionPanelsStayOpenExample">
                                                <div className="accordion-item">                                                    <h2 className="accordion-header" id="panelsStayOpen-headingThree">
                                                        <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseThree" aria-expanded="true" aria-controls="panelsStayOpen-collapseThree">
                                                            Thể Loại
                                                        </button>
                                                    </h2>
                                                    <div id="panelsStayOpen-collapseThree" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingThree">
                                                        <div className='panel'>
                                                            <ul className='no-bullets'>
                                                                {urlParams.get('category') && (
                                                                    <li>
                                                                        <Link 
                                                                            to={`/collections/${id}?page=1${urlParams.get('min') ? `&min=${urlParams.get('min')}` : ''}${urlParams.get('max') ? `&max=${urlParams.get('max')}` : ''}`}
                                                                            style={{ color: '#d51c24', fontWeight: 'bold' }}
                                                                        >
                                                                            ✕ Xóa bộ lọc thể loại
                                                                        </Link>
                                                                    </li>
                                                                )}
                                                                {categories.map(category => (
                                                                    <li key={category.id}>
                                                                        <Link 
                                                                            to={`/collections/${id}?page=1&category=${category.id}${urlParams.get('min') ? `&min=${urlParams.get('min')}` : ''}${urlParams.get('max') ? `&max=${urlParams.get('max')}` : ''}`}
                                                                            style={selectedCategory === category.id ? { color: '#d51c24', fontWeight: 'bold' } : {}}
                                                                        >
                                                                            {category.name}
                                                                        </Link>
                                                                    </li>
                                                                ))}
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>

                                            <div className="accordion" id="accordionPanelsStayOpenExample">
                                                <div className="accordion-item">                                                    <h2 className="accordion-header" id="panelsStayOpen-headingTwo">
                                                        <button className="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#panelsStayOpen-collapseTwo" aria-expanded="false" aria-controls="panelsStayOpen-collapseTwo">
                                                            Khoảng Giá
                                                        </button>
                                                    </h2>
                                                    <div id="panelsStayOpen-collapseTwo" className="accordion-collapse collapse show" aria-labelledby="panelsStayOpen-headingTwo">
                                                        <div className='panel'>
                                                            <ul className='no-bullets'>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} type='radio' name='price-filter'></input>
                                                                        <span>Tất cả</span>
                                                                    </label>
                                                                </li>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} type='radio' data-max='10000' name='price-filter'></input>
                                                                        <span>Nhỏ hơn 10,000₫</span>
                                                                    </label>
                                                                </li>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} type='radio' data-min='10000' data-max='20000' name='price-filter'></input>
                                                                        <span> Từ 10,000₫ - 20,000₫</span>
                                                                    </label>
                                                                </li>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} type='radio' data-min='20000' data-max='30000' name='price-filter'></input>
                                                                        <span>Từ 20,000₫ - 30,000₫</span>
                                                                    </label>
                                                                </li>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} type='radio' data-min='30000' data-max='40000' name='price-filter'></input>
                                                                        <span> Từ 30,000₫ - 40,000₫</span>
                                                                    </label>
                                                                </li>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} data-min='40000' data-max='50000' type='radio' name='price-filter'></input>
                                                                        <span>Từ 40,000₫ - 50,000₫</span>
                                                                    </label>
                                                                </li>
                                                                <li>
                                                                    <label>
                                                                        <input onClick={handlePrice} type='radio' data-min='50000' name='price-filter'></input>
                                                                        <span>Lớn hơn 50,000₫</span>
                                                                    </label>
                                                                </li>
                                                            </ul>
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div className='col-lg-9'>
                                <div className='collection-content-wrapper'>
                                    <div className='collection-head'>
                                        <div className='row'>
                                            <div className='col-lg-6'>
                                                <div className='collection-title'>
                                                    <h3>
                                                        {curCollection ? curCollection.name : "TẤT CẢ SẢN PHẨM"}
                                                        {selectedCategory && categories.length > 0 && (
                                                            <span style={{ fontSize: '0.8em', color: '#666', marginLeft: '10px' }}>
                                                                • {categories.find(c => c.id === selectedCategory)?.name}
                                                            </span>
                                                        )}
                                                    </h3>
                                                </div>
                                            </div>
                                            <div className='col-lg-6'>
                                                <div className='collection-sorting-wrapper'>
                                                    <div className="form-horizontal text-right">
                                                        <label htmlFor="SortBy">Sắp xếp</label>
                                                        <select onChange={(e) => handleChange(e)} name="SortBy" id="SortBy">
                                                            <option value="manual">Tùy chọn</option>
                                                            <option value="best-selling">Bán chạy nhất</option>
                                                            <option value="title-ascending">Tên A-Z</option>
                                                            <option value="title-descending">Tên Z-A</option>
                                                            <option value="price-ascending">Giá tăng dần</option>
                                                            <option value="price-descending">Giá giảm dần</option>
                                                        </select>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>

                                    <div className='collection-body'>
                                        <div className='row'>
                                            {
                                                books.map(book => {
                                                    return (
                                                        <div key={book.id} className='col-lg-3 mb-4'>
                                                            <div className="product-item">
                                                                <div className="product-img">
                                                                    <Link to={`/products/${book.id}`}>
                                                                        <img src={book.images[0].link} alt={book.title} />
                                                                    </Link>
                                                                    <div className="tag-saleoff text-center">
                                                                        -{book.discount * 100}%
                                                                    </div>
                                                                </div>
                                                            </div>
                                                            <div className="product-info">
                                                                {book.category && (
                                                                    <div style={{ fontSize: '0.85em', color: '#888', marginBottom: '5px' }}>
                                                                        {book.category.name}
                                                                    </div>
                                                                )}
                                                                <div className="product-title">
                                                                    <Link className="text-container" to={`/products/${book.id}`}>{book.title}</Link>
                                                                </div>
                                                                <div className="product-price">
                                                                    <span className="current-price">{book.salePrice.toLocaleString()}₫</span>
                                                                    <span className="original-price"><s>{book.price.toLocaleString()}₫</s></span>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    )
                                                })
                                            }
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </section>
            </div>
            <Pagination totalPage={totalPage} page={page} limit={limit} siblings={1} setCurrentPage={setCurrentPage} />
        </>
    )

}

export default BooksByCollection