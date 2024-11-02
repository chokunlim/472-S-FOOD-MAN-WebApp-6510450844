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
                    :key="item.food.id"
                    class="flex justify-between items-center shadow-md p-4 border border-gray-300 rounded-md mb-2"
                >
                    <div class="mr-4">
                        <img
                            class="w-32 h-32 rounded-md"
                            :src="item.food.imagePath"
                            alt="Food Image"
                        />
                    </div>
                    <div class="flex-grow">
                        <h3 class="font-bold">{{ item.food.name }}</h3>
                        <p>
                            Price:
                            {{ item.food.price + item.food.price * 0.07 }} ฿
                        </p>
                        <p>Quantity: {{ item.quantity }}</p>
                    </div>
                    <div class="flex flex-col gap-4">
                        <button
                            class="bg-red-400 text-white py-1 px-3 rounded-md hover:bg-red-600"
                            @click="removeFromCart(item.food.id)"
                        >
                            Decrease
                        </button>
                        <button
                            class="bg-red-500 text-white rounded-md py-1 px-2"
                            @click="removeAllCart(item.food.id)"
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

const removeFromCart = (id) => {
    const item = cart.value.find((item) => item.food.id === id)
    if (item && item.quantity > 0) {
        foodStore.removeFromCart(id)
        cart.value = JSON.parse(localStorage.getItem('carts')) || []
    }
}

const removeAllCart = (id) => {
    foodStore.removeAllById(id)
    cart.value = JSON.parse(localStorage.getItem('carts')) || []
}

const total = computed(() => {
    return cart.value.reduce(
        (sum, item) => sum + item.food.price * item.quantity,
        0
    )
})

watchEffect(() => {
    const storedCart = JSON.parse(localStorage.getItem('carts')) || []
    cart.value = storedCart
    console.log('Cart updated:', cart.value)
})

const createOrder = async () => {
    try {
        const orderData = JSON.stringify({ foods: foodStore.cart })
        console.log(orderData)
        const { data: res } = await orderApi.createOrder(orderData)

        window.location.href = res.data.paymentLink
        // window.open(data.paymentLink)
    } catch (err) {
        alert('Ingredients are not enough')
    }
}
</script>
