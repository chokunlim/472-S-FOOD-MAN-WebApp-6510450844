<script setup>
import Sidebar from '@/components/Sidebar.vue'
import receiptApi from '@/api/receiptApi'
import router from '@/router'
import { useRoute } from 'vue-router'
import { ref, onMounted } from 'vue'

const b_total = ref('')
const b_id = ref('')
const created_at = ref('')
const username = ref('')
const foodList = ref([])

const goBack = () => {
    router.push('/order')
}

onMounted(async () => {
    try {
        const route = useRoute()
        const { data: responseOrder } = await receiptApi.getReceiptById(
            route.params.id
        )
        const { data: responseUser } = await receiptApi.getUserById(
            route.params.id
        )
        const { data: responseFood } = await receiptApi.getFoodById(
            route.params.id
        )

        b_total.value = responseOrder.data.total
        b_id.value = responseOrder.data.id
        created_at.value = responseOrder.data.createdAt
        username.value = responseUser.data.username
        foodList.value = responseFood.data.foods
    } catch (error) {
        console.error('Error fetching receipt:', error)
    }
})
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>

        <main
            class="ml-[14rem] w-full h-full min-h-screen py-4 px-8 flex flex-col bg-[#FDFDFD]"
        >
            <!-- Back button -->
            <div
                class="flex bg-[#C7C7C7FF] text-white rounded-lg px-3 py-2 w-10 mb-4"
                @click="goBack"
            >
                <button>
                    <fa icon="arrow-left" />
                </button>
            </div>

            <div class="flex justify-center h-5/6 relative">
                <!-- Receipt -->
                <div
                    class="w-4/6 p-10 rounded-lg shadow-md border border-gray-300 bg-[#FDFDFD]"
                >
                    <h1 class="text-center text-3xl mb-4">
                        SuperDuper Restaurant
                    </h1>

                    <hr class="my-2 border-1 border-dashed border-gray-400" />

                    <!-- Items -->
                    <div>
                        <ul>
                            <li
                                v-for="item in foodList"
                                class="flex justify-between"
                            >
                                <p>{{ item.qty }} * {{ item.food.name }}</p>
                                <p>{{ item.food.price * item.qty }} B</p>
                            </li>
                        </ul>
                    </div>
                    <div class="flex-col justify-between">
                        <hr
                            class="my-2 border-1 border-dashed border-gray-400"
                        />

                        <!-- Subtotal -->
                        <div class="flex justify-between mb-2">
                            <p>Subtotal:</p>
                            <p>{{ b_total }}</p>
                        </div>

                        <!-- Subtotal -->
                        <div class="flex justify-between mb-2">
                            <p>VAT:</p>
                            <p>{{ (b_total * 0.07).toFixed(1) }}</p>
                        </div>

                        <!-- Total -->
                        <div class="flex justify-between mb-2">
                            <p>Total:</p>
                            <p>{{ b_total * 0.07 + b_total }}</p>
                        </div>

                        <hr
                            class="my-2 border-1 border-dashed border-gray-400"
                        />

                        <!-- Reference -->
                        <div class="flex justify-between mb-2">
                            <p>Ref. ID</p>
                            <p>{{ b_id }}</p>
                        </div>

                        <!-- Payment Time -->
                        <div class="flex justify-between mb-2">
                            <p>Payment Time</p>
                            <p>{{ created_at }}</p>
                        </div>

                        <!-- Customer Name -->
                        <div class="flex justify-between mb-2">
                            <p>Customer Name</p>
                            <p>{{ username }}</p>
                        </div>

                        <hr
                            class="my-2 border-1 border-dashed border-gray-400"
                        />

                        <p class="text-center mt-5">
                            Thank you for your purchase. Enjoy your meal! :D
                        </p>
                    </div>
                </div>
            </div>
        </main>
    </div>
</template>
