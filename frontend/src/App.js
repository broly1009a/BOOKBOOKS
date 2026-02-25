import { BrowserRouter as Router, Routes, Route, Navigate, useLocation } from "react-router-dom";
import { useCookies } from 'react-cookie';
import { jwtDecode } from "jwt-decode";
import { useEffect, useState, useContext } from 'react';

// Services
import { getUserInfoByEmail } from './services/UserService';
import { getAllCartByUserId } from './services/CartService';
import { addPublisher } from "./service/PublisherService";
import { addSlider } from "./service/SliderService";
import { addCollection } from "./service/CollectionService";
import { addAuthor } from "./service/AuthorService";

// Context
import { DarkModeContext } from "./context/darkModeContext";
import { AuthContext } from "./context/AuthContext";

// Role-Based Route Component
import RoleBasedRoute from "./components/RoleBasedRoute";

// Components - User-facing
import Header from './components/Header';
import HomeComponent from './components/Home/Home';
import CollectionComponent from './components/Collection/Collection';
import BooksByCollection from './components/Collection/BooksByCollection';
import ProductDetail from './components/Product/ProductDetail';
import Profile from './components/User/Profile';
import ProfileDetail from "./components/User/ProfileDetail";
import ResetPassword from './components/User/ResetPassword';
import ForgotPassword from './components/User/ForgotPassword';
import Activate from './components/User/Activate';
import CheckMail from './components/User/CheckMail';
import Cart from './components/Cart/Cart';
import Search from './components/Home/Search';
import Checkout from './components/Checkout/Checkout';
import Payment from './components/Checkout/Payment';
import Paypal from './components/Checkout/Paypal';
import Wishlist from './components/Wishlist/Wishlist';
import PostComponent from "./components/Post/Post";
import PostDetail from "./components/Post/PostDetail";
import PostCategoryManager from "./components/Post/PostCategoryManager";
import Page404 from "./components/page404";
import CustomerLogin from './components/Login/Login';

// Pages - Admin
import Home from "./pages/home/Home";
import ManagerHome from "./pages/home/ManagerHome";
import SaleHome from "./pages/home/SaleHome";
import AdminLogin from "./pages/login/Login";
import List from "./pages/list/List";
import ManagerList from "./pages/list/ManagerList";
import SaleList from "./pages/list/SaleList";
import New from "./pages/new/New";
import ProductSingle from "./pages/single/ProductSingle";
import ManagerProductSingle from "./pages/single/ManagerProductSingle";
import ProductNew from "./pages/new/ProductNew";
import ManagerProductNew from "./pages/new/ManagerProductNew";
import Order from "./pages/order/Order";
import OrderDetail from "./pages/order/OrderDetail";
import SaleOrderDetail from "./pages/order/SaleOrderDetail";
import Collection from "./pages/collection/Collection";
import Slider from "./pages/slider/Slider";
import Publisher from "./pages/publisher/Publisher";
import CollectionSingle from "./pages/collection/CollectionSingle";
import SliderSingle from "./pages/slider/SliderSingle";
import PublisherSingle from "./pages/publisher/PublisherSingle";
import AuthorSingle from "./pages/single/AuthorSingle";
import Post from "./pages/post/Post";
import PostSingle from "./pages/post/PostSingle";
import PostNew from "./pages/post/PostNew";
import Feedback from "./pages/feedback/Feedback";
import FeedbackSingle from "./pages/feedback/FeedbackSingle";
import ManagerFeedbackSingle from "./pages/feedback/ManagerFeedbackSingle";
import ChangeState from "./pages/order/ChangeState";
import ChangePassword from "./pages/changePass/changePass";
import AdminChangePassword from "./pages/changePass/AdminChangePassword";
import ManagerChangePassword from "./pages/changePass/ManagerChangePassword";
import SaleChangePassword from "./pages/changePass/SaleChangePassword";
import Customer from "./pages/customer/Customer";
import Staff from "./pages/staff/Staff";
import AddStaff from "./components/User/AddStaff";

// Form inputs and styles
import { productInputs, authorInputs, userInputs, collectionInputs, sliderInputs, publisherInputs } from "./formSource";
import "./style/dark.scss";

