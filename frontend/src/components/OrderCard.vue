<template>
    <div
        class="flex justify-between px-3 py-2 shadow-md rounded-xl bg-white mb-5"
    >
        <div class="flex flex-col">
            <div class="flex items-center">
                <span class="text-3xl pr-2"
                    >Order #{{ order.id.slice(0, 4) }}</span
                >
                <span
                    class="align-text-bottom text-lg"
                    :style="{
                        color:
                            order.status === 'COMPLETE'
                                ? '#ADE92E'
                                : order.status === 'PENDING'
                                ? '#FF6B00'
                                : order.status === 'SUCCESS'
                                ? '#000000'
                                : '#FF5151',
                    }"
                >
                    ●
                </span>
                <span class="align-text-bottom text-lg ml-1">{{
                    order.status
                }}</span>
            </div>
            <span class="text-md text-gray-500 py-1 ml-1"
                >By {{ props.order.user.username }}</span
            >
            <span class="text-md text-gray-500 ml-1"
                >Date: {{ order.createdAt.slice(0, 10) }}</span
            >
            <span class="text-md text-gray-500 ml-1"
                >Time: {{ order.createdAt.slice(11, 19) }}</span
            >
            <span class="py-2 pl-1">
                <button
                    v-if="order.status === 'COMPLETE' && role === 'ADMIN'"
                    class="inline-block w-52 px-10 py-2 mt-2 mr-10 rounded-lg"
                    style="background-color: #bcf14a; color: #000000"
                    @click="markOrderSuccess(order.id)"
                >
                    Mark as Success
                </button>
                <button
                    v-if="order.status === 'PENDING'"
                    class="inline-block w-52 px-10 py-2 mt-2 mr-10 rounded-lg bg-yellow-300"
                    @click="payAgain(order)"
                >
                    Pay Again
                </button>
                <button
                    class="px-12 py-2 mt-2 rounded-lg"
                    style="background-color: #f6f6f6; color: #000000"
                    @click="viewOrderDetail"
                >
                    View Details
                </button>
            </span>
        </div>
        <div class="flex flex-col items-end">
            <span class="text-4xl text-gray-750"
                >{{ order.total + order.total * 0.07 }} ฿</span
            >
        </div>
    </div>
</template>

<script setup>
import orderApi from '@/api/orderApi'
import userApi from '@/api/userApi'
import { onMounted, ref } from 'vue'

const role = ref('')

onMounted(async () => {
    const { data: res } = await userApi.getUserByJwt()
    role.value = res.data.role
})

const props = defineProps({
    order: {
        type: Object,
        required: true,
    },
    index: Number,
})

const emit = defineEmits(['mark-success', 'view-detail'])

const markOrderSuccess = async (id) => {
    try {
        await orderApi.updateOrderStatus({ id, status: 'SUCCESS' })
        emit('mark-success', id)
        window.location.reload()
    } catch (error) {
        console.error('Error marking order as success:', error)
    }
}

const viewOrderDetail = () => {
    emit('view-detail', props.order.id)
}

const payAgain = (order) => {
    window.location.href = order.paymentLink
    // console.log('Redirecting to payment link:', order.paymentLink)
}
</script>
