import axios from "axios";

const CATEGORY_BASE_URL = "http://localhost:8081/api/v1/book-category";

const getAllCategories = () => {
    return axios.get(CATEGORY_BASE_URL);
}

const deleteCategory = (categoryId) => {
    return axios.delete(CATEGORY_BASE_URL + '/' + categoryId);
}

const addCategory = (category) => {
    return axios.post(CATEGORY_BASE_URL, category);
}

const getCategoryById = (id) => {
    return axios.get(CATEGORY_BASE_URL + '/' + id);
}

const updateCategory = (data) => {
    return axios.put(CATEGORY_BASE_URL, data);
}

export { getAllCategories, deleteCategory, addCategory, getCategoryById, updateCategory }
