<script setup>
import { ref, onMounted, computed } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import OrderCard from '@/components/cards/OrderCard.vue'
import orderApi from '@/api/orderApi.js'
import userApi from '@/api/userApi.js'
import router from '@/router/index.js'

const orders = ref([])
const role = ref('')
const user = ref({})
const searchQuery = ref('')
const showRecipePopup = ref(false)
const selectedOrder = ref(null)

const fetchOrders = async () => {
    try {
        const { data: orderResponse } = await orderApi.getOrders()
        const { data: userResponse } = await userApi.getUserByJwt()

        user.value = userResponse.data
        role.value = userResponse.data.role
        const loggedInUserId = user.value.id

        console.log(orderResponse)
        console.log('---')
        console.log(userResponse)
        console.log(loggedInUserId)

        // Filter orders based on user role
        orders.value =
            role.value === 'RIDER'
                ? orderResponse.data
                : orderResponse.data.filter(
                      (order) => order.user.id === loggedInUserId
                  )

        console.log('Filtered orders for display:', orders.value)
    } catch (error) {
        console.error('Error fetching orders:', error)
    }
}

onMounted(() => {
    fetchOrders()
})

const filteredOrders = computed(() => {
    return orders.value.filter((order) => {
        const matchesStatus =
            role.value === 'RIDER' &&
            (order.status === 'READY' || order.status === 'DELIVERING')
        const matchesSearch = searchQuery.value
            ? order.user.toLowerCase().includes(searchQuery.value.toLowerCase())
            : true
        return matchesStatus && matchesSearch
    })
})

const handleOrderDelivered = async (orderId) => {
    // const order = orders.value.find((o) => o.id === orderId)
    // if (order) {
    //     try {
    //         await orderApi.createOrder({ ...order, status: 'Delivered' })
    //         order.status = 'Delivered'
    //     } catch (error) {
    //         console.error('Error updating order:', error)
    //     }
    // }
}

const handleViewDetail = (orderId) => {
    router.push({ name: 'receipt', params: { id: orderId } })
}
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main
            class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 h-screen"
        >
            <!-- Orders heading -->
            <section class="w-full">
                <span class="font-bold text-3xl">Order</span>
            </section>

            <!-- Order list -->
            <section class="mt-4">
                <OrderCard
                    v-for="(order, i) in filteredOrders"
                    :index="i + 1"
                    :key="order.id"
                    :order="order"
                    @mark-delivered="handleOrderDelivered"
                    @view-detail="handleViewDetail"
                >
                </OrderCard>
            </section>
        </main>
    </div>
</template>