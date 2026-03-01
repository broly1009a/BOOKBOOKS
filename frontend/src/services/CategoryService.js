import axios from "axios";

const CATEGORIES_API_URL = 'http://localhost:8081/api/v1/book-category'

const getCategories = () => {
    return axios.get(CATEGORIES_API_URL)
}

const getCategoryById = (id) => {
    return axios.get(CATEGORIES_API_URL + '/' + id);
}

const createCategory = (category) => {
    return axios.post(CATEGORIES_API_URL, category);
}

const updateCategory = (category) => {
    return axios.put(CATEGORIES_API_URL, category);
}

const deleteCategory = (id) => {
    return axios.delete(CATEGORIES_API_URL + '/' + id);
}

export { getCategories, getCategoryById, createCategory, updateCategory, deleteCategory }