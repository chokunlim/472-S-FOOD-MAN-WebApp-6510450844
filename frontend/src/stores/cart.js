import { defineStore } from 'pinia'
import foodApi from '@/api/foodApi'

export const foodsStore = defineStore('foods', {
    state: () => ({
        foods: [], // List of all foods
        promotions: [], // List of all promotions
        cart: JSON.parse(localStorage.getItem('carts')) || [],
    }),

    getters: {
        cartQty: (state) => {
            // Calculate total quantity for both food and promotion items
            return state.cart.reduce((sum, item) => sum + item.quantity, 0)
        },
    },

    actions: {
        async fetchFoods() {
            try {
                const { data: res } = await foodApi.getFoods()
                this.foods = res.data
            } catch (error) {
                console.error('Error fetching foods:', error)
            }
        },

        async fetchPromotions() {
            // Fetch the promotions from your API
            try {
                const { data: res } = await foodApi.getPromotions() // You should have an API for promotions
                this.promotions = res.data
            } catch (error) {
                console.error('Error fetching promotions:', error)
            }
        },

        // Add food or promotion to the cart
        addToCart(item, type = 'food') {
            const existingItem = this.cart.find(
                (cartItem) => cartItem.type === type && cartItem.id === item.id
            )
            if (existingItem) {
                // Increase quantity if the item already exists in the cart
                existingItem.quantity += 1
            } else {
                // Add a new item to the cart
                this.cart.push({ ...item, type, quantity: 1 })
            }

            // Save cart to localStorage
            localStorage.setItem('carts', JSON.stringify(this.cart))
            console.log('Updated cart:', this.cart)
            console.log(localStorage.getItem('carts'))
        },

        removeFromCart(id) {
            console.log('remove ', id)
            this.cart = this.cart.filter((item) => {
                if (item.id === id) {
                    // Decrease quantity if it's greater than 1, else remove the item
                    if (item.quantity > 1) {
                        item.quantity -= 1
                        return true
                    } else {
                        return false
                    }
                }
                return true
            })

            // Save cart to localStorage
            localStorage.setItem('carts', JSON.stringify(this.cart))
            console.log('Updated cart:', localStorage.getItem('carts'))
        },

        removeAllById(id) {
            this.cart = this.cart.filter((item) => !(item.id === id))

            // Save cart to localStorage
            localStorage.setItem('carts', JSON.stringify(this.cart))
            console.log('Updated cart after removing all by ID:', this.cart)
        },

        getCartItemCount(id, type = 'food') {
            const itemInCart = this.cart.find(
                (item) => item.type === type && item.id === id
            )
            return itemInCart ? itemInCart.quantity : 0
        },

        // Helper function to get cart items filtered by type (food or promotion)
        getItemsByType(type) {
            return this.cart.filter((item) => item.type === type)
        },
    },
})