function App() {
  const [cookies, setCookies, removeCookies] = useCookies([]);
  const [profileData, setProfileData] = useState()
  const [cart, setCart] = useState([])
  const [cartChange, setCartChange] = useState(false)
  const { darkMode } = useContext(DarkModeContext);

  const { currentUser, dispatch: authDispatch } = useContext(AuthContext);
  
  const handleCart = async () => {
    try {
      const user_email = cookies.authToken && jwtDecode(cookies.authToken).sub;
      if (!user_email) return;
      const userInfo = await getUserInfoByEmail(user_email);
      const cartData = await getAllCartByUserId(userInfo?.data.id);

      setProfileData(userInfo?.data);
      setCart(cartData?.data);
      
      // Lưu userInfo vào AuthContext
      if (userInfo?.data) {
        authDispatch({ type: "SET_USER_INFO", payload: userInfo.data });
      }
    } catch (error) {
      console.error(error);
    }
  };
  
  useEffect(() => {
    handleCart();
  }, [cookies, cartChange]);

  const ConditionalHeader = () => {
    const location = useLocation();
    const isAdminRoute = location.pathname.startsWith('/admin') || 
                        location.pathname.startsWith('/manager') ||
                        location.pathname.startsWith('/sale') ||
                        location.pathname.startsWith('/authors') ||
                        location.pathname.startsWith('/products') ||
                        location.pathname.startsWith('/orders') ||
                        location.pathname.startsWith('/changePass') ||
                        location.pathname.startsWith('/order-state') ||
                        location.pathname.startsWith('/collections') ||
                        location.pathname.startsWith('/sliders') ||
                        location.pathname.startsWith('/publishers') ||
                        location.pathname.startsWith('/posts') ||
                        location.pathname.startsWith('/post-categories') ||
                        location.pathname.startsWith('/feedbacks') ||
                        location.pathname.startsWith('/customers') ||
                        location.pathname.startsWith('/staff');
    
    return !isAdminRoute ? (
      <Header cookies={cookies} setCookies={setCookies} removeCookies={removeCookies} cart={cart} setCartChange={setCartChange} cartChange={cartChange} />
    ) : null;
  };

  return (
    <div>
      <Router>
        <ConditionalHeader />
        <Routes>
          {/* Customer Routes */}
          <Route path='/' element={<HomeComponent />} />
          <Route path='/login' element={<CustomerLogin cookies={cookies} setCookies={setCookies} removeCookies={removeCookies} cart={cart} setCartChange={setCartChange} cartChange={cartChange} />} />
          <Route path='/collections' element={<CollectionComponent />} />
          <Route path='/collections/:id' element={<BooksByCollection />} />
          <Route path='/products/:id' element={<ProductDetail cookies={cookies} setCart={setCart} cart={cart} cartChange={cartChange} setCartChange={setCartChange} setCookie={setCookies} profile={profileData} />} />
          <Route path='/account' element={<Profile cart={cart} cookies={cookies} />} />
          <Route path="/account-detail" element={<ProfileDetail cookies={cookies} />} />
          <Route path='/reset-password/:token' element={<ResetPassword />} />
          <Route path='/forgot-password' element={<ForgotPassword />} />
          <Route path='/cart' element={<Cart cart={cart} setCart={setCart} setCartChange={setCartChange} cartChange={cartChange} />} />
          <Route path='/search/:name' element={<Search />} />
          <Route path='/checkout' element={<Checkout cart={cart} setCart={setCart} cookies={cookies} setCartChange={setCartChange} cartChange={cartChange} />} />
          <Route path='/checkout/payment' element={<Payment cart={cart} setCart={setCart} setCartChange={setCartChange} cookies={cookies} cartChange={cartChange} />} />
          <Route path='/checkout/payment/paypal' element={<Paypal value={((cart?.orderDetails?.reduce((acc, item) => acc + item.salePrice * item.amount, 0) + 30000) / 24500).toFixed(2)} cart={cart} setCart={setCart} />} />
          <Route path='/activation/:token' element={<Activate />} />
          <Route path='/check-email' element={<CheckMail />} />
          <Route path='/wishlist' element={<Wishlist setCart={setCart} cart={cart} />} />
          <Route path='/blogs/:id' element={<PostComponent />} />
          <Route path='/blog-detail/:id' element={<PostDetail />} />
          <Route path="/order-detail/:id" element={<OrderDetail />} />
          <Route path="/change-password" element={<ChangePassword cookies={cookies} />} />

          {/* Admin Routes */}
          <Route path="/admin/login" element={<AdminLogin />} />
          <Route path="/admin" element={<RoleBasedRoute allowedRoles={['ADMIN']}><Home /></RoleBasedRoute>} />
          
          <Route path="/authors">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN']}><List type={'authors'} /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN']}><AuthorSingle /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN']}><New inputs={authorInputs} title="Add New Author" handleAdd={addAuthor} location={'/authors'} /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/products">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN', 'MANAGER']}><List type={'products'} /></RoleBasedRoute>} />
            <Route path=":productId" element={<RoleBasedRoute allowedRoles={['ADMIN', 'MANAGER']}><ProductSingle /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN', 'MANAGER']}><ProductNew title="Add New Product" /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/orders">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN', 'SALE']}><Order /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN', 'SALE']}><OrderDetail /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/changePass">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN', 'MANAGER', 'SALE']}><AdminChangePassword /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/order-state">
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN', 'SALE']}><ChangeState /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/collections">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN']}><Collection /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN']}><New inputs={collectionInputs} title="Add New Collection" location={'/collections'} handleAdd={addCollection} /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN']}><CollectionSingle /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/sliders">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN']}><Slider /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN']}><New inputs={sliderInputs} title="Add New Slider" location={'/sliders'} handleAdd={addSlider} /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN']}><SliderSingle /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/publishers">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN']}><Publisher /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN']}><New inputs={publisherInputs} title="Add New Publisher" location={'/publishers'} handleAdd={addPublisher} /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN']}><PublisherSingle /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/posts">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN']}><Post /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN']}><PostSingle /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN']}><PostNew currentUser={currentUser} /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/post-categories" element={<RoleBasedRoute allowedRoles={['ADMIN']}><PostCategoryManager /></RoleBasedRoute>} />
          
          <Route path="/feedbacks">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN', 'MANAGER', 'SALE']}><Feedback /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['ADMIN', 'MANAGER']}><FeedbackSingle /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/customers">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN', 'SALE']}><Customer /></RoleBasedRoute>} />
          </Route>
          
          <Route path="/staff">
            <Route index element={<RoleBasedRoute allowedRoles={['ADMIN']}><Staff /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['ADMIN']}><AddStaff /></RoleBasedRoute>} />
          </Route>

          {/* Manager Routes */}
          <Route path="/manager" element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerHome /></RoleBasedRoute>} />
          <Route path="/manager/products">
            <Route index element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerList type={'products'} /></RoleBasedRoute>} /><Route path=":productId" element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerProductSingle /></RoleBasedRoute>} />
            <Route path="new" element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerProductNew title="Add New Product" /></RoleBasedRoute>} />
          </Route>
          <Route path="/manager/feedbacks">
            <Route index element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerList type={'feedbacks'} /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerFeedbackSingle /></RoleBasedRoute>} />
          </Route>
          <Route path="/manager/change-password" element={<RoleBasedRoute allowedRoles={['MANAGER']}><ManagerChangePassword /></RoleBasedRoute>} />

          {/* Sale Routes */}
          <Route path="/sale" element={<RoleBasedRoute allowedRoles={['SALE']}><SaleHome /></RoleBasedRoute>} />
          <Route path="/sale/orders">
            <Route index element={<RoleBasedRoute allowedRoles={['SALE']}><SaleList type={'orders'} /></RoleBasedRoute>} />
            <Route path=":id" element={<RoleBasedRoute allowedRoles={['SALE']}><SaleOrderDetail /></RoleBasedRoute>} />
          </Route>
          <Route path="/sale/customers" element={<RoleBasedRoute allowedRoles={['SALE']}><SaleList type={'customers'} /></RoleBasedRoute>} />
          <Route path="/sale/feedbacks" element={<RoleBasedRoute allowedRoles={['SALE']}><SaleList type={'feedbacks'} /></RoleBasedRoute>} />
          <Route path="/sale/change-password" element={<RoleBasedRoute allowedRoles={['SALE']}><SaleChangePassword /></RoleBasedRoute>} />
          
          <Route path="*" element={<Page404 />} />
        </Routes>
      </Router>
    </div>
  );
}

export default App;