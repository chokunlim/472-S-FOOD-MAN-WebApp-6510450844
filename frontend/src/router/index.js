import { createRouter, createWebHistory } from 'vue-router'
import authApi from '@/api/authApi'

const routes = [

    // login-signup
    {
        path: '/signup',
        name: 'signup',
        component: () => import('@/views/SignupView.vue'),
    },
    {
        path: '/signin',
        name: 'signin',
        component: () => import('@/views/SigninView.vue'),
    },

    // Food
    {
        path: '/food',
        name: 'food',
        component: () => import('@/views/FoodView.vue'),
    },
    {
        path: '/addfood',
        name: 'addfood',
        component: () => import('@/views/AddFoodView.vue'),
    },

    {
        path: '/receipt/:id',
        name: 'receipt',
        component: () => import('@/views/ReceiptView.vue'),
    },
    {
        path: '/dashboard',
        name: 'dashboard',
        component: () => import('@/views/DashboardView.vue'),
    },
    {
        path: '/cart',
        name: 'cart',
        component: () => import('@/views/CartView.vue'),
    },

    // Ingredient
    {
        path: '/ingredient',
        name: 'ingredient',
        component: () => import('@/views/IngredientView.vue'),
    },
    {
        path: '/addingredients',
        name: 'addingredients',
        component: () => import('@/views/AddIngredientsView.vue'),
    },

    // Review
    {
        path: '/order/:id/review',
        name: 'ReviewView',
        component: () => import('@/views/ReviewView.vue'),
    },
    {
        path: '/reviewlistview',
        name: 'ReviewListView',
        component: () => import('@/views/ReviewListView.vue'),
    },

    // Promotions
    {
        path: '/promotions',
        name: 'promotions',
        component: () => import('@/views/PromotionsView.vue'),
    },
    {
        path: '/addpromotion',
        name: 'addpromotion',
        component: () => import('@/views/AddPromotion.vue'),
    },

     // Payment
     {
        path: '/payment/success',
        name: 'success',
        component: () => import('@/views/SuccessView.vue'),
    },
    {
        path: '/payment/fail',
        name: 'fail',
        component: () => import('@/views/CancelView.vue'),
    },
    
    // Order
    {
        path: '/order',
        name: 'order',
        component: () => import('@/views/orders/OrderView.vue'),
    },
    {
        path: '/orderforrider',
        name: 'orderforrider',
        component: () => import('@/views/orders/OrderRiderView.vue'),
    },
    {
        path: '/orderhistory',
        name: 'orderhistory',
        component: () => import('@/views/orders/OrderHistoryView.vue'),
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
