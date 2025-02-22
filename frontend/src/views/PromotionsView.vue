<script setup>
import { computed, onMounted, ref } from 'vue'
import Sidebar from '@/components/Sidebar.vue'
import Search from '@/components/Search.vue'
import Cart from '@/components/Cart.vue'
import router from '@/router'
import userApi from '@/api/userApi'
import promotionApi from '@/api/promotionApi'
import Card from '@/components/PromotionCard.vue'

const searchQuery = ref('')
const role = ref('')
const promotions = ref([])

const fetchPromotions = async () => {
    try {
        const { data: res } = await promotionApi.getAllPromotions()
        promotions.value = res.data
    } catch (error) {
        console.error('Failed to fetch promotions:', error)
    }
}

const filteredPromotions = computed(() => {
    if (!searchQuery.value) return promotions.value
    return promotions.value.filter((promotion) =>
        promotion.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    )
})

const getUser = async () => {
    try {
        const { data: res } = await userApi.getUserByJwt()
        role.value = res.data.role
    } catch (error) {
        console.error('Failed to fetch user role:', error)
    }
}

const addPromotion = () => {
    router.push('/addpromotion')
}
onMounted(() => {
    fetchPromotions()
    getUser()
})
</script>

<template>
    <div class="flex">
        <aside class="fixed">
            <Sidebar />
        </aside>
        <main class="ml-[14rem] w-full py-4 px-8 flex flex-col gap-4 bg-gray-50 min-h-screen h-full">
            <!-- search filter section  -->
            <section class="flex gap-4">
                <Search @update-search="searchQuery = $event" />
                <div class="flex items-center cursor-pointer">
                    <fa icon="bell" />
                </div>
                <Cart />
            </section>
            <!-- Name section  -->
            <section class="w-full h-12">
                <span class="font-bold text-3xl">Promotions</span>
            </section>
            <section>
                <div
                    v-if="role === 'ADMIN'"
                    @click="addPromotion"
                    class="inline py-2 px-4 rounded-md items-center cursor-pointer bg-yellow-300 shadow-md"
                >
                    <span><fa icon="plus" /> Add Promotion</span>
                </div>
            </section>
            <section class="pb-12">
                <ul class="promotions-grid">
                    <li v-for="promotion in filteredPromotions" :key="promotion.id">
                        <Card :promotionData="promotion" />
                    </li>
                </ul>
                <p v-if="promotions.length === 0" class="mt-4 text-gray-500">
                    No promotions available.
                </p>
            </section>
        </main>
    </div>
</template>