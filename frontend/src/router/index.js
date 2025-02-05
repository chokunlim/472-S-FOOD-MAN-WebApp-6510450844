import { createRouter, createWebHistory } from 'vue-router'
import SignupView from '@/views/SignupView.vue'
import SigninView from '@/views/SigninView.vue'
import FoodView from '@/views/FoodView.vue'
import OrderView from '@/views/OrderView.vue'
import IngredientView from '@/views/IngredientView.vue'
import ReceiptView from '@/views/ReceiptView.vue'
import DashboardView from '@/views/DashboardView.vue'
import CartView from '@/views/CartView.vue'
import SuccessView from '@/views/SuccessView.vue'
import CancelView from '@/views/CancelView.vue'
import AddIngredientsView from '@/views/AddIngredientsView.vue'
import AddFoodView from '@/views/AddFoodView.vue'
import authApi from '@/api/authApi'
import ReviewView from '@/views/ReviewView.vue'

const routes = [
    {
        path: '/signup',
        name: 'signup',
        component: SignupView,
    },
    {
        path: '/signin',
        name: 'signin',
        component: SigninView,
    },
    {
        path: '/food',
        name: 'food',
        component: FoodView,
    },
    {
        path: '/order',
        name: 'order',
        component: OrderView,
    },
    {
        path: '/ingredient',
        name: 'ingredient',
        component: IngredientView,
    },
    {
        path: '/receipt/:id',
        name: 'receipt',
        component: ReceiptView,
    },
    {
        path: '/dashboard',
        name: 'dashboard',
        component: DashboardView,
    },
    {
        path: '/cart',
        name: 'cart',
        component: CartView,
    },
    {
        path: '/payment/success',
        name: 'success',
        component: SuccessView,
    },
    {
        path: '/payment/fail',
        name: 'fail',
        component: CancelView,
    },
    {
        path: '/addingredients',
        name: 'addingredients',
        component: AddIngredientsView,
    },
    {
        path: '/addfood',
        name: 'addfood',
        component: AddFoodView,
    },
    {
        path: '/order/:id/review',
        name: 'ReviewView',
        component: ReviewView,
    },
]

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes,
})

// navigation guard
router.beforeEach(async (to, from, next) => {
    try {
        // Allow navigation to 'signin' and 'signup' routes without token validation
        if (to.name === 'signin' || to.name === 'signup') {
            next()
            return
        }

        const token = localStorage.getItem('token')

        // Check if the token exists
        if (!token) {
            return next({ name: 'signin' })
        }

        const { data: response } = await authApi.validateToken(token)

        const isAuthenticated = response.success

        // Redirect to 'signin' if the token is not valid
        if (!isAuthenticated) {
            return next({ name: 'signin' })
        }

        next()
    } catch (error) {
        console.error('Error validating token:', error)
        next({ name: 'signin' })
    }
})

export default router
