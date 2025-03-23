<template>
    <main class="w-full">
        <Sidebar class="fixed" />
        <div class="ml-[14rem] p-5">
            <div class="flex justify-between items-center">
                <h1 class="font-bold text-lg">Your Cart</h1>
                <button
                    class="bg-yellow-300 px-6 py-2 rounded-md hover:shadow-md duration-200"
                    @click="createOrder"
                >
                    Order
                </button>
            </div>
            <div v-if="cart.length === 0" class="mt-4">
                <p>Your cart is empty.</p>
            </div>
            <ul v-else class="mt-4">
                <li
                    v-for="item in cart"
                    :key="item.id"
                    class="flex justify-between items-center shadow-md p-4 border border-gray-300 rounded-md mb-2"
                >
                    <div v-if="item.type === 'food'" class="mr-4">
                        <img
                            class="w-32 h-32 rounded-md"
                            :src="item.imagePath"
                            alt="Food Image"
                        />
                    </div>
                    <div v-if="item.type === 'promotion'" class="mr-4">
                        <img
                            class="w-32 h-32 rounded-md"
                            :src="item.imagePath"
                        />
                    </div>
                    <div class="flex-grow">
                        <h3 class="font-bold">
                            {{ item.name }}
                        </h3>
                        <p>
                            Price:
                            {{ item.price }}
                            ฿
                        </p>
                        <p>Quantity: {{ item.quantity }}</p>
                    </div>
                    <div class="flex flex-col gap-4">
                        <button
                            class="bg-red-400 text-white py-1 px-3 rounded-md hover:bg-red-600"
                            @click="removeFromCart(item.id, item.type)"
                        >
                            Decrease
                        </button>
                        <button
                            class="bg-red-500 text-white rounded-md py-1 px-2"
                            @click="removeAllCart(item.id, item.type)"
                        >
                            Remove all
                        </button>
                    </div>
                </li>
            </ul>
            <div class="mt-4 font-bold">
                <h2>Total: {{ total + total * 0.07 }} ฿</h2>
            </div>
        </div>
    </main>
</template>

<script setup>
import { ref, computed, watchEffect } from 'vue'
import { foodsStore } from '@/stores/cart'
import Sidebar from '@/components/Sidebar.vue'
import orderApi from '@/api/orderApi'

const cart = ref(JSON.parse(localStorage.getItem('carts')) || [])

const foodStore = foodsStore()

const removeFromCart = (id, type) => {
    if (type === 'food') {
        const item = cart.value.find((item) => item.id === id)
        if (item && item.quantity > 0) {
            foodStore.removeFromCart(id)
            cart.value = JSON.parse(localStorage.getItem('carts')) || []
        }
    } else if (type === 'promotion') {
        const item = cart.value.find((item) => item.id === id)
        if (item && item.quantity > 0) {
            foodStore.removeFromCart(id)
            cart.value = JSON.parse(localStorage.getItem('carts')) || []
        }
    }
}

const removeAllCart = (id, type) => {
    if (type === 'food') {
        foodStore.removeAllById(id)
    } else if (type === 'promotion') {
        foodStore.removeAllById(id)
    }
    cart.value = JSON.parse(localStorage.getItem('carts')) || []
}

const total = computed(() => {
    return cart.value.reduce((sum, item) => {
        if (item.type === 'food') {
            return sum + item.price * item.quantity
        } else if (item.type === 'promotion') {
            return sum + item.price * item.quantity
        }
        return sum
    }, 0)
})

watchEffect(() => {
    const storedCart = JSON.parse(localStorage.getItem('carts')) || []
    cart.value = storedCart
    console.log('Cart updated:', cart.value)
})

const createOrder = async () => {
    try {
        const orderData = {
            foods: foodStore.cart
                .filter((item) => item.type === 'food')
                .map((item) => ({
                    foodId: item.id,
                    quantity: item.quantity,
                })),
            promotions: foodStore.cart
                .filter((item) => item.type === 'promotion')
                .map((item) => ({
                    promotionId: item.id,
                    quantity: item.quantity,
                })),
        }

        console.log('Order Data: ', orderData)

        // Send the order data to the API
        const { data: res } = await orderApi.createOrder(orderData)

        // Redirect to the payment link
        window.location.href = res.data.paymentLink
    } catch (err) {
        alert('Ingredients are not enough')
        console.log('Order Data: ', orderData)
        console.error('Error creating order:', err)
    }
}
</script>