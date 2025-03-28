<template>
    <div
        class="flex justify-between items-center flex-col w-60 h-96 rounded-lg shadow-md bg-white relative"
    >
        <div class="w-full h-3/5 relative cursor-pointer">
            <img
                :src="props.foodsData.imagePath"
                alt="Food Image"
                class="w-full h-full object-cover rounded-t-lg"
            />
        </div>
        <div
            class="flex flex-col items-center justify-center h-2/5 w-full gap-1"
        >
            <span class="font-bold">{{ props.foodsData.name }}</span>
            <span class="text-yellow-500"
                >{{
                    (
                        props.foodsData.price +
                        props.foodsData.price * 0.07
                    ).toFixed(2)
                }}
                ฿</span
            >
            <div
                class="flex items-center justify-center border border-gray-300 rounded w-32 h-8 mb-1"
            >
                <button
                    @click="decrease"
                    class="px-3 py-1 text-lg font-medium text-gray-600 hover:bg-gray-50 h-8"
                    :disabled="amount <= 0"
                    :class="{ 'opacity-50 cursor-not-allowed': amount <= 0 }"
                >
                    -
                </button>

                <div
                    class="px-3 text-center font-medium border-l border-r border-gray-300"
                >
                    {{ amount }}
                </div>

                <button
                    @click="increase"
                    class="px-3 py-1 text-lg font-medium text-gray-600 hover:bg-gray-50 h-8"
                    :disabled="amount >= props.foodsData.max"
                    :class="{
                        'opacity-50 cursor-not-allowed':
                            amount >= props.foodsData.max,
                    }"
                >
                    +
                </button>
            </div>
            <div class="flex gap-2">
                <button
                    class="px-2 py-1 rounded-md bg-yellow-300 shadow-md hover:bg-yellow-500 duration-100"
                    :class="{
                        'opacity-20 cursor-not-allowed':
                            foodStore.getCartItemCount(props.foodsData.id) +
                                amount >
                                props.foodsData.max || amount === 0,
                    }"
                    @click="addToCart(props.foodsData)"
                    :disabled="
                        foodStore.getCartItemCount(props.foodsData.id) +
                            amount >
                            props.foodsData.max || amount === 0
                    "
                >
                    Add to Cart
                </button>
            </div>
        </div>
    </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { foodsStore } from '@/stores/cart.js'
import userApi from '@/api/userApi.js'

const props = defineProps({
    foodsData: {
        type: Object,
        required: true,
    },
})

const foodStore = foodsStore()
const showRecipePopup = ref(false)
const selectedFoodId = ref(null)
const role = ref('')
const amount = ref(1) // Initial amount set to 1

// Amount counter functions
const increase = () => {
    // Check if the amount is less than max allowed
    if (amount.value < props.foodsData.max) {
        amount.value++
    }
}

const decrease = () => {
    // Don't allow negative values
    if (amount.value > 0) {
        amount.value--
    }
}

onMounted(async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.data.role // ตรวจสอบ role จาก API
})

const addToCart = (food) => {
    // Check if adding current amount would exceed the maximum
    const currentInCart = foodStore.getCartItemCount(food.id)
    const totalAfterAdd = currentInCart + amount.value

    if (totalAfterAdd <= food.max && amount.value > 0) {
        // Add to cart with the specified amount
        for (let i = 0; i < amount.value; i++) {
            foodStore.addToCart(food)
        }
        // Reset amount to 1 after adding to cart
        amount.value = 1
    }
}

</script>